package org.insightech.er.editor.controller.command.diagram_contents.element.node;

import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;

public class CreateElementCommand extends AbstractCreateElementCommand {

    private final NodeElement element;

    private final List<NodeElement> enclosedElementList;

    public CreateElementCommand(final ERDiagram diagram, final NodeElement element, final int x, final int y, final Dimension size, final List<NodeElement> enclosedElementList) {
        super(diagram);

        this.element = element;

        if (this.element instanceof Category && size != null) {
            this.element.setLocation(new Location(x, y, size.width, size.height));
        } else {
            this.element.setLocation(new Location(x, y, TableView.DEFAULT_WIDTH, TableView.DEFAULT_HEIGHT));
        }

        if (element instanceof ERTable) {
            final ERTable table = (ERTable) element;
            table.setLogicalName(ERTable.NEW_LOGICAL_NAME);
            table.setPhysicalName(ERTable.NEW_PHYSICAL_NAME);

        } else if (element instanceof View) {
            final View view = (View) element;
            view.setLogicalName(View.NEW_LOGICAL_NAME);
            view.setPhysicalName(View.NEW_PHYSICAL_NAME);
        }

        this.enclosedElementList = enclosedElementList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        if (element instanceof Category) {
            final Category category = (Category) element;
            category.setName(ResourceString.getResourceString("label.category"));
            category.setContents(enclosedElementList);

            diagram.addCategory(category);

        } else {
            diagram.addNewContent(element);
            addToCategory(element);

        }

        diagram.refreshChildren();
        if (category != null) {
            category.refresh();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        if (element instanceof Category) {
            final Category category = (Category) element;
            category.getContents().clear();
            diagram.removeCategory(category);

        } else {
            diagram.removeContent(element);
            removeFromCategory(element);

        }

        diagram.refreshChildren();

        if (category != null) {
            category.refresh();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canExecute() {
        if (element instanceof Category) {
            if (diagram.getCurrentCategory() != null) {
                return false;
            }
        }

        return super.canExecute();
    }

}
