package org.insightech.er.editor.model.dbexport.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.dbexport.ddl.DDLTarget;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.settings.DBSetting;
import org.insightech.er.editor.model.settings.Environment;

public abstract class PreTableExportManager {

    protected Connection con;

    protected DatabaseMetaData metaData;

    protected DBSetting dbSetting;

    private ERDiagram diagram;

    private Exception exception;

    private String errorSql;

    private String ddl;

    private Environment environment;

    private String ifExistsOption;

    private Set<String> newViewNames;

    protected Set<String> newTableNames;

    private Set<String> newSequenceNames;

    public void init(final Connection con, final DBSetting dbSetting, final ERDiagram diagram, final Environment environment) throws SQLException {
        this.con = con;
        this.dbSetting = dbSetting;
        this.diagram = diagram;
        this.environment = environment;

        metaData = con.getMetaData();

        ifExistsOption = DBManagerFactory.getDBManager(this.diagram).getDDLCreator(this.diagram, this.diagram.getCurrentCategory(), false).getIfExistsOption();

        prepareNewNames();
    }

    protected void prepareNewNames() {
        newTableNames = new HashSet<String>();

        for (final ERTable table : diagram.getDiagramContents().getContents().getTableSet()) {
            newTableNames.add(dbSetting.getTableNameWithSchema(table.getPhysicalName(), table.getTableViewProperties().getSchema(), true));
        }

        newViewNames = new HashSet<String>();

        for (final View view : diagram.getDiagramContents().getContents().getViewSet()) {
            newViewNames.add(dbSetting.getTableNameWithSchema(view.getPhysicalName(), view.getTableViewProperties().getSchema()));
        }

        newSequenceNames = new HashSet<String>();

        for (final Sequence sequence : diagram.getDiagramContents().getSequenceSet()) {
            newSequenceNames.add(dbSetting.getTableNameWithSchema(sequence.getName(), sequence.getSchema()));
        }
    }

    public void run() {
        try {
            final StringBuilder sb = new StringBuilder();
            sb.append(dropViews());
            sb.append(dropForeignKeys());
            sb.append(dropTables());
            sb.append(dropSequences());

            sb.append(executeDDL());

            ddl = sb.toString();

        } catch (final Exception e) {
            exception = e;
        }
    }

    private String dropSequences() throws SQLException {
        final StringBuilder ddl = new StringBuilder();

        ResultSet sequenceSet = null;

        try {
            sequenceSet = metaData.getTables(null, null, null, new String[] {"SEQUENCE"});

            while (sequenceSet.next()) {
                String name = sequenceSet.getString("TABLE_NAME");
                final String schema = sequenceSet.getString("TABLE_SCHEM");
                name = dbSetting.getTableNameWithSchema(name, schema);

                if (newSequenceNames == null || newSequenceNames.contains(name)) {
                    ddl.append(dropSequence(name));
                    ddl.append("\r\n");
                }
            }

        } finally {
            if (sequenceSet != null) {
                sequenceSet.close();
            }

        }

        return ddl.toString();
    }

    private String dropSequence(final String sequenceName) throws SQLException {
        final String sql = "DROP SEQUENCE " + ifExistsOption + sequenceName + ";";

        return sql;
    }

    private String dropViews() throws SQLException {
        final StringBuilder ddl = new StringBuilder();

        ResultSet viewSet = null;

        try {
            viewSet = metaData.getTables(null, null, null, new String[] {"VIEW"});

            while (viewSet.next()) {
                String name = viewSet.getString("TABLE_NAME");
                final String schema = viewSet.getString("TABLE_SCHEM");
                name = dbSetting.getTableNameWithSchema(name, schema);

                if (newViewNames == null || newViewNames.contains(name)) {
                    ddl.append(dropView(name));
                    ddl.append("\r\n");
                }
            }

        } finally {
            if (viewSet != null) {
                viewSet.close();
            }

        }

        return ddl.toString();
    }

    private String dropView(final String viewName) throws SQLException {
        final String sql = "DROP VIEW " + ifExistsOption + viewName + ";";

        return sql;
    }

    protected String dropForeignKeys() throws SQLException {
        final StringBuilder ddl = new StringBuilder();

        ResultSet foreignKeySet = null;

        try {
            foreignKeySet = metaData.getImportedKeys(null, null, null);

            final Set<String> fkNameSet = new HashSet<String>();

            while (foreignKeySet.next()) {
                final String constraintName = foreignKeySet.getString("FK_NAME");
                if (fkNameSet.contains(constraintName)) {
                    continue;
                }
                fkNameSet.add(constraintName);

                String tableName = foreignKeySet.getString("FKTABLE_NAME");
                final String schema = foreignKeySet.getString("FKTABLE_SCHEM");

                tableName = dbSetting.getTableNameWithSchema(tableName, schema);

                if (newTableNames == null || newTableNames.contains(tableName.toUpperCase())) {
                    ddl.append(dropForeignKey(tableName, constraintName));
                    ddl.append("\r\n");
                }
            }

        } finally {
            if (foreignKeySet != null) {
                foreignKeySet.close();
            }
        }

        return ddl.toString();
    }

    private String dropForeignKey(final String tableName, final String constraintName) throws SQLException {
        final String sql = "ALTER TABLE " + tableName + " DROP CONSTRAINT " + constraintName + ";";

        return sql;
    }

    protected String dropTables() throws SQLException, InterruptedException {
        final StringBuilder ddl = new StringBuilder();

        ResultSet tableSet = null;

        try {
            tableSet = metaData.getTables(null, null, null, new String[] {"TABLE"});

            while (tableSet.next()) {
                String tableName = tableSet.getString("TABLE_NAME");
                final String schema = tableSet.getString("TABLE_SCHEM");
                tableName = dbSetting.getTableNameWithSchema(tableName, schema);

                if (newTableNames == null || newTableNames.contains(tableName.toUpperCase())) {
                    try {
                        checkTableExist(con, tableName);
                    } catch (final SQLException e) {
                        continue;
                    }

                    ddl.append(dropTable(tableName));
                    ddl.append("\r\n");
                }
            }

        } finally {
            if (tableSet != null) {
                tableSet.close();
            }
        }

        return ddl.toString();
    }

    private String dropTable(final String tableName) throws SQLException {
        final String sql = "DROP TABLE " + ifExistsOption + tableName + ";";

        return sql;
    }

    private String executeDDL() throws SQLException {
        final DDLCreator ddlCreator = DBManagerFactory.getDBManager(diagram).getDDLCreator(diagram, diagram.getCurrentCategory(), true);
        ddlCreator.init(environment, new DDLTarget(), null);

        return ddlCreator.getCreateDDL(diagram);
    }

    protected void checkTableExist(final Connection con, final String tableNameWithSchema) throws SQLException {}

    public Exception getException() {
        return exception;
    }

    /**
     * errorSql ���擾���܂�.
     * 
     * @return errorSql
     */
    public String getErrorSql() {
        return errorSql;
    }

    /**
     * ddl ���擾���܂�.
     * 
     * @return ddl
     */
    public String getDdl() {
        return ddl;
    }

}
