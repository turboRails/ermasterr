package org.insightech.er.db.impl.postgres.tablespace;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;
import org.insightech.er.editor.view.dialog.outline.tablespace.TablespaceDialog;
import org.insightech.er.util.Format;

public class PostgresTablespaceDialog extends TablespaceDialog {

    private Text location;

    private Text owner;

    @Override
    protected void initialize(final Composite composite) {
        super.initialize(composite);

        location = CompositeFactory.createText(this, composite, "label.tablespace.location", false, false);
        CompositeFactory.filler(composite, 1);
        CompositeFactory.createExampleLabel(composite, "label.tablespace.data.file.example");
        owner = CompositeFactory.createText(this, composite, "label.tablespace.owner", false, false);
    }

    @Override
    protected TablespaceProperties setTablespaceProperties() {
        final PostgresTablespaceProperties properties = new PostgresTablespaceProperties();

        properties.setLocation(location.getText().trim());
        properties.setOwner(owner.getText().trim());

        return properties;
    }

    @Override
    protected void setData(final TablespaceProperties tablespaceProperties) {
        if (tablespaceProperties instanceof PostgresTablespaceProperties) {
            final PostgresTablespaceProperties properties = (PostgresTablespaceProperties) tablespaceProperties;

            location.setText(Format.toString(properties.getLocation()));
            owner.setText(Format.toString(properties.getOwner()));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getErrorMessage() {
        final String errorMessage = super.getErrorMessage();
        if (errorMessage != null) {
            return errorMessage;
        }

        final String text = location.getText().trim();
        if (text.equals("")) {
            return "error.tablespace.location.empty";
        }

        return null;
    }
}
