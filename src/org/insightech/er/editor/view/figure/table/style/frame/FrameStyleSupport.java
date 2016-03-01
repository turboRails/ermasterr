package org.insightech.er.editor.view.figure.table.style.frame;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.TitleBarBorder;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Font;
import org.insightech.er.editor.view.figure.table.TableFigure;
import org.insightech.er.editor.view.figure.table.column.NormalColumnFigure;
import org.insightech.er.editor.view.figure.table.style.AbstractStyleSupport;

public class FrameStyleSupport extends AbstractStyleSupport {

    private ImageFrameBorder border;

    private TitleBarBorder titleBarBorder;

    public FrameStyleSupport(final TableFigure tableFigure) {
        super(tableFigure);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(final TableFigure tableFigure) {
        border = new ImageFrameBorder();
        border.setFont(tableFigure.getFont());

        tableFigure.setBorder(border);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initTitleBar(final Figure top) {
        titleBarBorder = (TitleBarBorder) border.getInnerBorder();
        titleBarBorder.setTextAlignment(PositionConstants.CENTER);
        titleBarBorder.setPadding(new Insets(5, 20, 5, 20));
    }

    @Override
    public void setName(final String name) {
        titleBarBorder.setTextColor(getTextColor());
        titleBarBorder.setLabel(name);
    }

    @Override
    public void setFont(final Font font, final Font titleFont) {
        titleBarBorder.setFont(titleFont);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void adjustBounds(final Rectangle rect) {
        final int width = border.getTitleBarWidth(getTableFigure());

        if (width > rect.width) {
            rect.width = width;
        }
    }

    @Override
    public void addColumn(final NormalColumnFigure columnFigure, final int viewMode, final String physicalName, final String logicalName, final String type, final boolean primaryKey, final boolean foreignKey, final boolean isNotNull, final boolean uniqueKey, final boolean displayKey, final boolean displayDetail, final boolean displayType, final boolean isSelectedReferenced, final boolean isSelectedForeignKey, final boolean isAdded, final boolean isUpdated, final boolean isRemoved) {

        final Label label = createColumnLabel();

        label.setForegroundColor(getTextColor());

        final StringBuilder text = new StringBuilder();
        text.append(getColumnText(viewMode, physicalName, logicalName, type, isNotNull, uniqueKey, displayDetail, displayType));

        if (displayKey) {
            if (primaryKey && foreignKey) {
                label.setForegroundColor(ColorConstants.blue);

                text.append(" ");
                text.append("(PFK)");

            } else if (primaryKey) {
                label.setForegroundColor(ColorConstants.red);

                text.append(" ");
                text.append("(PK)");

            } else if (foreignKey) {
                label.setForegroundColor(ColorConstants.darkGreen);

                text.append(" ");
                text.append("(FK)");
            }
        }

        setColumnFigureColor(columnFigure, isSelectedReferenced, isSelectedForeignKey, isAdded, isUpdated, isRemoved);

        label.setText(text.toString());

        columnFigure.add(label);
    }
}
