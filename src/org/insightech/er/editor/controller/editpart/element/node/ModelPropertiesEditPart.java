package org.insightech.er.editor.controller.editpart.element.node;

import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.model_properties.ChangeModelPropertiesCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.model_properties.ModelProperties;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.dialog.element.ModelPropertiesDialog;
import org.insightech.er.editor.view.figure.ModelPropertiesFigure;

public class ModelPropertiesEditPart extends NodeElementEditPart implements IResizable {

    public ModelPropertiesEditPart() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IFigure createFigure() {
        final ERDiagram diagram = getDiagram();
        final Settings settings = diagram.getDiagramContents().getSettings();

        final ModelPropertiesFigure figure = new ModelPropertiesFigure();

        changeFont(figure);

        figure.setVisible(settings.getModelProperties().isDisplay());

        return figure;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doRefreshVisuals() {
        final ERDiagram diagram = getDiagram();
        final ModelProperties modelProperties = (ModelProperties) getModel();

        final ModelPropertiesFigure figure = (ModelPropertiesFigure) getFigure();

        figure.setData(modelProperties.getProperties(), modelProperties.getCreationDate(), modelProperties.getUpdatedDate(), diagram.getDiagramContents().getSettings().getTableStyle(), modelProperties.getColor());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refreshSettings(final Settings settings) {
        figure.setVisible(settings.getModelProperties().isDisplay());
        super.refreshSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setVisible() {
        final ERDiagram diagram = getDiagram();

        final Settings settings = diagram.getDiagramContents().getSettings();

        figure.setVisible(settings.getModelProperties().isDisplay());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performRequestOpen() {
        final ERDiagram diagram = getDiagram();

        final ModelProperties copyModelProperties = diagram.getDiagramContents().getSettings().getModelProperties().clone();

        final ModelPropertiesDialog dialog = new ModelPropertiesDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), copyModelProperties);

        if (dialog.open() == IDialogConstants.OK_ID) {
            final ChangeModelPropertiesCommand command = new ChangeModelPropertiesCommand(diagram, copyModelProperties);

            executeCommand(command);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDeleteable() {
        return false;
    }
}
