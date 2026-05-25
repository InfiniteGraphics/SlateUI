package top.huliawsl.slateui.authoring;

import java.nio.file.Path;

public final class SlateCompilerCli {

    private SlateCompilerCli() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            throw new IllegalArgumentException("Expected inputDir and outputDir");
        }
        new SlateCompiler().compileDirectory(Path.of(args[0]), Path.of(args[1]));
    }
}