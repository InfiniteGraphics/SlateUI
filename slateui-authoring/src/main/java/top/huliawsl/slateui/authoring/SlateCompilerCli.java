package top.huliawsl.slateui.authoring;

import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;

public final class SlateCompilerCli {

    private SlateCompilerCli() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 1 && "--schema".equals(args[0])) {
            System.out.println(SlateAuthoringSchema.export());
            return;
        }
        boolean watch = args.length == 3 && "--watch".equals(args[0]);
        if ((!watch && args.length != 2) || (watch && args.length != 3)) {
            throw new IllegalArgumentException("Expected inputDir and outputDir, --watch inputDir outputDir, or --schema");
        }
        Path input = Path.of(watch ? args[1] : args[0]);
        Path output = Path.of(watch ? args[2] : args[1]);
        SlateCompiler compiler = new SlateCompiler();
        compiler.compileDirectory(input, output);
        if (watch) {
            try (var watcher = input.getFileSystem().newWatchService()) {
                input.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
                while (true) {
                    watcher.take().pollEvents();
                    compiler.compileDirectory(input, output);
                }
            }
        }
    }
}
