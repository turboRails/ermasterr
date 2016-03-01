package org.insightech.er.editor.model.tracking;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeSet;
import org.insightech.er.editor.model.diagram_contents.element.node.note.Note;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.util.Check;

public class ChangeTrackingList implements Serializable {

    private static final long serialVersionUID = 3290113276160681941L;

    private final List<ChangeTracking> changeTrackingList;

    private List<NodeElement> addedNodeElements;

    private List<UpdatedNodeElement> updatedNodeElements;

    private List<RemovedNodeElement> removedNodeElements;

    private boolean calculated;

    public ChangeTrackingList() {
        changeTrackingList = new ArrayList<ChangeTracking>();
        addedNodeElements = new ArrayList<NodeElement>();
        updatedNodeElements = new ArrayList<UpdatedNodeElement>();
        removedNodeElements = new ArrayList<RemovedNodeElement>();
    }

    public void clear() {
        changeTrackingList.clear();
        addedNodeElements.clear();
        updatedNodeElements.clear();
        removedNodeElements.clear();
    }

    public void addChangeTracking(final ChangeTracking changeTracking) {
        changeTrackingList.add(changeTracking);
    }

    public void addChangeTracking(final int index, final ChangeTracking changeTracking) {
        changeTrackingList.add(index, changeTracking);
    }

    public void removeChangeTracking(final int index) {
        if (index >= 0 && index < changeTrackingList.size()) {
            changeTrackingList.remove(index);
        }
    }

    public void removeChangeTracking(final ChangeTracking changeTracking) {
        changeTrackingList.remove(changeTracking);
    }

    public List<ChangeTracking> getList() {
        return changeTrackingList;
    }

    public ChangeTracking get(final int index) {
        return changeTrackingList.get(index);
    }

    public List<UpdatedNodeElement> getUpdatedNodeElementSet() {
        return updatedNodeElements;
    }

    public List<NodeElement> getAddedNodeElementSet() {
        return addedNodeElements;
    }

    public List<RemovedNodeElement> getRemovedNodeElementSet() {
        return removedNodeElements;
    }

    public void setCalculated(final boolean calculated) {
        this.calculated = calculated;
    }

    public boolean isCalculated() {
        return calculated;
    }

    public void calculateUpdatedNodeElementSet(final NodeSet oldList, final NodeSet newList) {
        calculated = true;

        addedNodeElements.clear();
        updatedNodeElements.clear();
        removedNodeElements.clear();

        final List<Note> oldNotes = new ArrayList<Note>();
        final List<ERTable> oldTables = new ArrayList<ERTable>();

        for (final NodeElement nodeElement : oldList) {
            if (nodeElement instanceof Note) {
                final Note note = (Note) nodeElement;
                oldNotes.add(note);

            } else if (nodeElement instanceof ERTable) {
                oldTables.add((ERTable) nodeElement);
            }
        }

        for (final NodeElement newNodeElement : newList) {
            if (newNodeElement instanceof Note) {
                final Note newNote = (Note) newNodeElement;
                final String newNoteText = newNote.getText();

                boolean exists = false;

                for (final Iterator<Note> iter = oldNotes.iterator(); iter.hasNext();) {
                    final Note oldNote = iter.next();

                    if (oldNote.getText() != null && oldNote.getText().equals(newNoteText)) {
                        iter.remove();
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    addedNodeElements.add(newNote);
                }

            } else if (newNodeElement instanceof ERTable) {
                final ERTable newTable = (ERTable) newNodeElement;
                ERTable oldTable = null;

                boolean exists = false;

                for (final ERTable table : oldTables) {
                    oldTable = table;

                    if (oldTable.getPhysicalName().equals(newTable.getPhysicalName())) {
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    addedNodeElements.add(newTable);

                } else {
                    oldTables.remove(oldTable);

                    final Set<NormalColumn> addedColumns = new HashSet<NormalColumn>();
                    final Set<NormalColumn> updatedColumns = new HashSet<NormalColumn>();

                    final List<NormalColumn> oldColumns = new ArrayList<NormalColumn>(oldTable.getExpandedColumns());

                    for (final NormalColumn newColumn : newTable.getExpandedColumns()) {
                        Column originalColumn = null;

                        for (final NormalColumn oldColumn : oldColumns) {
                            if (newColumn.getName().equals(oldColumn.getName())) {
                                originalColumn = oldColumn;
                                oldColumns.remove(oldColumn);

                                if (!compareColumn(oldColumn, newColumn)) {
                                    updatedColumns.add(newColumn);
                                }

                                break;
                            }
                        }

                        if (originalColumn == null) {
                            addedColumns.add(newColumn);
                        }
                    }

                    if (!addedColumns.isEmpty() || !updatedColumns.isEmpty() || !oldColumns.isEmpty()) {
                        final UpdatedNodeElement updatedNodeElement = new UpdatedNodeElement(newTable);
                        updatedNodeElements.add(updatedNodeElement);

                        updatedNodeElement.setAddedColumns(addedColumns);
                        updatedNodeElement.setUpdatedColumns(updatedColumns);
                        updatedNodeElement.setRemovedColumns(oldColumns);
                    }
                }
            }
        }

        for (final Note oldNote : oldNotes) {
            removedNodeElements.add(new RemovedNote(oldNote));
        }

        for (final ERTable oldTable : oldTables) {
            removedNodeElements.add(new RemovedERTable(oldTable));
        }

    }

    private boolean compareColumn(final NormalColumn oldColumn, final NormalColumn newColumn) {
        if (!Check.equals(oldColumn.getPhysicalName(), newColumn.getPhysicalName())) {
            return false;
        }
        if (!Check.equals(oldColumn.getTypeData().getDecimal(), newColumn.getTypeData().getDecimal())) {
            return false;
        }
        if (!Check.equals(oldColumn.getDefaultValue(), newColumn.getDefaultValue())) {
            return false;
        }
        if (!Check.equals(oldColumn.getDescription(), newColumn.getDescription())) {
            return false;
        }
        if (!Check.equals(oldColumn.getTypeData().getLength(), newColumn.getTypeData().getLength())) {
            return false;
        }
        if (!Check.equals(oldColumn.getType(), newColumn.getType())) {
            return false;
        }
        if (oldColumn.isAutoIncrement() != newColumn.isAutoIncrement()) {
            return false;
        }
        if (oldColumn.isForeignKey() != newColumn.isForeignKey()) {
            return false;
        }
        if (oldColumn.isNotNull() != newColumn.isNotNull()) {
            return false;
        }
        if (oldColumn.isPrimaryKey() != newColumn.isPrimaryKey()) {
            return false;
        }
        if (oldColumn.isUniqueKey() != newColumn.isUniqueKey()) {
            return false;
        }

        return true;
    }

    public UpdatedNodeElement getUpdatedNodeElement(final NodeElement nodeElement) {
        for (final UpdatedNodeElement updatedNodeElement : updatedNodeElements) {
            if (updatedNodeElement.getNodeElement() == nodeElement) {
                return updatedNodeElement;
            }
        }

        return null;
    }

    public boolean isAdded(final NodeElement nodeElement) {
        return addedNodeElements.contains(nodeElement);
    }

    public void restore(final List<NodeElement> addedNodeElements, final List<UpdatedNodeElement> updatedNodeElements, final List<RemovedNodeElement> removedNodeElements) {
        this.addedNodeElements = addedNodeElements;
        this.updatedNodeElements = updatedNodeElements;
        this.removedNodeElements = removedNodeElements;
    }
}
