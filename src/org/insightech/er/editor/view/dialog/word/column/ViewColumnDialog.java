package org.insightech.er.editor.view.dialog.word.column;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;

public class ViewColumnDialog extends AbstractColumnDialog {

    public ViewColumnDialog(final Shell parentShell, final View view) {
        super(parentShell, view.getDiagram());
    }

    protected int getStyle(int style) {
        if (foreignKey) {
            style |= SWT.READ_ONLY;
        }

        return style;
    }

    @Override
    protected String getTitle() {
        return "dialog.title.column";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initializeComposite(final Composite parent) {
        super.initializeComposite(parent);

        if (foreignKey) {
            wordCombo.setEnabled(false);
            typeCombo.setEnabled(false);
            lengthText.setEnabled(false);
            decimalText.setEnabled(false);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void perfomeOK() {
        super.perfomeOK();

        returnColumn = new NormalColumn(returnWord, false, false, false, false, null, null, null, null, null);
    }

}
