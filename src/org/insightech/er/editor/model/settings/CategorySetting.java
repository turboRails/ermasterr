package org.insightech.er.editor.model.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;

public class CategorySetting implements Serializable, Cloneable {

    private static final long serialVersionUID = -7691417386790834828L;

    private List<Category> allCategories;

    private List<Category> selectedCategories;

    private boolean freeLayout;

    private boolean showReferredTables;

    public boolean isFreeLayout() {
        return freeLayout;
    }

    public void setFreeLayout(final boolean freeLayout) {
        this.freeLayout = freeLayout;
    }

    public boolean isShowReferredTables() {
        return showReferredTables;
    }

    public void setShowReferredTables(final boolean showReferredTables) {
        this.showReferredTables = showReferredTables;
    }

    public CategorySetting() {
        allCategories = new ArrayList<Category>();
        selectedCategories = new ArrayList<Category>();
    }

    public void setSelectedCategories(final List<Category> selectedCategories) {
        this.selectedCategories = selectedCategories;
    }

    public List<Category> getAllCategories() {
        return allCategories;
    }

    public void addCategory(final Category category) {
        allCategories.add(category);
    }

    public void addCategoryAsSelected(final Category category) {
        addCategory(category);
        selectedCategories.add(category);
    }

    public void removeCategory(final Category category) {
        allCategories.remove(category);
        selectedCategories.remove(category);
    }

    public void removeCategory(final int index) {
        allCategories.remove(index);
    }

    public boolean isSelected(final Category tableCategory) {
        if (selectedCategories.contains(tableCategory)) {
            return true;
        }

        return false;
    }

    public List<Category> getSelectedCategories() {
        return selectedCategories;
    }

    public Object clone(final Map<Category, Category> categoryCloneMap) {
        try {
            final CategorySetting clone = (CategorySetting) super.clone();
            clone.allCategories = new ArrayList<Category>();
            clone.selectedCategories = new ArrayList<Category>();

            for (final Category category : allCategories) {
                final Category cloneCategory = categoryCloneMap.get(category);
                clone.allCategories.add(cloneCategory);

                if (selectedCategories.contains(category)) {
                    clone.selectedCategories.add(cloneCategory);
                }
            }

            return clone;

        } catch (final CloneNotSupportedException e) {
            return null;
        }
    }

    public void setAllCategories(final List<Category> allCategories) {
        this.allCategories = allCategories;
    }

}
