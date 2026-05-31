package top.huliawsl.slateui.platform;

public record ClothConfigMigration(String sourceType, String recommendation) {

    public static ClothConfigMigration guide() {
        return new ClothConfigMigration("Cloth Config", "Map config categories to Panel, entries to form components, and save actions to Slate commands.");
    }
}
