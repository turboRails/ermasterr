package org.insightech.er.editor.view.dialog.word.column.real;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.ResourceString;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.view.dialog.word.column.AbstractColumnDialog;

public abstract class AbstractRealColumnDialog extends AbstractColumnDialog {

    protected Button notNullCheck;

    protected Button uniqueKeyCheck;

    protected Combo defaultText;

    protected Text constraintText;

    private TabFolder tabFolder;

    protected TabItem tabItem;

    public AbstractRealColumnDialog(final Shell parentShell, final ERDiagram diagram) {
        super(parentShell, diagram);
    }

    @Override
    protected Composite createRootComposite(final Composite parent) {
        tabFolder = new TabFolder(parent, SWT.NONE);

        tabItem = new TabItem(tabFolder, SWT.NONE);
        tabItem.setText(ResourceString.getResourceString("label.basic"));

        final Composite composite = CompositeFactory.createComposite(tabFolder, getCompositeNumColumns(), true);
        tabItem.setControl(composite);

        tabItem = new TabItem(tabFolder, SWT.NONE);
        tabItem.setText(ResourceString.getResourceString("label.detail"));

        final Composite detailComposite = CompositeFactory.createComposite(tabFolder, 2, true);
        initializeDetailTab(detailComposite);
        tabItem.setControl(detailComposite);

        return composite;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initializeComposite(final Composite composite) {
        final int numColumns = getCompositeNumColumns();

        final Composite checkBoxComposite = CompositeFactory.createChildComposite(composite, 40, numColumns, getCheckBoxCompositeNumColumns());

        initializeCheckBoxComposite(checkBoxComposite);

        super.initializeComposite(composite);

        defaultText = CompositeFactory.createCombo(this, composite, "label.column.default.value", numColumns - 1);
    }

    protected int getCheckBoxCompositeNumColumns() {
        return 2;
    }

    protected void initializeDetailTab(final Composite composite) {
        constraintText = CompositeFactory.createText(this, composite, "label.column.constraint", false, true);
    }

    protected void initializeCheckBoxComposite(final Composite composite) {
        notNullCheck = CompositeFactory.createCheckbox(this, composite, "label.not.null", false);
        uniqueKeyCheck = CompositeFactory.createCheckbox(this, composite, "label.unique.key", false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setWordData() {
        notNullCheck.setSelection(targetColumn.isNotNull());
        uniqueKeyCheck.setSelection(targetColumn.isUniqueKey());

        if (targetColumn.getConstraint() != null) {
            constraintText.setText(targetColumn.getConstraint());
        }

        if (targetColumn.getDefaultValue() != null) {
            defaultText.setText(targetColumn.getDefaultValue());
        }

        super.setWordData();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void perfomeOK() {
        super.perfomeOK();

        returnColumn = new NormalColumn(returnWord, notNullCheck.getSelection(), false, uniqueKeyCheck.getSelection(), false, defaultText.getText(), constraintText.getText(), null, null, null);
    }

    @Override
    protected void setEnabledBySqlType() {
        super.setEnabledBySqlType();

        final SqlType selectedType = SqlType.valueOf(diagram.getDatabase(), typeCombo.getText());

        if (selectedType != null) {
            final String defaultValue = defaultText.getText();
            defaultText.removeAll();

            if (selectedType.isTimestamp()) {
                defaultText.add(ResourceString.getResourceString(ResourceString.KEY_DEFAULT_VALUE_CURRENT_DATE_TIME));
                defaultText.setText(defaultValue);

            } else if (selectedType.isFullTextIndexable()) {
                defaultText.add(ResourceString.getResourceString(ResourceString.KEY_DEFAULT_VALUE_EMPTY_STRING));
                defaultText.setText(defaultValue);

            } else {
                if (!ResourceString.getResourceString("label.current.date.time").equals(defaultValue) && !ResourceString.getResourceString("label.empty.string").equals(defaultValue)) {
                    defaultText.setText(defaultValue);
                }
            }
        }
    }

}
