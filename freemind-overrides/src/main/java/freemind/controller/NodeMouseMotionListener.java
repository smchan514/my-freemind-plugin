/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2001  Joerg Mueller <joergmueller@bigfoot.com>
 *See COPYING for Details
 *
 *This program is free software; you can redistribute it and/or
 *modify it under the terms of the GNU General Public License
 *as published by the Free Software Foundation; either version 2
 *of the License, or (at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
/*$Id: NodeMouseMotionListener.java,v 1.15.14.3 2006/01/12 23:10:12 christianfoltin Exp $*/

package freemind.controller;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import freemind.main.Resources;

/**
 * The MouseMotionListener which belongs to every NodeView
 */
public class NodeMouseMotionListener implements MouseMotionListener,
		MouseListener {

	public static interface NodeMouseMotionObserver extends
			MouseMotionListener, MouseListener {

		void updateSelectionMethod();

	}

	private final Controller c;

	private NodeMouseMotionObserver mListener;

    private boolean _processMouseButton2Events;

	public NodeMouseMotionListener(Controller controller) {
		c = controller;
        _processMouseButton2Events = Resources.getInstance()
                .getBoolProperty("process_mouse_button2_events_in_NodeMouseMotionListener");
	}

	public void register(NodeMouseMotionObserver listener) {
		this.mListener = listener;

	}

	public void deregister() {
		mListener = null;
	}

	@Override
    public void mouseClicked(MouseEvent e) {
        if (!_processMouseButton2Events && e.getButton() == MouseEvent.BUTTON2) {
            // [2025-12-28] Process mouse button2 only if configured
	        return;
	    }
		if (mListener != null) {
            mListener.mouseClicked(e);
        }
	}

	@Override
    public void mouseDragged(MouseEvent e) {
        if (!_processMouseButton2Events && (e.getModifiersEx() & MouseEvent.BUTTON2_DOWN_MASK) != 0) {
            // [2025-12-28] Process mouse button2 only if configured
            // Use e.getModifiersEx() to test mouse button because e.getButton() seems to
            // always return 0 in MouseMotionListener methods.
            return;
        }
		if (mListener != null) {
            mListener.mouseDragged(e);
        }
	}

	@Override
    public void mouseEntered(MouseEvent e) {
        if (!_processMouseButton2Events && e.getButton() == MouseEvent.BUTTON2) {
            // [2025-12-28] Process mouse button2 only if configured
            return;
        }
		if (mListener != null) {
            mListener.mouseEntered(e);
        }
	}

	@Override
    public void mouseExited(MouseEvent e) {
        if (!_processMouseButton2Events && e.getButton() == MouseEvent.BUTTON2) {
            // [2025-12-28] Process mouse button2 only if configured
            return;
        }
		if (mListener != null) {
            mListener.mouseExited(e);
        }
	}

	@Override
    public void mouseMoved(MouseEvent e) {
        if (!_processMouseButton2Events && (e.getModifiersEx() & MouseEvent.BUTTON2_DOWN_MASK) != 0) {
            // [2025-12-28] Process mouse button2 only if configured
            // Use e.getModifiersEx() to test mouse button because e.getButton() seems to
            // always return 0 in MouseMotionListener methods.
            return;
        }
		if (mListener != null) {
            mListener.mouseMoved(e);
        }
	}

	@Override
    public void mousePressed(MouseEvent e) {
        if (!_processMouseButton2Events && e.getButton() == MouseEvent.BUTTON2) {
            // [2025-12-28] Process mouse button2 only if configured
            return;
        }
		if (mListener != null) {
            mListener.mousePressed(e);
        }
	}

	@Override
    public void mouseReleased(MouseEvent e) {
        if (!_processMouseButton2Events && e.getButton() == MouseEvent.BUTTON2) {
            // [2025-12-28] Process mouse button2 only if configured
            return;
        }
		if (mListener != null) {
            mListener.mouseReleased(e);
        }
	}

	public void updateSelectionMethod() {
		if (mListener != null) {
            mListener.updateSelectionMethod();
        }
	}

}
