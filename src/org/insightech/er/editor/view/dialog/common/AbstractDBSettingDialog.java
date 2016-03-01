package org.insightech.er.editor.view.dialog.common;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.common.widgets.ListenerAppender;
import org.insightech.er.db.DBManager;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.db.impl.standard_sql.StandardSQLDBManager;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.DBSetting;
import org.insightech.er.preference.PreferenceInitializer;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;

public abstract class AbstractDBSettingDialog extends AbstractDialog {

    private Text userName;

    private Text password;

    private Combo dbList;

    private Text serverName;

    private Text port;

    private Text dbName;

    private Text url;

    private Button useDefaultDriverButton;

    private Text driverClassName;

    private Button settingListButton;

    protected Button settingAddButton;

    protected DBSetting dbSetting;

    protected ERDiagram diagram;

    public AbstractDBSettingDialog(final Shell parentShell, final ERDiagram diagram) {
        super(parentShell);
        this.diagram = diagram;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize(final Composite parent) {
        final Composite group = new Composite(parent, SWT.NONE);
        final GridLayout layout = new GridLayout();
        layout.numColumns = 2;

        group.setLayout(layout);
        final GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 2;
        group.setLayoutData(gridData);

        initializeBody(group);

        setDBList();
    }

    private void setDBList() {
        if (isOnlyCurrentDatabase()) {
            dbList.add(diagram.getDatabase());
            dbList.select(0);

        } else {
            for (final String db : DBManagerFactory.getAllDBList()) {
                dbList.add(db);
            }

            dbList.setVisibleItemCount(20);
        }
    }

    protected void initializeBody(final Composite group) {
        dbList = CompositeFactory.createReadOnlyCombo(this, group, "label.database");
        dbList.setFocus();

        serverName = CompositeFactory.createText(this, group, "label.server.name", false, true);
        port = CompositeFactory.createText(this, group, "label.port", false, true);
        dbName = CompositeFactory.createText(this, group, "label.database.name", false, true);
        userName = CompositeFactory.createText(this, group, "label.user.name", false, true);
        password = CompositeFactory.createText(this, group, "label.user.password", false, true);
        password.setEchoChar('*');

        CompositeFactory.filler(group, 2);

        useDefaultDriverButton = CompositeFactory.createCheckbox(this, group, "label.use.default.driver", false, 2);

        url = CompositeFactory.createText(null, group, "label.url", 1, -1, SWT.BORDER | SWT.READ_ONLY, false, true);

        driverClassName = CompositeFactory.createText(null, group, "label.driver.class.name", 1, -1, SWT.BORDER | SWT.READ_ONLY, false, true);
    }

    @Override
    protected void createButtonsForButtonBar(final Composite parent) {
        createButton(parent, 0, IDialogConstants.NEXT_LABEL, true);
        createButton(parent, 1, IDialogConstants.CANCEL_LABEL, false);
        settingListButton = createButton(parent, IDialogConstants.OPEN_ID, ResourceString.getResourceString("label.load.database.setting"), false);
        settingAddButton = createButton(parent, IDialogConstants.YES_ID, ResourceString.getResourceString("label.load.database.setting.add"), false);
    }

    public String getDBSName() {
        return dbList.getText().trim();
    }

    public String getDBName() {
        return dbName.getText().trim();
    }

    public String getServerName() {
        return serverName.getText().trim();
    }

    public int getPort() {
        final String port = this.port.getText().trim();

        try {
            return Integer.parseInt(port);
        } catch (final Exception e) {
            return 0;
        }
    }

    public String getUserName() {
        return userName.getText().trim();
    }

    public String getPassword() {
        return password.getText().trim();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getErrorMessage() {
        DBManager manager = null;

        final String database = getDBSName();

        if (!Check.isEmpty(database)) {
            if (!useDefaultDriverButton.getSelection()) {
                if (isBlank(url)) {
                    return "error.url.is.empty";
                }
                if (isBlank(driverClassName)) {
                    return "error.driver.class.name.is.empty";
                }

            } else {
                manager = DBManagerFactory.getDBManager(getDBSName());
                final String url = manager.getURL(getServerName(), getDBName(), getPort());
                this.url.setText(url);

                if (isBlank(serverName) && manager.doesNeedURLServerName()) {
                    return "error.server.is.empty";
                }

                if (isBlank(port) && manager.doesNeedURLServerName()) {
                    return "error.port.is.empty";
                }

                if (isBlank(dbName) && manager.doesNeedURLDatabaseName()) {
                    return "error.database.name.is.empty";
                }
            }
        }

        if (settingAddButton != null) {
            settingAddButton.setEnabled(false);
        }

        if (isBlank(dbList)) {
            return "error.database.not.selected";
        }

        final String text = port.getText();

        if (!text.equals("")) {
            try {
                final int port = Integer.parseInt(text);
                if (port < 0) {
                    return "error.port.zero";
                }

            } catch (final NumberFormatException e) {
                return "error.port.degit";
            }
        }

        if (isBlank(userName)) {
            return "error.user.name.is.empty";
        }

        if (settingAddButton != null) {
            settingAddButton.setEnabled(true);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setData() {
        if (dbSetting != null) {
            String database = dbSetting.getDbsystem();
            if (Check.isEmpty(database)) {
                database = diagram.getDatabase();
            }
            dbList.setText(database);

            useDefaultDriverButton.setSelection(dbSetting.isUseDefaultDriver());
            enableField();

            serverName.setText(Format.null2blank(dbSetting.getServer()));
            port.setText(String.valueOf(dbSetting.getPort()));
            dbName.setText(Format.null2blank(dbSetting.getDatabase()));
            userName.setText(Format.null2blank(dbSetting.getUser()));
            password.setText(Format.null2blank(dbSetting.getPassword()));
            url.setText(Format.null2blank(dbSetting.getUrl()));
            driverClassName.setText(Format.null2blank(dbSetting.getDriverClassName()));

            if (!Check.isEmpty(database) && useDefaultDriverButton.getSelection()) {
                final DBManager manager = DBManagerFactory.getDBManager(getDBSName());
                final String url = manager.getURL(getServerName(), getDBName(), getPort());
                this.url.setText(url);

                final String driverClassName = manager.getDriverClassName();
                this.driverClassName.setText(driverClassName);
            }

        } else {
            enableUseDefaultDriver();
            enableField();
        }
    }

    @Override
    protected int getErrorLine() {
        return 2;
    }

    public DBSetting getDbSetting() {
        return dbSetting;
    }

    private void enableUseDefaultDriver() {
        final String database = getDBSName();

        if (!Check.isEmpty(database)) {
            final DBManager dbManager = DBManagerFactory.getDBManager(database);

            if (StandardSQLDBManager.ID.equals(dbManager.getId())) {
                useDefaultDriverButton.setSelection(false);
                useDefaultDriverButton.setEnabled(false);

            } else {
                useDefaultDriverButton.setSelection(true);
                useDefaultDriverButton.setEnabled(true);

            }
        }
    }

    private void enableField() {
        final String database = getDBSName();

        if (useDefaultDriverButton.getSelection()) {
            final DBManager dbManager = DBManagerFactory.getDBManager(database);

            dbName.setEnabled(true);
            url.setEditable(false);
            driverClassName.setEditable(false);
            driverClassName.setText(dbManager.getDriverClassName());

            if (dbManager.doesNeedURLServerName()) {
                port.setText(String.valueOf(dbManager.getDefaultPort()));
                port.setEnabled(true);
                serverName.setEnabled(true);

            } else {
                port.setEnabled(false);
                serverName.setEnabled(false);
            }

        } else {
            port.setEnabled(false);
            serverName.setEnabled(false);
            dbName.setEnabled(false);
            url.setEditable(true);
            driverClassName.setEditable(true);

        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addListener() {
        super.addListener();

        dbList.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(final ModifyEvent e) {
                enableUseDefaultDriver();
                enableField();
                validate();
            }
        });

        useDefaultDriverButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent selectionevent) {
                enableField();
                validate();
            }

        });

        ListenerAppender.addModifyListener(serverName, this);
        ListenerAppender.addModifyListener(port, this);
        ListenerAppender.addModifyListener(dbName, this);
        ListenerAppender.addModifyListener(userName, this);
        ListenerAppender.addModifyListener(driverClassName, this);

        url.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(final ModifyEvent e) {
                if (!useDefaultDriverButton.getSelection()) {
                    validate();
                }
            }
        });

        settingListButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                try {
                    String database = null;
                    if (isOnlyCurrentDatabase()) {
                        database = diagram.getDatabase();
                    }
                    final DBSettingListDialog dialog = new DBSettingListDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), database);

                    if (dialog.open() == IDialogConstants.OK_ID) {
                        dbSetting = dialog.getResult();
                        setData();
                    }

                } catch (final Exception ex) {
                    ERDiagramActivator.showExceptionDialog(ex);
                }

            }
        });

        settingAddButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                try {
                    if (validate()) {
                        setCurrentSetting();

                        PreferenceInitializer.addDBSetting(dbSetting);

                        ERDiagramActivator.showMessageDialog("dialog.message.add.to.connection.list");
                    }

                } catch (final Exception ex) {
                    ERDiagramActivator.showExceptionDialog(ex);
                }
            }
        });
    }

    protected boolean isOnlyCurrentDatabase() {
        return false;
    }

    protected void setCurrentSetting() {
        final String database = getDBSName();
        final String url = this.url.getText().trim();
        final String driverClassName = this.driverClassName.getText().trim();
        final String serverName = getServerName();
        final int port = getPort();
        final String dbName = getDBName();
        final boolean useDefaultDriver = useDefaultDriverButton.getSelection();

        dbSetting = new DBSetting(database, serverName, port, dbName, getUserName(), getPassword(), useDefaultDriver, url, driverClassName);

        PreferenceInitializer.saveSetting(0, dbSetting);
    }
}
