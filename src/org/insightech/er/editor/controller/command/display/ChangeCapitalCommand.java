package org.insightech.er.editor.controller.command.display;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.Settings;

public class ChangeCapitalCommand extends AbstractCommand {

    private final ERDiagram diagram;

    private final boolean oldCapital;

    private final boolean newCapital;

    private final Settings settings;

    public ChangeCapitalCommand(final ERDiagram diagram, final boolean isCapital) {
        this.diagram = diagram;
        settings = this.diagram.getDiagramContents().getSettings();
        newCapital = isCapital;
        oldCapital = settings.isCapital();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        settings.setCapital(newCapital);
        diagram.refreshVisuals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        settings.setCapital(oldCapital);
        diagram.refreshVisuals();
    }
}
