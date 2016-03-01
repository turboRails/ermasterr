package org.insightech.er.editor.controller.editpart.element.node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.insightech.er.Resources;
import org.insightech.er.editor.controller.editpart.element.node.column.ColumnEditPart;
import org.insightech.er.editor.controller.editpart.element.node.column.GroupColumnEditPart;
import org.insightech.er.editor.controller.editpart.element.node.column.NormalColumnEditPart;
import org.insightech.er.editor.controller.editpolicy.element.node.table_view.TableViewComponentEditPolicy;
import org.insightech.er.editor.controller.editpolicy.element.node.table_view.TableViewGraphicalNodeEditPolicy;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.model.tracking.UpdatedNodeElement;
import org.insightech.er.editor.view.figure.table.TableFigure;
import org.insightech.er.editor.view.figure.table.column.GroupColumnFigure;
import org.insightech.er.editor.view.figure.table.column.NormalColumnFigure;

public abstract class TableViewEditPart extends NodeElementEditPart implements IResizable {

    private Font titleFont;

    /**
     * {@inheritDoc}
     */
    @Override
    protected List getModelChildren() {
        final List<Object> modelChildren = new ArrayList<Object>();

        final TableView tableView = (TableView) getModel();

        final ERDiagram diagram = getDiagram();
        if (diagram.getDiagramContents().getSettings().isNotationExpandGroup()) {
            modelChildren.addAll(tableView.getExpandedColumns());

        } else {
            modelChildren.addAll(tableView.getColumns());
        }

        return modelChildren;
    }

    @Override
    public void doRefreshVisuals() {
        final TableFigure tableFigure = (TableFigure) getFigure();
        final TableView tableView = (TableView) getModel();

        tableFigure.create(tableView.getColor());

        final ERDiagram diagram = getDiagram();
        tableFigure.setName(getTableViewName(tableView, diagram));

        UpdatedNodeElement updated = null;
        if (diagram.getChangeTrackingList().isCalculated()) {
            updated = diagram.getChangeTrackingList().getUpdatedNodeElement(tableView);
        }

        for (final Object child : getChildren()) {
            final ColumnEditPart part = (ColumnEditPart) child;
            part.refreshTableColumns(updated);
        }

        if (updated != null) {
            showRemovedColumns(diagram, tableFigure, updated.getRemovedColumns(), true);
        }
    }

    public static void showRemovedColumns(final ERDiagram diagram, final TableFigure tableFigure, final Collection<Column> removedColumns, final boolean isRemoved) {

        final int notationLevel = diagram.getDiagramContents().getSettings().getNotationLevel();

        for (final Column removedColumn : removedColumns) {

            if (removedColumn instanceof ColumnGroup) {
                if (diagram.getDiagramContents().getSettings().isNotationExpandGroup()) {
                    final ColumnGroup columnGroup = (ColumnGroup) removedColumn;

                    for (final NormalColumn normalColumn : columnGroup.getColumns()) {
                        if (notationLevel == Settings.NOTATION_LEVLE_KEY && !normalColumn.isPrimaryKey() && !normalColumn.isForeignKey() && !normalColumn.isReferedStrictly()) {
                            continue;
                        }

                        final NormalColumnFigure columnFigure = new NormalColumnFigure();
                        tableFigure.getColumns().add(columnFigure);

                        NormalColumnEditPart.addColumnFigure(diagram, tableFigure, columnFigure, normalColumn, false, false, false, false, isRemoved);
                    }

                } else {
                    if ((notationLevel == Settings.NOTATION_LEVLE_KEY)) {
                        continue;
                    }

                    final GroupColumnFigure columnFigure = new GroupColumnFigure();
                    tableFigure.getColumns().add(columnFigure);

                    GroupColumnEditPart.addGroupColumnFigure(diagram, tableFigure, columnFigure, removedColumn, false, false, isRemoved);
                }

            } else {
                final NormalColumn normalColumn = (NormalColumn) removedColumn;
                if (notationLevel == Settings.NOTATION_LEVLE_KEY && !normalColumn.isPrimaryKey() && !normalColumn.isForeignKey() && !normalColumn.isReferedStrictly()) {
                    continue;
                }

                final NormalColumnFigure columnFigure = new NormalColumnFigure();
                tableFigure.getColumns().add(columnFigure);

                NormalColumnEditPart.addColumnFigure(diagram, tableFigure, columnFigure, normalColumn, false, false, false, false, isRemoved);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refreshSettings(final Settings settings) {
        final TableFigure figure = (TableFigure) getFigure();
        figure.setTableStyle(settings.getTableStyle());

        super.refreshSettings(settings);
    }

    protected Font changeFont(final TableFigure tableFigure) {
        final Font font = super.changeFont(tableFigure);

        final FontData fonData = font.getFontData()[0];

        titleFont = Resources.getFont(fonData.getName(), fonData.getHeight(), SWT.BOLD);

        tableFigure.setFont(font, titleFont);

        return font;
    }

    public static String getTableViewName(final TableView tableView, final ERDiagram diagram) {
        String name = null;

        final int viewMode = diagram.getDiagramContents().getSettings().getViewMode();

        if (viewMode == Settings.VIEW_MODE_PHYSICAL) {
            name = diagram.filter(tableView.getPhysicalName());

        } else if (viewMode == Settings.VIEW_MODE_LOGICAL) {
            name = diagram.filter(tableView.getLogicalName());

        } else {
            name = diagram.filter(tableView.getLogicalName()) + "/" + diagram.filter(tableView.getPhysicalName());
        }

        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IFigure getContentPane() {
        final TableFigure figure = (TableFigure) super.getContentPane();

        return figure.getColumns();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new TableViewComponentEditPolicy());
        installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new TableViewGraphicalNodeEditPolicy());
    }
}
