package top.huliawsl.slateui.debug;

import top.huliawsl.slateui.api.SlateComponent;

public final class SlateRuntimeException extends RuntimeException {

    private final String stage;
    private final String componentPath;
    private final String detail;

    public SlateRuntimeException(String stage, String componentPath, String detail, Throwable cause) {
        super(message(stage, componentPath, detail), cause);
        this.stage = stage == null ? "runtime" : stage;
        this.componentPath = componentPath == null || componentPath.isBlank() ? "<unknown>" : componentPath;
        this.detail = detail == null || detail.isBlank() ? "<none>" : detail;
    }

    public String stage() {
        return stage;
    }

    public String componentPath() {
        return componentPath;
    }

    public String detail() {
        return detail;
    }

    public static SlateRuntimeException component(String stage, SlateComponent component, Throwable cause) {
        return new SlateRuntimeException(stage, path(component), component == null ? "<component unavailable>" : component.debugName(), cause);
    }

    public static SlateRuntimeException command(SlateComponent component, String commandId, Throwable cause) {
        return new SlateRuntimeException("command", path(component), commandId, cause);
    }

    public static SlateRuntimeException binding(String componentPath, String expression, Throwable cause) {
        return new SlateRuntimeException("binding", componentPath, expression, cause);
    }

    private static String path(SlateComponent component) {
        return component == null ? "<unknown>" : component.debugPath();
    }

    private static String message(String stage, String componentPath, String detail) {
        return "SlateUI " + (stage == null ? "runtime" : stage)
            + " failed at " + (componentPath == null || componentPath.isBlank() ? "<unknown>" : componentPath)
            + " detail=" + (detail == null || detail.isBlank() ? "<none>" : detail);
    }
}
