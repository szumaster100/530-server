package core.game.system.mysql;

import java.util.ArrayList;
import java.util.List;

public final class SQLTable {

    private final SQLColumn[] columns;

    public SQLTable(SQLColumn... columns) {
        this.columns = columns;
    }

    public SQLColumn getColumn(String name) {
        for (SQLColumn column : columns) {
            if (column.getName().equals(name)) {
                return column;
            }
        }
        return null;
    }

    public List<SQLColumn> getChanged() {
        List<SQLColumn> updated = new ArrayList<>(20);
        for (int i = 0; i < columns.length; i++) {
            SQLColumn column = columns[i];
            if (column.isChanged()) {
                updated.add(column);
            }
        }
        return updated;
    }

    public SQLColumn[] getColumns() {
        return columns;
    }

}