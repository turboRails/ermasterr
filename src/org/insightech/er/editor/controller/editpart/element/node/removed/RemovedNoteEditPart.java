package org.insightech.er.editor.controller.editpart.element.node.removed;

import org.eclipse.draw2d.IFigure;
import org.insightech.er.editor.controller.editpart.element.node.IResizable;
import org.insightech.er.editor.model.diagram_contents.element.node.note.Note;
import org.insightech.er.editor.model.tracking.RemovedNote;
import org.insightech.er.editor.view.figure.NoteFigure;

public class RemovedNoteEditPart extends RemovedNodeElementEditPart implements IResizable {

    /**
     * {@inheritDoc}
     */
    @Override
    protected IFigure createFigure() {
        final NoteFigure noteFigure = new NoteFigure();

        changeFont(noteFigure);

        return noteFigure;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void refreshVisuals() {
        final RemovedNote removedNote = (RemovedNote) getModel();

        final NoteFigure figure = (NoteFigure) getFigure();

        final Note note = (Note) removedNote.getNodeElement();
        figure.setText(note.getText(), note.getColor());

        super.refreshVisuals();
    }
}
