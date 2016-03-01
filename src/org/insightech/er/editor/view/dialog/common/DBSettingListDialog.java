package org.insightech.er.editor.view.dialog.common;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.settings.DBSetting;
import org.insightech.er.preference.PreferenceInitializer;
import org.insightech.er.util.Format;

public class DBSettingListDialog extends AbstractDialog {

    private Table settingTable;

    private List<DBSetting> dbSettingList;

    private DBSetting result;

    private final String database;

    public DBSettingListDialog(final Shell parentShell, final String database) {
        super(parentShell);

        this.database = database;
        dbSettingList = new ArrayList<DBSetting>();
    }

    @Override
    protected void initialize(final Composite composite) {
        settingTable = CompositeFactory.createTable(composite, 150);

        CompositeFactory.createTableColumn(settingTable, "label.database", 150);
        CompositeFactory.createTableColumn(settingTable, "label.server.name", 130);
        CompositeFactory.createTableColumn(settingTable, "label.port", 80, SWT.RIGHT);
        CompositeFactory.createTableColumn(settingTable, "label.database.name", 130);
        CompositeFactory.createTableColumn(settingTable, "label.user.name", 100);
        CompositeFactory.createTableColumn(settingTable, "label.url", 350);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addListener() {
        super.addListener();

        settingTable.addMouseListener(new MouseAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void mouseDoubleClick(final MouseEvent e) {
                buttonPressed(IDialogConstants.OK_ID);
            }
        });

        settingTable.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                validate();

                final int index = settingTable.getSelectionIndex();
                if (index == -1) {
                    return;
                }

                selectTable(index);
            }
        });

    }

    @Override
    protected void perfomeOK() throws InputException {
        final int index = settingTable.getSelectionIndex();
        result = dbSettingList.get(index);
    }

    public DBSetting getResult() {
        return result;
    }

    public int getResultIndex() {
        return dbSettingList.indexOf(result);
    }

    @Override
    protected void setData() {
        dbSettingList = PreferenceInitializer.getDBSettingList(database);

        for (final DBSetting dbSetting : dbSettingList) {
            final TableItem item = new TableItem(settingTable, SWT.NONE);
            item.setText(0, dbSetting.getDbsystem());
            item.setText(1, dbSetting.getServer());
            if (dbSetting.getPort() != 0) {
                item.setText(2, String.valueOf(dbSetting.getPort()));
            }
            item.setText(3, dbSetting.getDatabase());
            item.setText(4, dbSetting.getUser());
            item.setText(5, Format.null2blank(dbSetting.getUrl()));
        }

        setButtonEnabled(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createButtonsForButtonBar(final Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, ResourceString.getResourceString("label.load.setting"), true);
        createButton(parent, IDialogConstants.STOP_ID, ResourceString.getResourceString("label.delete"), false);
        createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, false);

        setButtonEnabled(false);
    }

    private void setButtonEnabled(final boolean enabled) {
        final Button okButton = getButton(IDialogConstants.OK_ID);
        if (okButton != null) {
            okButton.setEnabled(enabled);
        }

        final Button deleteButton = getButton(IDialogConstants.STOP_ID);
        if (deleteButton != null) {
            deleteButton.setEnabled(enabled);
        }
    }

    private void selectTable(final int index) {
        settingTable.select(index);

        if (index >= 0) {
            setButtonEnabled(true);
        } else {
            setButtonEnabled(false);
        }
    }

    @Override
    protected String getErrorMessage() {
        final int index = settingTable.getSelectionIndex();
        if (index == -1) {
            return "dialog.message.load.db.setting";
        }

        return null;
    }

    @Override
    protected String getTitle() {
        return "label.load.database.setting";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void buttonPressed(final int buttonId) {
        if (buttonId == IDialogConstants.STOP_ID) {
            int index = settingTable.getSelectionIndex();

            if (index != -1) {
                settingTable.remove(index);
                dbSettingList.remove(index);

                PreferenceInitializer.saveSetting(dbSettingList);

                if (index >= settingTable.getItemCount()) {
                    index = settingTable.getItemCount() - 1;
                }

                selectTable(index);
            }
        }

        super.buttonPressed(buttonId);
    }

}
