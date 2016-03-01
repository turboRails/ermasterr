package org.insightech.er.db.impl.db2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.insightech.er.db.DBManagerBase;
import org.insightech.er.db.impl.db2.tablespace.DB2TablespaceProperties;
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

public class DB2DBManager extends DBManagerBase {

    public static final String ID = "DB2";

    @Override
    public String getId() {
        return ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDriverClassName() {
        return "com.ibm.db2.jcc.DB2Driver";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getURL() {
        return "jdbc:db2://<SERVER NAME>:<PORT>/<DB NAME>";
    }

    @Override
    public int getDefaultPort() {
        return 50000;
    }

    @Override
    public SqlTypeManager getSqlTypeManager() {
        return new DB2SqlTypeManager();
    }

    @Override
    public TableProperties createTableProperties(final TableProperties tableProperties) {
        if (tableProperties != null && tableProperties instanceof DB2TableProperties) {
            return tableProperties;
        }

        return new DB2TableProperties();
    }

    @Override
    public DDLCreator getDDLCreator(final ERDiagram diagram, final Category targetCategory, final boolean semicolon) {
        return new DB2DDLCreator(diagram, targetCategory, semicolon);
    }

    @Override
    public List<String> getIndexTypeList(final ERTable table) {
        final List<String> list = new ArrayList<String>();

        list.add("BTREE");

        return list;
    }

    @Override
    protected int[] getSupportItems() {
        return new int[] {SUPPORT_AUTO_INCREMENT, SUPPORT_SCHEMA, SUPPORT_SEQUENCE, SUPPORT_SEQUENCE_NOCACHE};
    }

    @Override
    public ImportFromDBManager getTableImportManager() {
        return new DB2TableImportManager();
    }

    @Override
    public PreImportFromDBManager getPreTableImportManager() {
        return new DB2PreTableImportManager();
    }

    @Override
    public PreTableExportManager getPreTableExportManager() {
        return new DB2PreTableExportManager();
    }

    @Override
    public TablespaceProperties createTablespaceProperties() {
        return new DB2TablespaceProperties();
    }

    @Override
    public TablespaceProperties checkTablespaceProperties(final TablespaceProperties tablespaceProperties) {

        if (!(tablespaceProperties instanceof DB2TablespaceProperties)) {
            return new DB2TablespaceProperties();
        }

        return tablespaceProperties;
    }

    @Override
    public String[] getCurrentTimeValue() {
        return new String[] {"CURRENT TIMESTAMP"};
    }

    @Override
    public List<String> getSystemSchemaList() {
        final List<String> list = new ArrayList<String>();

        list.add("nullid");
        list.add("sqlj");
        list.add("syscat");
        list.add("sysfun");
        list.add("sysibm");
        list.add("sysibmadm");
        list.add("sysibminternal");
        list.add("sysibmts");
        list.add("sysproc");
        list.add("syspublic");
        list.add("sysstat");
        list.add("systools");
        list.add("db2admin");

        return list;
    }

    @Override
    public BigDecimal getSequenceMaxValue() {
        return null;
    }

}
