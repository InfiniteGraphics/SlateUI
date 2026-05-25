package top.huliawsl.slateui.render;

public sealed interface DrawCommand permits DrawRectCommand, DrawBorderCommand, DrawTextCommand, DrawDebugRectCommand, DrawImageCommand, PushClipCommand, PopClipCommand {

    String describe();
}
