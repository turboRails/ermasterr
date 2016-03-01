package org.insightech.er.db.impl.sqlite;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.insightech.er.editor.model.dbimport.DBObject;
import org.insightech.er.editor.model.dbimport.ImportFromDBManagerEclipseBase;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.progress_monitor.ProgressMonitor;
import org.insightech.er.util.Format;

public class SQLiteTableImportManager extends ImportFromDBManagerEclipseBase {

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getViewDefinitionSQL(final String schema) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Index> getIndexes(final ERTable table, final DatabaseMetaData metaData, final List<PrimaryKeyData> primaryKeys) throws SQLException {
        return new ArrayList<Index>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setForeignKeys(final List<ERTable> list) throws SQLException {
        // SQLite note yet implemented
    }

    /**
     * {@inheritDoc}
     * 
     * @throws InterruptedException
     * @throws SQLException
     */
    @Override
    protected Map<String, ColumnData> getColumnDataMap(final String tableNameWithSchema, final String tableName, final String schema) throws SQLException, InterruptedException {
        cacheColumnDataX(schema, tableName, null, null);

        return super.getColumnDataMap(tableNameWithSchema, tableName, schema);
    }

    @Override
    protected ColumnData createColumnData(final ResultSet columnSet) throws SQLException {
        final ColumnData columnData = super.createColumnData(columnSet);

        final String type = Format.null2blank(columnData.type).toLowerCase();

        if (type.indexOf("int") != -1) {
            columnData.type = "integer";

        } else if (type.indexOf("char") != -1 || type.indexOf("clob") != -1 || type.indexOf("text") != -1) {
            columnData.type = "text";

        } else if (type.equals("") || type.indexOf("blob") != -1) {
            columnData.type = "none";

        } else if (type.indexOf("real") != -1 || type.indexOf("floa") != -1 || type.indexOf("doub") != -1) {
            columnData.type = "real";

        } else {
            columnData.type = "numeric";
        }

        return columnData;
    }

    @Override
    protected void cacheColumnData(final List<DBObject> dbObjectList, final ProgressMonitor monitor) throws SQLException, InterruptedException {
        // can not cache
    }

    @Override
    protected String getTableNameWithSchema(final String schema, final String tableName) {
        return "[" + super.getTableNameWithSchema(schema, tableName) + "]";
    }

}
