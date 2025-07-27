package smchan.freemind_my_plugin.timeline;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

/**
 * Timeline data ingest adapter for the in-process H2 database
 */
class H2DataIngestAdaptor implements Closeable {

    private static final int TEXT_NODE_MAX_LENGTH = 64;
    private final Connection _conn;

    public H2DataIngestAdaptor(String jdbcCnxString) throws SQLException {
        try {
            // Explicitly register H2 driver...
            // We have to do this likely because the H2 jar is in the plugin class path
            // instead of the starting class path
            DriverManager.registerDriver(new org.h2.Driver());
            _conn = DriverManager.getConnection(jdbcCnxString);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load H2 driver", e);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            _conn.close();
        } catch (SQLException e) {
            throw new IOException("Failed to close connection", e);
        }
    }

    public void recreateTables() throws SQLException {
        execStmt(_conn, "DROP TABLE IF EXISTS ALL_MAPS;");
        execStmt(_conn, "CREATE TABLE ALL_MAPS(MAP_IDENTITY IDENTITY NOT NULL PRIMARY KEY, "
                + "PATH VARCHAR(1024), LAST_INGEST TIMESTAMP);");

        execStmt(_conn, "DROP TABLE IF EXISTS ALL_NODES;");
        execStmt(_conn, "CREATE TABLE ALL_NODES(NODE_IDENTITY IDENTITY NOT NULL PRIMARY KEY, "
                + "NODE_ID VARCHAR(32) NOT NULL, NODE_TEXT VARCHAR(64), MAP_IDENTITY LONG NOT NULL);");

        execStmt(_conn, "DROP TABLE IF EXISTS ALL_TIMESTAMPS;");
        // Refer to NODE_IDENTITY as LONG instead of IDENTITY to avoid "Unique index or
        // primary key violation"
        execStmt(_conn, "CREATE TABLE ALL_TIMESTAMPS(TS TIMESTAMP NOT NULL, TYPE CHAR(1) NOT NULL, "
                + "DURATION INT, NODE_IDENTITY LONG NOT NULL);");
    }

    public long insertMap(String path) throws SQLException {
        PreparedStatement prepStmt = _conn.prepareStatement("INSERT INTO ALL_MAPS (PATH, LAST_INGEST) VALUES (?, ?);",
                PreparedStatement.RETURN_GENERATED_KEYS);

        prepStmt.setString(1, path);
        prepStmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
        prepStmt.execute();

        ResultSet rs = prepStmt.getGeneratedKeys();
        if (rs.next()) {
            return rs.getLong(1);
        }

        throw new RuntimeException("No generated key?!");
    }

    public long insertNode(long mapIdenity, String nodeId, String text) throws SQLException {
        PreparedStatement prepStmt = _conn.prepareStatement(
                "INSERT INTO ALL_NODES (NODE_ID, NODE_TEXT, MAP_IDENTITY) VALUES (?, ?, ?);",
                PreparedStatement.RETURN_GENERATED_KEYS);

        prepStmt.setString(1, nodeId);

        if (text != null && text.length() > TEXT_NODE_MAX_LENGTH) {
            text = text.substring(0, TEXT_NODE_MAX_LENGTH - 1) + "…";
        }
        prepStmt.setString(2, text);
        prepStmt.setLong(3, mapIdenity);
        prepStmt.execute();

        ResultSet rs = prepStmt.getGeneratedKeys();
        if (rs.next()) {
            return rs.getLong(1);
        }

        throw new RuntimeException("No generated key?!");
    }

    public void insertTimestamp(long millis, String type, long nodeIdentity) throws SQLException {
        PreparedStatement prepStmt = _conn
                .prepareStatement("INSERT INTO ALL_TIMESTAMPS (TS, TYPE, NODE_IDENTITY) VALUES (?, ?, ?);");

        Timestamp sqlTS = new Timestamp(millis);
        prepStmt.setTimestamp(1, sqlTS);
        prepStmt.setString(2, type);
        prepStmt.setLong(3, nodeIdentity);
        prepStmt.execute();
    }

    private void execStmt(Connection conn, String sqlStmt) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute(sqlStmt);
        stmt.close();
    }

}
