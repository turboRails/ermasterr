package org.insightech.er.editor.controller.command.diagram_contents.element.node.category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Rectangle;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.MoveElementCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.Bendpoint;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.model_properties.ModelProperties;

public class MoveCategoryCommand extends MoveElementCommand {

    private final boolean move;

    private final List<NodeElement> nodeElementList;

    private Map<NodeElement, Rectangle> nodeElementOldLocationMap;

    private final Category category;

    private int diffX;

    private int diffY;

    private Map<ConnectionElement, List<Bendpoint>> bendpointListMap;

    private final List<NodeElement> newlyAddedNodeElementList;

    public MoveCategoryCommand(final ERDiagram diagram, int x, int y, int width, int height, final Category category, final List<Category> otherCategories, final boolean move) {
        super(diagram, null, x, y, width, height, category);

        nodeElementList = new ArrayList<NodeElement>(category.getContents());
        this.category = category;
        this.move = move;
        newlyAddedNodeElementList = new ArrayList<NodeElement>();

        if (!this.move) {
            for (final NodeElement nodeElement : nodeElementList) {
                final int nodeElementX = nodeElement.getX();
                final int nodeElementY = nodeElement.getY();
                int nodeElementWidth = nodeElement.getWidth();
                int nodeElementHeight = nodeElement.getHeight();

                if (x > nodeElementX) {
                    nodeElementWidth += x - nodeElementX;
                    x = nodeElementX;
                }
                if (y > nodeElementY) {
                    nodeElementHeight += y - nodeElementY;
                    y = nodeElementY;
                }

                if (nodeElementX - x + nodeElementWidth > width) {
                    width = nodeElementX - x + nodeElementWidth;
                }

                if (nodeElementY - y + nodeElementHeight > height) {
                    height = nodeElementY - y + nodeElementHeight;
                }

            }

            setNewRectangle(x, y, width, height);

        } else {
            nodeElementOldLocationMap = new HashMap<NodeElement, Rectangle>();
            diffX = x - category.getX();
            diffY = y - category.getY();

            for (final Iterator<NodeElement> iter = nodeElementList.iterator(); iter.hasNext();) {
                final NodeElement nodeElement = iter.next();
                for (final Category otherCategory : otherCategories) {
                    if (otherCategory.contains(nodeElement)) {
                        iter.remove();
                        break;
                    }
                }
            }

            for (final NodeElement nodeElement : nodeElementList) {
                nodeElementOldLocationMap.put(nodeElement, new Rectangle(nodeElement.getX(), nodeElement.getY(), nodeElement.getWidth(), nodeElement.getHeight()));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        if (move) {
            bendpointListMap = new HashMap<ConnectionElement, List<Bendpoint>>();

            for (final NodeElement nodeElement : nodeElementList) {
                nodeElement.setLocation(new Location(nodeElement.getX() + diffX, nodeElement.getY() + diffY, nodeElement.getWidth(), nodeElement.getHeight()));
                moveBendpoints(nodeElement);

                nodeElement.refreshVisuals();
                for (final ConnectionElement connectionElement : bendpointListMap.keySet()) {
                    connectionElement.refreshBendpoint();
                }
            }

        } else {
            addNodeToCategory();
        }

        super.doExecute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        if (move) {
            for (final NodeElement nodeElement : nodeElementList) {
                final Rectangle rectangle = nodeElementOldLocationMap.get(nodeElement);
                nodeElement.setLocation(new Location(rectangle.x, rectangle.y, rectangle.width, rectangle.height));

                nodeElement.refreshVisuals();
            }

            restoreBendpoints();

            for (final ConnectionElement connectionElement : bendpointListMap.keySet()) {
                connectionElement.refreshBendpoint();
            }

        } else {
            for (final NodeElement nodeElement : newlyAddedNodeElementList) {
                category.getContents().remove(nodeElement);
            }
        }

        super.doUndo();
    }

    private void moveBendpoints(final NodeElement source) {
        for (final ConnectionElement connectionElement : source.getOutgoings()) {
            final NodeElement target = connectionElement.getTarget();

            if (category.contains(target)) {
                final List<Bendpoint> bendpointList = connectionElement.getBendpoints();

                final List<Bendpoint> oldBendpointList = new ArrayList<Bendpoint>();

                for (int index = 0; index < bendpointList.size(); index++) {
                    final Bendpoint oldBendPoint = bendpointList.get(index);

                    if (oldBendPoint.isRelative()) {
                        break;
                    }

                    final Bendpoint newBendpoint = new Bendpoint(oldBendPoint.getX() + diffX, oldBendPoint.getY() + diffY);
                    connectionElement.replaceBendpoint(index, newBendpoint);

                    oldBendpointList.add(oldBendPoint);
                }

                bendpointListMap.put(connectionElement, oldBendpointList);
            }
        }
    }

    private void restoreBendpoints() {
        for (final ConnectionElement connectionElement : bendpointListMap.keySet()) {
            final List<Bendpoint> oldBendpointList = bendpointListMap.get(connectionElement);

            for (int index = 0; index < oldBendpointList.size(); index++) {
                connectionElement.replaceBendpoint(index, oldBendpointList.get(index));
            }
        }
    }

    private void addNodeToCategory() {
        for (final NodeElement nodeElement : diagram.getDiagramContents().getContents().getNodeElementList()) {
            if (nodeElement instanceof ModelProperties) {
                continue;
            }

            if (nodeElementList.contains(nodeElement)) {
                continue;
            }

            final Location actualLocation = nodeElement.getActualLocation();

            if (actualLocation.x >= x && actualLocation.y >= y && actualLocation.x + actualLocation.width <= x + width && actualLocation.y + actualLocation.height <= y + height) {
                category.getContents().add(nodeElement);
                newlyAddedNodeElementList.add(nodeElement);
            }
        }
    }
}
