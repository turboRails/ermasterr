package org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation;

import org.eclipse.gef.EditPart;
import org.insightech.er.editor.model.diagram_contents.element.connection.Bendpoint;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;

public class CreateSelfRelationCommand extends AbstractCreateRelationCommand {

    private final Relation relation;

    public CreateSelfRelationCommand(final Relation relation) {
        super();
        this.relation = relation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSource(final EditPart source) {
        this.source = source;
        target = source;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        // ERDiagramEditPart.setUpdateable(false);

        boolean anotherSelfRelation = false;

        final ERTable sourceTable = (ERTable) source.getModel();

        for (final Relation otherRelation : sourceTable.getOutgoingRelations()) {
            if (otherRelation.getSource() == otherRelation.getTarget()) {
                anotherSelfRelation = true;
                break;
            }
        }

        int rate = 0;

        if (anotherSelfRelation) {
            rate = 50;

        } else {
            rate = 100;
        }

        final Bendpoint bendpoint0 = new Bendpoint(rate, rate);
        bendpoint0.setRelative(true);

        final int xp = 100 - (rate / 2);
        final int yp = 100 - (rate / 2);

        relation.setSourceLocationp(100, yp);
        relation.setTargetLocationp(xp, 100);

        relation.addBendpoint(0, bendpoint0);

        relation.setSource(sourceTable);

        // ERDiagramEditPart.setUpdateable(true);

        relation.setTargetTableView((ERTable) target.getModel());

        // sourceTable.setDirty();

        getTargetModel().refresh();
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

        relation.removeBendpoint(0);

        // ERTable targetTable = (ERTable) this.target.getModel();
        // targetTable.setDirty();

        getTargetModel().refresh();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canExecute() {
        return source != null && target != null;
    }

}
