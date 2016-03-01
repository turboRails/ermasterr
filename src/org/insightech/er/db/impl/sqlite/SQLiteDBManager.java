package org.insightech.er.db.impl.sqlite;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.insightech.er.db.DBManagerBase;
import org.insightech.er.db.sqltype.SqlTypeManager;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.db.PreTableExportManager;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.dbimport.ImportFromDBManager;
import org.insightech.er.editor.model.dbimport.PreImportFromDBManager;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;

public class SQLiteDBManager extends DBManagerBase {

    public static final String ID = "SQLite";

    @Override
    public String getId() {
        return ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDriverClassName() {
        return "org.sqlite.JDBC";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getURL() {
        return "jdbc:sqlite:<DB NAME>";
    }

    @Override
    public int getDefaultPort() {
        return 0;
    }

    @Override
    public SqlTypeManager getSqlTypeManager() {
        return new SQLiteSqlTypeManager();
    }

    @Override
    public TableProperties createTableProperties(final TableProperties tableProperties) {
        if (tableProperties != null && tableProperties instanceof SQLiteTableProperties) {
            return tableProperties;
        }

        return new SQLiteTableProperties();
    }

    @Override
    public DDLCreator getDDLCreator(final ERDiagram diagram, final Category targetCategory, final boolean semicolon) {
        return new SQLiteDDLCreator(diagram, targetCategory, semicolon);
    }

    @Override
    public List<String> getIndexTypeList(final ERTable table) {
        final List<String> list = new ArrayList<String>();

        list.add("BTREE");

        return list;
    }

    @Override
    protected int[] getSupportItems() {
        return new int[] {SUPPORT_SCHEMA, SUPPORT_AUTO_INCREMENT};
    }

    @Override
    public ImportFromDBManager getTableImportManager() {
        return new SQLiteTableImportManager();
    }

    @Override
    public PreImportFromDBManager getPreTableImportManager() {
        return new SQLitePreTableImportManager();
    }

    @Override
    public PreTableExportManager getPreTableExportManager() {
        return new SQLitePreTableExportManager();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean doesNeedURLServerName() {
        return false;
    }

    @Override
    public TablespaceProperties createTablespaceProperties() {
        return null;
    }

    @Override
    public TablespaceProperties checkTablespaceProperties(final TablespaceProperties tablespaceProperties) {
        return null;
    }

    @Override
    public String[] getCurrentTimeValue() {
        return new String[] {"CURRENT_TIMESTAMP"};
    }

    @Override
    public BigDecimal getSequenceMaxValue() {
        return BigDecimal.ZERO;
    }

}
