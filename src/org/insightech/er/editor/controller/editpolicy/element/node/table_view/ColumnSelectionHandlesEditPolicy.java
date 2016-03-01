package org.insightech.er.editor.controller.editpolicy.element.node.table_view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.CreateRelationCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.DeleteRelationCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.table_view.AddColumnGroupCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.table_view.AddWordCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.table_view.ChangeColumnOrderCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.table_view.ChangeTableViewPropertyCommand;
import org.insightech.er.editor.controller.editpart.element.node.NodeElementEditPart;
import org.insightech.er.editor.controller.editpart.element.node.column.ColumnEditPart;
import org.insightech.er.editor.controller.editpart.element.node.column.NormalColumnEditPart;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ViewableModel;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.CopyColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.CopyWord;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.view.drag_drop.ERDiagramTransferDragSourceListener;

public class ColumnSelectionHandlesEditPolicy extends NonResizableEditPolicy {

    /**
     * {@inheritDoc}
     */
    @Override
    protected List createSelectionHandles() {
        final List selectedEditParts = getHost().getViewer().getSelectedEditParts();
        if (selectedEditParts.size() == 1) {
            final ViewableModel currentElement = (ViewableModel) getHost().getParent().getModel();

            final ERDiagram diagram = (ERDiagram) getHost().getRoot().getContents().getModel();

            final List<NodeElement> nodeElementList = diagram.getDiagramContents().getContents().getNodeElementList();
            nodeElementList.remove(currentElement);
            nodeElementList.add((NodeElement) currentElement);

            final NodeElementEditPart editPart = (NodeElementEditPart) getHost().getParent();
            editPart.reorder();
        }

        return new ArrayList();
    }

    private Rectangle getColumnRectangle() {
        final ColumnEditPart columnEditPart = (ColumnEditPart) getHost();
        final Column column = (Column) columnEditPart.getModel();

        final IFigure figure = columnEditPart.getFigure();
        final Rectangle rect = figure.getBounds();

        int startY = 0;
        int endY = 0;

        if (column.getColumnHolder() instanceof ColumnGroup) {
            final ColumnGroup columnGroup = (ColumnGroup) column.getColumnHolder();

            final NormalColumn firstColumn = columnGroup.getColumns().get(0);
            final NormalColumn finalColumn = columnGroup.getColumns().get(columnGroup.getColumns().size() - 1);

            for (final Object editPart : columnEditPart.getParent().getChildren()) {
                final NormalColumnEditPart normalColumnEditPart = (NormalColumnEditPart) editPart;
                if (normalColumnEditPart.getModel() == firstColumn) {
                    final Rectangle bounds = normalColumnEditPart.getFigure().getBounds();
                    startY = bounds.y;

                } else if (normalColumnEditPart.getModel() == finalColumn) {
                    final Rectangle bounds = normalColumnEditPart.getFigure().getBounds();
                    endY = bounds.y + bounds.height;
                }
            }

        } else {
            startY = rect.y;
            endY = rect.y + rect.height;
        }

        return new Rectangle(rect.x, startY, rect.width, endY - startY);
    }

