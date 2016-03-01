package org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation;

import org.insightech.er.ResourceString;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.AbstractCreateConnectionCommand;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;

public abstract class AbstractCreateRelationCommand extends AbstractCreateConnectionCommand {

    /**
     * {@inheritDoc}
     */
    @Override
    public String validate() {
        final ERTable sourceTable = (ERTable) getSourceModel();

        if (!sourceTable.isReferable()) {
            return ResourceString.getResourceString("error.no.referenceable.column");
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canExecute() {
        if (!super.canExecute()) {
            return false;
        }

        if (!(getSourceModel() instanceof ERTable) || !(getTargetModel() instanceof TableView)) {
            return false;
        }

        return true;
    }

}
