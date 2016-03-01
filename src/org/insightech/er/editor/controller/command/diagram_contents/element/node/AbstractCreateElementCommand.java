package org.insightech.er.editor.controller.command.diagram_contents.element.node;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;

public abstract class AbstractCreateElementCommand extends AbstractCommand {

    protected ERDiagram diagram;

    protected Category category;

    protected Location newCategoryLocation;

    protected Location oldCategoryLocation;

    public AbstractCreateElementCommand(final ERDiagram diagram) {
        this.diagram = diagram;
        category = this.diagram.getCurrentCategory();
        if (category != null) {
            oldCategoryLocation = category.getLocation();
        }
    }

    protected void addToCategory(final NodeElement nodeElement) {
        if (category != null) {
            category.add(nodeElement);
            final Location newLocation = category.getNewCategoryLocation(nodeElement);

            if (newLocation != null) {
                newCategoryLocation = newLocation;
                category.setLocation(newCategoryLocation);
            }
        }
    }

    protected void removeFromCategory(final NodeElement nodeElement) {
        if (category != null) {
            category.remove(nodeElement);

            if (newCategoryLocation != null) {
                category.setLocation(oldCategoryLocation);
            }
        }
    }

}
