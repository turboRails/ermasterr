package org.insightech.er.editor.controller.command.diagram_contents.element.node.table_view;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;

public class ChangeColumnOrderCommand extends AbstractCommand {

    private final TableView tableView;

    private final Column column;

    private int newIndex;

    private final int oldIndex;

    public ChangeColumnOrderCommand(final TableView tableView, final Column column, final int index) {
        this.tableView = tableView;
        this.column = column;
        newIndex = index;
        oldIndex = this.tableView.getColumns().indexOf(column);

        if (oldIndex < newIndex) {
            newIndex--;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        tableView.removeColumn(column);
        tableView.addColumn(newIndex, column);

        tableView.refresh();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        tableView.removeColumn(column);
        tableView.addColumn(oldIndex, column);

        tableView.refresh();
    }

}
