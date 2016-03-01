package org.insightech.er.editor.view.dialog.element.table_view.tab;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableViewProperties;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;

public class AdvancedComposite extends Composite {

    private Combo tableSpaceCombo;

    private Text schemaText;

    protected TableViewProperties tableViewProperties;

    protected ERDiagram diagram;

    protected AbstractDialog dialog;

    protected ERTable table;

    public AdvancedComposite(final Composite parent) {
        super(parent, SWT.NONE);

        final GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;

        setLayoutData(gridData);
    }

    public final void initialize(final AbstractDialog dialog, final TableViewProperties tableViewProperties, final ERDiagram diagram, final ERTable table) {
        this.tableViewProperties = tableViewProperties;
        this.diagram = diagram;
        this.dialog = dialog;
        this.table = table;

        initComposite();
        this.addListener();
        this.setData();
    }

    protected void initComposite() {
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;

        setLayout(gridLayout);

        tableSpaceCombo = CompositeFactory.createReadOnlyCombo(null, this, "label.tablespace");
        schemaText = CompositeFactory.createText(null, this, "label.schema", 1, 120, false, true);

        initTablespaceCombo();
    }

    private void initTablespaceCombo() {
        tableSpaceCombo.add("");

        for (final Tablespace tablespace : diagram.getDiagramContents().getTablespaceSet()) {
            tableSpaceCombo.add(tablespace.getName());
        }
    }

    protected void addListener() {}

    protected void setData() {
        final Tablespace tablespace = tableViewProperties.getTableSpace();

        if (tablespace != null) {
            final int index = diagram.getDiagramContents().getTablespaceSet().getObjectList().indexOf(tablespace);
            tableSpaceCombo.select(index + 1);
        }

        if (tableViewProperties.getSchema() != null && schemaText != null) {
            schemaText.setText(tableViewProperties.getSchema());
        }
    }

    public boolean validate() throws InputException {
        if (tableSpaceCombo != null) {
            final int tablespaceIndex = tableSpaceCombo.getSelectionIndex();
            if (tablespaceIndex > 0) {
                final Tablespace tablespace = diagram.getDiagramContents().getTablespaceSet().getObjectList().get(tablespaceIndex - 1);
                tableViewProperties.setTableSpace(tablespace);

            } else {
                tableViewProperties.setTableSpace(null);
            }
        }

        if (schemaText != null) {
            tableViewProperties.setSchema(schemaText.getText());
        }

        return true;
    }

    public void setInitFocus() {
        tableSpaceCombo.setFocus();
    }
}
