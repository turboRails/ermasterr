package org.insightech.er.editor.view.action.option.notation.system;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.controller.command.display.notation.ChangeNotationCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.view.action.AbstractBaseAction;

public abstract class AbstractChangeNotationAction extends AbstractBaseAction {

    public AbstractChangeNotationAction(final String id, final String type, final ERDiagramEditor editor) {
        super(id, null, IAction.AS_RADIO_BUTTON, editor);
        setText(ResourceString.getResourceString("action.title.change.notation." + type));
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

        final ChangeNotationCommand command = new ChangeNotationCommand(diagram, getNotation());

        this.execute(command);
    }

    protected abstract String getNotation();
}
