package top.huliawsl.slateui.platform;

public enum SupportLevel {
    SUPPORTED(true, true),
    EXPERIMENTAL(true, false),
    CONSIDERED(false, false),
    UNSUPPORTED(false, false);

    private final boolean runtimeAllowed;
    private final boolean publishStable;

    SupportLevel(boolean runtimeAllowed, boolean publishStable) {
        this.runtimeAllowed = runtimeAllowed;
        this.publishStable = publishStable;
    }

    public boolean runtimeAllowed() {
        return runtimeAllowed;
    }

    public boolean publishStable() {
        return publishStable;
    }
}
