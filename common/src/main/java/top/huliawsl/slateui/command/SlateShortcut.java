package top.huliawsl.slateui.command;

import org.lwjgl.glfw.GLFW;

public record SlateShortcut(int keyCode, int modifiers, String label) {

    public static SlateShortcut of(int keyCode, int modifiers, String label) {
        return new SlateShortcut(keyCode, modifiers, label);
    }

    public static SlateShortcut ctrl(int keyCode, String label) {
        return new SlateShortcut(keyCode, GLFW.GLFW_MOD_CONTROL, label);
    }

    public static SlateShortcut ctrlShift(int keyCode, String label) {
        return new SlateShortcut(keyCode, GLFW.GLFW_MOD_CONTROL | GLFW.GLFW_MOD_SHIFT, label);
    }

    public boolean matches(int keyCode, int modifiers) {
        return this.keyCode == keyCode && (modifiers & this.modifiers) == this.modifiers;
    }
}
