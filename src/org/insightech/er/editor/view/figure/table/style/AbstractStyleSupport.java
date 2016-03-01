package org.insightech.er.editor.view.figure.table.style;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.OrderedLayout;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.insightech.er.Resources;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.figure.table.TableFigure;
import org.insightech.er.editor.view.figure.table.column.GroupColumnFigure;

public abstract class AbstractStyleSupport implements StyleSupport {

    private final TableFigure tableFigure;

    public AbstractStyleSupport(final TableFigure tableFigure) {
        super();
        this.tableFigure = tableFigure;
    }

    @Override
    public void init() {
        this.init(tableFigure);

    }

    abstract protected void init(TableFigure tableFigure);

    @Override
    public void createTitleBar() {
        final Figure top = new Figure();
        tableFigure.add(top, BorderLayout.TOP);

        initTitleBar(top);
    }

    abstract protected void initTitleBar(Figure top);

    protected Color getTextColor() {
        return tableFigure.getTextColor();
    }

    @Override
    public void createColumnArea(final IFigure columns) {
        initColumnArea(columns);
        tableFigure.add(columns, BorderLayout.CENTER);
    }

    protected void initColumnArea(final IFigure columns) {
        final ToolbarLayout layout = new ToolbarLayout();
        layout.setMinorAlignment(OrderedLayout.ALIGN_TOPLEFT);
        layout.setStretchMinorAxis(true);
        layout.setSpacing(0);

        columns.setBorder(new MarginBorder(0, 2, 2, 2));
        columns.setLayoutManager(layout);

        columns.setBackgroundColor(null);
        columns.setOpaque(false);
    }

    @Override
    public void createFooter() {}

    protected String getColumnText(final int viewMode, final String physicalName, final String logicalName, final String type, final boolean isNotNull, final boolean uniqueKey, final boolean detail, final boolean displayType) {
        final StringBuilder text = new StringBuilder();

        String name = null;
        if (viewMode == Settings.VIEW_MODE_PHYSICAL) {
            name = physicalName;

        } else if (viewMode == Settings.VIEW_MODE_LOGICAL) {
            name = logicalName;

        } else {
            name = logicalName + "/" + physicalName;
        }

        if (name != null) {
            text.append(name);
        }

        if (displayType) {
            text.append(" ");

            text.append(type);
        }

        if (detail) {
            if (isNotNull && uniqueKey) {
                text.append(" (UNN)");

            } else if (isNotNull) {
                text.append(" (NN)");

            } else if (uniqueKey) {
                text.append(" (U)");
            }
        }

        return text.toString();
    }

    protected Label createColumnLabel() {
        final Label label = new Label();
        label.setBorder(new MarginBorder(new Insets(3, 5, 3, 5)));
        label.setLabelAlignment(PositionConstants.LEFT);

        return label;
    }

    protected void setColumnFigureColor(final IFigure figure, final boolean isSelectedReferenced, final boolean isSelectedForeignKey, final boolean isAdded, final boolean isUpdated, final boolean isRemoved) {
        if (isAdded) {
            figure.setBackgroundColor(Resources.ADDED_COLOR);
        } else if (isUpdated) {
            figure.setBackgroundColor(Resources.UPDATED_COLOR);
        } else if (isRemoved) {
            figure.setBackgroundColor(Resources.REMOVED_COLOR);
        }

        if (isSelectedReferenced && isSelectedForeignKey) {
            figure.setBackgroundColor(Resources.SELECTED_REFERENCED_AND_FOREIGNKEY_COLUMN);

        } else if (isSelectedReferenced) {
            figure.setBackgroundColor(Resources.SELECTED_REFERENCED_COLUMN);

        } else if (isSelectedForeignKey) {
            figure.setBackgroundColor(Resources.SELECTED_FOREIGNKEY_COLUMN);

        }

        figure.setOpaque(true);
    }

    @Override
    public void adjustBounds(final Rectangle rect) {}

    protected TableFigure getTableFigure() {
        return tableFigure;
    }

    @Override
    public void addColumnGroup(final GroupColumnFigure columnFigure, final int viewMode, final String name, final boolean isAdded, final boolean isUpdated, final boolean isRemoved) {

        final Label label = createColumnLabel();

        label.setForegroundColor(getTextColor());

        final StringBuilder text = new StringBuilder();
        text.append(name);
        text.append(" (GROUP)");

        setColumnFigureColor(columnFigure, false, false, isAdded, isUpdated, isRemoved);

        label.setText(text.toString());

        columnFigure.add(label);
    }
}
