package org.insightech.er.editor.view.dialog.element.table_view.tab;

import org.eclipse.swt.widgets.TabFolder;
import org.insightech.er.common.dialog.AbstractTabbedDialog;
import org.insightech.er.common.dialog.ValidatableTabWrapper;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;

public class AdvancedTabWrapper extends ValidatableTabWrapper {

    protected TableView tableView;

    private AdvancedComposite composite;

    public AdvancedTabWrapper(final AbstractTabbedDialog dialog, final TabFolder parent, final TableView tableView) {
        super(dialog, parent, "label.advanced.settings");

        this.tableView = tableView;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validatePage() throws InputException {
        composite.validate();
    }

    protected AdvancedComposite createAdvancedComposite() {
        return new AdvancedComposite(this);
    }

    @Override
    public void initComposite() {
        composite = createAdvancedComposite();

        ERTable table = null;

        if (tableView instanceof ERTable) {
            table = (ERTable) tableView;
        }

        composite.initialize(dialog, tableView.getTableViewProperties(), tableView.getDiagram(), table);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInitFocus() {
        composite.setInitFocus();
    }

    @Override
    public void perfomeOK() {}
}
