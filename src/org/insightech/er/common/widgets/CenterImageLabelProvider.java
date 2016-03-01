package org.insightech.er.common.widgets;

import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TableItem;

public abstract class CenterImageLabelProvider extends OwnerDrawLabelProvider {

    @Override
    protected void measure(final Event event, final Object element) {}

    @Override
    protected void paint(final Event event, final Object element) {
        final Image img = getImage(element);

        if (img != null) {
            final Rectangle bounds = ((TableItem) event.item).getBounds(event.index);
            final Rectangle imgBounds = img.getBounds();
            bounds.width /= 2;
            bounds.width -= imgBounds.width / 2;
            bounds.height /= 2;
            bounds.height -= imgBounds.height / 2;

            final int x = bounds.width > 0 ? bounds.x + bounds.width : bounds.x;
            final int y = bounds.height > 0 ? bounds.y + bounds.height : bounds.y;

            event.gc.drawImage(img, x, y);
        }
    }

    protected abstract Image getImage(Object element);
}
