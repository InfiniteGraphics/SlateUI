package top.huliawsl.slateui.render;

import java.util.List;
import top.huliawsl.slateui.runtime.SlateRenderer;

public final class DrawCommandDispatcher {

    private DrawCommandDispatcher() {
    }

    public static void render(List<DrawCommand> commands, SlateRenderer renderer) {
        for (DrawCommand command : commands) {
            switch (command) {
                case DrawRectCommand rectCommand -> renderer.fill(rectCommand.rect(), rectCommand.color(), rectCommand.radius());
                case DrawBorderCommand borderCommand -> renderer.drawBorder(borderCommand.rect(), borderCommand.color(), borderCommand.thickness(), borderCommand.radius());
                case DrawTextCommand textCommand -> renderer.drawText(textCommand.x(), textCommand.y(), textCommand.slateText(), textCommand.color());
                case DrawDebugRectCommand debugRectCommand -> renderer.drawBorder(debugRectCommand.rect(), debugRectCommand.color(), 1, 0);
                case DrawTextureCommand textureCommand -> renderer.drawTexture(
                    textureCommand.rect(),
                    textureCommand.texture(),
                    textureCommand.u(),
                    textureCommand.v(),
                    textureCommand.textureWidth(),
                    textureCommand.textureHeight(),
                    textureCommand.regionWidth(),
                    textureCommand.regionHeight()
                );
                case DrawNineSliceTextureCommand nineSliceCommand -> renderer.drawNineSliceTexture(
                    nineSliceCommand.rect(),
                    nineSliceCommand.texture(),
                    nineSliceCommand.slices(),
                    nineSliceCommand.textureWidth(),
                    nineSliceCommand.textureHeight()
                );
                case DrawItemIconCommand itemIconCommand -> renderer.drawItemIcon(itemIconCommand.rect(), itemIconCommand.itemId(), itemIconCommand.count());
                case DrawEntityPreviewCommand entityPreviewCommand -> renderer.drawEntityPreview(entityPreviewCommand.rect(), entityPreviewCommand.entityType(), entityPreviewCommand.yaw(), entityPreviewCommand.pitch());
                case PushTransformCommand transformCommand -> renderer.pushTransform(transformCommand.translateX(), transformCommand.translateY(), transformCommand.scale(), transformCommand.rotationDegrees(), transformCommand.opacity());
                case PopTransformCommand ignored -> renderer.popTransform();
                case PushClipCommand pushClipCommand -> renderer.pushClip(pushClipCommand.rect(), pushClipCommand.radius());
                case PopClipCommand ignored -> renderer.popClip();
            }
        }
    }
}
