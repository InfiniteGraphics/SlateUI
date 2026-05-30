package top.huliawsl.slateui.api.component;

import java.util.ArrayList;
import java.util.List;
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
        for (Section section : sections == null ? List.<Section>of() : sections) {
            children.add(new Button((section.open() ? "- " : "+ ") + section.title(), toggleCommand, SlateStyle.EMPTY));
            if (section.open()) {
                children.add(section.content());
            }
        }
        return children;
    }
}
