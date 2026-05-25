package top.huliawsl.slateui.authoring;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class SlateCompilerTest {

    @Test
    void compilesSlateIntoJsonIr() throws Exception {
        Path input = Files.createTempDirectory("slate-input");
        Path output = Files.createTempDirectory("slate-output");
        Files.writeString(input.resolve("sample.slate"), """
            <template>
              <Box>
                <Text value=\"Hello\" />
              </Box>
            </template>
            """);

        new SlateCompiler().compileDirectory(input, output);

        String json = Files.readString(output.resolve("sample.json"));
        assertTrue(json.contains("\"componentType\": \"Box\""));
        assertTrue(json.contains("\"componentType\": \"Text\""));
    }

    @Test
    void reportsUnknownComponent() throws Exception {
        Path input = Files.createTempFile("broken", ".slate");
        Path output = Files.createTempFile("broken-out", ".json");
        Files.writeString(input, """
            <template>
              <Nope />
            </template>
            """);

        SlateCompileException exception = assertThrows(SlateCompileException.class, () -> new SlateCompiler().compileFile(input, output));
        assertTrue(exception.getMessage().contains("Unknown component"));
    }
}