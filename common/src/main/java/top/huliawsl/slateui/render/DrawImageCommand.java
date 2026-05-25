package top.huliawsl.slateui.render;

import net.minecraft.resources.ResourceLocation;
import top.huliawsl.slateui.layout.Rect;

public record DrawImageCommand(Rect rect, ResourceLocation resourceLocation, boolean missing) implements DrawCommand {

    @Override
    public String describe() {
        return "DrawImage rect=" + rect + " resource=" + resourceLocation + (missing ? " missing" : "");
    }
}
