package org.insightech.er.editor.controller.editpart.element.node.removed;

import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.Resources;
import org.insightech.er.editor.controller.editpart.element.node.IResizable;
import org.insightech.er.editor.controller.editpart.element.node.TableViewEditPart;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.model.tracking.RemovedERTable;
import org.insightech.er.editor.view.figure.table.TableFigure;

public class RemovedERTableEditPart extends RemovedNodeElementEditPart implements IResizable {

    private Font titleFont;

    /**
     * {@inheritDoc}
     */
    @Override
    protected IFigure createFigure() {
        final ERDiagram diagram = getDiagram();
        final Settings settings = diagram.getDiagramContents().getSettings();

        final TableFigure figure = new TableFigure(settings.getTableStyle());

        this.changeFont(figure);

        return figure;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refreshVisuals() {
        try {
            final TableFigure figure = (TableFigure) getFigure();

            final RemovedERTable removedERTable = (RemovedERTable) getModel();
            final ERTable table = (ERTable) removedERTable.getNodeElement();

            figure.create(null);

            final ERDiagram diagram = getDiagram();

            final int viewMode = diagram.getDiagramContents().getSettings().getViewMode();

            if (viewMode == Settings.VIEW_MODE_PHYSICAL) {
                figure.setName(diagram.filter(table.getPhysicalName()));

            } else if (viewMode == Settings.VIEW_MODE_LOGICAL) {
                figure.setName(diagram.filter(table.getLogicalName()));

            } else {
                figure.setName(diagram.filter(table.getLogicalName()) + "/" + diagram.filter(table.getPhysicalName()));
            }

            refreshTableColumns(figure);

            super.refreshVisuals();

        } catch (final Exception e) {
            ERDiagramActivator.showExceptionDialog(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changeSettings(final Settings settings) {
        final TableFigure figure = (TableFigure) getFigure();
        figure.setTableStyle(settings.getTableStyle());

        super.changeSettings(settings);
    }

    private Font changeFont(final TableFigure tableFigure) {
        final Font font = super.changeFont(tableFigure);

        final FontData fonDatat = font.getFontData()[0];

        titleFont = Resources.getFont(fonDatat.getName(), fonDatat.getHeight(), SWT.BOLD);

        tableFigure.setFont(font, titleFont);

        return font;
    }

    private void refreshTableColumns(final TableFigure tableFigure) {
        final RemovedERTable removedERTable = (RemovedERTable) getModel();
        final ERTable table = (ERTable) removedERTable.getNodeElement();

        final ERDiagram diagram = getDiagram();

        tableFigure.clearColumns();

        TableViewEditPart.showRemovedColumns(diagram, tableFigure, table.getColumns(), false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IFigure getContentPane() {
        final TableFigure figure = (TableFigure) super.getContentPane();

        return figure.getColumns();
    }
}
