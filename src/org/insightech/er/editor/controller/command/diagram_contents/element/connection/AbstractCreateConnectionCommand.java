package org.insightech.er.editor.controller.command.diagram_contents.element.connection;

import org.eclipse.gef.EditPart;
import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;

public abstract class AbstractCreateConnectionCommand extends AbstractCommand {

    protected EditPart source;

    protected EditPart target;

    public AbstractCreateConnectionCommand() {
        super();
    }

    public void setSource(final EditPart source) {
        this.source = source;
    }

    public void setTarget(final EditPart target) {
        this.target = target;
    }

    public NodeElement getSourceModel() {
        return (NodeElement) source.getModel();
    }

    public NodeElement getTargetModel() {
        return (NodeElement) target.getModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canExecute() {
        return source != null && target != null && source != target;
    }

    abstract public String validate();

}
