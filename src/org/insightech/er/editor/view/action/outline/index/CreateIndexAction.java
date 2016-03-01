package org.insightech.er.editor.view.action.outline.index;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.controller.command.diagram_contents.not_element.index.CreateIndexCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.view.action.outline.AbstractOutlineBaseAction;
import org.insightech.er.editor.view.dialog.element.table.sub.IndexDialog;

public class CreateIndexAction extends AbstractOutlineBaseAction {

    public static final String ID = CreateIndexAction.class.getName();

    public CreateIndexAction(final TreeViewer treeViewer) {
        super(ID, ResourceString.getResourceString("action.title.create.index"), treeViewer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(final Event event) {

        final ERDiagram diagram = getDiagram();

        final List selectedEditParts = getTreeViewer().getSelectedEditParts();
        final EditPart editPart = (EditPart) selectedEditParts.get(0);
        final ERTable table = (ERTable) editPart.getModel();

        final IndexDialog dialog = new IndexDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), null, table);

        if (dialog.open() == IDialogConstants.OK_ID) {
            final CreateIndexCommand command = new CreateIndexCommand(diagram, dialog.getResultIndex());

            this.execute(command);
        }
    }

}
