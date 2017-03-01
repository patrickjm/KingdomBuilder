package kingdombuilder;

import java.awt.Point;

public class Input {

    private static InputMouse _mouse;
    private static InputKeyboard _keyboard;
    
    public static void init() {
        _mouse = new InputMouse();
        _keyboard = new InputKeyboard();
    }

    public static InputMouse mouse() {
        return _mouse;
    }

    public static InputKeyboard keyboard() {
        return _keyboard;
    }

    public static void poll() {
        _mouse.poll();
        _keyboard.poll();
    }
}
