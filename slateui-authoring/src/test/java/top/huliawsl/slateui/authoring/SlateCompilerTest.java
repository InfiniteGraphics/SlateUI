package top.huliawsl.slateui.authoring;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class SlateCompilerTest {

    @Test
    void compilesDirectivesSlotsAndCustomComponents() throws Exception {
        Path input = Files.createTempDirectory("slate-input");
        Path output = Files.createTempDirectory("slate-output");
        Files.writeString(input.resolve("sample.slate"), """
            <template>
              <MyPanel if="{ui.visible}">
                <Stack for="quest in quests" key="quest.id">
                  <Text value="{quest.name}" />
                </Stack>
                <template #header>
                  <Text value="Header" />
                </template>
              </MyPanel>
            </template>
            """);

        new SlateCompiler().compileDirectory(input, output);

        String json = Files.readString(output.resolve("sample.json"));
        assertTrue(json.contains("\"componentType\": \"MyPanel\""));
        assertTrue(json.contains("\"if\": \"{ui.visible}\""));
        assertTrue(json.contains("\"for\": \"quests\""));
        assertTrue(json.contains("\"alias\": \"quest\""));
        assertTrue(json.contains("\"key\": \"quest.id\""));
        assertTrue(json.contains("\"slots\""));
        assertTrue(json.contains("\"header\""));
    }

    @Test
    void reportsInvalidForDirective() throws Exception {
        Path input = Files.createTempFile("broken", ".slate");
        Path output = Files.createTempFile("broken-out", ".json");
        Files.writeString(input, """
            <template>
              <Text for="quests" value="Broken" />
            </template>
            """);

        SlateCompileException exception = assertThrows(SlateCompileException.class, () -> new SlateCompiler().compileFile(input, output));
        assertTrue(exception.getMessage().contains("Invalid for directive"));
    }

    @Test
    void rejectsUnknownNamedSlotOnBuiltinComponent() throws Exception {
        Path input = Files.createTempFile("broken-slot", ".slate");
        Path output = Files.createTempFile("broken-slot-out", ".json");
        Files.writeString(input, """
            <template>
              <Box>
                <template #header>
                  <Text value="Nope" />
                </template>
              </Box>
            </template>
            """);

        SlateCompileException exception = assertThrows(SlateCompileException.class, () -> new SlateCompiler().compileFile(input, output));
        assertTrue(exception.getMessage().contains("Unknown slot"));
    }

    @Test
    void rejectsNamedSlotThatRuntimeWouldIgnore() throws Exception {
        Path input = Files.createTempFile("ignored-slot", ".slate");
        Path output = Files.createTempFile("ignored-slot-out", ".json");
        Files.writeString(input, """
            <template>
              <Box>
                <template #tooltip>
                  <Text value="Nope" />
                </template>
              </Box>
            </template>
            """);

        SlateCompileException exception = assertThrows(SlateCompileException.class, () -> new SlateCompiler().compileFile(input, output));
        assertTrue(exception.getMessage().contains("Unknown slot"));
    }

    @Test
    void rejectsLowercaseUnknownComponent() throws Exception {
        Path input = Files.createTempFile("broken-lower", ".slate");
        Path output = Files.createTempFile("broken-lower-out", ".json");
        Files.writeString(input, """
            <template>
              <div />
            </template>
            """);

        SlateCompileException exception = assertThrows(SlateCompileException.class, () -> new SlateCompiler().compileFile(input, output));
        assertTrue(exception.getMessage().contains("Unknown component"));
    }
    @Test
    void compilesPanelToggleListAndStateSelectors() throws Exception {
        Path input = Files.createTempFile("authoring-v01", ".slate");
        Path output = Files.createTempFile("authoring-v01-out", ".json");
        Files.writeString(input, """
            <template>
              <Panel id="settings" title="Settings">
                <Toggle label="Enabled" checked="{settings.enabled}" />
                <List itemGap="4">
                  <Text value="Row" />
                </List>
              </Panel>
            </template>
            <style scoped>
              Panel { padding: 12; }
              #settings:focus { focusBorderColor: #60A5FA; }
              .primary:hover { background: #2563EB; }
            </style>
            """);

        new SlateCompiler().compileFile(input, output);

        String json = Files.readString(output);
        assertTrue(json.contains("\"componentType\": \"Panel\""));
        assertTrue(json.contains("\"componentType\": \"Toggle\""));
        assertTrue(json.contains("\"componentType\": \"List\""));
        assertTrue(json.contains("\"#settings:focus\""));
        assertTrue(json.contains("\"primary:hover\""));
    }

    @Test
    void rejectsInvalidStylePropertyWithLocation() throws Exception {
        Path input = Files.createTempFile("broken-style-property", ".slate");
        Path output = Files.createTempFile("broken-style-property-out", ".json");
        Files.writeString(input, """
            <template>
              <Text value="Broken" style-borderRaduis="8" />
            </template>
            """);

        SlateCompileException exception = assertThrows(SlateCompileException.class, () -> new SlateCompiler().compileFile(input, output));
        assertTrue(exception.getMessage().contains("Unknown style property"));
        assertTrue(exception.getMessage().contains(":"));
    }

    @Test
    void rejectsInvalidStyleValueWithExpectedType() throws Exception {
        Path input = Files.createTempFile("broken-style-value", ".slate");
        Path output = Files.createTempFile("broken-style-value-out", ".json");
        Files.writeString(input, """
            <template>
              <Text value="Broken" style-width="wide" />
            </template>
            """);

        SlateCompileException exception = assertThrows(SlateCompileException.class, () -> new SlateCompiler().compileFile(input, output));
        assertTrue(exception.getMessage().contains("Invalid style value"));
    }

    @Test
    void rejectsMarginBecauseV01DoesNotImplementLayoutBehavior() throws Exception {
        Path input = Files.createTempFile("broken-margin", ".slate");
        Path output = Files.createTempFile("broken-margin-out", ".json");
        Files.writeString(input, """
            <template>
              <Text value="Broken" style-margin="8" />
            </template>
            """);

        SlateCompileException exception = assertThrows(SlateCompileException.class, () -> new SlateCompiler().compileFile(input, output));
        assertTrue(exception.getMessage().contains("Unknown style property"));
    }

    @Test
    void rejectsUnconsumedInvalidStyleSelectorText() throws Exception {
        Path input = Files.createTempFile("broken-selector", ".slate");
        Path output = Files.createTempFile("broken-selector-out", ".json");
        Files.writeString(input, """
            <template>
              <Box />
            </template>
            <style scoped>
              .valid { padding: 4; }
              !!! broken
            </style>
            """);

        SlateCompileException exception = assertThrows(SlateCompileException.class, () -> new SlateCompiler().compileFile(input, output));
        assertTrue(exception.getMessage().contains("Invalid style selector"));
    }

    @Test
    void sourceMapUsesDistinctLocationsForRepeatedComponents() throws Exception {
        Path input = Files.createTempFile("source-map-repeat", ".slate");
        Path output = Files.createTempFile("source-map-repeat-out", ".json");
        Files.writeString(input, """
            <template>
              <Stack>
                <Text value="First" />
                <Text value="Second" />
              </Stack>
            </template>
            """);

        new SlateCompiler().compileFile(input, output);

        JsonArray sourceMap = JsonParser.parseString(Files.readString(output)).getAsJsonObject().getAsJsonArray("sourceMap");
        int firstTextLine = sourceMap.get(1).getAsJsonObject().get("line").getAsInt();
        int secondTextLine = sourceMap.get(2).getAsJsonObject().get("line").getAsInt();
        assertTrue(secondTextLine > firstTextLine);
    }

    @Test
    void imageAcceptsTextureRegionProps() throws Exception {
        Path input = Files.createTempFile("image-region", ".slate");
        Path output = Files.createTempFile("image-region-out", ".json");
        Files.writeString(input, """
            <template>
              <Image resource="minecraft:textures/gui/widgets.png" u="1" v="2" regionWidth="18" regionHeight="19" textureWidth="256" textureHeight="128" />
            </template>
            """);

        new SlateCompiler().compileFile(input, output);

        String json = Files.readString(output);
        assertTrue(json.contains("\"u\": \"1\""));
        assertTrue(json.contains("\"regionWidth\": \"18\""));
        assertTrue(json.contains("\"textureHeight\": \"128\""));
    }

    @Test
    void textAcceptsTranslationProps() throws Exception {
        Path input = Files.createTempFile("text-translation", ".slate");
        Path output = Files.createTempFile("text-translation-out", ".json");
        Files.writeString(input, """
            <template>
              <Text translationKey="slateui.test.title" translationArgs="one,two" />
            </template>
            """);

        new SlateCompiler().compileFile(input, output);

        String json = Files.readString(output);
        assertTrue(json.contains("\"translationKey\": \"slateui.test.title\""));
        assertTrue(json.contains("\"translationArgs\": \"one,two\""));
    }

    @Test
    void styleErrorsPointNearCurrentRule() throws Exception {
        Path input = Files.createTempFile("style-current-location", ".slate");
        Path output = Files.createTempFile("style-current-location-out", ".json");
        Files.writeString(input, """
            <template>
              <Box />
            </template>
            <style scoped>
              .first { width: 1; }
              .second { width: nope; }
            </style>
            """);

        SlateCompileException exception = assertThrows(SlateCompileException.class, () -> new SlateCompiler().compileFile(input, output));
        assertTrue(exception.getMessage().contains("Invalid style value"));
        assertTrue(exception.getMessage().contains(":6:"));
    }

    @Test
    void schemaExportIncludesStableAndExperimentalComponents() {
        String schema = SlateAuthoringSchema.export().toString();

        assertTrue(schema.contains("\"Text\""));
        assertTrue(schema.contains("\"translationKey\""));
        assertTrue(schema.contains("\"Image\""));
        assertTrue(schema.contains("\"regionWidth\""));
        assertTrue(schema.contains("\"SlotGrid\""));
        assertTrue(schema.contains("\"experimental\":true"));
    }

}
