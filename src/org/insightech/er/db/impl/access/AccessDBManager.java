package org.insightech.er.db.impl.access;

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

public class AccessDBManager extends DBManagerBase {

    public static final String ID = "MSAccess";

    @Override
    public String getId() {
        return ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDriverClassName() {
        return "sun.jdbc.odbc.JdbcOdbcDriver";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getURL() {
        return "jdbc:odbc:<DB NAME>";
    }

    @Override
    public int getDefaultPort() {
        return 0;
    }

    @Override
    public SqlTypeManager getSqlTypeManager() {
        return new AccessSqlTypeManager();
    }

    @Override
    public TableProperties createTableProperties(final TableProperties tableProperties) {
        if (tableProperties != null && tableProperties instanceof AccessTableProperties) {
            return tableProperties;
        }

        return new AccessTableProperties();
    }

    @Override
    public DDLCreator getDDLCreator(final ERDiagram diagram, final Category targetCategory, final boolean semicolon) {
        return new AccessDDLCreator(diagram, targetCategory, semicolon);
    }

    @Override
    public List<String> getIndexTypeList(final ERTable table) {
        final List<String> list = new ArrayList<String>();

        list.add("BTREE");

        return list;
    }

    @Override
    protected int[] getSupportItems() {
        return new int[] {SUPPORT_AUTO_INCREMENT, SUPPORT_AUTO_INCREMENT_SETTING};
    }

    @Override
    public ImportFromDBManager getTableImportManager() {
        return new AccessTableImportManager();
    }

    @Override
    public PreImportFromDBManager getPreTableImportManager() {
        return new AccessPreTableImportManager();
    }

    @Override
    public PreTableExportManager getPreTableExportManager() {
        return new AccessPreTableExportManager();
    }

    @Override
    public String[] getCurrentTimeValue() {
        return new String[] {"GETDATE()", "CURRENT_TIMESTAMP"};
    }

    @Override
    public List<String> getSystemSchemaList() {
        final List<String> list = new ArrayList<String>();

        return list;
    }

    @Override
    public BigDecimal getSequenceMaxValue() {
        return null;
    }

    @Override
    public TablespaceProperties checkTablespaceProperties(final TablespaceProperties tablespaceProperties) {
        return null;
    }

    @Override
    public TablespaceProperties createTablespaceProperties() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean doesNeedURLServerName() {
        return false;
    }

}
