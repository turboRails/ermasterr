package org.insightech.er.editor.controller.editpart.outline.view;

import java.util.Collections;
import java.util.List;

import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.model.diagram_contents.element.node.view.ViewSet;
import org.insightech.er.editor.model.settings.Settings;

public class ViewSetOutlineEditPart extends AbstractOutlineEditPart {

    /**
     * {@inheritDoc}
     */
    @Override
    protected List getModelChildren() {
        final ViewSet viewSet = (ViewSet) getModel();

        final List<View> list = viewSet.getList();

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
        setWidgetText(ResourceString.getResourceString("label.view") + " (" + getModelChildren().size() + ")");
        setWidgetImage(ERDiagramActivator.getImage(ImageKey.DICTIONARY));
    }

}
