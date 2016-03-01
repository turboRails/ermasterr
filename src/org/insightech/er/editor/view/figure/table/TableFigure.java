package org.insightech.er.editor.view.figure.table;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.insightech.er.ImageKey;
import org.insightech.er.editor.view.action.option.notation.design.ChangeDesignToFrameAction;
import org.insightech.er.editor.view.action.option.notation.design.ChangeDesignToSimpleAction;
import org.insightech.er.editor.view.figure.table.column.GroupColumnFigure;
import org.insightech.er.editor.view.figure.table.column.NormalColumnFigure;
import org.insightech.er.editor.view.figure.table.style.StyleSupport;
import org.insightech.er.editor.view.figure.table.style.frame.FrameStyleSupport;
import org.insightech.er.editor.view.figure.table.style.funny.FunnyStyleSupport;
import org.insightech.er.editor.view.figure.table.style.simple.SimpleStyleSupport;

public class TableFigure extends RoundedRectangle {

    private final Figure columns;

    private StyleSupport styleSupport;

    private Color foregroundColor;

    public TableFigure(final String tableStyle) {
        columns = new Figure();
        setLayoutManager(new BorderLayout());
        setTableStyle(tableStyle);
    }

    public void setTableStyle(final String tableStyle) {
        if (ChangeDesignToSimpleAction.TYPE.equals(tableStyle)) {
            styleSupport = new SimpleStyleSupport(this);

        } else if (ChangeDesignToFrameAction.TYPE.equals(tableStyle)) {
            styleSupport = new FrameStyleSupport(this);

        } else {
            styleSupport = new FunnyStyleSupport(this);
        }

        styleSupport.init();

        create(null);
    }

    public void create(final int[] color) {
        decideColor(color);

        removeAll();

        styleSupport.createTitleBar();

        columns.removeAll();

        styleSupport.createColumnArea(columns);

        styleSupport.createFooter();
    }

    private void decideColor(final int[] color) {
        if (color != null) {
            final int sum = color[0] + color[1] + color[2];

            if (sum > 255) {
                foregroundColor = ColorConstants.black;
            } else {
                foregroundColor = ColorConstants.white;
            }
        }
    }

    public void setName(final String name) {
        styleSupport.setName(name);
    }

    public void setFont(final Font font, final Font titleFont) {
        this.setFont(font);
        styleSupport.setFont(font, titleFont);
    }

    public void clearColumns() {
        columns.removeAll();
    }

    public void addColumn(final NormalColumnFigure columnFigure, final int viewMode, final String physicalName, final String logicalName, final String type, final boolean primaryKey, final boolean foreignKey, final boolean isNotNull, final boolean uniqueKey, final boolean displayKey, final boolean displayDetail, final boolean displayType, final boolean isSelectedReferenced, final boolean isSelectedForeignKey, final boolean isAdded, final boolean isUpdated, final boolean isRemoved) {

        columnFigure.removeAll();
        columnFigure.setBackgroundColor(null);

        styleSupport.addColumn(columnFigure, viewMode, physicalName, logicalName, type, primaryKey, foreignKey, isNotNull, uniqueKey, displayKey, displayDetail, displayType, isSelectedReferenced, isSelectedForeignKey, isAdded, isUpdated, isRemoved);
    }

    public void addColumnGroup(final GroupColumnFigure columnFigure, final int viewMode, final String name, final boolean isAdded, final boolean isUpdated, final boolean isRemoved) {

        columnFigure.removeAll();
        columnFigure.setBackgroundColor(null);

        styleSupport.addColumnGroup(columnFigure, viewMode, name, isAdded, isUpdated, isRemoved);
    }

    @Override
    public Rectangle getBounds() {
        final Rectangle bounds = super.getBounds();

        styleSupport.adjustBounds(bounds);

        return bounds;
    }

    public Color getTextColor() {
        return foregroundColor;
    }

    @Override
    protected void fillShape(final Graphics graphics) {
        graphics.setAlpha(200);
        super.fillShape(graphics);
    }

    /**
     * columns ���擾���܂�.
     * 
     * @return columns
     */
    public Figure getColumns() {
        return columns;
    }

    public String getImageKey() {
        return ImageKey.TABLE;
    }
}
