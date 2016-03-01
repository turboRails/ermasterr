package org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation;

import java.util.List;

import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;

public class CreateRelationCommand extends AbstractCreateRelationCommand {

    private final Relation relation;

    private final List<NormalColumn> foreignKeyColumnList;

    public CreateRelationCommand(final Relation relation) {
        this(relation, null);
    }

    public CreateRelationCommand(final Relation relation, final List<NormalColumn> foreignKeyColumnList) {
        super();
        this.relation = relation;
        this.foreignKeyColumnList = foreignKeyColumnList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        // ERDiagramEditPart.setUpdateable(false);

        relation.setSource((TableView) source.getModel());

        // ERDiagramEditPart.setUpdateable(true);

        relation.setTargetTableView((TableView) target.getModel(), foreignKeyColumnList);

        getTargetModel().refresh();
        getSourceModel().refreshSourceConnections();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        // ERDiagramEditPart.setUpdateable(false);

        relation.setSource(null);

        // ERDiagramEditPart.setUpdateable(true);

        relation.setTargetTableView(null);

        getTargetModel().refresh();
        getSourceModel().refreshSourceConnections();
    }
}
