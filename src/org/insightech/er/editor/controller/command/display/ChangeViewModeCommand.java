package org.insightech.er.editor.controller.command.display;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.Settings;

public class ChangeViewModeCommand extends AbstractCommand {

    private final ERDiagram diagram;

    private final int oldViewMode;

    private final int newViewMode;

    private final Settings settings;

    public ChangeViewModeCommand(final ERDiagram diagram, final int viewMode) {
        this.diagram = diagram;
        settings = this.diagram.getDiagramContents().getSettings();
        newViewMode = viewMode;
        oldViewMode = settings.getViewMode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        settings.setViewMode(newViewMode);
        diagram.refreshVisuals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        settings.setViewMode(oldViewMode);
        diagram.refreshVisuals();
    }
}
