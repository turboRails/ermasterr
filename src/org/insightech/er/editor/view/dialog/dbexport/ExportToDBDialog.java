package org.insightech.er.editor.view.dialog.dbexport;

import java.lang.reflect.InvocationTargetException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.db.ExportToDBManager;
import org.insightech.er.editor.model.settings.DBSetting;
import org.insightech.er.util.Check;

public class ExportToDBDialog extends AbstractDialog {

    private Text textArea;

    private final DBSetting dbSetting;

    private final String ddl;

    public ExportToDBDialog(final Shell parentShell, final ERDiagram diagram, final DBSetting dbSetting, final String ddl) {
        super(parentShell);

        this.dbSetting = dbSetting;
        this.ddl = ddl;
    }

    @Override
    protected void initLayout(final GridLayout layout) {
        super.initLayout(layout);
        layout.numColumns = 1;
    }

    @Override
    protected void initialize(final Composite composite) {
        textArea = CompositeFactory.createTextArea(null, composite, "dialog.message.export.db.sql", 600, 400, 1, false, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createButtonsForButtonBar(final Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, ResourceString.getResourceString("label.button.execute"), true);
    }

    @Override
    protected String getErrorMessage() {
        if ("".equals(textArea.getText().trim())) {
            return "";
        }

        return null;
    }

    @Override
    protected String getTitle() {
        return "dialog.title.export.db";
    }

    @Override
    protected void perfomeOK() throws InputException {
        String executeDDL = textArea.getSelectionText();
        if (Check.isEmpty(executeDDL)) {
            executeDDL = textArea.getText();
        }

        if (!ERDiagramActivator.showConfirmDialog("dialog.message.export.db.confirm")) {
            return;
        }

        Connection con = null;

        try {
            con = dbSetting.connect();

            final ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());

            final ExportToDBManager exportToDBManager = new ExportToDBManager();
            exportToDBManager.init(con, executeDDL);

            try {
                dialog.run(true, true, exportToDBManager);

                final Exception e = exportToDBManager.getException();
                if (e != null) {
                    ERDiagramActivator.showMessageDialog(e.getMessage());
                    throw new InputException();

                } else {
//					Activator
//							.showMessageDialog("dialog.message.export.db.finish");
                }

            } catch (final InvocationTargetException e) {} catch (final InterruptedException e) {}

        } catch (final InputException e) {
            throw e;

        } catch (final Exception e) {
            final Throwable cause = e.getCause();

            if (cause instanceof UnknownHostException) {
                throw new InputException("error.server.not.found");
            }

            ERDiagramActivator.log(e);

            ERDiagramActivator.showMessageDialog(e.getMessage());
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

    @Override
    protected void setData() {
        textArea.setText(ddl);
    }

}
