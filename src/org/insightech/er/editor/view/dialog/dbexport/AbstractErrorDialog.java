package org.insightech.er.editor.view.dialog.dbexport;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.ResourceString;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.util.Format;

public abstract class AbstractErrorDialog extends Dialog {

    protected Text textArea;

    public AbstractErrorDialog(final Shell parentShell) {
        super(parentShell);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Control createDialogArea(final Composite parent) {
        getShell().setText(ResourceString.getResourceString(getTitle()));

        final Composite composite = (Composite) super.createDialogArea(parent);

        final GridLayout layout = new GridLayout();
        initLayout(layout);
        composite.setLayout(layout);

        textArea = CompositeFactory.createTextArea(null, composite, getMessage(), 400, 200, 1, false, false);

        textArea.setText(Format.null2blank(getData()));

        return composite;
    }

    protected void initLayout(final GridLayout layout) {
        layout.numColumns = 1;
        layout.marginLeft = 20;
        layout.marginRight = 20;
        layout.marginBottom = 20;
        layout.marginTop = 10;
        layout.verticalSpacing = 15;
    }

    protected abstract String getData();

    protected String getMessage() {
        return "dialog.message.export.ddl.error";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createButtonsForButtonBar(final Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void buttonPressed(final int buttonId) {
        setReturnCode(buttonId);
        close();

        super.buttonPressed(buttonId);
    }

    protected abstract String getTitle();
}
