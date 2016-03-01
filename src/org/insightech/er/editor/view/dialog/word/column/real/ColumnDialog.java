package org.insightech.er.editor.view.dialog.word.column.real;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.db.DBManager;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.db.impl.mysql.MySQLDBManager;
import org.insightech.er.db.impl.postgres.PostgresDBManager;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.CopyColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.view.dialog.element.table.sub.AutoIncrementSettingDialog;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;

public class ColumnDialog extends AbstractRealColumnDialog {

    private final ERTable erTable;

    private Sequence autoIncrementSetting;

    protected Button primaryKeyCheck;

    protected Text uniqueKeyNameText;

    protected Combo characterSetCombo;

    protected Combo collationCombo;

    protected Button autoIncrementCheck;

    protected Button autoIncrementSettingButton;

    public ColumnDialog(final Shell parentShell, final ERTable erTable) {
        super(parentShell, erTable.getDiagram());

        this.erTable = erTable;
    }

    @Override
    protected void initializeDetailTab(final Composite composite) {
        uniqueKeyNameText = CompositeFactory.createText(this, composite, "label.unique.key.name", false, true);

        super.initializeDetailTab(composite);

        final DBManager manager = DBManagerFactory.getDBManager(diagram);

        if (MySQLDBManager.ID.equals(diagram.getDatabase())) {
            characterSetCombo = CompositeFactory.createCombo(this, composite, "label.character.set");
            collationCombo = CompositeFactory.createCombo(this, composite, "label.collation");
        }

        if (manager.isSupported(DBManager.SUPPORT_AUTO_INCREMENT_SETTING)) {
            CompositeFactory.fillLine(composite);

            autoIncrementSettingButton = CompositeFactory.createLargeButton(composite, "label.auto.increment.setting", 2);
            autoIncrementSettingButton.setEnabled(false);
        }
    }

    @Override
    protected int getCheckBoxCompositeNumColumns() {
        final DBManager manager = DBManagerFactory.getDBManager(diagram);

        if (manager.isSupported(DBManager.SUPPORT_AUTO_INCREMENT)) {
            return 4;
        }

        return 3;
    }

    @Override
    protected void initializeCheckBoxComposite(final Composite composite) {
        primaryKeyCheck = CompositeFactory.createCheckbox(this, composite, "label.primary.key", false);

        super.initializeCheckBoxComposite(composite);

        final DBManager manager = DBManagerFactory.getDBManager(diagram);

        if (manager.isSupported(DBManager.SUPPORT_AUTO_INCREMENT)) {
            autoIncrementCheck = CompositeFactory.createCheckbox(this, composite, "label.auto.increment", false);
        }

        if (isRefered) {
            uniqueKeyCheck.setEnabled(false);
        }

        enableAutoIncrement(false);
    }

