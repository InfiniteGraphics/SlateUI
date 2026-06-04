package top.huliawsl.slateui.api;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public final class SelectionModel<T> {

    private final LinkedHashSet<T> selected = new LinkedHashSet<>();
    private T primarySelection;
    private boolean multiSelect;

    public SelectionModel() {
        this(false);
    }

    public SelectionModel(boolean multiSelect) {
        this.multiSelect = multiSelect;
    }

    public boolean multiSelect() {
        return multiSelect;
    }

    public SelectionModel<T> multiSelect(boolean multiSelect) {
        this.multiSelect = multiSelect;
        if (!multiSelect && selected.size() > 1) {
            T keep = primarySelection == null ? selected.iterator().next() : primarySelection;
            selected.clear();
            selected.add(keep);
            primarySelection = keep;
        }
        return this;
    }

    public boolean isSelected(T value) {
        return selected.contains(value);
    }

    public T primarySelection() {
        return primarySelection;
    }

    public Set<T> selected() {
        return Set.copyOf(selected);
    }

    public boolean isEmpty() {
        return selected.isEmpty();
    }

    public int size() {
        return selected.size();
    }

    public boolean select(T value) {
        Objects.requireNonNull(value, "value");
        boolean changed = false;
        if (!multiSelect) {
            changed = selected.size() != 1 || !selected.contains(value);
            selected.clear();
        }
        changed |= selected.add(value);
        primarySelection = value;
        return changed;
    }

    public boolean selectAll(Collection<T> values) {
        if (values == null || values.isEmpty()) {
            return false;
        }
        boolean changed = false;
        if (!multiSelect) {
            return select(values.iterator().next());
        }
        for (T value : values) {
            if (value != null) {
                changed |= selected.add(value);
                primarySelection = value;
            }
        }
        return changed;
    }

    public boolean toggle(T value) {
        Objects.requireNonNull(value, "value");
        if (selected.contains(value)) {
            selected.remove(value);
            if (Objects.equals(primarySelection, value)) {
                primarySelection = selected.isEmpty() ? null : selected.iterator().next();
            }
            return true;
        }
        return select(value);
    }

    public boolean deselect(T value) {
        if (!selected.remove(value)) {
            return false;
        }
        if (Objects.equals(primarySelection, value)) {
            primarySelection = selected.isEmpty() ? null : selected.iterator().next();
        }
        return true;
    }

    public boolean clear() {
        if (selected.isEmpty()) {
            return false;
        }
        selected.clear();
        primarySelection = null;
        return true;
    }
}
