package org.insightech.er.editor.controller.editpart.element.node.removed;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.insightech.er.Resources;
import org.insightech.er.editor.controller.editpart.DeleteableEditPart;
import org.insightech.er.editor.controller.editpart.element.AbstractModelEditPart;
import org.insightech.er.editor.model.ViewableModel;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.model.tracking.RemovedNodeElement;

public abstract class RemovedNodeElementEditPart extends AbstractModelEditPart implements NodeEditPart, DeleteableEditPart {

    private Font font;

    @Override
    public void doPropertyChange(final PropertyChangeEvent event) {
        if (event.getPropertyName().equals("refreshFont")) {
            changeFont(figure);
            refreshVisuals();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createEditPolicies() {}

    protected void setVisible() {
        final Category category = getCurrentCategory();

        if (category != null) {
            figure.setVisible(false);
        } else {
            figure.setVisible(true);
        }
    }

    protected Font changeFont(final IFigure figure) {
        final RemovedNodeElement removedNodeElement = (RemovedNodeElement) getModel();

        String fontName = removedNodeElement.getFontName();
        int fontSize = removedNodeElement.getFontSize();

        if (fontName == null) {
            final FontData fontData = Display.getCurrent().getSystemFont().getFontData()[0];
            fontName = fontData.getName();
        }
        if (fontSize <= 0) {
            fontSize = ViewableModel.DEFAULT_FONT_SIZE;
        }

        font = Resources.getFont(fontName, fontSize);

        figure.setFont(font);

        return font;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void refreshVisuals() {
        setVisible();

        final Rectangle rectangle = getRectangle();

        final GraphicalEditPart parent = (GraphicalEditPart) getParent();

        final IFigure figure = getFigure();

        figure.setBackgroundColor(Resources.REMOVED_COLOR);

        parent.setLayoutConstraint(this, figure, rectangle);
    }

    protected Rectangle getRectangle() {
        final RemovedNodeElement removedNodeElement = (RemovedNodeElement) getModel();

        final NodeElement nodeElement = removedNodeElement.getNodeElement();

        final Point point = new Point(nodeElement.getX(), nodeElement.getY());

        final Dimension dimension = new Dimension(nodeElement.getWidth(), nodeElement.getHeight());

        final Dimension minimumSize = figure.getMinimumSize();
        if (dimension.width != -1 && dimension.width < minimumSize.width) {
            dimension.width = minimumSize.width;
        }
        if (dimension.height != -1 && dimension.height < minimumSize.height) {
            dimension.height = minimumSize.height;
        }

        return new Rectangle(point, dimension);
    }

    @Override
    public ConnectionAnchor getSourceConnectionAnchor(final ConnectionEditPart arg0) {
        return new ChopboxAnchor(getFigure());
    }

    @Override
    public ConnectionAnchor getSourceConnectionAnchor(final Request arg0) {
        return new ChopboxAnchor(getFigure());
    }

    @Override
    public ConnectionAnchor getTargetConnectionAnchor(final ConnectionEditPart arg0) {
        return new ChopboxAnchor(getFigure());
    }

    @Override
    public ConnectionAnchor getTargetConnectionAnchor(final Request arg0) {
        return new ChopboxAnchor(getFigure());
    }

    public void changeSettings(final Settings settings) {
        refresh();
    }

    @Override
    public boolean isDeleteable() {
        return false;
    }

}
