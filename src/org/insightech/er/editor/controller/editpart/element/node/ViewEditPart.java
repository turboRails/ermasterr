package org.insightech.er.editor.controller.editpart.element.node;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.table_view.ChangeTableViewPropertyCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.dialog.element.view.ViewDialog;
import org.insightech.er.editor.view.figure.view.ViewFigure;

public class ViewEditPart extends TableViewEditPart {

    /**
     * {@inheritDoc}
     */
    @Override
    protected IFigure createFigure() {
        final ERDiagram diagram = getDiagram();
        final Settings settings = diagram.getDiagramContents().getSettings();

        final ViewFigure figure = new ViewFigure(settings.getTableStyle());

        this.changeFont(figure);

        return figure;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performRequestOpen() {
        final View view = (View) getModel();
        final ERDiagram diagram = getDiagram();

        final View copyView = view.copyData();

        final ViewDialog dialog = new ViewDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), getViewer(), copyView);

        if (dialog.open() == IDialogConstants.OK_ID) {
            final CompoundCommand command = createChangeViewPropertyCommand(diagram, view, copyView);

            executeCommand(command.unwrap());
        }
    }

    public static CompoundCommand createChangeViewPropertyCommand(final ERDiagram diagram, final View view, final View copyView) {
        final CompoundCommand command = new CompoundCommand();

        final ChangeTableViewPropertyCommand changeViewPropertyCommand = new ChangeTableViewPropertyCommand(view, copyView);
        command.add(changeViewPropertyCommand);

        return command;
    }

}
