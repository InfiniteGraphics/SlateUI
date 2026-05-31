package top.huliawsl.slateui.scripting;

public record ServerProvidedUiPolicy(boolean allowed, boolean requireSignedSchema, int maxSchemaBytes) {

    public static ServerProvidedUiPolicy disabled() {
        return new ServerProvidedUiPolicy(false, true, 0);
    }
}
