package org.insightech.er.db.impl.postgres;

import org.insightech.er.editor.model.dbimport.PreImportFromDBManager;

public class PostgresPreTableImportManager extends PreImportFromDBManager {

    @Override
    protected String getTableNameWithSchema(final String schema, final String tableName) {
        return dbSetting.getTableNameWithSchema("\"" + tableName + "\"", schema);
    }

}
