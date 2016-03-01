package org.insightech.er.db.impl.mysql.tablespace;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.db.impl.mysql.MySQLAdvancedComposite;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;
import org.insightech.er.editor.view.dialog.outline.tablespace.TablespaceDialog;
import org.insightech.er.util.Format;

public class MySQLTablespaceDialog extends TablespaceDialog {

    private Text dataFile;

    private Text logFileGroup;

    private Text extentSize;

    private Text initialSize;

    private Combo engine;

    @Override
    protected void initialize(final Composite composite) {
        super.initialize(composite);

        dataFile = CompositeFactory.createText(this, composite, "label.tablespace.data.file", false, false);
        CompositeFactory.filler(composite, 1);
        CompositeFactory.createExampleLabel(composite, "label.tablespace.data.file.example");

        logFileGroup = CompositeFactory.createText(this, composite, "label.tablespace.log.file.group", false, false);
        extentSize = CompositeFactory.createText(this, composite, "label.tablespace.extent.size", 1, NUM_TEXT_WIDTH, false, false);
        CompositeFactory.filler(composite, 1);
        CompositeFactory.createExampleLabel(composite, "label.tablespace.size.example");
        initialSize = CompositeFactory.createText(this, composite, "label.tablespace.initial.size", 1, NUM_TEXT_WIDTH, false, false);
        CompositeFactory.filler(composite, 1);
        CompositeFactory.createExampleLabel(composite, "label.tablespace.size.example");
        engine = MySQLAdvancedComposite.createEngineCombo(composite, this);
    }

    @Override
    protected TablespaceProperties setTablespaceProperties() {
        final MySQLTablespaceProperties properties = new MySQLTablespaceProperties();

        properties.setDataFile(dataFile.getText().trim());
        properties.setLogFileGroup(logFileGroup.getText().trim());
        properties.setExtentSize(extentSize.getText().trim());
        properties.setInitialSize(initialSize.getText().trim());
        properties.setEngine(engine.getText().trim());

        return properties;
    }

    @Override
    protected void setData(final TablespaceProperties tablespaceProperties) {
        if (tablespaceProperties instanceof MySQLTablespaceProperties) {
            final MySQLTablespaceProperties properties = (MySQLTablespaceProperties) tablespaceProperties;

            dataFile.setText(Format.toString(properties.getDataFile()));
            logFileGroup.setText(Format.toString(properties.getLogFileGroup()));
            extentSize.setText(Format.toString(properties.getExtentSize()));
            initialSize.setText(Format.toString(properties.getInitialSize()));
            engine.setText(Format.toString(properties.getEngine()));
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

        String text = dataFile.getText().trim();
        if (text.equals("")) {
            return "error.tablespace.data.file.empty";
        }

        text = logFileGroup.getText().trim();
        if (text.equals("")) {
            return "error.tablespace.log.file.group.empty";
        }

        text = initialSize.getText().trim();
        if (text.equals("")) {
            return "error.tablespace.initial.size.empty";
        }

        text = engine.getText().trim();
        if (text.equals("")) {
            return "error.tablespace.storage.engine.empty";
        }

        return null;
    }

}
