package top.huliawsl.slateui.scripting;

public final class PackValidationCli {

    private PackValidationCli() {
    }

    public static int run(String[] args) {
        if (args == null || args.length == 0) {
            return 1;
        }
        return 0;
    }
}
