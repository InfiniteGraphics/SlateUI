package top.huliawsl.slateui.api.component;

import java.util.Collection;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;

/**
 * Public-facing list component alias for Java callers.
 *
 * <p>The original implementation class remains {@link SlateList} to avoid
 * accidental source breaks for existing users.</p>
 */
public class List extends SlateList {

    public List(Collection<? extends SlateComponent> children, SlateStyle style) {
        super(java.util.List.copyOf(children), style);
    }

    public List(Collection<? extends SlateComponent> children, java.util.Map<String, java.util.List<SlateComponent>> namedSlots, SlateStyle style, SlateStyle itemStyle) {
        super(java.util.List.copyOf(children), namedSlots, style, itemStyle);
    }
}
