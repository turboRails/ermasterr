package org.insightech.er.editor.controller.command.display.notation;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.Settings;

public class ChangeNotationLevelCommand extends AbstractCommand {

    private final ERDiagram diagram;

    private final int oldNotationLevel;

    private final int newNotationLevel;

    private final Settings settings;

    public ChangeNotationLevelCommand(final ERDiagram diagram, final int notationLevel) {
        this.diagram = diagram;
        settings = diagram.getDiagramContents().getSettings();
        newNotationLevel = notationLevel;
        oldNotationLevel = settings.getNotationLevel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        settings.setNotationLevel(newNotationLevel);
        diagram.refreshVisuals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        settings.setNotationLevel(oldNotationLevel);
        diagram.refreshVisuals();
    }
}
