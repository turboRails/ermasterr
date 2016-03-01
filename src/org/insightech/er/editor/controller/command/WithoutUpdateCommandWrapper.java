package org.insightech.er.editor.controller.command;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ERDiagram;

public class WithoutUpdateCommandWrapper extends Command {

    private final CompoundCommand command;

    private final ERDiagram diagram;

    public WithoutUpdateCommandWrapper(final CompoundCommand command, final ERDiagram diagram) {
        this.command = command;
        this.diagram = diagram;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        AbstractModel.setUpdateable(false);

        diagram.getEditor().getActiveEditor().getGraphicalViewer().deselectAll();

        command.execute();

        AbstractModel.setUpdateable(true);

        diagram.refresh();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() {
        AbstractModel.setUpdateable(false);

        command.undo();

        AbstractModel.setUpdateable(true);

        diagram.refresh();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canExecute() {
        return command.canExecute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canUndo() {
        return command.canUndo();
    }

}
