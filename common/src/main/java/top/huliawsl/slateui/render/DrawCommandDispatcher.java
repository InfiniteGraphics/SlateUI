package top.huliawsl.slateui.render;

import java.util.List;
import top.huliawsl.slateui.runtime.SlateRenderer;

public final class DrawCommandDispatcher {

    private DrawCommandDispatcher() {
    }

    public static void render(List<DrawCommand> commands, SlateRenderer renderer) {
        for (DrawCommand command : commands) {
            if (command instanceof DrawRectCommand rectCommand) {
                renderer.fill(rectCommand.rect(), rectCommand.color(), rectCommand.radius());
            } else if (command instanceof DrawBorderCommand borderCommand) {
                renderer.drawBorder(borderCommand.rect(), borderCommand.color(), borderCommand.thickness(), borderCommand.radius());
            } else if (command instanceof DrawTextCommand textCommand) {
                renderer.drawText(textCommand.x(), textCommand.y(), textCommand.slateText(), textCommand.color());
            } else if (command instanceof DrawLineCommand lineCommand) {
                renderer.drawLine(lineCommand.start(), lineCommand.end(), lineCommand.color(), lineCommand.thickness());
            } else if (command instanceof DrawPolylineCommand polylineCommand) {
                renderer.drawPolyline(polylineCommand.points(), polylineCommand.color(), polylineCommand.thickness());
            } else if (command instanceof DrawBezierCommand bezierCommand) {
                renderer.drawBezier(bezierCommand.start(), bezierCommand.control1(), bezierCommand.control2(), bezierCommand.end(), bezierCommand.color(), bezierCommand.thickness());
            } else if (command instanceof DrawCircleCommand circleCommand) {
                renderer.drawCircle(circleCommand.center(), circleCommand.radius(), circleCommand.color(), circleCommand.filled(), circleCommand.thickness());
            } else if (command instanceof DrawDashedLineCommand dashedLineCommand) {
                renderer.drawDashedLine(dashedLineCommand.start(), dashedLineCommand.end(), dashedLineCommand.color(), dashedLineCommand.thickness(), dashedLineCommand.dashLength(), dashedLineCommand.gapLength());
            } else if (command instanceof DrawArrowCommand arrowCommand) {
                renderer.drawArrow(arrowCommand.start(), arrowCommand.end(), arrowCommand.color(), arrowCommand.thickness(), arrowCommand.headLength());
            } else if (command instanceof DrawRawCommand rawCommand) {
                renderer.drawRaw(rawCommand.bounds(), rawCommand.callback());
            } else if (command instanceof DrawDebugRectCommand debugRectCommand) {
                renderer.drawBorder(debugRectCommand.rect(), debugRectCommand.color(), 1, 0);
            } else if (command instanceof DrawTextureCommand textureCommand) {
                renderer.drawTexture(
                    textureCommand.rect(),
                    textureCommand.texture(),
                    textureCommand.u(),
                    textureCommand.v(),
                    textureCommand.textureWidth(),
                    textureCommand.textureHeight(),
                    textureCommand.regionWidth(),
                    textureCommand.regionHeight()
                );
            } else if (command instanceof DrawNineSliceTextureCommand nineSliceCommand) {
                renderer.drawNineSliceTexture(
                    nineSliceCommand.rect(),
                    nineSliceCommand.texture(),
                    nineSliceCommand.slices(),
                    nineSliceCommand.textureWidth(),
                    nineSliceCommand.textureHeight()
                );
            } else if (command instanceof DrawItemIconCommand itemIconCommand) {
                renderer.drawItemIcon(itemIconCommand.rect(), itemIconCommand.itemId(), itemIconCommand.count());
            } else if (command instanceof DrawEntityPreviewCommand entityPreviewCommand) {
                renderer.drawEntityPreview(entityPreviewCommand.rect(), entityPreviewCommand.entityType(), entityPreviewCommand.yaw(), entityPreviewCommand.pitch());
            } else if (command instanceof PushTransformCommand transformCommand) {
                renderer.pushTransform(transformCommand.translateX(), transformCommand.translateY(), transformCommand.scale(), transformCommand.rotationDegrees(), transformCommand.opacity());
            } else if (command instanceof PopTransformCommand) {
                renderer.popTransform();
            } else if (command instanceof PushClipCommand pushClipCommand) {
                renderer.pushClip(pushClipCommand.rect(), pushClipCommand.radius());
            } else if (command instanceof PopClipCommand) {
                renderer.popClip();
            }
        }
    }
}
