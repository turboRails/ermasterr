package org.insightech.er.editor.controller.command.diagram_contents.element.node.model_properties;

import java.util.List;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.model_properties.ModelProperties;
import org.insightech.er.util.NameValue;

public class ChangeModelPropertiesCommand extends AbstractCommand {

    private final List<NameValue> oldProperties;

    private final List<NameValue> newProperties;

    private final ModelProperties modelProperties;

    public ChangeModelPropertiesCommand(final ERDiagram diagram, final ModelProperties properties) {
        modelProperties = diagram.getDiagramContents().getSettings().getModelProperties();

        oldProperties = modelProperties.getProperties();
        newProperties = properties.getProperties();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        modelProperties.setProperties(newProperties);
        modelProperties.refresh();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        modelProperties.setProperties(oldProperties);
        modelProperties.refresh();
    }

}
