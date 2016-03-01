package org.insightech.er.editor.controller.editpart.element.node.column;

import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.insightech.er.editor.controller.editpart.element.node.TableViewEditPart;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.model.tracking.UpdatedNodeElement;
import org.insightech.er.editor.view.figure.table.TableFigure;
import org.insightech.er.editor.view.figure.table.column.GroupColumnFigure;

public class GroupColumnEditPart extends ColumnEditPart {

    private boolean selected;

    @Override
    protected IFigure createFigure() {
        final GroupColumnFigure figure = new GroupColumnFigure();
        return figure;
    }

    @Override
    public void refreshTableColumns(final UpdatedNodeElement updated) {
        final ERDiagram diagram = getDiagram();

        final GroupColumnFigure columnFigure = (GroupColumnFigure) getFigure();

        final TableViewEditPart parent = (TableViewEditPart) getParent();
        parent.getContentPane().add(figure);

        final int notationLevel = diagram.getDiagramContents().getSettings().getNotationLevel();

        final Column column = (Column) getModel();

        if (notationLevel != Settings.NOTATION_LEVLE_TITLE) {
            final TableFigure tableFigure = (TableFigure) parent.getFigure();

            boolean isAdded = false;
            boolean isUpdated = false;
            if (updated != null) {
                isAdded = updated.isAdded(column);
                isUpdated = updated.isUpdated(column);
            }

            if ((notationLevel == Settings.NOTATION_LEVLE_KEY)) {
                columnFigure.clearLabel();
                return;
            }

            addGroupColumnFigure(diagram, tableFigure, columnFigure, column, isAdded, isUpdated, false);

            if (selected) {
                columnFigure.setBackgroundColor(ColorConstants.titleBackground);
                columnFigure.setForegroundColor(ColorConstants.titleForeground);
            }

        } else {
            columnFigure.clearLabel();
            return;
        }
    }

    public static void addGroupColumnFigure(final ERDiagram diagram, final TableFigure tableFigure, final GroupColumnFigure columnFigure, final Column column, final boolean isAdded, final boolean isUpdated, final boolean isRemoved) {

        final ColumnGroup groupColumn = (ColumnGroup) column;

        tableFigure.addColumnGroup(columnFigure, diagram.getDiagramContents().getSettings().getViewMode(), diagram.filter(groupColumn.getName()), isAdded, isUpdated, isRemoved);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSelected(final int value) {
        final GroupColumnFigure figure = (GroupColumnFigure) getFigure();

        if (value != 0 && getParent() != null && getParent().getParent() != null) {
            final List selectedEditParts = getViewer().getSelectedEditParts();

            if (selectedEditParts != null && selectedEditParts.size() == 1) {
                figure.setBackgroundColor(ColorConstants.titleBackground);
                figure.setForegroundColor(ColorConstants.titleForeground);
                selected = true;

                super.setSelected(value);
            }

        } else {
            figure.setBackgroundColor(null);
            figure.setForegroundColor(null);
            selected = false;

            super.setSelected(value);
        }

    }
}
