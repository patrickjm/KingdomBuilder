package kingdombuilder;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputKeyboard implements KeyListener {

    private enum KeyState {

        RELEASED, // Not down
        PRESSED, // Down, but not the first time
        ONCE      // Down for the first time
    }
    private static final int KEY_COUNT = 256;
    private boolean[] currentKeys = null;
    private KeyState[] keys = null;

    public InputKeyboard() {
        currentKeys = new boolean[KEY_COUNT];
        keys = new KeyState[KEY_COUNT];
        for (int i = 0; i < KEY_COUNT; ++i) {
            keys[ i] = KeyState.RELEASED;
        }
    }

    public synchronized void poll() {
        for (int i = 0; i < KEY_COUNT; ++i) {
            if (currentKeys[ i]) {
                if (keys[i] == KeyState.RELEASED) {
                    keys[i] = KeyState.ONCE;
                } else {
                    keys[i] = KeyState.PRESSED;
                }
            } else {
                keys[i] = KeyState.RELEASED;
            }
        }
    }

    public boolean keyDown(int keyCode) {
        return keys[ keyCode] == KeyState.ONCE
                || keys[ keyCode] == KeyState.PRESSED;
    }

    public boolean keyDownOnce(int keyCode) {
        return keys[ keyCode] == KeyState.ONCE;
    }

    @Override
    public synchronized void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode >= 0 && keyCode < KEY_COUNT) {
            currentKeys[ keyCode] = true;
        }
    }

    @Override
    public synchronized void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode >= 0 && keyCode < KEY_COUNT) {
            currentKeys[ keyCode] = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
