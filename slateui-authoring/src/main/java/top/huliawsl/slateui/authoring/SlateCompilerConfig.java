package top.huliawsl.slateui.authoring;

public record SlateCompilerConfig(WarningLevel warningLevel, boolean strictUnknownAttributes) {

    public static final SlateCompilerConfig DEFAULT = new SlateCompilerConfig(WarningLevel.WARN, true);
}
