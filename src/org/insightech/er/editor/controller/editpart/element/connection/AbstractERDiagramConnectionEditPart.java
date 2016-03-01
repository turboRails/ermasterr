package org.insightech.er.editor.controller.editpart.element.connection;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.draw2d.AbsoluteBendpoint;
import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.Resources;
import org.insightech.er.editor.controller.editpart.element.node.NodeElementEditPart;
import org.insightech.er.editor.controller.editpart.element.node.TableViewEditPart;
import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.Bendpoint;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.settings.CategorySetting;
import org.insightech.er.editor.view.figure.anchor.XYChopboxAnchor;
import org.insightech.er.editor.view.figure.connection.ERDiagramConnection;

public abstract class AbstractERDiagramConnectionEditPart extends AbstractConnectionEditPart implements PropertyChangeListener {

    private static Logger logger = Logger.getLogger(AbstractERDiagramConnectionEditPart.class.getName());

    private static final boolean DEBUG = false;

    /**
     * {@inheritDoc}
     */
    @Override
    public void activate() {
        super.activate();

        final AbstractModel model = (AbstractModel) getModel();
        model.addPropertyChangeListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deactivate() {
        final AbstractModel model = (AbstractModel) getModel();
        model.removePropertyChangeListener(this);

        super.deactivate();
    }

    protected ERDiagramConnection createERDiagramConnection() {
        final boolean bezier = getDiagram().getDiagramContents().getSettings().isUseBezierCurve();
        final ERDiagramConnection connection = new ERDiagramConnection(bezier);
        connection.setConnectionRouter(new BendpointConnectionRouter());

        return connection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, new ConnectionEndpointEditPolicy());
        // this.installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
        // new ConnectionGraphicalNodeEditPolicy());
    }

    @Override
    public final void propertyChange(final PropertyChangeEvent event) {
        try {
            if (DEBUG) {
                logger.log(Level.INFO, this.getClass().getName() + ":" + event.getPropertyName() + ":" + event.toString());
            }

            doPropertyChange(event);

        } catch (final Exception e) {
            ERDiagramActivator.showExceptionDialog(e);
        }
    }

    protected void doPropertyChange(final PropertyChangeEvent event) {
        if (event.getPropertyName().equals("refreshBendpoint")) {
            refreshBendpoints();

        } else if (event.getPropertyName().equals("refreshVisuals")) {
            refreshVisuals();
        }
    }

    protected ERDiagram getDiagram() {
        return (ERDiagram) getRoot().getContents().getModel();
    }

    protected Category getCurrentCategory() {
        return getDiagram().getCurrentCategory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refreshVisuals() {
        if (isActive()) {
            final ConnectionElement element = (ConnectionElement) getModel();

            ((ERDiagramConnection) figure).setColor(Resources.getColor(element.getColor()));

            fillterConnectionByCategory();
            decorateRelation();
            calculateAnchorLocation();
            refreshBendpoints();
        }
    }

    public void refreshVisualsWithColumn() {
        refreshVisuals();

        final TableViewEditPart sourceTableViewEditPart = (TableViewEditPart) getSource();
        if (sourceTableViewEditPart != null) {
            sourceTableViewEditPart.refreshVisuals();
        }
        final TableViewEditPart targetTableViewEditPart = (TableViewEditPart) getTarget();
        if (targetTableViewEditPart != null) {
            targetTableViewEditPart.refreshVisuals();
        }
    }

    private void fillterConnectionByCategory() {
        final EditPart sourceEditPart = getSource();
        final EditPart targetEditPart = getTarget();

        final ERDiagram diagram = getDiagram();

        if (diagram != null) {
            final Category category = getCurrentCategory();

            if (category != null) {
                figure.setVisible(false);

                final CategorySetting categorySettings = getDiagram().getDiagramContents().getSettings().getCategorySetting();

                if (sourceEditPart != null && targetEditPart != null) {
                    final NodeElement sourceModel = (NodeElement) sourceEditPart.getModel();
                    final NodeElement targetModel = (NodeElement) targetEditPart.getModel();

                    boolean containsSource = false;

                    if (category.contains(sourceModel)) {
                        containsSource = true;

                    } else if (categorySettings.isShowReferredTables()) {
                        for (final NodeElement referringElement : sourceModel.getReferringElementList()) {
                            if (category.contains(referringElement)) {
                                containsSource = true;
                                break;
                            }
                        }
                    }

                    if (containsSource) {
                        if (category.contains(targetModel)) {
                            figure.setVisible(true);

                        } else if (categorySettings.isShowReferredTables()) {
                            for (final NodeElement referringElement : targetModel.getReferringElementList()) {
                                if (category.contains(referringElement)) {
                                    figure.setVisible(true);
                                    break;
                                }
                            }
                        }
                    }
                }

            } else {
                figure.setVisible(true);
            }
        }
    }

    private void calculateAnchorLocation() {
        final ConnectionElement connection = (ConnectionElement) getModel();

        final NodeElementEditPart sourceEditPart = (NodeElementEditPart) getSource();

        Point sourcePoint = null;
        Point targetPoint = null;

        if (sourceEditPart != null && connection.getSourceXp() != -1 && connection.getSourceYp() != -1) {
            final Rectangle bounds = sourceEditPart.getFigure().getBounds();
            sourcePoint = new Point(bounds.x + (bounds.width * connection.getSourceXp() / 100), bounds.y + (bounds.height * connection.getSourceYp() / 100));
        }

        final NodeElementEditPart targetEditPart = (NodeElementEditPart) getTarget();

        if (targetEditPart != null && connection.getTargetXp() != -1 && connection.getTargetYp() != -1) {
            final Rectangle bounds = targetEditPart.getFigure().getBounds();
            targetPoint = new Point(bounds.x + (bounds.width * connection.getTargetXp() / 100), bounds.y + (bounds.height * connection.getTargetYp() / 100));
        }

        final ConnectionAnchor sourceAnchor = getConnectionFigure().getSourceAnchor();

        if (sourceAnchor instanceof XYChopboxAnchor) {
            ((XYChopboxAnchor) sourceAnchor).setLocation(sourcePoint);
        }

        final ConnectionAnchor targetAnchor = getConnectionFigure().getTargetAnchor();

        if (targetAnchor instanceof XYChopboxAnchor) {
            ((XYChopboxAnchor) targetAnchor).setLocation(targetPoint);
        }
    }

    protected void refreshBendpoints() {
        final ConnectionElement connection = (ConnectionElement) getModel();

        final List<org.eclipse.draw2d.Bendpoint> constraint = new ArrayList<org.eclipse.draw2d.Bendpoint>();

        for (final Bendpoint bendPoint : connection.getBendpoints()) {
            final List<org.eclipse.draw2d.Bendpoint> realPointList = getRealBendpoint(bendPoint);

            constraint.addAll(realPointList);
        }

        getConnectionFigure().setRoutingConstraint(constraint);
    }

    protected List<org.eclipse.draw2d.Bendpoint> getRealBendpoint(final Bendpoint bendPoint) {
        final List<org.eclipse.draw2d.Bendpoint> constraint = new ArrayList<org.eclipse.draw2d.Bendpoint>();

        constraint.add(new AbsoluteBendpoint(bendPoint.getX(), bendPoint.getY()));

        return constraint;
    }

    protected void decorateRelation() {}
}
