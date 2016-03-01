package org.insightech.er.editor.view.action.outline;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.editor.model.ERDiagram;

public abstract class AbstractOutlineBaseAction extends Action {

    private final TreeViewer treeViewer;

    public AbstractOutlineBaseAction(final String id, final String text, final TreeViewer treeViewer) {
        this(id, text, SWT.NONE, treeViewer);
    }

    public AbstractOutlineBaseAction(final String id, final String text, final int style, final TreeViewer treeViewer) {
        super(text, style);
        setId(id);

        this.treeViewer = treeViewer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void runWithEvent(final Event event) {
        try {
            execute(event);
        } catch (final Exception e) {
            ERDiagramActivator.showExceptionDialog(e);
        }
    }

    protected void execute(final Command command) {
        treeViewer.getEditDomain().getCommandStack().execute(command);
    }

    protected ERDiagram getDiagram() {
        final EditPart editPart = treeViewer.getContents();
        final ERDiagram diagram = (ERDiagram) editPart.getModel();

        return diagram;
    }

    protected TreeViewer getTreeViewer() {
        return treeViewer;
    }

    abstract public void execute(Event event) throws Exception;

}
