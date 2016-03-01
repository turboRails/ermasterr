package org.insightech.er.db.impl.oracle;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.insightech.er.db.DBManagerBase;
import org.insightech.er.db.impl.oracle.tablespace.OracleTablespaceProperties;
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

public class OracleDBManager extends DBManagerBase {

    public static final String ID = "Oracle";

    @Override
    public String getId() {
        return ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDriverClassName() {
        return "oracle.jdbc.driver.OracleDriver";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getURL() {
        return "jdbc:oracle:thin:@<SERVER NAME>:<PORT>:<DB NAME>";
    }

    @Override
    public int getDefaultPort() {
        return 1521;
    }

    @Override
    public SqlTypeManager getSqlTypeManager() {
        return new OracleSqlTypeManager();
    }

    @Override
    public TableProperties createTableProperties(final TableProperties tableProperties) {
        if (tableProperties != null && tableProperties instanceof OracleTableProperties) {
            return tableProperties;
        }

        return new OracleTableProperties();
    }

    @Override
    public DDLCreator getDDLCreator(final ERDiagram diagram, final Category targetCategory, final boolean semicolon) {
        return new OracleDDLCreator(diagram, targetCategory, semicolon);
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
        return new OracleTableImportManager();
    }

    @Override
    public PreImportFromDBManager getPreTableImportManager() {
        return new OraclePreTableImportManager();
    }

    @Override
    public PreTableExportManager getPreTableExportManager() {
        return new OraclePreTableExportManager();
    }

    @Override
    public TablespaceProperties createTablespaceProperties() {
        return new OracleTablespaceProperties();
    }

    @Override
    public TablespaceProperties checkTablespaceProperties(final TablespaceProperties tablespaceProperties) {

        if (!(tablespaceProperties instanceof OracleTablespaceProperties)) {
            return new OracleTablespaceProperties();
        }

        return tablespaceProperties;
    }

    @Override
    public String[] getCurrentTimeValue() {
        return new String[] {"SYSDATE"};
    }

    @Override
    public List<String> getSystemSchemaList() {
        final List<String> list = new ArrayList<String>();

        list.add("anonymous");
        list.add("ctxsys");
        list.add("dbsnmp");
        list.add("dip");
        list.add("flows_020100");
        list.add("flows_files");
        list.add("hr");
        list.add("mdsys");
        list.add("outln");
        list.add("sys");
        list.add("system");
        list.add("tsmsys");
        list.add("xdb");

        return list;
    }

    @Override
    public BigDecimal getSequenceMaxValue() {
        return new BigDecimal("9999999999999999999999999999");
    }
}
