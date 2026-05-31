package top.huliawsl.slateui.animation;

public enum SlateEasing {
    LINEAR {
        @Override
        public float apply(float t) {
            return clamp(t);
        }
    },
    EASE_OUT {
        @Override
        public float apply(float t) {
            float value = clamp(t);
            return 1F - (1F - value) * (1F - value);
        }
    };

    public abstract float apply(float t);

    static float clamp(float t) {
        return Math.max(0F, Math.min(1F, t));
    }
}
