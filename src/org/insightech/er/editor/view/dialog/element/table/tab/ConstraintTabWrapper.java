package org.insightech.er.editor.view.dialog.element.table.tab;

import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.common.dialog.ValidatableTabWrapper;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.view.dialog.element.table.TableDialog;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;

public class ConstraintTabWrapper extends ValidatableTabWrapper {

    private final ERTable copyData;

    private Text constraintText;

    private Text primaryKeyNameText;

    private Text optionText;

    public ConstraintTabWrapper(final TableDialog tableDialog, final TabFolder parent, final ERTable copyData) {
        super(tableDialog, parent, "label.constraint.and.option");

        this.copyData = copyData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validatePage() throws InputException {
        String text = constraintText.getText().trim();
        copyData.setConstraint(text);

        text = primaryKeyNameText.getText().trim();
        if (!Check.isAlphabet(text)) {
            throw new InputException("error.primary.key.name.not.alphabet");
        }
        copyData.setPrimaryKeyName(text);

        text = optionText.getText().trim();
        copyData.setOption(text);
    }

    @Override
    public void initComposite() {
        CompositeFactory.createLeftLabel(this, "label.table.constraint", 1);

        constraintText = CompositeFactory.createTextArea(dialog, this, null, -1, 100, 1, false);

        constraintText.setText(Format.null2blank(copyData.getConstraint()));

        CompositeFactory.fillLine(this);

        primaryKeyNameText = CompositeFactory.createText(dialog, this, "label.primary.key.name", 1, false, false);
        primaryKeyNameText.setText(Format.null2blank(copyData.getPrimaryKeyName()));

        CompositeFactory.fillLine(this);

        CompositeFactory.createLeftLabel(this, "label.option", 1);

        optionText = CompositeFactory.createTextArea(dialog, this, null, -1, 100, 1, false);

        optionText.setText(Format.null2blank(copyData.getOption()));
    }

    @Override
    public void setInitFocus() {
        constraintText.setFocus();
    }

    @Override
    public void perfomeOK() {}

}
