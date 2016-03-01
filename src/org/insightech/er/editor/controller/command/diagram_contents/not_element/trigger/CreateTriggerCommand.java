package org.insightech.er.editor.controller.command.diagram_contents.not_element.trigger;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.TriggerSet;

public class CreateTriggerCommand extends AbstractCommand {

    private final TriggerSet triggerSet;

    private final Trigger trigger;

    public CreateTriggerCommand(final ERDiagram diagram, final Trigger trigger) {
        triggerSet = diagram.getDiagramContents().getTriggerSet();
        this.trigger = trigger;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        triggerSet.addObject(trigger);
        triggerSet.refresh();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        triggerSet.remove(trigger);
        triggerSet.refresh();
    }

}
