package smchan.freemind_my_plugin.timeline;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;

import javax.swing.SwingUtilities;

/**
 * Timeline data query adapter for the in-process H2 database
 */
class H2QueryOverallAdaptor {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger
            .getLogger(H2QueryOverallAdaptor.class.getName());

    private final String _jdbcCnxString;
    private ITimeScaleSource _timeScaleSourceOverall;
    private ITimeScaleSource _timeScaleSourceDetails;

    private ITimelineModel<ITimelineEvent> _tmOverall;
    private ITimelineModel<ITimelineEvent> _tmSelection;
    private SelectionEvent _selectionEvent;

    private ScheduledExecutorService _executor;

    public H2QueryOverallAdaptor(String jdbcCnxString, ITimeScaleSource timeScaleSourceOverall,
            ITimeScaleSource timeScaleSourceDetails, ITimelineModel<ITimelineEvent> tmOverall,
            ITimelineModel<ITimelineEvent> tmSelection, ScheduledExecutorService executor) {
        assert jdbcCnxString != null;
        assert timeScaleSourceOverall != null;
        assert timeScaleSourceDetails != null;
        assert tmOverall != null;
        assert tmSelection != null;
        assert executor != null;

        try {
            // Explicitly register H2 driver...
            // We have to do this likely because the H2 jar is in the plugin class path
            // instead of the starting class path
            DriverManager.registerDriver(new org.h2.Driver());
            _jdbcCnxString = jdbcCnxString;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load H2 driver", e);
        }

        _timeScaleSourceOverall = timeScaleSourceOverall;
        _timeScaleSourceDetails = timeScaleSourceDetails;
        _tmOverall = tmOverall;
        _tmSelection = tmSelection;
        _executor = executor;

        // Register for "overall time scale" change events so that we can update the
        // timeline model with corresponding data in the new time range
        _timeScaleSourceOverall.addTimeScaleChangedListener(new OverallTimeScaleChangeAdapter());

        // Register for "details time scale" change events so that we can update the
        // timeline model with corresponding data in the new time range
        _timeScaleSourceDetails.addTimeScaleChangedListener(new DetailsTimeScaleChangeAdapter());
    }

    public void triggerUpdateTimeScaleSourceOverall() throws Exception {
        try (Connection conn = DriverManager.getConnection(_jdbcCnxString)) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MIN(TS), MAX(TS) FROM ALL_TIMESTAMPS");
            while (rs.next()) {
                Timestamp tsMin = rs.getTimestamp(1);
                Timestamp tsMax = rs.getTimestamp(2);

                long startTime = tsMin.getTime();
                long endTime = tsMax.getTime();

                // Update UI in AWT event dispatcher
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        ITimeScale timeScale = _timeScaleSourceOverall.getTimeScale();
                        timeScale = timeScale.reset(startTime, endTime);
                        _timeScaleSourceOverall.setTimeScale(timeScale);
                    }
                });
            }
        }
    }

    public void fetch(long minTime, long maxTime, String truncateUnit, long durationMillis) throws Exception {
        try (Connection conn = DriverManager.getConnection(_jdbcCnxString)) {
            LinkedList<ITimelineEvent> list = new LinkedList<>();
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT DATE_TRUNC('" + truncateUnit + "', TS) AS TRTS, count(1) FROM ALL_TIMESTAMPS "
                            + "WHERE TS >= ? AND TS <= ? GROUP BY TRTS ORDER BY TRTS");

            stmt.setTimestamp(1, new Timestamp(minTime));
            stmt.setTimestamp(2, new Timestamp(maxTime));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Timestamp ts = rs.getTimestamp(1);
                int count = rs.getInt(2);

                long startTime = ts.getTime();
                long endTime = startTime + durationMillis;
                list.add(new IntValueEvent(count, startTime, endTime));
            }

            // Update UI in AWT event dispatcher
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    _tmOverall.clearEvents();
                    _tmOverall.addEvents(list);
                }
            });
        }
    }

    ////////////////
    private class OverallTimeScaleChangeAdapter implements ITimeScaleChangedListener {

        private static final long MILLIS_PER_MINUTE = 60 * 1000;
        private static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;
        private static final long MILLIS_PER_DAY = 24 * MILLIS_PER_HOUR;
        private static final long MILLIS_PER_MONTH = 31 * MILLIS_PER_DAY;
        private static final long MILLIS_PER_YEAR = 365 * MILLIS_PER_DAY;

        public OverallTimeScaleChangeAdapter() {
            // ...
        }

        @Override
        public void updateTimeScale(ITimeScale timeScale) {

            _executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        int pixels = timeScale.getWidthPixels();
                        long minTime = timeScale.getMinTime();
                        long maxTime = timeScale.getMaxTime();
                        long durationMillis = maxTime - minTime;
                        long millisPerPixels = durationMillis / pixels;

                        if (millisPerPixels > MILLIS_PER_MONTH) {
                            fetch(minTime, maxTime, "YEAR", MILLIS_PER_YEAR);
                        } else if (millisPerPixels > MILLIS_PER_DAY) {
                            fetch(minTime, maxTime, "MONTH", MILLIS_PER_MONTH);
                        } else if (millisPerPixels > MILLIS_PER_HOUR) {
                            fetch(minTime, maxTime, "DAY", MILLIS_PER_DAY);
                        } else if (millisPerPixels > MILLIS_PER_MINUTE) {
                            fetch(minTime, maxTime, "HOUR", MILLIS_PER_HOUR);
                        } else {
                            fetch(minTime, maxTime, "MINUTE", MILLIS_PER_MINUTE);
                        }
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Failed to fetch", e);
                    }
                }
            });
        }
    }

    ////////////////
    private class DetailsTimeScaleChangeAdapter implements ITimeScaleChangedListener {

        public DetailsTimeScaleChangeAdapter() {
            // ...
        }

        @Override
        public void updateTimeScale(ITimeScale timeScale) {
            if (_selectionEvent != null) {
                _tmSelection.removeEvent(_selectionEvent);
                _selectionEvent = null;
            }

            if (timeScale != null) {
                _selectionEvent = new SelectionEvent(timeScale.getMinTime(), timeScale.getMaxTime());
                _tmSelection.addEvent(_selectionEvent);
            }
        }

    }

}
