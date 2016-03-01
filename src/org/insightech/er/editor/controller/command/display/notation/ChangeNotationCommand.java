package org.insightech.er.editor.controller.command.display.notation;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.Settings;

public class ChangeNotationCommand extends AbstractCommand {

    private final ERDiagram diagram;

    private final String oldNotation;

    private final String newNotation;

    private final Settings settings;

    public ChangeNotationCommand(final ERDiagram diagram, final String notation) {
        this.diagram = diagram;
        settings = diagram.getDiagramContents().getSettings();
        newNotation = notation;
        oldNotation = settings.getNotation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        settings.setNotation(newNotation);
        diagram.refreshConnection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        settings.setNotation(oldNotation);
        diagram.refreshConnection();
    }
}
