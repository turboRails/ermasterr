package org.insightech.er.editor.view.figure.table.column;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.OrderedLayout;
import org.eclipse.draw2d.geometry.Insets;

public class NormalColumnFigure extends Figure {

    public NormalColumnFigure() {
        final FlowLayout layout = new FlowLayout();
        layout.setMinorAlignment(OrderedLayout.ALIGN_CENTER);
        setLayoutManager(layout);

        setBorder(new MarginBorder(new Insets(0, 5, 0, 0)));
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
