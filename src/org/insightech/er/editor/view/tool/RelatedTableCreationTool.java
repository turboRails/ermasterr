package org.insightech.er.editor.view.tool;

import org.eclipse.gef.tools.ConnectionCreationTool;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.CreateRelatedTableCommand;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;

public class RelatedTableCreationTool extends ConnectionCreationTool {

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean handleCreateConnection() {
        final CreateRelatedTableCommand command = (CreateRelatedTableCommand) getCommand();

        if (command != null) {
            final ERTable target = (ERTable) command.getTargetModel();

            if (!target.isReferable()) {
                ERDiagramActivator.showErrorDialog("error.no.referenceable.column");

                eraseSourceFeedback();

                return false;
            }
        }

        return super.handleCreateConnection();
    }

}
