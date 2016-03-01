package org.insightech.er.editor.view.action;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IEditorPart;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.model.ERDiagram;

public abstract class AbstractBaseSelectionAction extends SelectionAction {

    private final ERDiagramEditor editor;

    public AbstractBaseSelectionAction(final String id, final String text, final ERDiagramEditor editor) {
        this(id, text, SWT.NONE, editor);
    }

    public AbstractBaseSelectionAction(final String id, final String text, final int style, final ERDiagramEditor editor) {
        super(editor, style);
        setId(id);
        setText(text);

        this.editor = editor;
    }

    protected ERDiagram getDiagram() {
        final EditPart editPart = editor.getGraphicalViewer().getContents();
        final ERDiagram diagram = (ERDiagram) editPart.getModel();

        return diagram;
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected void execute(final Command command) {
        editor.getGraphicalViewer().getEditDomain().getCommandStack().execute(command);
    }

    protected IEditorPart getEditorPart() {
        return editor;
    }

    protected void execute(final Event event) {
        final GraphicalViewer viewer = getGraphicalViewer();

        final List<Command> commandList = new ArrayList<Command>();

        for (final Object object : viewer.getSelectedEditParts()) {
            final List<Command> subCommandList = getCommand((EditPart) object, event);
            commandList.addAll(subCommandList);
        }

        if (!commandList.isEmpty()) {
            final CompoundCommand compoundCommand = new CompoundCommand();
            for (final Command command : commandList) {
                compoundCommand.add(command);
            }

            this.execute(compoundCommand);
        }
    }

    abstract protected List<Command> getCommand(EditPart editPart, Event event);

    @Override
    protected boolean calculateEnabled() {
        final GraphicalViewer viewer = getGraphicalViewer();

        if (viewer.getSelectedEditParts().isEmpty()) {
            return false;
        }

        return true;
    }
}
