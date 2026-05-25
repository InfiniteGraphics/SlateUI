package top.huliawsl.slateui.api;

@FunctionalInterface
public interface StateListener {

    void onStateDirty(String path);
}
