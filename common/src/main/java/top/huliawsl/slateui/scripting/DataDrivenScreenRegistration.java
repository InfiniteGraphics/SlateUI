package top.huliawsl.slateui.scripting;

public record DataDrivenScreenRegistration(String id, String slateResource, boolean resourcePackOnly) {

    public DataDrivenScreenRegistration {
        id = id == null ? "" : id;
        slateResource = slateResource == null ? "" : slateResource;
    }
}