    protected int getStyle(int style) {
        if (foreignKey) {
            style |= SWT.READ_ONLY;
        }

        return style;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initializeComposite(final Composite composite) {
        super.initializeComposite(composite);

        if (foreignKey) {
            wordCombo.setEnabled(false);
            typeCombo.setEnabled(false);
            defaultText.setEnabled(false);
            lengthText.setEnabled(false);
            decimalText.setEnabled(false);
        }
    }

    @Override
    protected void initData() {
        super.initData();

        if (characterSetCombo != null) {
            characterSetCombo.add("");

            for (final String characterSet : MySQLDBManager.getCharacterSetList()) {
                characterSetCombo.add(characterSet);
            }

            collationCombo.add("");
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setWordData() {
        super.setWordData();

        primaryKeyCheck.setSelection(targetColumn.isPrimaryKey());

        if (autoIncrementCheck != null) {
            autoIncrementCheck.setSelection(targetColumn.isAutoIncrement());
        }

        if (primaryKeyCheck.getSelection()) {
            notNullCheck.setSelection(true);
            notNullCheck.setEnabled(false);
        } else {
            notNullCheck.setEnabled(true);
        }

        final NormalColumn autoIncrementColumn = erTable.getAutoIncrementColumn();

        if (primaryKeyCheck.getSelection()) {
            if (autoIncrementColumn == null || autoIncrementColumn == targetColumn) {
                enableAutoIncrement(true);

            } else {
                enableAutoIncrement(false);
            }

        } else {
            enableAutoIncrement(false);
        }

        defaultText.setText(Format.null2blank(targetColumn.getDefaultValue()));

        uniqueKeyNameText.setText(Format.null2blank(targetColumn.getUniqueKeyName()));

        if (characterSetCombo != null) {
            characterSetCombo.setText(Format.null2blank(targetColumn.getCharacterSet()));

            for (final String collation : MySQLDBManager.getCollationList(targetColumn.getCharacterSet())) {
                collationCombo.add(collation);
            }

            collationCombo.setText(Format.null2blank(targetColumn.getCollation()));
        }
    }

    @Override
    protected String getTitle() {
        return "dialog.title.column";
    }

    private void enableAutoIncrement(final boolean enabled) {
        if (autoIncrementCheck != null) {
            if (!enabled) {
                autoIncrementCheck.setSelection(false);
            }

            autoIncrementCheck.setEnabled(enabled);

            if (autoIncrementSettingButton != null) {
                autoIncrementSettingButton.setEnabled(enabled && autoIncrementCheck.getSelection());
            }
        }
    }

    @Override
    protected void setEnabledBySqlType() {
        super.setEnabledBySqlType();

        final SqlType selectedType = SqlType.valueOf(diagram.getDatabase(), typeCombo.getText());

        if (selectedType != null) {
            if (PostgresDBManager.ID.equals(diagram.getDatabase())) {
                if (SqlType.SQL_TYPE_ID_BIG_SERIAL.equals(selectedType.getId()) || SqlType.SQL_TYPE_ID_SERIAL.equals(selectedType.getId())) {
                    autoIncrementSettingButton.setEnabled(true);
                } else {
                    autoIncrementSettingButton.setEnabled(false);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void perfomeOK() {
        super.perfomeOK();

        returnColumn.setPrimaryKey(primaryKeyCheck.getSelection());

        if (autoIncrementCheck != null) {
            returnColumn.setAutoIncrement(autoIncrementCheck.getSelection());
        }

        returnColumn.setAutoIncrementSetting(autoIncrementSetting);

        returnColumn.setUniqueKeyName(uniqueKeyNameText.getText());

        if (characterSetCombo != null) {
            returnColumn.setCharacterSet(characterSetCombo.getText());
            returnColumn.setCollation(collationCombo.getText());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getErrorMessage() {
        if (autoIncrementCheck != null && autoIncrementCheck.getSelection()) {
            final SqlType selectedType = SqlType.valueOf(diagram.getDatabase(), typeCombo.getText());
            if (selectedType == null || !selectedType.isNumber()) {
                return "error.no.auto.increment.column";
            }
        }

        final String text = uniqueKeyNameText.getText().trim();
        if (!Check.isAlphabet(text)) {
            return "error.unique.key.name.not.alphabet";
        }

        return super.getErrorMessage();
    }

    @Override
    protected void addListener() {
        super.addListener();

        if (autoIncrementSettingButton != null) {
            autoIncrementSettingButton.addSelectionListener(new SelectionAdapter() {

                /**
                 * {@inheritDoc}
                 */
                @Override
                public void widgetSelected(final SelectionEvent e) {
                    final AutoIncrementSettingDialog dialog = new AutoIncrementSettingDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), autoIncrementSetting, diagram.getDatabase());

                    if (dialog.open() == IDialogConstants.OK_ID) {
                        autoIncrementSetting = dialog.getResult();
                    }
                }
            });
        }

        final NormalColumn autoIncrementColumn = erTable.getAutoIncrementColumn();

        primaryKeyCheck.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                if (primaryKeyCheck.getSelection()) {
                    notNullCheck.setSelection(true);
                    notNullCheck.setEnabled(false);

                    if (autoIncrementColumn == null || autoIncrementColumn == targetColumn) {
                        enableAutoIncrement(true);

                    } else {
                        enableAutoIncrement(false);
                    }

                } else {
                    notNullCheck.setEnabled(true);
                    enableAutoIncrement(false);
                }
            }
        });

        uniqueKeyCheck.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                uniqueKeyNameText.setEnabled(uniqueKeyCheck.getSelection());
            }
        });

        if (autoIncrementSettingButton != null && autoIncrementCheck != null) {
            autoIncrementCheck.addSelectionListener(new SelectionAdapter() {

                /**
                 * {@inheritDoc}
                 */
                @Override
                public void widgetSelected(final SelectionEvent e) {

                    autoIncrementSettingButton.setEnabled(autoIncrementCheck.getSelection());
                }

            });
        }

        if (characterSetCombo != null) {

            characterSetCombo.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(final SelectionEvent e) {
                    final String selectedCollation = collationCombo.getText();

                    collationCombo.removeAll();
                    collationCombo.add("");

                    for (final String collation : MySQLDBManager.getCollationList(characterSetCombo.getText())) {
                        collationCombo.add(collation);
                    }

                    final int index = collationCombo.indexOf(selectedCollation);

                    collationCombo.select(index);
                }
            });
        }
    }

    @Override
    public void setTargetColumn(final CopyColumn targetColumn, final boolean foreignKey, final boolean isRefered) {
        super.setTargetColumn(targetColumn, foreignKey, isRefered);

        if (targetColumn != null) {
            autoIncrementSetting = targetColumn.getAutoIncrementSetting();

        } else {
            autoIncrementSetting = new Sequence();
        }
    }

}
