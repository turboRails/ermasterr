package org.insightech.er.editor.view.action;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.model.ERDiagram;

public abstract class AbstractBaseAction extends Action {

    private final ERDiagramEditor editor;

    public AbstractBaseAction(final String id, final String text, final ERDiagramEditor editor) {
        this(id, text, SWT.NONE, editor);
    }

    public AbstractBaseAction(final String id, final String text, final int style, final ERDiagramEditor editor) {
        super(text, style);
        setId(id);

        this.editor = editor;
    }

    protected void refreshProject() {
        final IEditorInput input = getEditorPart().getEditorInput();

        if (input instanceof IFileEditorInput) {
            final IFile iFile = ((IFileEditorInput) getEditorPart().getEditorInput()).getFile();
            final IProject project = iFile.getProject();

            try {
                project.refreshLocal(IResource.DEPTH_INFINITE, null);

            } catch (final CoreException e) {
                ERDiagramActivator.showExceptionDialog(e);
            }
        }
    }

    protected ERDiagram getDiagram() {
        final EditPart editPart = editor.getGraphicalViewer().getContents();
        final ERDiagram diagram = (ERDiagram) editPart.getModel();

        return diagram;
    }

    protected String getBasePath() {
        return getDiagram().getEditor().getBasePath();
    }

    protected GraphicalViewer getGraphicalViewer() {
        return editor.getGraphicalViewer();
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

    abstract public void execute(Event event) throws Exception;

    protected void execute(final Command command) {
        editor.getGraphicalViewer().getEditDomain().getCommandStack().execute(command);
    }

    protected ERDiagramEditor getEditorPart() {
        return editor;
    }
}
