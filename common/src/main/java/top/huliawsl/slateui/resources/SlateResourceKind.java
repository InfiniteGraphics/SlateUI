package top.huliawsl.slateui.resources;

public enum SlateResourceKind {
    SCREEN("screens", ".slate"),
    THEME("themes", ".json"),
    COMPONENT("components", ".slate"),
    STATE("state", ".json"),
    SCHEMA("schema", ".json");

    private final String directory;
    private final String extension;

    SlateResourceKind(String directory, String extension) {
        this.directory = directory;
        this.extension = extension;
    }

    public String directory() {
        return directory;
    }

    public String extension() {
        return extension;
    }
}
