package top.huliawsl.slateui.demo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import top.huliawsl.slateui.api.ComputedStateProvider;
import top.huliawsl.slateui.api.SlateScreen;
import top.huliawsl.slateui.layout.Rect;
import top.huliawsl.slateui.layout.Size;
import top.huliawsl.slateui.render.DrawCommand;
import top.huliawsl.slateui.render.DrawTextCommand;
import top.huliawsl.slateui.runtime.SlateLayoutContext;
import top.huliawsl.slateui.runtime.SlateRenderContext;

class SlateDemoEntrypointTest {

    @Test
    void missingAuthoringNoticeDoesNotPolluteStatusOrSummary() {
        SlateScreen screen = SlateDemoEntrypoint.createGalleryScreen(
            false,
            "Ready",
            "Authoring screen unavailable. Run compileSlate first."
        );

        List<String> texts = collectTexts(screen, 360, 600);

        assertTrue(texts.contains("Status: Ready"));
        assertTrue(texts.contains("Summary: Slate Tester / Ready"));
        assertTrue(texts.stream().anyMatch(text -> text.contains("Authoring screen unavailable")));
        assertFalse(texts.stream().anyMatch(text -> text.startsWith("Status:") && text.contains("Authoring screen unavailable")));
        assertFalse(texts.stream().anyMatch(text -> text.startsWith("Summary:") && text.contains("Authoring screen unavailable")));
    }

    @Test
    void authoringAndInspectorButtonsStayVisibleInSettingsPanel() {
        SlateScreen screen = SlateDemoEntrypoint.createGalleryScreen(false, "Ready", null);

        List<DrawTextCommand> textCommands = collectTextCommands(screen, 360, 600);

        assertTextFits(textCommands, "Open .slate Screen", 360);
        assertTextFits(textCommands, "Inspect Runtime", 360);
    }

    @Test
    void galleryShowsRecentRuntimeLayoutAndEcosystemPages() {
        SlateScreen screen = SlateDemoEntrypoint.createGalleryScreen(false, "Ready", null);
        ComputedStateProvider provider = (ComputedStateProvider) screen.stateProvider();

        provider.set("gallery.page", "list");
        assertTrue(collectTexts(screen, 420, 800).stream().anyMatch(text -> text.contains("VirtualList")));

        provider.set("gallery.page", "runtime");
        List<String> runtimeTexts = collectTexts(screen, 420, 800);
        assertTrue(runtimeTexts.stream().anyMatch(text -> text.contains("Disabled parent")));
        assertTrue(runtimeTexts.stream().anyMatch(text -> text.contains("Clip hit count")));

        provider.set("gallery.page", "settings");
        List<String> settingsTexts = collectTexts(screen, 420, 900);
        assertTrue(settingsTexts.stream().anyMatch(text -> text.contains("Mode:")));
        assertTrue(settingsTexts.stream().anyMatch(text -> text.contains("Open Confirm Dialog")));

        provider.set("gallery.page", "layout");
        List<String> layoutTexts = collectTexts(screen, 420, 800);
        assertTrue(layoutTexts.stream().anyMatch(text -> text.contains("AbsoluteOverlay")));
        assertTrue(layoutTexts.stream().anyMatch(text -> text.contains("SlateTween")));

        provider.set("gallery.page", "container");
        List<String> containerTexts = collectTexts(screen, 420, 900);
        assertTrue(containerTexts.stream().anyMatch(text -> text.contains("Native container shell")));
        assertTrue(containerTexts.stream().anyMatch(text -> text.contains("Shift+click quick-moves")));

        provider.set("gallery.page", "ecosystem");
        List<String> ecosystemTexts = collectTexts(screen, 420, 800);
        assertTrue(ecosystemTexts.stream().anyMatch(text -> text.contains("GhostIngredient")));
        assertTrue(ecosystemTexts.stream().anyMatch(text -> text.contains("Recipe + Ecosystem")));
    }

    private static List<String> collectTexts(SlateScreen screen, int width, int height) {
        return collectTextCommands(screen, width, height).stream()
            .map(DrawTextCommand::text)
            .toList();
    }

    private static List<DrawTextCommand> collectTextCommands(SlateScreen screen, int width, int height) {
        SlateLayoutContext layoutContext = new SlateLayoutContext(null);
        screen.root().measure(layoutContext, new Size(width, height));
        screen.root().layout(layoutContext, new Rect(0, 0, width, height));
        List<DrawCommand> commands = new ArrayList<>();
        screen.root().collectDrawCommands(new SlateRenderContext(false, screen.theme()), commands);
        return commands.stream()
            .filter(DrawTextCommand.class::isInstance)
            .map(DrawTextCommand.class::cast)
            .toList();
    }

    private static void assertTextFits(List<DrawTextCommand> commands, String text, int screenWidth) {
        SlateLayoutContext layoutContext = new SlateLayoutContext(null);
        boolean fits = commands.stream()
            .anyMatch(command -> command.text().equals(text) && command.x() >= 0 && command.x() + layoutContext.textWidth(text) <= screenWidth);
        assertTrue(fits, text + " should be visible within the screen width");
    }
}
