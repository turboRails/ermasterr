package org.insightech.er.editor.controller.command.diagram_contents.element.node.note;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.node.note.Note;

public class NoteEditCommand extends AbstractCommand {

    private final String oldText;

    private final String text;

    private final Note note;

    public NoteEditCommand(final Note note, final String text) {
        this.note = note;
        oldText = this.note.getText();
        this.text = text;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        note.setText(text);

        note.refreshVisuals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        note.setText(oldText);

        note.refreshVisuals();
    }
}
