package org.insightech.er.editor.view.tool;

import org.eclipse.gef.tools.ConnectionCreationTool;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.CreateRelationCommand;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;

public class RelationCreationTool extends ConnectionCreationTool {

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean handleCreateConnection() {
        final CreateRelationCommand command = (CreateRelationCommand) getCommand();

        if (command == null) {
            return false;
        }

        final TableView source = (TableView) command.getSourceModel();
        final TableView target = (TableView) command.getTargetModel();

        if (ERTable.isRecursive(source, target)) {
            ERDiagramActivator.showErrorDialog("error.recursive.relation");

            eraseSourceFeedback();

            return false;
        }

        return super.handleCreateConnection();
    }

}
