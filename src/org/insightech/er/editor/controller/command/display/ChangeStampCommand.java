package org.insightech.er.editor.controller.command.display;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.model_properties.ModelProperties;

public class ChangeStampCommand extends AbstractCommand {

    private final ERDiagram diagram;

    private final boolean oldStamp;

    private final boolean newStamp;

    private final ModelProperties modelProperties;

    public ChangeStampCommand(final ERDiagram diagram, final boolean isDisplay) {
        this.diagram = diagram;
        modelProperties = this.diagram.getDiagramContents().getSettings().getModelProperties();
        newStamp = isDisplay;
        oldStamp = modelProperties.isDisplay();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        modelProperties.setDisplay(newStamp);
        modelProperties.refreshVisuals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        modelProperties.setDisplay(oldStamp);
        modelProperties.refreshVisuals();
    }
}
