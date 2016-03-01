package org.insightech.er.editor.model.diagram_contents.element.node.category;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.controller.editpart.element.node.IResizable;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.model.settings.CategorySetting;
import org.insightech.er.util.Format;

public class Category extends NodeElement implements IResizable, Comparable<Category> {

    private static final long serialVersionUID = -7691417386790834828L;

    private static int MARGIN = 30;

    private List<NodeElement> nodeElementList;

    private String name;

    public Category() {
        nodeElementList = new ArrayList<NodeElement>();
    }

    public void setContents(final List<NodeElement> contetns) {
        nodeElementList = contetns;

        if (getWidth() == 0) {

            int categoryX = 0;
            int categoryY = 0;

            int categoryWidth = 300;
            int categoryHeight = 400;

            if (!nodeElementList.isEmpty()) {
                categoryX = nodeElementList.get(0).getX();
                categoryY = nodeElementList.get(0).getY();
                categoryWidth = nodeElementList.get(0).getWidth();
                categoryHeight = nodeElementList.get(0).getHeight();

                for (final NodeElement nodeElement : nodeElementList) {
                    final int x = nodeElement.getX();
                    final int y = nodeElement.getY();
                    int width = nodeElement.getWidth();
                    int height = nodeElement.getHeight();

                    if (categoryX > x - MARGIN) {
                        width += categoryX - x + MARGIN;
                        categoryX = x - MARGIN;
                    }
                    if (categoryY > y - MARGIN) {
                        height += categoryY - y + MARGIN;
                        categoryY = y - MARGIN;
                    }

                    if (x - categoryX + width + MARGIN > categoryWidth) {
                        categoryWidth = x - categoryX + width + MARGIN;
                    }

                    if (y - categoryY + height + MARGIN > categoryHeight) {
                        categoryHeight = y - categoryY + height + MARGIN;
                    }

                }
            }

            setLocation(new Location(categoryX, categoryY, categoryWidth, categoryHeight));
        }
    }

    public boolean contains(final NodeElement nodeElement) {
        return nodeElementList.contains(nodeElement);
    }

    public boolean isVisible(final NodeElement nodeElement, final ERDiagram diagram) {
        boolean isVisible = false;

        if (contains(nodeElement)) {
            isVisible = true;

        } else {
            final CategorySetting categorySettings = diagram.getDiagramContents().getSettings().getCategorySetting();

            if (categorySettings.isShowReferredTables()) {
                for (final NodeElement referringElement : nodeElement.getReferringElementList()) {
                    if (contains(referringElement)) {
                        isVisible = true;
                        break;
                    }
                }
            }
        }

        return isVisible;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<NodeElement> getContents() {
        return nodeElementList;
    }

    public void add(final NodeElement nodeElement) {
        if (!(nodeElement instanceof Category)) {
            nodeElementList.add(nodeElement);
        }
    }

    public void remove(final NodeElement nodeElement) {
        nodeElementList.remove(nodeElement);
    }

    public List<ERTable> getTableContents() {
        final List<ERTable> tableList = new ArrayList<ERTable>();

        for (final NodeElement nodeElement : nodeElementList) {
            if (nodeElement instanceof ERTable) {
                tableList.add((ERTable) nodeElement);
            }
        }

        return tableList;
    }

    public List<View> getViewContents() {
        final List<View> viewList = new ArrayList<View>();

        for (final NodeElement nodeElement : nodeElementList) {
            if (nodeElement instanceof View) {
                viewList.add((View) nodeElement);
            }
        }

        return viewList;
    }

    public List<TableView> getTableViewContents() {
        final List<TableView> tableList = new ArrayList<TableView>();

        for (final NodeElement nodeElement : nodeElementList) {
            if (nodeElement instanceof TableView) {
                tableList.add((TableView) nodeElement);
            }
        }

        return tableList;
    }

    public Location getNewCategoryLocation(final NodeElement element) {
        if (element instanceof Category) {
            return null;
        }

        if (contains(element)) {
            final Location elementLocation = element.getLocation();
            final Location newLocation = calculateCategoryLocation(elementLocation);

            if (!newLocation.equals(getLocation())) {
                return newLocation;
            }
        }

        return null;
    }

    public Location getNewCategoryLocation(final Location elementLocation) {
        final Location newLocation = calculateCategoryLocation(elementLocation);

        if (!newLocation.equals(getLocation())) {
            return newLocation;
        }

        return null;
    }

    private Location calculateCategoryLocation(final Location elementLocation) {
        final Location location = getLocation();

        if (elementLocation.x < location.x) {
            location.width += location.x - elementLocation.x;
            location.x = elementLocation.x;
        }
        if (elementLocation.y < location.y) {
            location.height += location.y - elementLocation.y;
            location.y = elementLocation.y;
        }
        if (elementLocation.x + elementLocation.width > location.x + location.width) {
            location.width = elementLocation.x + elementLocation.width - location.x;
        }
        if (elementLocation.y + elementLocation.height > location.y + location.height) {
            location.height = elementLocation.y + elementLocation.height - location.y;
        }

        return location;
    }

    @Override
    public int compareTo(final Category other) {
        int compareTo = 0;

        compareTo = Format.null2blank(name).compareTo(Format.null2blank(other.name));

        return compareTo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Category clone() {
        final Category clone = (Category) super.clone();

        return clone;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getObjectType() {
        return "category";
    }

}
