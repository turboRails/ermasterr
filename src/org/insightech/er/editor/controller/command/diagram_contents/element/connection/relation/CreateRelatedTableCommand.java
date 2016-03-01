package org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.insightech.er.editor.controller.editpart.element.node.TableViewEditPart;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;

public class CreateRelatedTableCommand extends AbstractCreateRelationCommand {

    private Relation relation1;

    private Relation relation2;

    private final ERTable relatedTable;

    private final ERDiagram diagram;

    private int sourceX;

    private int sourceY;

    private int targetX;

    private int targetY;

    private final Category category;

    protected Location newCategoryLocation;

    protected Location oldCategoryLocation;

    public CreateRelatedTableCommand(final ERDiagram diagram) {
        super();

        relatedTable = new ERTable();

        this.diagram = diagram;
        category = this.diagram.getCurrentCategory();
        if (category != null) {
            oldCategoryLocation = category.getLocation();
        }
    }

    public void setSourcePoint(final int x, final int y) {
        sourceX = x;
        sourceY = y;
    }

    private void setTargetPoint(final int x, final int y) {
        targetX = x;
        targetY = y;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTarget(final EditPart target) {
        super.setTarget(target);

        if (target != null) {
            if (target instanceof TableViewEditPart) {
                final TableViewEditPart tableEditPart = (TableViewEditPart) target;

                final Point point = tableEditPart.getFigure().getBounds().getCenter();
                setTargetPoint(point.x, point.y);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        // ERDiagramEditPart.setUpdateable(false);

        init();

        diagram.addNewContent(relatedTable);
        addToCategory(relatedTable);

        relation1.setSource((ERTable) source.getModel());
        relation1.setTargetTableView(relatedTable);

        relation2.setSource((ERTable) target.getModel());
        relation2.setTargetTableView(relatedTable);

        diagram.refreshChildren();
        getTargetModel().refresh();
        getSourceModel().refresh();

        if (category != null) {
            category.refresh();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        diagram.removeContent(relatedTable);
        removeFromCategory(category);

        relation1.setSource(null);
        relation1.setTargetTableView(null);

        relation2.setSource(null);
        relation2.setTargetTableView(null);

        diagram.refreshChildren();
        getTargetModel().refresh();
        getSourceModel().refresh();

        if (category != null) {
            category.refresh();
        }
    }

    private void init() {
        final ERTable sourceTable = (ERTable) getSourceModel();

        relation1 = sourceTable.createRelation();

        final ERTable targetTable = (ERTable) getTargetModel();
        relation2 = targetTable.createRelation();

        relatedTable.setLocation(new Location((sourceX + targetX - TableView.DEFAULT_WIDTH) / 2, (sourceY + targetY - TableView.DEFAULT_HEIGHT) / 2, TableView.DEFAULT_WIDTH, TableView.DEFAULT_HEIGHT));

        relatedTable.setLogicalName(ERTable.NEW_LOGICAL_NAME);
        relatedTable.setPhysicalName(ERTable.NEW_PHYSICAL_NAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canExecute() {
        if (!super.canExecute()) {
            return false;
        }

        if (!(getSourceModel() instanceof ERTable) || !(getTargetModel() instanceof ERTable)) {
            return false;
        }

        return true;
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
