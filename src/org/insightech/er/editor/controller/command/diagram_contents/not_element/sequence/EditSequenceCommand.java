package org.insightech.er.editor.controller.command.diagram_contents.not_element.sequence;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.SequenceSet;

public class EditSequenceCommand extends AbstractCommand {

    private final SequenceSet sequenceSet;

    private final Sequence oldSequence;

    private final Sequence newSequence;

    public EditSequenceCommand(final ERDiagram diagram, final Sequence oldSequence, final Sequence newSequence) {
        sequenceSet = diagram.getDiagramContents().getSequenceSet();
        this.oldSequence = oldSequence;
        this.newSequence = newSequence;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        sequenceSet.remove(oldSequence);
        sequenceSet.addObject(newSequence);
        sequenceSet.refresh();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        sequenceSet.remove(newSequence);
        sequenceSet.addObject(oldSequence);
        sequenceSet.refresh();
    }
}
