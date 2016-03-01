package org.insightech.er.editor.view.dialog.dbexport;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.db.DBManager;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.db.PreTableExportManager;
import org.insightech.er.editor.model.settings.Environment;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.dialog.common.AbstractDBSettingDialog;

public class ExportDBSettingDialog extends AbstractDBSettingDialog {

    private Combo environmentCombo;

    private String ddl;

    public ExportDBSettingDialog(final Shell parentShell, final ERDiagram diagram) {
        super(parentShell, diagram);
        dbSetting = this.diagram.getDbSetting();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initializeBody(final Composite group) {
        environmentCombo = CompositeFactory.createReadOnlyCombo(this, group, "label.tablespace.environment", 1);
        environmentCombo.setVisibleItemCount(20);

        super.initializeBody(group);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize(final Composite parent) {
        super.initialize(parent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getErrorMessage() {
        if (settingAddButton != null) {
            settingAddButton.setEnabled(false);
        }

        if (isBlank(environmentCombo)) {
            return "error.tablespace.environment.empty";
        }

        if (!diagram.getDatabase().equals(getDBSName())) {
            return "error.database.not.correct";
        }

        return super.getErrorMessage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void perfomeOK() throws InputException {
        setCurrentSetting();

        final String db = getDBSName();
        final DBManager manager = DBManagerFactory.getDBManager(db);

        Connection con = null;

        try {
            diagram.setDbSetting(dbSetting);

            con = dbSetting.connect();

            final int index = environmentCombo.getSelectionIndex();
            final Environment environment = diagram.getDiagramContents().getSettings().getEnvironmentSetting().getEnvironments().get(index);

            final PreTableExportManager exportToDBManager = manager.getPreTableExportManager();
            exportToDBManager.init(con, dbSetting, diagram, environment);

            exportToDBManager.run();

            final Exception e = exportToDBManager.getException();
            if (e != null) {
                ERDiagramActivator.log(e);
                String message = e.getMessage();
                final String errorSql = exportToDBManager.getErrorSql();

                if (errorSql != null) {
                    message += "\r\n\r\n" + errorSql;
                }
                final ErrorDialog errorDialog = new ErrorDialog(getShell(), message);
                errorDialog.open();

                throw new InputException("error.jdbc.version");
            }

            ddl = exportToDBManager.getDdl();

        } catch (final InputException e) {
            throw e;

        } catch (final Exception e) {
            final Throwable cause = e.getCause();

            if (cause instanceof UnknownHostException) {
                throw new InputException("error.server.not.found");

            } else if (cause instanceof ConnectException) {
                throw new InputException("error.server.not.connected");

            }

            ERDiagramActivator.showExceptionDialog(e);
            throw new InputException("error.database.not.found");

        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (final SQLException e) {
                    ERDiagramActivator.showExceptionDialog(e);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTitle() {
        return "dialog.title.export.db";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setData() {
        super.setData();

        final Settings settings = diagram.getDiagramContents().getSettings();

        for (final Environment environment : settings.getEnvironmentSetting().getEnvironments()) {
            environmentCombo.add(environment.getName());
        }
        environmentCombo.select(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isOnlyCurrentDatabase() {
        return true;
    }

    public String getDdl() {
        return ddl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addListener() {
        super.addListener();

        environmentCombo.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                validate();
            }
        });
    }

}
