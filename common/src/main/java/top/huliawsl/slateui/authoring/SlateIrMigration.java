package top.huliawsl.slateui.authoring;

import com.google.gson.JsonObject;

public final class SlateIrMigration {

    private SlateIrMigration() {
    }

    public static JsonObject migrate(JsonObject ir, int supportedSchema) {
        JsonObject copy = ir == null ? new JsonObject() : ir.deepCopy();
        if (!copy.has("schemaVersion")) {
            copy.addProperty("schemaVersion", supportedSchema);
        }
        return copy;
    }
}
