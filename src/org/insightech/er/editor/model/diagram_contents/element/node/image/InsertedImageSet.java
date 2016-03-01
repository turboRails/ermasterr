package org.insightech.er.editor.model.diagram_contents.element.node.image;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ObjectListModel;

public class InsertedImageSet extends AbstractModel implements ObjectListModel, Iterable<InsertedImage> {

    private static final long serialVersionUID = 6136074447375448999L;

    private List<InsertedImage> insertedImageList;

    public InsertedImageSet() {
        insertedImageList = new ArrayList<InsertedImage>();
    }

    public void sort() {
        Collections.sort(insertedImageList);
    }

    public void add(final InsertedImage insertedImage) {
        insertedImageList.add(insertedImage);
    }

    public int remove(final InsertedImage insertedImage) {
        final int index = insertedImageList.indexOf(insertedImage);
        insertedImageList.remove(index);

        return index;
    }

    public List<InsertedImage> getList() {
        return insertedImageList;
    }

    @Override
    public Iterator<InsertedImage> iterator() {
        return insertedImageList.iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InsertedImageSet clone() {
        final InsertedImageSet insertedImageSet = (InsertedImageSet) super.clone();
        final List<InsertedImage> newInsertedImageList = new ArrayList<InsertedImage>();

        for (final InsertedImage insertedImage : insertedImageList) {
            final InsertedImage newInsertedImage = (InsertedImage) insertedImage.clone();
            newInsertedImageList.add(newInsertedImage);
        }

        insertedImageSet.insertedImageList = newInsertedImageList;

        return insertedImageSet;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getObjectType() {
        return "list";
    }

}
