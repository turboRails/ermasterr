package org.insightech.er.editor.view.action.option.notation.level;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.controller.command.display.notation.ChangeNotationLevelCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.view.action.AbstractBaseAction;

public abstract class AbstractChangeNotationLevelAction extends AbstractBaseAction {

    public AbstractChangeNotationLevelAction(final String id, final ERDiagramEditor editor) {
        super(id, null, IAction.AS_RADIO_BUTTON, editor);
        setText(ResourceString.getResourceString("action.title.change.notation.level." + getLevel()));
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

        final ChangeNotationLevelCommand command = new ChangeNotationLevelCommand(diagram, getLevel());

        this.execute(command);
    }

    protected abstract int getLevel();
}
