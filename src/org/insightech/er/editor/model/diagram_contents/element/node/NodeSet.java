package org.insightech.er.editor.model.diagram_contents.element.node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.diagram_contents.element.node.image.InsertedImage;
import org.insightech.er.editor.model.diagram_contents.element.node.image.InsertedImageSet;
import org.insightech.er.editor.model.diagram_contents.element.node.note.Note;
import org.insightech.er.editor.model.diagram_contents.element.node.note.NoteSet;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableSet;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.model.diagram_contents.element.node.view.ViewSet;

public class NodeSet extends AbstractModel implements Iterable<NodeElement> {

    private static final long serialVersionUID = -120487815554383179L;

    private final NoteSet noteSet;

    private final TableSet tableSet;

    private final ViewSet viewSet;

    private final List<NodeElement> nodeElementList;

    private final InsertedImageSet insertedImageSet;

    public NodeSet() {
        tableSet = new TableSet();
        viewSet = new ViewSet();
        noteSet = new NoteSet();
        insertedImageSet = new InsertedImageSet();

        nodeElementList = new ArrayList<NodeElement>();
    }

    public void sort() {
        tableSet.sort();
        viewSet.sort();
        noteSet.sort();
        insertedImageSet.sort();
    }

    public void addNodeElement(final NodeElement nodeElement) {
        if (nodeElement instanceof ERTable) {
            tableSet.add((ERTable) nodeElement);

        } else if (nodeElement instanceof View) {
            viewSet.add((View) nodeElement);

        } else if (nodeElement instanceof Note) {
            noteSet.add((Note) nodeElement);

        } else if (nodeElement instanceof InsertedImage) {
            insertedImageSet.add((InsertedImage) nodeElement);

        }

        nodeElementList.add(nodeElement);
    }

    public void remove(final NodeElement nodeElement) {
        if (nodeElement instanceof ERTable) {
            tableSet.remove((ERTable) nodeElement);

        } else if (nodeElement instanceof View) {
            viewSet.remove((View) nodeElement);

        } else if (nodeElement instanceof Note) {
            noteSet.remove((Note) nodeElement);

        } else if (nodeElement instanceof InsertedImage) {
            insertedImageSet.remove((InsertedImage) nodeElement);

        }

        nodeElementList.remove(nodeElement);
    }

    public boolean contains(final NodeElement nodeElement) {
        return nodeElementList.contains(nodeElement);
    }

    public void clear() {
        tableSet.getList().clear();
        viewSet.getList().clear();
        noteSet.getList().clear();
        insertedImageSet.getList().clear();

        nodeElementList.clear();
    }

    public boolean isEmpty() {
        return nodeElementList.isEmpty();
    }

    public List<NodeElement> getNodeElementList() {
        return nodeElementList;
    }

    public List<TableView> getTableViewList() {
        final List<TableView> nodeElementList = new ArrayList<TableView>();

        nodeElementList.addAll(tableSet.getList());
        nodeElementList.addAll(viewSet.getList());

        return nodeElementList;
    }

    @Override
    public Iterator<NodeElement> iterator() {
        return getNodeElementList().iterator();
    }

    public ViewSet getViewSet() {
        return viewSet;
    }

    public NoteSet getNoteSet() {
        return noteSet;
    }

    public TableSet getTableSet() {
        return tableSet;
    }

    public InsertedImageSet getInsertedImageSet() {
        return insertedImageSet;
    }
}
