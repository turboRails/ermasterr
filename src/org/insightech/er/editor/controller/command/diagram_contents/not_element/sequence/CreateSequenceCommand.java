package org.insightech.er.editor.controller.command.diagram_contents.not_element.sequence;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.SequenceSet;

public class CreateSequenceCommand extends AbstractCommand {

    private final SequenceSet sequenceSet;

    private final Sequence sequence;

    public CreateSequenceCommand(final ERDiagram diagram, final Sequence sequence) {
        sequenceSet = diagram.getDiagramContents().getSequenceSet();
        this.sequence = sequence;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        sequenceSet.addObject(sequence);
        sequenceSet.refresh();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        sequenceSet.remove(sequence);
        sequenceSet.refresh();
    }
}
