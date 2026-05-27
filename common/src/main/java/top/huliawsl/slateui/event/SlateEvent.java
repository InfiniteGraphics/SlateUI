package top.huliawsl.slateui.event;

import java.util.List;
import top.huliawsl.slateui.api.SlateComponent;

public final class SlateEvent {

    private final String type;
    private final List<SlateComponent> path;
    private final SlateComponent target;
    private SlateComponent currentTarget;
    private SlateEventPhase phase;
    private boolean propagationStopped;
    private boolean immediatePropagationStopped;
    private boolean consumed;

    public SlateEvent(String type, List<SlateComponent> path) {
        this.type = type == null || type.isBlank() ? "event" : type;
        this.path = path == null ? List.of() : List.copyOf(path);
        this.target = this.path.isEmpty() ? null : this.path.get(this.path.size() - 1);
        this.currentTarget = target;
        this.phase = SlateEventPhase.TARGET;
    }

    public String type() { return type; }
    public List<SlateComponent> path() { return path; }
    public SlateComponent target() { return target; }
    public SlateComponent currentTarget() { return currentTarget; }
    public SlateEventPhase phase() { return phase; }
    public boolean propagationStopped() { return propagationStopped; }
    public boolean immediatePropagationStopped() { return immediatePropagationStopped; }
    public boolean consumed() { return consumed; }

    public void setCurrentTarget(SlateComponent currentTarget, SlateEventPhase phase) {
        this.currentTarget = currentTarget;
        this.phase = phase == null ? SlateEventPhase.TARGET : phase;
    }

    public void stopPropagation() {
        this.propagationStopped = true;
    }

    public void stopImmediatePropagation() {
        this.immediatePropagationStopped = true;
        this.propagationStopped = true;
    }

    public void consume() {
        this.consumed = true;
    }

    public String describePath() {
        if (path.isEmpty()) {
            return "<none>";
        }
        return path.stream().map(SlateComponent::debugName).reduce((left, right) -> left + " > " + right).orElse("<none>");
    }
}
