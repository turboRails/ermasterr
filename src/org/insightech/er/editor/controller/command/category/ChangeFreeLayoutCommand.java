package org.insightech.er.editor.controller.command.category;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.CategorySetting;

public class ChangeFreeLayoutCommand extends AbstractCommand {

    private final ERDiagram diagram;

    private final boolean oldFreeLayout;

    private final boolean newFreeLayout;

    private final CategorySetting categorySettings;

    public ChangeFreeLayoutCommand(final ERDiagram diagram, final boolean isFreeLayout) {
        this.diagram = diagram;
        categorySettings = this.diagram.getDiagramContents().getSettings().getCategorySetting();

        newFreeLayout = isFreeLayout;
        oldFreeLayout = categorySettings.isFreeLayout();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        categorySettings.setFreeLayout(newFreeLayout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        categorySettings.setFreeLayout(oldFreeLayout);
    }
}
