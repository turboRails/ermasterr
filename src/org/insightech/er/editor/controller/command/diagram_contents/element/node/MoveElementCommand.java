package org.insightech.er.editor.controller.command.diagram_contents.element.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.geometry.Rectangle;
import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;

public class MoveElementCommand extends AbstractCommand {

    protected int x;

    protected int oldX;

    protected int y;

    protected int oldY;

    protected int width;

    protected int oldWidth;

    protected int height;

    protected int oldHeight;

    private final NodeElement element;

    private final Map<Category, Location> oldCategoryLocationMap;

    private final Map<Category, Location> newCategoryLocationMap;

    private final List<Category> removedCategories;

    private final List<Category> addCategories;

    protected ERDiagram diagram;

    private final Category currentCategory;

    private final Rectangle bounds;

    public MoveElementCommand(final ERDiagram diagram, final Rectangle bounds, final int x, final int y, final int width, final int height, final NodeElement element) {

        this.element = element;
        setNewRectangle(x, y, width, height);

        oldX = element.getX();
        oldY = element.getY();
        oldWidth = element.getWidth();
        oldHeight = element.getHeight();

        removedCategories = new ArrayList<Category>();
        addCategories = new ArrayList<Category>();

        this.bounds = bounds;
        this.diagram = diagram;
        currentCategory = diagram.getCurrentCategory();

        oldCategoryLocationMap = new HashMap<Category, Location>();
        newCategoryLocationMap = new HashMap<Category, Location>();
    }

    protected void setNewRectangle(final int x, final int y, final int width, final int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    private void initCategory(final ERDiagram diagram, final Location elementLocation) {
        for (final Category category : diagram.getDiagramContents().getSettings().getCategorySetting().getSelectedCategories()) {
            if (category.contains(element)) {
                if (currentCategory == null) {
                    if (elementLocation.x + elementLocation.width < category.getX() || elementLocation.x > category.getX() + category.getWidth() || elementLocation.y + elementLocation.height < category.getY() || elementLocation.y > category.getY() + category.getHeight()) {

                        removedCategories.add(category);

                        continue;
                    }
                }

                final Location newCategoryLocation = category.getNewCategoryLocation(elementLocation);

                if (newCategoryLocation != null) {
                    newCategoryLocationMap.put(category, newCategoryLocation);
                    oldCategoryLocationMap.put(category, category.getLocation());
                }

            } else {
                if (diagram.getCurrentCategory() == null) {
                    if (elementLocation.x >= category.getX() && elementLocation.x + elementLocation.width <= category.getX() + category.getWidth() && elementLocation.y >= category.getY() && elementLocation.y + bounds.height <= category.getY() + category.getHeight()) {
                        addCategories.add(category);
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        if (bounds != null) {
            final Location elementLocation = new Location(x, y, bounds.width, bounds.height);

            if (elementLocation.width < width) {
                elementLocation.width = width;
            }
            if (elementLocation.height < height) {
                elementLocation.height = height;
            }

            initCategory(diagram, elementLocation);
        }

        for (final Category category : newCategoryLocationMap.keySet()) {
            category.setLocation(newCategoryLocationMap.get(category));
            category.refreshVisuals();
        }

        for (final Category category : removedCategories) {
            category.remove(element);
        }

        for (final Category category : addCategories) {
            category.add(element);
        }

        element.setLocation(new Location(x, y, width, height));
        element.refreshVisuals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        element.setLocation(new Location(oldX, oldY, oldWidth, oldHeight));
        element.refreshVisuals();

        for (final Category category : oldCategoryLocationMap.keySet()) {
            category.setLocation(oldCategoryLocationMap.get(category));
            category.refreshVisuals();
        }

        for (final Category category : removedCategories) {
            category.add(element);
        }

        for (final Category category : addCategories) {
            category.remove(element);
        }
    }
}
