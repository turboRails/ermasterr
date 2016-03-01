package org.insightech.er.editor.controller.editpart.outline.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableSet;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.settings.Settings;

public class TableSetOutlineEditPart extends AbstractOutlineEditPart {

    /**
     * {@inheritDoc}
     */
    @Override
    protected List getModelChildren() {
        final TableSet tableSet = (TableSet) getModel();

        final List<ERTable> list = new ArrayList<ERTable>();

        final Category category = getCurrentCategory();
        for (final ERTable table : tableSet) {
            if (category == null || category.contains(table)) {
                list.add(table);
            }
        }

        if (getDiagram().getDiagramContents().getSettings().getViewOrderBy() == Settings.VIEW_MODE_LOGICAL) {
            Collections.sort(list, TableView.LOGICAL_NAME_COMPARATOR);

        } else {
            Collections.sort(list, TableView.PHYSICAL_NAME_COMPARATOR);

        }

        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void refreshOutlineVisuals() {
        setWidgetText(ResourceString.getResourceString("label.table") + " (" + getModelChildren().size() + ")");
        setWidgetImage(ERDiagramActivator.getImage(ImageKey.DICTIONARY));
    }

}
