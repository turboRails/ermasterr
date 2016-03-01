package org.insightech.er.editor.model.dbexport.db;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.insightech.er.ResourceString;

public class ExportToDBManager implements IRunnableWithProgress {

    //	private static Logger logger = Logger.getLogger(ExportToDBManager.class
//			.getName());

    protected Connection con;

    private String ddl;

    private Exception exception;

    private String errorSql;

    public ExportToDBManager() {}

    public void init(final Connection con, final String ddl) throws SQLException {
        this.con = con;
        this.con.setAutoCommit(false);
        this.ddl = ddl;
    }

    @Override
    public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

        try {
            final String[] ddls = ddl.split(";[\r\n]+");

            monitor.beginTask(ResourceString.getResourceString("dialog.message.drop.table"), ddls.length);

            for (int i = 0; i < ddls.length; i++) {
                String message = ddls[i];
                final int index = message.indexOf("\r\n");
                if (index != -1) {
                    message = message.substring(0, index);
                }

                monitor.subTask("(" + (i + 1) + "/" + ddls.length + ") " + message);

                executeDDL(ddls[i]);
                monitor.worked(1);

                if (monitor.isCanceled()) {
                    throw new InterruptedException("Cancel has been requested.");
                }
            }

            con.commit();

        } catch (final InterruptedException e) {
            throw e;

        } catch (final Exception e) {
            exception = e;
        }

        monitor.done();
    }

    private void executeDDL(final String ddl) throws SQLException {
        Statement stmt = null;

        try {
//			logger.info(ddl);
            stmt = con.createStatement();
            stmt.execute(ddl);

        } catch (final SQLException e) {
//			Activator.log(e);
            errorSql = ddl;
            throw e;

        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    public Exception getException() {
        return exception;
    }

    public String getErrorSql() {
        return errorSql;
    }

}
