package org.insightech.er.editor.view.figure.table.column;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.Graphics;

public class GroupColumnFigure extends Figure {

    public GroupColumnFigure() {
        final FlowLayout layout = new FlowLayout();
        setLayoutManager(layout);
    }

    public void clearLabel() {
        removeAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void paintFigure(final Graphics graphics) {
        if (graphics.getBackgroundColor().equals(getParent().getBackgroundColor())) {
            graphics.setAlpha(0);
        }

        super.paintFigure(graphics);
    }

}
