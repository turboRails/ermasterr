package org.insightech.er.editor.view.dialog.option.tab;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.ValidatableTabWrapper;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.dialog.option.OptionSettingDialog;

public class DBSelectTabWrapper extends ValidatableTabWrapper {

    private Combo databaseCombo;

    private final Settings settings;

    public DBSelectTabWrapper(final OptionSettingDialog dialog, final TabFolder parent, final Settings settings) {
        super(dialog, parent, "label.database");

        this.settings = settings;
    }

    @Override
    protected void initLayout(final GridLayout layout) {
        super.initLayout(layout);
        layout.numColumns = 2;
    }

    @Override
    public void initComposite() {

        databaseCombo = CompositeFactory.createReadOnlyCombo(null, this, "label.database");
        databaseCombo.setVisibleItemCount(10);

        for (final String db : DBManagerFactory.getAllDBList()) {
            databaseCombo.add(db);
        }

        databaseCombo.setFocus();
    }

    @Override
    protected void addListener() {
        super.addListener();

        databaseCombo.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                changeDatabase();
            }
        });
    }

    @Override
    public void setData() {
        for (int i = 0; i < databaseCombo.getItemCount(); i++) {
            final String database = databaseCombo.getItem(i);
            if (database.equals(settings.getDatabase())) {
                databaseCombo.select(i);
                break;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validatePage() throws InputException {
        settings.setDatabase(databaseCombo.getText());
    }

    private void changeDatabase() {
        final MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
        messageBox.setText(ResourceString.getResourceString("dialog.title.change.database"));
        messageBox.setMessage(ResourceString.getResourceString("dialog.message.change.database"));

        if (messageBox.open() == SWT.OK) {
            final String database = databaseCombo.getText();
            settings.setDatabase(database);

            dialog.resetTabs();

        } else {
            this.setData();
        }
    }

    @Override
    public void setInitFocus() {
        databaseCombo.setFocus();
    }

    @Override
    public void perfomeOK() {}
}
