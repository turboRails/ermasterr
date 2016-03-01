package org.insightech.er.editor.controller.command.diagram_contents.not_element.trigger;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.TriggerSet;

public class EditTriggerCommand extends AbstractCommand {

    private final TriggerSet triggerSet;

    private final Trigger oldTrigger;

    private final Trigger newTrigger;

    public EditTriggerCommand(final ERDiagram diagram, final Trigger oldTrigger, final Trigger newTrigger) {
        triggerSet = diagram.getDiagramContents().getTriggerSet();
        this.oldTrigger = oldTrigger;
        this.newTrigger = newTrigger;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        triggerSet.remove(oldTrigger);
        triggerSet.addObject(newTrigger);

        triggerSet.refresh();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        triggerSet.remove(newTrigger);
        triggerSet.addObject(oldTrigger);

        triggerSet.refresh();
    }
}
