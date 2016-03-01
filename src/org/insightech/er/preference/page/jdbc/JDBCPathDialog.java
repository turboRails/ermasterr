package org.insightech.er.preference.page.jdbc;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.db.DBManager;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.model.settings.JDBCDriverSetting;
import org.insightech.er.preference.editor.MultiFileFieldEditor;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;

public class JDBCPathDialog extends AbstractDialog {

    private MultiFileFieldEditor fileFieldEditor;

    private Combo databaseCombo;

    private Text driverClassNameText;

    private String database;

    private String driverClassName;

    private String path;

    private final List<JDBCDriverSetting> otherDriverSettingList;

    private boolean editable;

    public JDBCPathDialog(final Shell parentShell, final String database, final String driverClassName, final String path, final List<JDBCDriverSetting> otherDriverSettingList, final boolean editable) {
        super(parentShell);

        this.database = database;
        this.driverClassName = driverClassName;
        this.path = path;

        this.otherDriverSettingList = otherDriverSettingList;
        this.editable = editable;
    }

    @Override
    protected void initLayout(final GridLayout layout) {
        super.initLayout(layout);

        layout.numColumns = 3;
    }

    @Override
    protected Object createLayoutData() {
        final GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.widthHint = 750;
        gridData.heightHint = 200;
        gridData.horizontalIndent = 10;
        gridData.horizontalSpan = 10;

        return gridData;
    }

    @Override
    protected void initialize(final Composite composite) {
        final GridData gridData = new GridData();
        gridData.horizontalSpan = 3;
        gridData.heightHint = 50;

        final Label label = new Label(composite, SWT.NONE);
        label.setLayoutData(gridData);
        label.setText(ResourceString.getResourceString("label.jdbc.driver.message"));

        if (database != null) {
            final DBManager dbManager = DBManagerFactory.getDBManager(database);

            if (dbManager.getDriverClassName().equals(driverClassName) && !dbManager.getDriverClassName().equals("")) {
                editable = false;
            }

        }

        if (editable) {
            databaseCombo = CompositeFactory.createReadOnlyCombo(this, composite, "label.database", 2, -1);
            databaseCombo.setVisibleItemCount(10);

        } else {
            CompositeFactory.createLabel(composite, "label.database");
            CompositeFactory.createLabel(composite, database, 2);
        }

        driverClassNameText = CompositeFactory.createText(this, composite, "label.driver.class.name", 2, -1, SWT.BORDER, false, false);

        driverClassNameText.setEditable(editable);

        fileFieldEditor = new MultiFileFieldEditor("", ResourceString.getResourceString("label.path"), composite);
        fileFieldEditor.setMultiple(true);

        fileFieldEditor.setFocus();
    }

    @Override
    protected String getTitle() {
        return "label.path";
    }

    @Override
    protected String getErrorMessage() {
        String selectedDatabase = database;

        if (databaseCombo != null) {
            selectedDatabase = databaseCombo.getText();

            if (Check.isEmpty(selectedDatabase)) {
                return "error.database.name.is.empty";
            }
        }

        final String text = driverClassNameText.getText();

        if (Check.isEmpty(text)) {
            return "error.driver.class.name.is.empty";

        } else {
            final JDBCDriverSetting driverSetting = new JDBCDriverSetting(selectedDatabase, text, null);

            if (otherDriverSettingList.contains(driverSetting)) {
                return "error.driver.class.is.already.exist";
            }
        }

        return null;
    }

    @Override
    protected void perfomeOK() throws InputException {
        path = fileFieldEditor.getStringValue();
        driverClassName = driverClassNameText.getText();

        if (databaseCombo != null) {
            database = databaseCombo.getText();
        }
    }

    @Override
    protected void setData() {
        fileFieldEditor.setStringValue(path);
        driverClassNameText.setText(Format.null2blank(driverClassName));

        if (databaseCombo != null) {
            for (final String db : DBManagerFactory.getAllDBList()) {
                databaseCombo.add(db);
            }

            databaseCombo.setText(Format.null2blank(database));
        }
    }

    public String getPath() {
        return path;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public String getDatabase() {
        return database;
    }

}
