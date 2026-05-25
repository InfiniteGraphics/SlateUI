package top.huliawsl.slateui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import top.huliawsl.slateui.api.MutableStateProvider;
import top.huliawsl.slateui.binding.BindingEvaluator;

class BindingEvaluatorTest {

    @Test
    void evaluatesPathConcatAndComparison() {
        MutableStateProvider provider = new MutableStateProvider()
            .set("player.name", "Alex")
            .set("count", 4)
            .set("max", 9);

        assertEquals("Alex", BindingEvaluator.evaluate("{player.name}", provider));
        assertEquals("4/9", BindingEvaluator.evaluate("{count + '/' + max}", provider));
        assertTrue((Boolean) BindingEvaluator.evaluate("{count < max}", provider));
    }
}
