package org.insightech.er.db.impl.sqlite;

import org.insightech.er.editor.model.dbimport.PreImportFromDBManager;

public class SQLitePreTableImportManager extends PreImportFromDBManager {

    @Override
    protected String getTableNameWithSchema(final String schema, final String tableName) {
        return "[" + super.getTableNameWithSchema(schema, tableName) + "]";
    }
}
