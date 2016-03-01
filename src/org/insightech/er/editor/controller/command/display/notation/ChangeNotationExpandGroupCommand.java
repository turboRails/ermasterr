package org.insightech.er.editor.controller.command.display.notation;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.Settings;

public class ChangeNotationExpandGroupCommand extends AbstractCommand {

    private final ERDiagram diagram;

    private final boolean oldNotationExpandGroup;

    private final boolean newNotationExpandGroup;

    private final Settings settings;

    public ChangeNotationExpandGroupCommand(final ERDiagram diagram, final boolean notationExpandGroup) {
        this.diagram = diagram;
        settings = this.diagram.getDiagramContents().getSettings();
        newNotationExpandGroup = notationExpandGroup;
        oldNotationExpandGroup = settings.isNotationExpandGroup();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        settings.setNotationExpandGroup(newNotationExpandGroup);
        diagram.refreshVisuals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        settings.setNotationExpandGroup(oldNotationExpandGroup);
        diagram.refreshVisuals();
    }
}
