package org.insightech.er.editor.controller.editpolicy.element.node;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.DeleteConnectionCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.DeleteRelationCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.DeleteElementCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.category.DeleteCategoryCommand;
import org.insightech.er.editor.controller.editpart.DeleteableEditPart;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;

public class NodeElementComponentEditPolicy extends ComponentEditPolicy {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Command createDeleteCommand(final GroupRequest request) {
        try {
            if (getHost() instanceof DeleteableEditPart) {
                final DeleteableEditPart editPart = (DeleteableEditPart) getHost();

                if (!editPart.isDeleteable()) {
                    return null;
                }

            } else {
                return null;
            }

            final Set<NodeElement> targets = new HashSet<NodeElement>();

            for (final Object object : request.getEditParts()) {
                final EditPart editPart = (EditPart) object;

                final Object model = editPart.getModel();

                if (model instanceof NodeElement) {
                    targets.add((NodeElement) model);
                }
            }

            final ERDiagram diagram = (ERDiagram) getHost().getRoot().getContents().getModel();
            final NodeElement element = (NodeElement) getHost().getModel();

            if (element instanceof Category) {
                return new DeleteCategoryCommand(diagram, (Category) element);
            }

            if (!diagram.getDiagramContents().getContents().contains(element) && !(element instanceof Category)) {
                return null;
            }

            final CompoundCommand command = new CompoundCommand();

            for (final ConnectionElement connection : element.getIncomings()) {
                if (connection instanceof Relation) {
                    command.add(new DeleteRelationCommand((Relation) connection, true));

                } else {
                    command.add(new DeleteConnectionCommand(connection));
                }
            }

            for (final ConnectionElement connection : element.getOutgoings()) {

                final NodeElement target = connection.getTarget();

                if (!targets.contains(target)) {
                    if (connection instanceof Relation) {
                        command.add(new DeleteRelationCommand((Relation) connection, true));
                    } else {
                        command.add(new DeleteConnectionCommand(connection));
                    }
                }
            }

            command.add(new DeleteElementCommand(diagram, element));

            return command.unwrap();

        } catch (final Exception e) {
            ERDiagramActivator.showExceptionDialog(e);
        }

        return null;
    }

}
