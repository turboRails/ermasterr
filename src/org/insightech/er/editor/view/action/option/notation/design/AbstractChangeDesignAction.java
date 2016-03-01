package org.insightech.er.editor.view.action.option.notation.design;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.controller.command.display.ChangeDesignCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.view.action.AbstractBaseAction;

public class AbstractChangeDesignAction extends AbstractBaseAction {

    private final String type;

    public AbstractChangeDesignAction(final String ID, final String type, final ERDiagramEditor editor) {
        super(ID, ResourceString.getResourceString("action.title.change.design." + type), IAction.AS_RADIO_BUTTON, editor);

        this.type = type;
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

        final ChangeDesignCommand command = new ChangeDesignCommand(diagram, type);

        this.execute(command);
    }

}
