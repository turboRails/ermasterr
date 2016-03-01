package org.insightech.er.editor.view.action.edit;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.controller.command.edit.PasteCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeSet;
import org.insightech.er.editor.model.edit.CopyManager;

public class PasteAction extends SelectionAction {

    private final ERDiagramEditor editor;

    public PasteAction(final IWorkbenchPart part) {
        super(part);

        setText(ResourceString.getResourceString("action.title.paste"));
        final ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
        setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
        setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE_DISABLED));

        setId(ActionFactory.PASTE.getId());

        final ERDiagramEditor editor = (ERDiagramEditor) part;

        this.editor = editor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean calculateEnabled() {
        return CopyManager.canCopy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        try {
            execute(createCommand());
        } catch (final Exception e) {
            ERDiagramActivator.log(e);
        }
    }

    private Command createCommand() {

        if (!calculateEnabled()) {
            return null;
        }

        final EditPart editPart = editor.getGraphicalViewer().getContents();
        final ERDiagram diagram = (ERDiagram) editPart.getModel();

        final NodeSet pasteList = CopyManager.paste(diagram);

        final int numberOfCopy = CopyManager.getNumberOfCopy();

        boolean first = true;
        int x = 0;
        int y = 0;

        for (final NodeElement nodeElement : pasteList) {
            if (first || x > nodeElement.getX()) {
                x = nodeElement.getX();
            }
            if (first || y > nodeElement.getY()) {
                y = nodeElement.getY();
            }

            first = false;
        }

        final Command command = new PasteCommand(editor, pasteList, diagram.mousePoint.x - x + (numberOfCopy - 1) * 20, diagram.mousePoint.y - y + (numberOfCopy - 1) * 20);

        return command;
    }

}
