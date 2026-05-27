package top.huliawsl.slateui.authoring;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

}
