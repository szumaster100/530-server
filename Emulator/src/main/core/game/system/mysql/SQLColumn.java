package core.game.system.mysql;

public final class SQLColumn {

    private final String name;

    private final Class<?> type;

    private final boolean neverUpdate;

    private Object value;

    private boolean changed;

    private boolean parse;

    public SQLColumn(String name, Class<?> type) {
        this(name, type, false, true);
    }

    public SQLColumn(String name, Class<?> type, boolean parse) {
        this(name, type, false, parse);
    }

    public SQLColumn(String name, Class<?> type, boolean neverUpdate, boolean parse) {
        this.name = name;
        this.type = type;
        this.neverUpdate = neverUpdate;
        this.parse = parse;
    }

    public String getName() {
        return name;
    }

    public void updateValue(Object value) {
        this.changed = value != this.value;
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
        this.changed = false;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public Class<?> getType() {
        return type;
    }

    public boolean isNeverUpdate() {
        return neverUpdate;
    }

    public boolean isParse() {
        return parse;
    }

    public void setParse(boolean parse) {
        this.parse = parse;
    }
}