package org.insightech.er.editor.view.figure.table.style.simple;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.OrderedLayout;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.swt.graphics.Font;
import org.insightech.er.editor.view.figure.table.TableFigure;
import org.insightech.er.editor.view.figure.table.column.NormalColumnFigure;
import org.insightech.er.editor.view.figure.table.style.AbstractStyleSupport;

public class SimpleStyleSupport extends AbstractStyleSupport {

    private Label nameLabel;

    public SimpleStyleSupport(final TableFigure tableFigure) {
        super(tableFigure);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(final TableFigure tableFigure) {
        tableFigure.setCornerDimensions(new Dimension(10, 10));
        tableFigure.setBorder(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initTitleBar(final Figure top) {
        final ToolbarLayout topLayout = new ToolbarLayout();

        topLayout.setMinorAlignment(OrderedLayout.ALIGN_TOPLEFT);
        topLayout.setStretchMinorAxis(true);
        top.setLayoutManager(topLayout);

        nameLabel = new Label();
        nameLabel.setBorder(new MarginBorder(new Insets(5, 20, 5, 20)));
        top.add(nameLabel);

        final Figure separater = new Figure();
        separater.setSize(-1, 1);
        separater.setBackgroundColor(getTextColor());
        separater.setOpaque(true);

        top.add(separater);
    }

    @Override
    public void setName(final String name) {
        nameLabel.setForegroundColor(getTextColor());
        nameLabel.setText(name);
    }

    @Override
    public void setFont(final Font font, final Font titleFont) {
        nameLabel.setFont(titleFont);
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

        label.setText(text.toString());

        setColumnFigureColor(columnFigure, isSelectedReferenced, isSelectedForeignKey, isAdded, isUpdated, isRemoved);

        columnFigure.add(label);
    }
}
