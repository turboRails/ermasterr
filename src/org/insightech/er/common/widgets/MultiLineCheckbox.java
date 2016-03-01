package org.insightech.er.common.widgets;

import java.awt.Component;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.insightech.er.ResourceString;
import org.insightech.er.Resources;
import org.insightech.er.common.dialog.AbstractDialog;

public class MultiLineCheckbox extends Component {

    private static final long serialVersionUID = 1L;

    private final Button checkboxButton;

    private final Label label;

    public MultiLineCheckbox(final AbstractDialog dialog, final Composite parent, final String title, final boolean indent, final int span) {
        super();

        final Composite box = new Composite(parent, SWT.NONE);

        final GridData boxGridData = new GridData(SWT.FILL, SWT.LEFT, true, false, span, 1);
        if (indent) {
            boxGridData.horizontalIndent = Resources.INDENT;
        }
        box.setLayoutData(boxGridData);

        final GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = 0;
        box.setLayout(layout);

        checkboxButton = new Button(box, SWT.CHECK);
        final GridData checkboxGridData = new GridData();
        checkboxGridData.verticalAlignment = SWT.TOP;
        checkboxButton.setLayoutData(checkboxGridData);

        label = new Label(box, SWT.NONE);
        final GridData labelGridData = new GridData();
        labelGridData.horizontalIndent = Resources.CHECKBOX_INDENT;

        label.setLayoutData(labelGridData);
        label.setText(ResourceString.getResourceString(title));

        ListenerAppender.addCheckBoxListener(checkboxButton, dialog);

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(final MouseEvent e) {
                checkboxButton.setSelection(!checkboxButton.getSelection());
                dialog.validate();
            }
        });
    }

    @Override
    public void setEnabled(final boolean enabled) {
        checkboxButton.setEnabled(enabled);
        label.setEnabled(enabled);
    }

    public boolean getSelection() {
        return checkboxButton.getSelection();
    }

    public void setSelection(final boolean selected) {
        checkboxButton.setSelection(selected);
    }
}
