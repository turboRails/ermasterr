package org.insightech.er.editor.controller.command.display.outline;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.Settings;

public class ChangeOutlineViewOrderByCommand extends AbstractCommand {

    private final ERDiagram diagram;

    private final int oldViewOrderBy;

    private final int newViewOrderBy;

    private final Settings settings;

    public ChangeOutlineViewOrderByCommand(final ERDiagram diagram, final int viewOrderBy) {
        this.diagram = diagram;
        settings = this.diagram.getDiagramContents().getSettings();
        newViewOrderBy = viewOrderBy;
        oldViewOrderBy = settings.getViewOrderBy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        settings.setViewOrderBy(newViewOrderBy);
        diagram.refreshOutline();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        settings.setViewOrderBy(oldViewOrderBy);
        diagram.refreshOutline();
    }
}
