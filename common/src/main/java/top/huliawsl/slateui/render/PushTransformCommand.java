package top.huliawsl.slateui.render;

public record PushTransformCommand(float translateX, float translateY, float scale, float rotationDegrees, float opacity) implements DrawCommand {

    @Override
    public String describe() {
        return "push-transform translate=(" + translateX + "," + translateY + ") scale=" + scale + " rotation=" + rotationDegrees + " opacity=" + opacity;
    }
}
