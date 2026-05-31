package top.huliawsl.slateui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import top.huliawsl.slateui.animation.*;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.Theme;
import top.huliawsl.slateui.api.component.*;
import top.huliawsl.slateui.debug.SlateDiagnostics;
import top.huliawsl.slateui.debug.SlateInspectorModel;
import top.huliawsl.slateui.ecosystem.*;
import top.huliawsl.slateui.editor.*;
import top.huliawsl.slateui.layout.*;
import top.huliawsl.slateui.platform.*;
import top.huliawsl.slateui.scripting.*;
import top.huliawsl.slateui.style.*;

class V09RoadmapRuntimeTest {

    @Test
    void inspectorModelFiltersAndCopiesReports() {
        SlateDiagnostics diagnostics = new SlateDiagnostics();
        SlateInspectorModel model = new SlateInspectorModel()
            .select("OverlayRoot/Panel[0]")
            .search("panel")
            .commandFilter("save")
            .eventFilter("click")
            .liveBounds(new Rect(1, 2, 3, 4));

        assertTrue(model.selectedPanel(diagnostics).get(0).contains("Panel"));
        assertTrue(model.copyReport(diagnostics).contains("Runtime summary"));
    }

    @Test
    void editorPreviewAndActionsExposeReadOnlySurface() {
        SlateEditorPreview preview = new SlateEditorPreview(new Text("Preview"), Theme.DEFAULT);
        SlateEditorDocument document = SlateEditorDocument.importSlate("<Text value=\"Preview\" />");
        SlateEditorAction action = new SlateEditorAction(SlateEditorAction.Type.STYLE_EDIT, "Text", Map.of("color", "white"));

        assertFalse(preview.renderPreview(new Size(120, 40)).isEmpty());
        assertTrue(preview.componentTreePanel().contains("Text"));
        assertTrue(document.exportSlate().contains("Preview"));
        assertEquals(SlateEditorAction.Type.STYLE_EDIT, action.type());
    }

    @Test
    void advancedStyleLayoutAndAnimationModelsWork() {
        SlateAdvancedStyle style = new SlateAdvancedStyle(
            new SlateShadow(1, 2, 3, 0x66000000),
            new SlateGradient(0xFF000000, 0xFFFFFFFF, SlateGradient.Direction.VERTICAL),
            0.5F,
            true,
            "demo:texture.png",
            "demo:panel.png",
            true,
            Map.of("--accent", "color.primary"),
            Map.of(SlatePseudoClass.HOVER, "hover"),
            new SlateMediaRule(0, 400, 0.8F),
            SlateThemeVariant.HIGH_CONTRAST
        );
        ScrollSnap snap = new ScrollSnap(16);
        SlateTween tween = new SlateTween(0F, 10F, 100L, SlateEasing.EASE_OUT);

        assertEquals(0.5F, style.opacity());
        assertEquals(32, snap.snap(30));
        assertTrue(tween.valueAt(50L) > 5F);
        assertEquals(ResponsivePreset.COMPACT, ResponsivePreset.fromWidth(320));
    }

    @Test
    void ecosystemAndScriptingPoliciesAreExplicit() {
        SlatePlatformRegistration registration = new SlatePlatformRegistration()
            .registerConfigScreen(new SlateConfigScreenRegistration("demo", new Text("Config")));
        RecipeIntegration integration = new RecipeIntegration(RecipeIntegrationId.JEI, false, "optional");
        ExternalUiSecurityPolicy security = new ExternalUiSecurityPolicy(Set.of("demo"), true, false);
        RecipeTransferAction transfer = new RecipeTransferAction("demo:recipe", List.of("minecraft:stone"), "minecraft:button");

        assertEquals(1, registration.configScreens().size());
        assertEquals(RecipeIntegrationId.JEI, integration.id());
        assertTrue(security.commandAllowed("demo.save"));
        assertFalse(security.commandAllowed("other.save"));
        assertEquals("minecraft:button", transfer.output());
        assertEquals(0, PackValidationCli.run(new String[] {"pack"}));
    }

    @Test
    void recipeComponentsMeasure() {
        RecipeLayout layout = new RecipeLayout(List.of("minecraft:stone"), "minecraft:button", SlateStyle.EMPTY);
        GhostIngredient ghost = new GhostIngredient("minecraft:diamond", SlateStyle.EMPTY);
        IngredientView ingredient = new IngredientView("minecraft:iron_ingot", 2, SlateStyle.EMPTY);

        assertTrue(layout.measure(new top.huliawsl.slateui.runtime.SlateLayoutContext(null), new Size(200, 80)).width() >= 0);
        assertEquals("minecraft:diamond", ghost.itemId());
        assertTrue(ingredient.children().size() > 0);
    }
}
