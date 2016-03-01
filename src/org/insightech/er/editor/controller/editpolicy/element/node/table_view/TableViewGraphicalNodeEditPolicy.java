package org.insightech.er.editor.controller.editpolicy.element.node.table_view;

import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.SimpleRaisedBorder;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.swt.widgets.Display;
import org.insightech.er.editor.controller.editpart.element.node.TableViewEditPart;
import org.insightech.er.editor.controller.editpolicy.element.node.NodeElementGraphicalNodeEditPolicy;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;

public class TableViewGraphicalNodeEditPolicy extends NodeElementGraphicalNodeEditPolicy {

    @Override
    public void showTargetFeedback(final Request request) {
        final ERDiagram diagram = (ERDiagram) getHost().getRoot().getContents().getModel();

        if (diagram.isTooltip()) {
            final ZoomManager zoomManager = ((ScalableFreeformRootEditPart) getHost().getRoot()).getZoomManager();
            final double zoom = zoomManager.getZoom();

            final TableView tableView = (TableView) getHost().getModel();
            final Rectangle tableBounds = getHostFigure().getBounds();

            final String name = TableViewEditPart.getTableViewName(tableView, diagram);

            final Label label = new Label();
            label.setText(name);
            label.setBorder(new SimpleRaisedBorder());
            label.setBackgroundColor(ColorConstants.orange);
            label.setOpaque(true);

            final Dimension dim = FigureUtilities.getTextExtents(name, Display.getCurrent().getSystemFont());

            label.setBounds(new Rectangle((int) (zoom * (tableBounds.x + 33)), (int) (zoom * (tableBounds.y + 5)), (int) (dim.width * 1.5), 20));

            addFeedback(label);
        }
        super.showTargetFeedback(request);
    }

    @Override
    public void eraseTargetFeedback(final Request request) {
        final LayerManager manager = (LayerManager) getHost().getRoot();
        final IFigure layer = manager.getLayer(LayerConstants.PRIMARY_LAYER);
        getFeedbackLayer().setBounds(layer.getBounds());

        final List list = getFeedbackLayer().getChildren();

        for (final Iterator iter = list.iterator(); iter.hasNext();) {
            final Object obj = iter.next();
            if (obj instanceof Label) {
                iter.remove();
            }
        }
        getFeedbackLayer().repaint();

        super.eraseTargetFeedback(request);
    }

}
