package org.insightech.er.db.impl.postgres;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.insightech.er.ResourceString;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.editor.view.dialog.element.table_view.tab.AdvancedComposite;

public class PostgresAdvancedComposite extends AdvancedComposite {

    private Button withoutOIDs;

    public PostgresAdvancedComposite(final Composite parent) {
        super(parent);
    }

    @Override
    protected void initComposite() {
        super.initComposite();

        final GridData gridData = new GridData();
        gridData.horizontalSpan = 2;

        withoutOIDs = new Button(this, SWT.CHECK);
        withoutOIDs.setText(ResourceString.getResourceString("label.without.oids"));
        withoutOIDs.setLayoutData(gridData);
    }

    @Override
    protected void setData() {
        super.setData();

        withoutOIDs.setSelection(((PostgresTableProperties) tableViewProperties).isWithoutOIDs());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validate() throws InputException {
        ((PostgresTableProperties) tableViewProperties).setWithoutOIDs(withoutOIDs.getSelection());

        return super.validate();
    }

}
