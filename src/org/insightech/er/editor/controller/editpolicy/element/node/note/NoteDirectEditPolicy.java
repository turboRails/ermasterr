package org.insightech.er.editor.controller.editpolicy.element.node.note;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.MoveElementCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.note.NoteEditCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.note.Note;

public class NoteDirectEditPolicy extends DirectEditPolicy {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Command getDirectEditCommand(final DirectEditRequest request) {
        final CompoundCommand command = new CompoundCommand();

        final String text = (String) request.getCellEditor().getValue();

        final Note note = (Note) getHost().getModel();
        final NoteEditCommand noteEditCommand = new NoteEditCommand(note, text);
        command.add(noteEditCommand);

        final MoveElementCommand autoResizeCommand = new MoveElementCommand((ERDiagram) getHost().getRoot().getContents().getModel(), getHostFigure().getBounds(), note.getX(), note.getY(), -1, -1, note);
        command.add(autoResizeCommand);

        return command.unwrap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void showCurrentEditValue(final DirectEditRequest request) {}
}
