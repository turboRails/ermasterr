package org.insightech.er.editor.controller.command.diagram_contents.element.connection.bendpoint;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.controller.editpart.element.connection.RelationEditPart;
import org.insightech.er.editor.model.diagram_contents.element.connection.Bendpoint;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;

public class MoveRelationBendpointCommand extends AbstractCommand {

    private final RelationEditPart editPart;

    private final Bendpoint bendPoint;

    private Bendpoint oldBendpoint;

    private final int index;

    public MoveRelationBendpointCommand(final RelationEditPart editPart, final int x, final int y, final int index) {
        this.editPart = editPart;
        bendPoint = new Bendpoint(x, y);
        this.index = index;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        final Relation relation = (Relation) editPart.getModel();
        final boolean relative = relation.getBendpoints().get(0).isRelative();

        if (relative) {
            oldBendpoint = relation.getBendpoints().get(0);

            bendPoint.setRelative(true);

            final float rateX = (100f - (bendPoint.getX() / 2)) / 100;
            final float rateY = (100f - (bendPoint.getY() / 2)) / 100;

            relation.setSourceLocationp(100, (int) (100 * rateY));
            relation.setTargetLocationp((int) (100 * rateX), 100);

            // relation.setParentMove();

            relation.replaceBendpoint(0, bendPoint);

        } else {
            oldBendpoint = relation.getBendpoints().get(index);
            relation.replaceBendpoint(index, bendPoint);
        }

        if (relation.isSelfRelation()) {
            relation.getSource().refreshVisuals();

        } else {
            relation.refreshBendpoint();

        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        final Relation relation = (Relation) editPart.getModel();
        final boolean relative = relation.getBendpoints().get(0).isRelative();

        if (relative) {
            final float rateX = (100f - (oldBendpoint.getX() / 2)) / 100;
            final float rateY = (100f - (oldBendpoint.getY() / 2)) / 100;

            relation.setSourceLocationp(100, (int) (100 * rateY));
            relation.setTargetLocationp((int) (100 * rateX), 100);

            // relation.setParentMove();

            relation.replaceBendpoint(0, oldBendpoint);

        } else {
            relation.replaceBendpoint(index, oldBendpoint);
        }

        relation.refreshBendpoint();

        if (relation.isSelfRelation()) {
            relation.getSource().refreshVisuals();
        }

    }

}