    @Override
    public void showTargetFeedback(final Request request) {
        if (request instanceof DirectEditRequest) {
            final ZoomManager zoomManager = ((ScalableFreeformRootEditPart) getHost().getRoot()).getZoomManager();
            final double zoom = zoomManager.getZoom();

            final Rectangle columnRectangle = getColumnRectangle();
            final int center = (int) ((columnRectangle.y + (columnRectangle.height / 2)) * zoom);

            final DirectEditRequest directEditRequest = (DirectEditRequest) request;

            int y = 0;

            if (directEditRequest.getLocation().y < center) {
                y = columnRectangle.y - 1;

            } else {
                y = columnRectangle.y + columnRectangle.height - 1;
            }

            final RectangleFigure feedbackFigure = new RectangleFigure();
            feedbackFigure.setForegroundColor(ColorConstants.lightGray);
            feedbackFigure.setBackgroundColor(ColorConstants.lightGray);
            feedbackFigure.setBounds(new Rectangle((int) (zoom * columnRectangle.x), (int) (zoom * y), (int) (zoom * columnRectangle.width), (int) (zoom * 2)));

            final LayerManager manager = (LayerManager) getHost().getRoot();
            final IFigure layer = manager.getLayer(LayerConstants.PRIMARY_LAYER);

            final IFigure feedbackLayer = getFeedbackLayer();

            final List children = getFeedbackLayer().getChildren();
            children.clear();

            feedbackLayer.setBounds(layer.getBounds());

            feedbackLayer.add(feedbackFigure);
            feedbackLayer.repaint();
        }

        super.showTargetFeedback(request);
    }

    @Override
    public void eraseTargetFeedback(final Request request) {
        if (request instanceof DirectEditRequest) {
            getFeedbackLayer().getChildren().clear();
        }

        super.eraseTargetFeedback(request);
    }

