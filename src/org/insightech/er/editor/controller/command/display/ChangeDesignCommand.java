package org.insightech.er.editor.controller.command.display;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.Settings;

public class ChangeDesignCommand extends AbstractCommand {

    private final ERDiagram diagram;

    private final String oldDesign;

    private final String newDesign;

    private final Settings settings;

    public ChangeDesignCommand(final ERDiagram diagram, final String design) {
        this.diagram = diagram;
        settings = this.diagram.getDiagramContents().getSettings();
        newDesign = design;
        oldDesign = settings.getTableStyle();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        settings.setTableStyle(newDesign);
        diagram.refreshSettings();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        settings.setTableStyle(oldDesign);
        diagram.refreshSettings();
    }
}
