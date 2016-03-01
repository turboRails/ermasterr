package org.insightech.er.editor.model.diagram_contents.not_element.group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.insightech.er.editor.model.AbstractModel;

public class GroupSet extends AbstractModel implements Iterable<ColumnGroup> {

    private static final long serialVersionUID = 6192280105150073360L;

    private String database;

    private final List<ColumnGroup> groups;

    public GroupSet() {
        groups = new ArrayList<ColumnGroup>();
    }

    public void sort() {
        Collections.sort(groups);
    }

    public void add(final ColumnGroup group) {
        groups.add(group);
    }

    public void remove(final ColumnGroup group) {
        groups.remove(group);
    }

    @Override
    public Iterator<ColumnGroup> iterator() {
        return groups.iterator();
    }

    public List<ColumnGroup> getGroupList() {
        return groups;
    }

    public void clear() {
        groups.clear();
    }

    public boolean contains(final ColumnGroup group) {
        return groups.contains(group);
    }

    public ColumnGroup get(final int index) {
        return groups.get(index);
    }

    public ColumnGroup find(final ColumnGroup group) {
        final int index = groups.indexOf(group);

        if (index != -1) {
            return groups.get(groups.indexOf(group));
        }

        return null;
    }

    public ColumnGroup findSame(final ColumnGroup group) {
        for (final ColumnGroup columnGroup : groups) {
            if (columnGroup == group) {
                return columnGroup;
            }
        }

        return null;
    }

    public int indexOf(final ColumnGroup group) {
        return groups.indexOf(group);
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(final String database) {
        this.database = database;
    }
}
