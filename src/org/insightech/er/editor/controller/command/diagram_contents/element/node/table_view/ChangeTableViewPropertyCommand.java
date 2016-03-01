package org.insightech.er.editor.controller.command.diagram_contents.element.node.table_view;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;

public class ChangeTableViewPropertyCommand extends AbstractCommand {

    private final TableView oldCopyTableView;

    private final TableView tableView;

    private final TableView newCopyTableView;

    public ChangeTableViewPropertyCommand(final TableView tableView, final TableView newCopyTableView) {
        this.tableView = tableView;
        oldCopyTableView = tableView.copyData();
        this.newCopyTableView = newCopyTableView;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        newCopyTableView.restructureData(tableView);

        tableView.getDiagram().refreshVisuals();
        tableView.getDiagram().getDiagramContents().getIndexSet().refresh();

        for (final Relation relation : tableView.getIncomingRelations()) {
            relation.refreshVisuals();
        }

        tableView.getDiagram().getEditor().refreshPropertySheet();
        tableView.getDiagram().refreshCategories();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        oldCopyTableView.restructureData(tableView);

        tableView.getDiagram().refreshVisuals();
        tableView.getDiagram().getDiagramContents().getIndexSet().refresh();

        tableView.getDiagram().getEditor().refreshPropertySheet();

        tableView.getDiagram().refresh();
    }

}
