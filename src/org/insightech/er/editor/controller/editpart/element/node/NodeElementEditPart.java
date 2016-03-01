package org.insightech.er.editor.controller.editpart.element.node;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.SnapFeedbackPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.Resources;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.CreateCommentConnectionCommand;
import org.insightech.er.editor.controller.editpart.DeleteableEditPart;
import org.insightech.er.editor.controller.editpart.element.AbstractModelEditPart;
import org.insightech.er.editor.controller.editpart.element.connection.AbstractERDiagramConnectionEditPart;
import org.insightech.er.editor.controller.editpart.element.node.column.ColumnEditPart;
import org.insightech.er.editor.controller.editpolicy.element.node.NodeElementGraphicalNodeEditPolicy;
import org.insightech.er.editor.model.ViewableModel;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.note.Note;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.model.tracking.ChangeTrackingList;
import org.insightech.er.editor.view.figure.anchor.XYChopboxAnchor;
import org.insightech.er.editor.view.figure.connection.ERDiagramConnection;
import org.insightech.er.util.Check;

public abstract class NodeElementEditPart extends AbstractModelEditPart implements NodeEditPart, DeleteableEditPart {

    private Font font;

    /**
     * {@inheritDoc}
     */
    @Override
    public void deactivate() {
        super.deactivate();
    }

    @Override
    public void doPropertyChange(final PropertyChangeEvent event) {
        if (event.getPropertyName().equals("refreshFont")) {
            changeFont(figure);
            refreshVisuals();

        } else if (event.getPropertyName().equals("refreshSourceConnections")) {
            refreshSourceConnections();

        } else if (event.getPropertyName().equals("refreshTargetConnections")) {
            refreshTargetConnections();

            // this.getFigure().getUpdateManager().performValidation();
        }

        super.doPropertyChange(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new NodeElementGraphicalNodeEditPolicy());
        installEditPolicy("Snap Feedback", new SnapFeedbackPolicy());
    }

    protected void setVisible() {
        final NodeElement element = (NodeElement) getModel();
        final Category category = getCurrentCategory();

        if (category != null) {
            figure.setVisible(category.isVisible(element, getDiagram()));

        } else {
            figure.setVisible(true);
        }
    }

    protected Font changeFont(final IFigure figure) {
        final NodeElement nodeElement = (NodeElement) getModel();

        String fontName = nodeElement.getFontName();
        int fontSize = nodeElement.getFontSize();

        if (Check.isEmpty(fontName)) {
            final FontData fontData = Display.getCurrent().getSystemFont().getFontData()[0];
            fontName = fontData.getName();
            nodeElement.setFontName(fontName);
        }
        if (fontSize <= 0) {
            fontSize = ViewableModel.DEFAULT_FONT_SIZE;
            nodeElement.setFontSize(fontSize);
        }

        font = Resources.getFont(fontName, fontSize);

        figure.setFont(font);

        return font;
    }

    protected void doRefreshVisuals() {}

    /**
     * {@inheritDoc}
     */
    @Override
    final public void refreshVisuals() {
        refreshChildren();
        doRefreshVisuals();
        setVisible();

        final NodeElement element = (NodeElement) getModel();
        final IFigure figure = getFigure();

        final int[] color = element.getColor();

        if (color != null) {
            final ChangeTrackingList changeTrackingList = getDiagram().getChangeTrackingList();

            if (changeTrackingList.isCalculated() && (element instanceof Note || element instanceof ERTable)) {
                if (changeTrackingList.isAdded(element)) {
                    figure.setBackgroundColor(Resources.ADDED_COLOR);

                } else if (changeTrackingList.getUpdatedNodeElement(element) != null) {
                    figure.setBackgroundColor(Resources.UPDATED_COLOR);

                } else {
                    figure.setBackgroundColor(ColorConstants.white);
                }

            } else {
                final Color bgColor = Resources.getColor(color);
                figure.setBackgroundColor(bgColor);
            }

        }

        final Rectangle rectangle = getRectangle();

        final GraphicalEditPart parent = (GraphicalEditPart) getParent();

        parent.setLayoutConstraint(this, figure, rectangle);

        getFigure().getUpdateManager().performValidation();

        element.setActualLocation(toLocation(getFigure().getBounds()));

        refreshMovedAnchor();
    }

