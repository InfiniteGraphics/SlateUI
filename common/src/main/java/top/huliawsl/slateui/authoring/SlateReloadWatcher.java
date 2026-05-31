package top.huliawsl.slateui.authoring;

import java.nio.file.Path;
import java.time.Instant;

public final class SlateReloadWatcher {

    private final Path watchedPath;
    private Instant lastReload = Instant.EPOCH;

    public SlateReloadWatcher(Path watchedPath) {
        this.watchedPath = watchedPath;
    }

    public Path watchedPath() {
        return watchedPath;
    }

    public Instant lastReload() {
        return lastReload;
    }

    public SlateReloadResult markReloaded(String reason) {
        lastReload = Instant.now();
        return SlateReloadResult.success(reason);
    }
}
