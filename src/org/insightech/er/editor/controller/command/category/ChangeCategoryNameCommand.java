package org.insightech.er.editor.controller.command.category;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;

public class ChangeCategoryNameCommand extends AbstractCommand {

    private final ERDiagram diagram;

    private final String oldName;

    private final String newName;

    private final Category category;

    public ChangeCategoryNameCommand(final ERDiagram diagram, final Category category, final String newName) {
        this.diagram = diagram;
        this.category = category;
        this.newName = newName;

        oldName = category.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        category.setName(newName);
        diagram.setCurrentCategoryPageName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        category.setName(oldName);
        diagram.setCurrentCategoryPageName();
    }
}
