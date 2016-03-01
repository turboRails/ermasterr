package org.insightech.er.editor.controller.editpart.outline.index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;

public class IndexSetOutlineEditPart extends AbstractOutlineEditPart {

    /**
     * {@inheritDoc}
     */
    @Override
    protected List getModelChildren() {
        final List<Index> children = new ArrayList<Index>();

        final ERDiagram diagram = getDiagram();
        final Category category = getCurrentCategory();

        for (final ERTable table : diagram.getDiagramContents().getContents().getTableSet()) {
            if (category == null || category.contains(table)) {
                children.addAll(table.getIndexes());
            }
        }

        Collections.sort(children);

        return children;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void refreshOutlineVisuals() {
        setWidgetText(ResourceString.getResourceString("label.index") + " (" + getModelChildren().size() + ")");
        setWidgetImage(ERDiagramActivator.getImage(ImageKey.DICTIONARY));
    }

}
