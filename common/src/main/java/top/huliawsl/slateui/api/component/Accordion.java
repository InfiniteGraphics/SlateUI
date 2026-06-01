package top.huliawsl.slateui.api.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StackDirection;

public final class Accordion extends Stack {

    public record Section(String title, SlateComponent content, boolean open) {}

    public Accordion(List<Section> sections, String toggleCommand, SlateStyle style) {
        super(StackDirection.COLUMN, createChildren(sections, toggleCommand), style);
    }

    private static List<SlateComponent> createChildren(List<Section> sections, String toggleCommand) {
        List<SlateComponent> children = new ArrayList<>();
        List<Section> safeSections = sections == null ? List.of() : sections;
        for (int index = 0; index < safeSections.size(); index++) {
            Section section = safeSections.get(index);
            children.add(new Button(
                (section.open() ? "- " : "+ ") + section.title(),
                toggleCommand,
                Map.of("sectionIndex", index, "sectionTitle", section.title(), "open", section.open(), "nextOpen", !section.open()),
                SlateStyle.EMPTY
            ));
            if (section.open()) {
                children.add(section.content());
            }
        }
        return children;
    }
}
