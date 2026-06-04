package top.huliawsl.slateui.api.component;

import java.util.ArrayList;
import java.util.List;
import top.huliawsl.slateui.api.HorizontalAlign;
import top.huliawsl.slateui.api.SlateBorder;
import top.huliawsl.slateui.api.SlateComponent;
import top.huliawsl.slateui.api.SlateCompositeComponent;
import top.huliawsl.slateui.api.SlateStyle;
import top.huliawsl.slateui.api.StackDirection;
import top.huliawsl.slateui.layout.Insets;

public final class DataTable<T> extends SlateCompositeComponent {

    private static final SlateStyle DEFAULT_STYLE = SlateStyle.builder()
        .padding(Insets.all(4))
        .backgroundColor(0xFF020617)
        .border(new SlateBorder(0xFF334155, 1))
        .borderRadiusToken("radius.md")
        .clipContent(true)
        .build();

    private final List<TableColumn<T>> columns;
    private final List<T> rows;

    public DataTable(List<TableColumn<T>> columns, List<T> rows, SlateStyle style) {
        super(List.of(), SlateStyle.withDefaults(DEFAULT_STYLE, style));
        this.columns = columns == null ? List.of() : List.copyOf(columns);
        this.rows = rows == null ? List.of() : List.copyOf(rows);
    }

    @Override
    protected SlateComponent compose() {
        List<SlateComponent> content = new ArrayList<>();
        content.add(rowComponent(null, true));
        for (T row : rows) {
            content.add(rowComponent(row, false));
        }
        return new ScrollView(new Stack(StackDirection.COLUMN, content, SlateStyle.builder().gap(2).build()), style());
    }

    private SlateComponent rowComponent(T row, boolean header) {
        List<SlateComponent> cells = new ArrayList<>();
        for (TableColumn<T> column : columns) {
            String value = header ? column.header() : column.valueResolver().apply(row);
            cells.add(new Text(value, SlateStyle.builder()
                .width(column.width())
                .padding(Insets.symmetric(6, 3))
                .textColor(header ? 0xFF93C5FD : 0xFFE2E8F0)
                .horizontalAlign(header ? HorizontalAlign.START : HorizontalAlign.START)
                .build()));
        }
        return new Stack(StackDirection.ROW, cells, SlateStyle.builder()
            .backgroundColor(header ? 0x6623344D : 0x331E293B)
            .borderRadius(3)
            .build());
    }
}
