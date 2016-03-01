package org.insightech.er.editor.controller.command.diagram_contents.element.node;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;

public class DeleteElementCommand extends AbstractCommand {

    private final ERDiagram diagram;

    private final NodeElement element;

    private List<Category> categoryList;

    public DeleteElementCommand(final ERDiagram diagram, final NodeElement element) {
        this.diagram = diagram;
        this.element = element;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        diagram.removeContent(element);

        categoryList = new ArrayList<Category>();

        for (final Category category : diagram.getDiagramContents().getSettings().getCategorySetting().getAllCategories()) {
            if (category.contains(element)) {
                category.remove(element);
                categoryList.add(category);
            }
        }

        diagram.refreshChildren();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        for (final Category category : categoryList) {
            category.add(element);
        }

        diagram.addContent(element);
        diagram.refreshChildren();
    }
}