    @Override
    public EditPart getTargetEditPart(final Request request) {

        if (ERDiagramTransferDragSourceListener.REQUEST_TYPE_ADD_COLUMN_GROUP.equals(request.getType()) || ERDiagramTransferDragSourceListener.REQUEST_TYPE_MOVE_COLUMN_GROUP.equals(request.getType())) {
            final DirectEditRequest editRequest = (DirectEditRequest) request;

            final TableView tableView = (TableView) getHost().getParent().getModel();
            final ColumnGroup columnGroup = (ColumnGroup) ((Map) editRequest.getDirectEditFeature()).get(ERDiagramTransferDragSourceListener.MOVE_COLUMN_GROUP_PARAM_GROUP);

            final Object parent = ((Map) editRequest.getDirectEditFeature()).get(ERDiagramTransferDragSourceListener.MOVE_COLUMN_GROUP_PARAM_PARENT);

            if (parent == tableView || !tableView.getColumns().contains(columnGroup)) {
                return getHost();
            }

        } else if (ERDiagramTransferDragSourceListener.REQUEST_TYPE_ADD_WORD.equals(request.getType())) {
            return getHost();

        } else if (ERDiagramTransferDragSourceListener.REQUEST_TYPE_MOVE_COLUMN.equals(request.getType())) {
            return getHost();

        }

        return super.getTargetEditPart(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Command getCommand(final Request request) {
        try {
            if (ERDiagramTransferDragSourceListener.REQUEST_TYPE_ADD_COLUMN_GROUP.equals(request.getType())) {
                final DirectEditRequest editRequest = (DirectEditRequest) request;

                final TableView tableView = (TableView) getHost().getParent().getModel();
                final ColumnGroup columnGroup = (ColumnGroup) ((Map) editRequest.getDirectEditFeature()).get(ERDiagramTransferDragSourceListener.MOVE_COLUMN_GROUP_PARAM_GROUP);

                if (!tableView.getColumns().contains(columnGroup)) {
                    return new AddColumnGroupCommand(tableView, columnGroup, getColumnIndex(editRequest));
                }

            } else if (ERDiagramTransferDragSourceListener.REQUEST_TYPE_ADD_WORD.equals(request.getType())) {
                final DirectEditRequest editRequest = (DirectEditRequest) request;

                final TableView table = (TableView) getHost().getParent().getModel();
                final Word word = (Word) editRequest.getDirectEditFeature();

                return new AddWordCommand(table, word, getColumnIndex(editRequest));

            } else if (ERDiagramTransferDragSourceListener.REQUEST_TYPE_MOVE_COLUMN.equals(request.getType())) {
                final DirectEditRequest editRequest = (DirectEditRequest) request;

                final TableView newTableView = (TableView) getHost().getParent().getModel();

                return createMoveColumnCommand(editRequest, getHost().getViewer(), newTableView, getColumnIndex(editRequest));

            } else if (ERDiagramTransferDragSourceListener.REQUEST_TYPE_MOVE_COLUMN_GROUP.equals(request.getType())) {
                final DirectEditRequest editRequest = (DirectEditRequest) request;

                final TableView newTableView = (TableView) getHost().getParent().getModel();

                return createMoveColumnGroupCommand(editRequest, newTableView, getColumnIndex(editRequest));
            }

        } catch (final Exception e) {
            ERDiagramActivator.showExceptionDialog(e);
        }

        return super.getCommand(request);
    }

    public static Command createMoveColumnCommand(final DirectEditRequest editRequest, final EditPartViewer viewer, final TableView newTableView, final int index) {
        final NormalColumn oldColumn = (NormalColumn) editRequest.getDirectEditFeature();

        final TableView oldTableView = (TableView) oldColumn.getColumnHolder();
        if (newTableView == oldTableView) {
            return new ChangeColumnOrderCommand(newTableView, oldColumn, index);
        }

        final CompoundCommand command = new CompoundCommand();

        final List<Relation> relationList = oldColumn.getOutgoingRelationList();

        if (!relationList.isEmpty()) {
            ERDiagramActivator.showErrorDialog("error.reference.key.not.moveable");
            return null;

        } else if (oldColumn.isForeignKey()) {
            final Relation oldRelation = oldColumn.getRelationList().get(0);
            final TableView referencedTableView = oldRelation.getSourceTableView();

            if (ERTable.isRecursive(referencedTableView, newTableView)) {
                ERDiagramActivator.showErrorDialog("error.recursive.relation");
                return null;
            }

            final DeleteRelationCommand deleteOldRelationCommand = new DeleteRelationCommand(oldRelation, true);
            command.add(deleteOldRelationCommand);

            final Relation newRelation = new Relation(oldRelation.isReferenceForPK(), oldRelation.getReferencedComplexUniqueKey(), oldRelation.getReferencedColumn(), oldColumn.isNotNull(), oldColumn.isUniqueKey() || oldColumn.isSinglePrimaryKey());
            newRelation.setParentCardinality(oldRelation.getParentCardinality());
            newRelation.setChildCardinality(oldRelation.getChildCardinality());

            final List<NormalColumn> oldForeignKeyColumnList = new ArrayList<NormalColumn>();

            if (referencedTableView == newTableView) {
                ERDiagramActivator.showErrorDialog("error.foreign.key.not.moveable.to.reference.table");
                return null;
            }

            if (oldRelation.isReferenceForPK()) {
                for (final NormalColumn referencedPrimaryKey : ((ERTable) referencedTableView).getPrimaryKeys()) {
                    for (final NormalColumn oldTableColumn : oldTableView.getNormalColumns()) {
                        if (oldTableColumn.isForeignKey()) {
                            if (oldTableColumn.getReferencedColumn(oldRelation) == referencedPrimaryKey) {
                                oldForeignKeyColumnList.add(oldTableColumn);
                                break;
                            }
                        }
                    }
                }
            } else if (oldRelation.getReferencedComplexUniqueKey() != null) {
                for (final NormalColumn referencedColumn : oldRelation.getReferencedComplexUniqueKey().getColumnList()) {
                    for (final NormalColumn oldTableColumn : oldTableView.getNormalColumns()) {
                        if (oldTableColumn.isForeignKey()) {
                            if (oldTableColumn.getReferencedColumn(oldRelation) == referencedColumn) {
                                oldForeignKeyColumnList.add(oldTableColumn);
                                break;
                            }
                        }
                    }
                }

            } else {
                oldForeignKeyColumnList.add(oldColumn);
            }

            for (final NormalColumn oldForeignKey : oldForeignKeyColumnList) {
                final List<Relation> oldRelationList = oldForeignKey.getOutgoingRelationList();

                if (!oldRelationList.isEmpty()) {
                    ERDiagramActivator.showErrorDialog("error.reference.key.not.moveable");
                    return null;
                }
            }

            final CreateRelationCommand createNewRelationCommand = new CreateRelationCommand(newRelation, oldForeignKeyColumnList);

            final EditPart sourceEditPart = (EditPart) viewer.getEditPartRegistry().get(referencedTableView);
            final EditPart targetEditPart = (EditPart) viewer.getEditPartRegistry().get(newTableView);

            createNewRelationCommand.setSource(sourceEditPart);
            createNewRelationCommand.setTarget(targetEditPart);

            command.add(createNewRelationCommand);

        } else {
            final TableView copyOldTableView = oldTableView.copyData();
            for (final NormalColumn column : copyOldTableView.getNormalColumns()) {
                final CopyColumn copyColumn = (CopyColumn) column;
                if (copyColumn.getOriginalColumn() == oldColumn) {
                    copyOldTableView.removeColumn(copyColumn);
                    break;
                }
            }

            final ChangeTableViewPropertyCommand sourceTableCommand = new ChangeTableViewPropertyCommand(oldTableView, copyOldTableView);
            command.add(sourceTableCommand);

            final TableView copyNewTableView = newTableView.copyData();
            final CopyColumn copyColumn = new CopyColumn(oldColumn);
            copyColumn.setWord(new CopyWord(oldColumn.getWord()));
            copyNewTableView.addColumn(index, copyColumn);
            final ChangeTableViewPropertyCommand targetTableCommand = new ChangeTableViewPropertyCommand(newTableView, copyNewTableView);
            command.add(targetTableCommand);
        }

        return command.unwrap();
    }

    public static Command createMoveColumnGroupCommand(final DirectEditRequest editRequest, final TableView newTableView, final int index) {
        final ColumnGroup columnGroup = (ColumnGroup) ((Map) editRequest.getDirectEditFeature()).get(ERDiagramTransferDragSourceListener.MOVE_COLUMN_GROUP_PARAM_GROUP);

        final TableView oldTableView = (TableView) ((Map) editRequest.getDirectEditFeature()).get(ERDiagramTransferDragSourceListener.MOVE_COLUMN_GROUP_PARAM_PARENT);

        if (newTableView == oldTableView) {
            return new ChangeColumnOrderCommand(newTableView, columnGroup, index);
        }

        final CompoundCommand command = new CompoundCommand();

        final TableView copyOldTableView = oldTableView.copyData();
        for (final Column column : copyOldTableView.getColumns()) {
            if (column == columnGroup) {
                copyOldTableView.removeColumn(column);
                break;
            }
        }

        final ChangeTableViewPropertyCommand sourceTableCommand = new ChangeTableViewPropertyCommand(oldTableView, copyOldTableView);
        command.add(sourceTableCommand);

        if (!newTableView.getColumns().contains(columnGroup)) {
            command.add(new AddColumnGroupCommand(newTableView, columnGroup, index));
        }

        return command.unwrap();
    }

    private int getColumnIndex(final DirectEditRequest editRequest) {
        final ZoomManager zoomManager = ((ScalableFreeformRootEditPart) getHost().getRoot()).getZoomManager();
        final double zoom = zoomManager.getZoom();

        final ColumnEditPart columnEditPart = (ColumnEditPart) getHost();

        Column column = (Column) columnEditPart.getModel();
        final TableView newTableView = (TableView) getHost().getParent().getModel();

        final List<Column> columns = newTableView.getColumns();

        if (column.getColumnHolder() instanceof ColumnGroup) {
            column = (ColumnGroup) column.getColumnHolder();
        }
        int index = columns.indexOf(column);

        final Rectangle columnRectangle = getColumnRectangle();
        final int center = (int) ((columnRectangle.y + (columnRectangle.height / 2)) * zoom);

        if (editRequest.getLocation().y >= center) {
            index++;
        }

        return index;
    }
}
