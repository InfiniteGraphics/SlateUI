package top.huliawsl.slateui.render;

public sealed interface DrawCommand permits DrawRectCommand, DrawBorderCommand, DrawTextCommand, DrawDebugRectCommand, DrawTextureCommand, DrawNineSliceTextureCommand, DrawItemIconCommand, DrawEntityPreviewCommand, PushTransformCommand, PopTransformCommand, PushClipCommand, PopClipCommand {

    String describe();
}
