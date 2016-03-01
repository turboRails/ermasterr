package org.insightech.er.editor.view.figure.anchor;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;

public class XYChopboxAnchor extends ChopboxAnchor {

    private Point location;

    public XYChopboxAnchor(final IFigure owner) {
        super(owner);
    }

    public void setLocation(final Point location) {
        this.location = location;
        fireAnchorMoved();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Point getLocation(final Point reference) {
        if (location != null) {
            final Point point = new Point(location);
            getOwner().translateToAbsolute(point);
            return point;
        }

        return super.getLocation(reference);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Point getReferencePoint() {
        if (location != null) {
            final Point point = new Point(location);
            getOwner().translateToAbsolute(point);
            return point;
        }

        return super.getReferencePoint();
    }

}
