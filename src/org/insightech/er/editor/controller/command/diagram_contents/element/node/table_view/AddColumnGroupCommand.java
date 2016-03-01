package org.insightech.er.editor.controller.command.diagram_contents.element.node.table_view;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;

public class AddColumnGroupCommand extends AbstractCommand {

    private final TableView tableView;

    private final ColumnGroup columnGroup;

    private final int index;

    public AddColumnGroupCommand(final TableView tableView, final ColumnGroup columnGroup, final int index) {
        this.tableView = tableView;
        this.columnGroup = columnGroup;
        this.index = index;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        if (index != -1) {
            tableView.addColumn(index, columnGroup);
        }

        tableView.refresh();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        tableView.removeColumn(columnGroup);

        tableView.refresh();
    }

}
