package org.insightech.er.editor.controller.editpolicy.element.node.table_view;

import java.util.Map;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.requests.DirectEditRequest;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.table_view.AddColumnGroupCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.table_view.AddWordCommand;
import org.insightech.er.editor.controller.editpart.element.node.TableViewEditPart;
import org.insightech.er.editor.controller.editpolicy.element.node.NodeElementComponentEditPolicy;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.view.drag_drop.ERDiagramTransferDragSourceListener;

public class TableViewComponentEditPolicy extends NodeElementComponentEditPolicy {

    @Override
    public void showTargetFeedback(final Request request) {
        super.showTargetFeedback(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EditPart getTargetEditPart(final Request request) {
        if (ERDiagramTransferDragSourceListener.REQUEST_TYPE_ADD_COLUMN_GROUP.equals(request.getType()) || ERDiagramTransferDragSourceListener.REQUEST_TYPE_MOVE_COLUMN_GROUP.equals(request.getType())) {
            final DirectEditRequest editRequest = (DirectEditRequest) request;

            final TableView tableView = (TableView) getHost().getModel();
            final ColumnGroup columnGroup = (ColumnGroup) ((Map) editRequest.getDirectEditFeature()).get("group");

            if (!tableView.getColumns().contains(columnGroup)) {
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

                final TableView tableView = (TableView) getHost().getModel();
                final ColumnGroup columnGroup = (ColumnGroup) ((Map) editRequest.getDirectEditFeature()).get("group");

                if (!tableView.getColumns().contains(columnGroup)) {
                    return new AddColumnGroupCommand(tableView, columnGroup, getColumnIndex(editRequest));
                }

            } else if (ERDiagramTransferDragSourceListener.REQUEST_TYPE_ADD_WORD.equals(request.getType())) {
                final DirectEditRequest editRequest = (DirectEditRequest) request;

                final TableView table = (TableView) getHost().getModel();
                final Word word = (Word) editRequest.getDirectEditFeature();

                return new AddWordCommand(table, word, getColumnIndex(editRequest));

            } else if (ERDiagramTransferDragSourceListener.REQUEST_TYPE_MOVE_COLUMN.equals(request.getType())) {
                final DirectEditRequest editRequest = (DirectEditRequest) request;

                return ColumnSelectionHandlesEditPolicy.createMoveColumnCommand(editRequest, getHost().getViewer(), (TableView) getHost().getModel(), getColumnIndex(editRequest));

            } else if (ERDiagramTransferDragSourceListener.REQUEST_TYPE_MOVE_COLUMN_GROUP.equals(request.getType())) {
                final DirectEditRequest editRequest = (DirectEditRequest) request;

                return ColumnSelectionHandlesEditPolicy.createMoveColumnGroupCommand(editRequest, (TableView) getHost().getModel(), getColumnIndex(editRequest));
            }

        } catch (final Exception e) {
            ERDiagramActivator.showExceptionDialog(e);
        }

        return super.getCommand(request);
    }

    private int getColumnIndex(final DirectEditRequest editRequest) {
        final ZoomManager zoomManager = ((ScalableFreeformRootEditPart) getHost().getRoot()).getZoomManager();
        final double zoom = zoomManager.getZoom();

        final IFigure figure = ((TableViewEditPart) getHost()).getFigure();

        final int center = (int) (figure.getBounds().y + (figure.getBounds().height / 2) * zoom);

        int index = 0;

        if (editRequest.getLocation().y >= center) {
            final TableView newTableView = (TableView) getHost().getModel();

            index = newTableView.getColumns().size();
        }

        return index;
    }
}
