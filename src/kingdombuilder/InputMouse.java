package kingdombuilder;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class InputMouse implements MouseListener, MouseMotionListener {
    private enum MouseState {
        RELEASED,
        PRESSED,
        ONCE
    }
    private static final int BUTTON_COUNT = 3;
    private Point mousePos = null;
    private Point currentPos = null;
    private boolean[] state = null;
    private MouseState[] poll = null;

    public InputMouse() {
        mousePos = new Point(0, 0);
        currentPos = new Point(0, 0);
        state = new boolean[BUTTON_COUNT];
        poll = new MouseState[BUTTON_COUNT];
        for (int i = 0; i < BUTTON_COUNT; ++i) {
            poll[i] = MouseState.RELEASED;
        }
    }

    public synchronized void poll() {
        mousePos = new Point(currentPos);
        for (int i = 0; i < BUTTON_COUNT; ++i) {
            if (state[i]) {
                if (poll[i] == MouseState.RELEASED) {
                    poll[i] = MouseState.ONCE;
                } else {
                    poll[i] = MouseState.PRESSED;
                }
            } else {
                poll[i] = MouseState.RELEASED;
            }
        }
    }

    public Point getPosition() {
        return new Point((int)mousePos.getX(), (int)mousePos.getY());
    }
    
    public Vector getVector() {
        return new Vector((int)mousePos.getX(), (int)mousePos.getY());
    }
    
    public int getX() {
        return (int)mousePos.getX();
    }
    
    public int getY() {
        return (int)mousePos.getY();
    }

    public boolean getPressed(int button) {
        return poll[button - 1] == MouseState.ONCE;
    }

    public boolean getDown(int button) {
        return poll[button - 1] == MouseState.ONCE || poll[button - 1] == MouseState.PRESSED;
    }

    public synchronized void mousePressed(MouseEvent e) {
        state[e.getButton() - 1] = true;
    }

    public synchronized void mouseReleased(MouseEvent e) {
        state[e.getButton() - 1] = false;
    }

    public synchronized void mouseEntered(MouseEvent e) {
        mouseMoved(e);
    }

    public synchronized void mouseExited(MouseEvent e) {
        mouseMoved(e);
    }

    public synchronized void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

    public synchronized void mouseMoved(MouseEvent e) {
        currentPos = e.getPoint();
    }

    public void mouseClicked(MouseEvent e) {
        // Not needed
    }
    
    public boolean mouseIn(Rectangle rect) {
    	return (getX() > rect.getX()) && 
    		   (getX() < rect.getX() + rect.getWidth()) &&
    		   (getY() > rect.getY()) &&
    		   (getY() < rect.getY() + rect.getHeight());
    	
    }
}
