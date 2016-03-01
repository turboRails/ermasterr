package org.insightech.er.editor.view.action.option.notation.type;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.controller.command.display.ChangeViewModeCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.view.action.AbstractBaseAction;

public abstract class AbstractChangeViewAction extends AbstractBaseAction {

    public AbstractChangeViewAction(final String id, final String type, final ERDiagramEditor editor) {
        super(id, null, IAction.AS_RADIO_BUTTON, editor);
        setText(ResourceString.getResourceString("action.title.change.mode.to." + type));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(final Event event) {
        if (!isChecked()) {
            return;
        }

        final ERDiagram diagram = getDiagram();

        final ChangeViewModeCommand command = new ChangeViewModeCommand(diagram, getViewMode());

        this.execute(command);
    }

    protected abstract int getViewMode();
}
