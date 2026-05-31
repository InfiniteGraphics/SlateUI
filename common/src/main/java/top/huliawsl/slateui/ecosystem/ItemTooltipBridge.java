package top.huliawsl.slateui.ecosystem;

import java.util.List;

public interface ItemTooltipBridge {

    List<String> tooltipLines(String itemId);

    static ItemTooltipBridge empty() {
        return itemId -> List.of();
    }
}
