package org.insightech.er.editor.view.action.edit;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.actions.SelectAllAction;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.controller.editpart.element.node.NodeElementEditPart;

public class SelectAllContentsAction extends SelectAllAction {

    private final IWorkbenchPart part;

    public SelectAllContentsAction(final IWorkbenchPart part) {
        super(part);
        this.part = part;
        setText(ResourceString.getResourceString("action.title.select.all"));

        setActionDefinitionId("org.eclipse.ui.edit.selectAll");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        final GraphicalViewer viewer = part.getAdapter(GraphicalViewer.class);

        if (viewer != null) {
            final List<NodeElementEditPart> children = new ArrayList<NodeElementEditPart>();

            for (final Object child : viewer.getContents().getChildren()) {
                if (child instanceof NodeElementEditPart) {
                    final NodeElementEditPart editPart = (NodeElementEditPart) child;
                    if (editPart.getFigure().isVisible()) {
                        children.add(editPart);
                    }
                }
            }

            viewer.setSelection(new StructuredSelection(children));
        }
    }
}
