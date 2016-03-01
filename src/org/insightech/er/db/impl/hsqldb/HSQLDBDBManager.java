package org.insightech.er.db.impl.hsqldb;

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

public class HSQLDBDBManager extends DBManagerBase {

    public static final String ID = "HSQLDB";

    @Override
    public String getId() {
        return ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDriverClassName() {
        return "org.hsqldb.jdbcDriver";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getURL() {
        return "jdbc:hsqldb:hsql://<SERVER NAME>:<PORT>/<DB NAME>";
    }

    @Override
    public int getDefaultPort() {
        return 9001;
    }

    @Override
    public SqlTypeManager getSqlTypeManager() {
        return new HSQLDBSqlTypeManager();
    }

    @Override
    public TableProperties createTableProperties(final TableProperties tableProperties) {
        if (tableProperties != null && tableProperties instanceof HSQLDBTableProperties) {
            return tableProperties;
        }

        return new HSQLDBTableProperties();
    }

    @Override
    public DDLCreator getDDLCreator(final ERDiagram diagram, final Category targetCategory, final boolean semicolon) {
        return new HSQLDBDDLCreator(diagram, targetCategory, semicolon);
    }

    @Override
    public List<String> getIndexTypeList(final ERTable table) {
        final List<String> list = new ArrayList<String>();

        list.add("BTREE");

        return list;
    }

    @Override
    protected int[] getSupportItems() {
        return new int[] {SUPPORT_SCHEMA, SUPPORT_SEQUENCE, SUPPORT_SEQUENCE_NOCACHE};
    }

    @Override
    public ImportFromDBManager getTableImportManager() {
        return new HSQLDBTableImportManager();
    }

    @Override
    public PreImportFromDBManager getPreTableImportManager() {
        return new HSQLDBPreTableImportManager();
    }

    @Override
    public PreTableExportManager getPreTableExportManager() {
        return new HSQLDBPreTableExportManager();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean doesNeedURLDatabaseName() {
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
    public List<String> getSystemSchemaList() {
        final List<String> list = new ArrayList<String>();

        list.add("information_schema");
        list.add("system_lobs");

        return list;
    }

    @Override
    public BigDecimal getSequenceMaxValue() {
        return null;
    }

}
