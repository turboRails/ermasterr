package org.insightech.er.editor.model.diagram_contents.element.node.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ObjectListModel;

public class ViewSet extends AbstractModel implements ObjectListModel, Iterable<View> {

    private static final long serialVersionUID = -120487815554383179L;

    private List<View> viewList;

    public ViewSet() {
        viewList = new ArrayList<View>();
    }

    public void sort() {
        Collections.sort(viewList);
    }

    public void add(final View view) {
        viewList.add(view);
    }

    public void add(final int index, final View view) {
        viewList.add(index, view);
    }

    public int remove(final View view) {
        final int index = viewList.indexOf(view);
        viewList.remove(index);

        return index;
    }

    public List<View> getList() {
        ;
        return viewList;
    }

    @Override
    public Iterator<View> iterator() {
        return viewList.iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewSet clone() {
        final ViewSet viewSet = (ViewSet) super.clone();
        final List<View> newViewList = new ArrayList<View>();

        for (final View view : viewList) {
            final View newView = view.clone();
            newViewList.add(newView);
        }

        viewSet.viewList = newViewList;

        return viewSet;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getName() {
        return ResourceString.getResourceString("label.object.type.view_list");
    }

    @Override
    public String getObjectType() {
        return "list";
    }
}
