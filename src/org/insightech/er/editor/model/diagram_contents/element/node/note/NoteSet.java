package org.insightech.er.editor.model.diagram_contents.element.node.note;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ObjectListModel;

public class NoteSet extends AbstractModel implements ObjectListModel, Iterable<Note> {

    private static final long serialVersionUID = -7000722010136664297L;

    private List<Note> noteList;

    public NoteSet() {
        noteList = new ArrayList<Note>();
    }

    public void sort() {
        Collections.sort(noteList);
    }

    public void add(final Note note) {
        noteList.add(note);
    }

    public int remove(final Note note) {
        final int index = noteList.indexOf(note);
        noteList.remove(index);

        return index;
    }

    public List<Note> getList() {
        return noteList;
    }

    @Override
    public Iterator<Note> iterator() {
        return noteList.iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NoteSet clone() {
        final NoteSet noteSet = (NoteSet) super.clone();
        final List<Note> newNoteList = new ArrayList<Note>();

        for (final Note note : noteList) {
            final Note newNote = (Note) note.clone();
            newNoteList.add(newNote);
        }

        noteSet.noteList = newNoteList;

        return noteSet;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getName() {
        return ResourceString.getResourceString("label.object.type.note_list");
    }

    @Override
    public String getObjectType() {
        return "list";
    }

}
