package top.huliawsl.slateui.render;

import java.lang.reflect.Method;
import java.util.function.Function;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

final class GuiGraphicsCompat {

    private static final Method LEGACY_BLIT = findLegacyBlit();
    private static final Method RENDER_TYPE_BLIT = findRenderTypeBlit();
    private static final Method GUI_TEXTURED_FACTORY = findGuiTexturedFactory();
    private static final Function<ResourceLocation, RenderType> GUI_TEXTURED = GuiGraphicsCompat::resolveGuiTextured;

    private GuiGraphicsCompat() {
    }

    static void blit(
        GuiGraphics graphics,
        ResourceLocation texture,
        int x,
        int y,
        int u,
        int v,
        int regionWidth,
        int regionHeight,
        int textureWidth,
        int textureHeight
    ) {
        try {
            if (LEGACY_BLIT != null) {
                LEGACY_BLIT.invoke(graphics, texture, x, y, (float) u, (float) v, regionWidth, regionHeight, textureWidth, textureHeight);
                return;
            }
            if (RENDER_TYPE_BLIT != null) {
                RENDER_TYPE_BLIT.invoke(graphics, GUI_TEXTURED, texture, x, y, (float) u, (float) v, regionWidth, regionHeight, textureWidth, textureHeight);
                return;
            }
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Failed to invoke GuiGraphics.blit compatibility bridge", exception);
        }
        throw new IllegalStateException("Unsupported GuiGraphics.blit signature for current Minecraft version");
    }

    private static Method findLegacyBlit() {
        try {
            return GuiGraphics.class.getMethod(
                "blit",
                ResourceLocation.class,
                int.class,
                int.class,
                float.class,
                float.class,
                int.class,
                int.class,
                int.class,
                int.class
            );
        } catch (NoSuchMethodException ignored) {
            return null;
        }
    }

    private static Method findRenderTypeBlit() {
        try {
            return GuiGraphics.class.getMethod(
                "blit",
                Function.class,
                ResourceLocation.class,
                int.class,
                int.class,
                float.class,
                float.class,
                int.class,
                int.class,
                int.class,
                int.class
            );
        } catch (NoSuchMethodException ignored) {
            return null;
        }
    }

    private static Method findGuiTexturedFactory() {
        try {
            return RenderType.class.getMethod("guiTextured", ResourceLocation.class);
        } catch (NoSuchMethodException ignored) {
            return null;
        }
    }

    private static RenderType resolveGuiTextured(ResourceLocation texture) {
        if (GUI_TEXTURED_FACTORY == null) {
            throw new IllegalStateException("RenderType.guiTextured is unavailable for current Minecraft version");
        }
        try {
            return (RenderType) GUI_TEXTURED_FACTORY.invoke(null, texture);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Failed to resolve RenderType.guiTextured", exception);
        }
    }
}