    private Location toLocation(final Rectangle rectangle) {
        return new Location(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }

    private void refreshMovedAnchor() {
        for (final Object sourceConnection : getSourceConnections()) {
            final ConnectionEditPart editPart = (ConnectionEditPart) sourceConnection;
            final ConnectionElement connectinoElement = (ConnectionElement) editPart.getModel();
            if (connectinoElement.isSourceAnchorMoved()) {
                ((AbstractERDiagramConnectionEditPart) editPart).refreshVisuals();
            }
        }

        for (final Object targetConnection : getTargetConnections()) {
            final ConnectionEditPart editPart = (ConnectionEditPart) targetConnection;
            final ConnectionElement connectinoElement = (ConnectionElement) editPart.getModel();
            if (connectinoElement.isTargetAnchorMoved()) {
                if (connectinoElement.getSource() != connectinoElement.getTarget()) {
                    ((AbstractERDiagramConnectionEditPart) editPart).refreshVisuals();
                }
            }
        }
    }

    protected Rectangle getRectangle() {
        final NodeElement element = (NodeElement) getModel();

        final Point point = new Point(element.getX(), element.getY());

        final Dimension dimension = new Dimension(element.getWidth(), element.getHeight());

        final Dimension minimumSize = figure.getMinimumSize();

        if (dimension.width != -1 && dimension.width < minimumSize.width) {
            dimension.width = minimumSize.width;
        }
        if (dimension.height != -1 && dimension.height < minimumSize.height) {
            dimension.height = minimumSize.height;
        }

        return new Rectangle(point, dimension);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List getModelSourceConnections() {
        final NodeElement element = (NodeElement) getModel();
        return element.getOutgoings();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List getModelTargetConnections() {
        final NodeElement element = (NodeElement) getModel();
        return element.getIncomings();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConnectionAnchor getSourceConnectionAnchor(final ConnectionEditPart editPart) {
        // if (!(editPart instanceof RelationEditPart)) {
        // return super.getSourceConnectionAnchor(editPart);
        // }

        final ConnectionElement connection = (ConnectionElement) editPart.getModel();

        final Rectangle bounds = getFigure().getBounds();

        final XYChopboxAnchor anchor = new XYChopboxAnchor(getFigure());

        if (connection.getSourceXp() != -1 && connection.getSourceYp() != -1) {
            anchor.setLocation(new Point(bounds.x + (bounds.width * connection.getSourceXp() / 100), bounds.y + (bounds.height * connection.getSourceYp() / 100)));
        }

        return anchor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConnectionAnchor getSourceConnectionAnchor(final Request request) {
        if (request instanceof ReconnectRequest) {
            final ReconnectRequest reconnectRequest = (ReconnectRequest) request;

            final ConnectionEditPart connectionEditPart = reconnectRequest.getConnectionEditPart();

            // if (!(connectionEditPart instanceof RelationEditPart)) {
            // return super.getSourceConnectionAnchor(request);
            // }

            final ConnectionElement connection = (ConnectionElement) connectionEditPart.getModel();
            if (connection.getSource() == connection.getTarget()) {
                return new XYChopboxAnchor(getFigure());
            }

            final EditPart editPart = reconnectRequest.getTarget();

            if (editPart == null || !editPart.getModel().equals(connection.getSource())) {
                return new XYChopboxAnchor(getFigure());
            }

            final Point location = new Point(reconnectRequest.getLocation());
            getFigure().translateToRelative(location);
            final IFigure sourceFigure = ((TableViewEditPart) connectionEditPart.getSource()).getFigure();

            final XYChopboxAnchor anchor = new XYChopboxAnchor(getFigure());

            final Rectangle bounds = sourceFigure.getBounds();

            final Rectangle centerRectangle = new Rectangle(bounds.x + (bounds.width / 4), bounds.y + (bounds.height / 4), bounds.width / 2, bounds.height / 2);

            if (!centerRectangle.contains(location)) {
                final Point point = getIntersectionPoint(location, sourceFigure);
                anchor.setLocation(point);
            }

            return anchor;

        } else if (request instanceof CreateConnectionRequest) {
            final CreateConnectionRequest connectionRequest = (CreateConnectionRequest) request;

            final Command command = connectionRequest.getStartCommand();

            if (command instanceof CreateCommentConnectionCommand) {
                return new ChopboxAnchor(getFigure());
            }
        }

        return new XYChopboxAnchor(getFigure());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConnectionAnchor getTargetConnectionAnchor(final ConnectionEditPart editPart) {
        // if (!(editPart instanceof RelationEditPart)) {
        // return new ChopboxAnchor(this.getFigure());
        // }

        final ConnectionElement connection = (ConnectionElement) editPart.getModel();

        final XYChopboxAnchor anchor = new XYChopboxAnchor(getFigure());

        final Rectangle bounds = getFigure().getBounds();

        if (connection.getTargetXp() != -1 && connection.getTargetYp() != -1) {
            anchor.setLocation(new Point(bounds.x + (bounds.width * connection.getTargetXp() / 100), bounds.y + (bounds.height * connection.getTargetYp() / 100)));
        }

        return anchor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConnectionAnchor getTargetConnectionAnchor(final Request request) {
        if (request instanceof ReconnectRequest) {
            final ReconnectRequest reconnectRequest = (ReconnectRequest) request;

            final ConnectionEditPart connectionEditPart = reconnectRequest.getConnectionEditPart();

            // if (!(connectionEditPart instanceof RelationEditPart)) {
            // return super.getTargetConnectionAnchor(request);
            // }

            final ConnectionElement connection = (ConnectionElement) connectionEditPart.getModel();
            if (connection.getSource() == connection.getTarget()) {
                return new XYChopboxAnchor(getFigure());
            }

            final EditPart editPart = reconnectRequest.getTarget();

            if (editPart == null || !editPart.getModel().equals(connection.getTarget())) {
                return new XYChopboxAnchor(getFigure());
            }

            final Point location = new Point(reconnectRequest.getLocation());
            getFigure().translateToRelative(location);
            final IFigure targetFigure = ((AbstractModelEditPart) connectionEditPart.getTarget()).getFigure();

            final XYChopboxAnchor anchor = new XYChopboxAnchor(getFigure());

            final Rectangle bounds = targetFigure.getBounds();

            final Rectangle centerRectangle = new Rectangle(bounds.x + (bounds.width / 4), bounds.y + (bounds.height / 4), bounds.width / 2, bounds.height / 2);

            if (!centerRectangle.contains(location)) {
                final Point point = getIntersectionPoint(location, targetFigure);
                anchor.setLocation(point);
            }

            return anchor;

        } else if (request instanceof CreateConnectionRequest) {
            final CreateConnectionRequest connectionRequest = (CreateConnectionRequest) request;

            final Command command = connectionRequest.getStartCommand();

            if (command instanceof CreateCommentConnectionCommand) {
                return new ChopboxAnchor(getFigure());
            }
        }

        return new XYChopboxAnchor(getFigure());
    }

    public static Point getIntersectionPoint(final Point s, final IFigure figure) {

        final Rectangle r = figure.getBounds();

        final int x1 = s.x - r.x;
        final int x2 = r.x + r.width - s.x;
        final int y1 = s.y - r.y;
        final int y2 = r.y + r.height - s.y;

        int x = 0;
        int dx = 0;
        if (x1 < x2) {
            x = r.x;
            dx = x1;

        } else {
            x = r.x + r.width;
            dx = x2;
        }

        int y = 0;
        int dy = 0;

        if (y1 < y2) {
            y = r.y;
            dy = y1;

        } else {
            y = r.y + r.height;
            dy = y2;
        }

        if (dx < dy) {
            y = s.y;
        } else {
            x = s.x;
        }

        return new Point(x, y);
    }

    public void refreshSettings(final Settings settings) {
        refresh();

        for (final Object object : getSourceConnections()) {
            final AbstractERDiagramConnectionEditPart editPart = (AbstractERDiagramConnectionEditPart) object;
            final ERDiagramConnection connection = (ERDiagramConnection) editPart.getFigure();
            connection.setBezier(settings.isUseBezierCurve());

            editPart.refresh();
        }
    }

    @Override
    public boolean isDeleteable() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSelected(final int value) {
        if (value != 0) {
            for (final Object editPartObject : getViewer().getSelectedEditParts()) {
                if (editPartObject instanceof ColumnEditPart) {
                    ((ColumnEditPart) editPartObject).setSelected(0);
                }
            }
        }

        super.setSelected(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performRequest(final Request request) {
        if (request.getType().equals(RequestConstants.REQ_OPEN)) {
            try {
                performRequestOpen();

            } catch (final Exception e) {
                ERDiagramActivator.showExceptionDialog(e);
            }
        }

        super.performRequest(request);
    }

    public void reorder() {
        final IFigure parentFigure = figure.getParent();
        parentFigure.remove(figure);
        parentFigure.add(figure);
        figure.repaint();
    }

    abstract protected void performRequestOpen();
}
