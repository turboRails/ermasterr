package org.insightech.er.editor.view.action.edit;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.ui.actions.DeleteAction;
import org.eclipse.ui.IWorkbenchPart;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.controller.command.WithoutUpdateCommandWrapper;
import org.insightech.er.editor.model.ERDiagram;

public class DeleteWithoutUpdateAction extends DeleteAction {

    private final ERDiagramEditor editor;

    public DeleteWithoutUpdateAction(final ERDiagramEditor editor) {
        super((IWorkbenchPart) editor);
        this.editor = editor;
        setText(ResourceString.getResourceString("action.title.delete"));
        setToolTipText(ResourceString.getResourceString("action.title.delete"));

        setActionDefinitionId("org.eclipse.ui.edit.delete");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Command createDeleteCommand(final List objects) {
        final Command command = super.createDeleteCommand(objects);

        if (command == null) {
            return null;
        }

        if (command instanceof CompoundCommand) {
            final CompoundCommand compoundCommand = (CompoundCommand) command;
            if (compoundCommand.getCommands().isEmpty()) {
                return null;
            }

            if (compoundCommand.getCommands().size() == 1) {
                return compoundCommand;
            }

            final EditPart editPart = editor.getGraphicalViewer().getContents();
            final ERDiagram diagram = (ERDiagram) editPart.getModel();

            return new WithoutUpdateCommandWrapper(compoundCommand, diagram);
        }

        return command;
    }

    @Override
    protected boolean calculateEnabled() {
        final Command cmd = createDeleteCommand(getSelectedObjects());
        if (cmd == null) {
            return false;
        }

        return true;
    }

}
