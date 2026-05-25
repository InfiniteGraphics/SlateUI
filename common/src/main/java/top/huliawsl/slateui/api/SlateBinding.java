package top.huliawsl.slateui.api;

import java.util.Objects;
import java.util.function.Function;

public final class SlateBinding<T> {

    private final String expression;
    private final Function<StateProvider, T> evaluator;

    public SlateBinding(String expression, Function<StateProvider, T> evaluator) {
        this.expression = Objects.requireNonNull(expression, "expression");
        this.evaluator = Objects.requireNonNull(evaluator, "evaluator");
    }

    public String expression() {
        return expression;
    }

    public T evaluate(StateProvider provider) {
        return evaluator.apply(provider);
    }

    @Override
    public String toString() {
        return expression;
    }
}
