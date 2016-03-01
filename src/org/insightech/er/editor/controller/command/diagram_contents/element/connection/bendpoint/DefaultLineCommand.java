package org.insightech.er.editor.controller.command.diagram_contents.element.connection.bendpoint;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.Bendpoint;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;

public class DefaultLineCommand extends AbstractCommand {

    private int sourceXp;

    private int sourceYp;

    private int targetXp;

    private int targetYp;

    private final ConnectionElement connection;

    private final List<Bendpoint> oldBendpointList;

    public DefaultLineCommand(final ERDiagram diagram, final ConnectionElement connection) {
        if (connection instanceof Relation) {
            final Relation relation = (Relation) connection;

            sourceXp = relation.getSourceXp();
            sourceYp = relation.getSourceYp();
            targetXp = relation.getTargetXp();
            targetYp = relation.getTargetYp();
        }

        this.connection = connection;
        oldBendpointList = this.connection.getBendpoints();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        connection.setBendpoints(new ArrayList<Bendpoint>());
        if (connection instanceof Relation) {
            final Relation relation = (Relation) connection;

            relation.setSourceLocationp(-1, -1);
            relation.setTargetLocationp(-1, -1);
            // relation.setParentMove();
        }

        connection.refreshBendpoint();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        connection.setBendpoints(oldBendpointList);
        if (connection instanceof Relation) {
            final Relation relation = (Relation) connection;

            relation.setSourceLocationp(sourceXp, sourceYp);
            relation.setTargetLocationp(targetXp, targetYp);
            // relation.setParentMove();
        }

        connection.refreshBendpoint();
    }
}
