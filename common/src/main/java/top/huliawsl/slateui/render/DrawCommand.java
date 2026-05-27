package top.huliawsl.slateui.render;

public sealed interface DrawCommand permits DrawRectCommand, DrawBorderCommand, DrawTextCommand, DrawDebugRectCommand, DrawTextureCommand, PushClipCommand, PopClipCommand {

    String describe();
}
