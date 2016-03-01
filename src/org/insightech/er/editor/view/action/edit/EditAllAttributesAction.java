package org.insightech.er.editor.view.action.edit;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.controller.command.edit.EditAllAttributesCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.DiagramContents;
import org.insightech.er.editor.view.action.AbstractBaseAction;
import org.insightech.er.editor.view.dialog.edit.EditAllAttributesDialog;

public class EditAllAttributesAction extends AbstractBaseAction {

    public static final String ID = EditAllAttributesAction.class.getName();

    public EditAllAttributesAction(final ERDiagramEditor editor) {
        super(ID, ResourceString.getResourceString("action.title.edit.all.attributes"), editor);
        setImageDescriptor(ERDiagramActivator.getImageDescriptor(ImageKey.EDIT));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(final Event event) {
        final ERDiagram diagram = getDiagram();

        final EditAllAttributesDialog dialog = new EditAllAttributesDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), diagram);

        if (dialog.open() == IDialogConstants.OK_ID) {
            final DiagramContents newDiagramContents = dialog.getDiagramContents();
            final EditAllAttributesCommand command = new EditAllAttributesCommand(diagram, newDiagramContents);
            this.execute(command);
        }
    }
}
