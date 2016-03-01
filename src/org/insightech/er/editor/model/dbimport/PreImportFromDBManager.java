package org.insightech.er.editor.model.dbimport;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.DBSetting;

public abstract class PreImportFromDBManager implements IRunnableWithProgress {

    private static Logger logger = Logger.getLogger(PreImportFromDBManager.class.getName());

    protected Connection con;

    private DatabaseMetaData metaData;

    protected DBSetting dbSetting;

    private DBObjectSet importObjects;

    protected List<String> schemaList;

    private Exception exception;

    private int taskTotalCount = 1;

    private int taskCount = 0;

    IProgressMonitor monitor;

    public void init(final Connection con, final DBSetting dbSetting, final ERDiagram diagram, final List<String> schemaList) throws SQLException {
        this.con = con;
        this.dbSetting = dbSetting;

        metaData = con.getMetaData();

        importObjects = new DBObjectSet();
        this.schemaList = schemaList;
    }

    @Override
    public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

        try {
            if (!schemaList.isEmpty()) {
                taskTotalCount = schemaList.size();
            }
            taskTotalCount *= 4;
            this.monitor = monitor;

            monitor.beginTask(ResourceString.getResourceString("dialog.message.import.schema.information"), taskTotalCount);

            importObjects.addAll(importTables());
            importObjects.addAll(importSequences());
            importObjects.addAll(importViews());
            importObjects.addAll(importTriggers());

        } catch (final InterruptedException e) {
            throw e;

        } catch (final Exception e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            exception = e;
        }
    }

    protected List<DBObject> importTables() throws SQLException, InterruptedException {
        return importObjects(new String[] {"TABLE", "SYSTEM TABLE", "SYSTEM TOAST TABLE", "TEMPORARY TABLE"}, DBObject.TYPE_TABLE);
    }

    protected List<DBObject> importSequences() throws SQLException, InterruptedException {
        return importObjects(new String[] {"SEQUENCE"}, DBObject.TYPE_SEQUENCE);
    }

    protected List<DBObject> importViews() throws SQLException, InterruptedException {
        return importObjects(new String[] {"VIEW", "SYSTEM VIEW"}, DBObject.TYPE_VIEW);
    }

    protected List<DBObject> importTriggers() throws SQLException, InterruptedException {
        return importObjects(new String[] {"TRIGGER"}, DBObject.TYPE_TRIGGER);
    }

    private List<DBObject> importObjects(final String[] types, final String dbObjectType) throws SQLException, InterruptedException {
        final List<DBObject> list = new ArrayList<DBObject>();

        ResultSet resultSet = null;

        if (schemaList.isEmpty()) {
            schemaList.add(null);
        }

        for (final String schemaPattern : schemaList) {
            try {
                taskCount++;

                monitor.subTask("(" + taskCount + "/" + taskTotalCount + ")  [TYPE : " + dbObjectType.toUpperCase() + ",  SCHEMA : " + schemaPattern + "]");
                monitor.worked(1);

                resultSet = metaData.getTables(null, schemaPattern, null, types);

                while (resultSet.next()) {
                    final String schema = resultSet.getString("TABLE_SCHEM");
                    final String name = resultSet.getString("TABLE_NAME");

                    if (DBObject.TYPE_TABLE.equals(dbObjectType)) {
                        try {
                            getAutoIncrementColumnName(con, schema, name);

                        } catch (final SQLException e) {
                            logger.log(Level.WARNING, e.getMessage());
                            // テーブル情報が取得できない場合（他のユーザの所有物などの場合）、
                            // このテーブルは使用しない。
                            continue;
                        }
                    }

                    final DBObject dbObject = new DBObject(schema, name, dbObjectType);
                    list.add(dbObject);
                }

                if (monitor.isCanceled()) {
                    throw new InterruptedException("Cancel has been requested.");
                }

            } finally {
                if (resultSet != null) {
                    resultSet.close();
                    resultSet = null;
                }

            }
        }

        return list;
    }

    private String getAutoIncrementColumnName(final Connection con, final String schema, final String tableName) throws SQLException {
        final String autoIncrementColumnName = null;

        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = con.createStatement();

            rs = stmt.executeQuery("SELECT 1 FROM " + getTableNameWithSchema(schema, tableName));

        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }

        }

        return autoIncrementColumnName;
    }

    protected String getTableNameWithSchema(final String schema, final String tableName) {
        return dbSetting.getTableNameWithSchema(tableName, schema);
    }

    public DBObjectSet getImportObjects() {
        return importObjects;
    }

    public Exception getException() {
        return exception;
    }

}
