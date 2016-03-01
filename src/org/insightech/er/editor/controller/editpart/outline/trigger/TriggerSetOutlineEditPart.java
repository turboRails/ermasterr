package org.insightech.er.editor.controller.editpart.outline.trigger;

import java.util.List;

import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.TriggerSet;

public class TriggerSetOutlineEditPart extends AbstractOutlineEditPart {

    /**
     * {@inheritDoc}
     */
    @Override
    protected List getModelChildren() {
        final TriggerSet triggerSet = (TriggerSet) getModel();

        return triggerSet.getObjectList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void refreshOutlineVisuals() {
        setWidgetText(ResourceString.getResourceString("label.trigger") + " (" + getModelChildren().size() + ")");
        setWidgetImage(ERDiagramActivator.getImage(ImageKey.DICTIONARY));
    }

}
