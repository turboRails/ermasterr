package org.insightech.er.editor.controller.editpart.outline;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;

public abstract class AbstractOutlineEditPart extends AbstractTreeEditPart implements PropertyChangeListener {

    /**
     * {@inheritDoc}
     */
    @Override
    public void activate() {
        super.activate();
        ((AbstractModel) getModel()).addPropertyChangeListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deactivate() {
        ((AbstractModel) getModel()).removePropertyChangeListener(this);
        super.deactivate();
    }

    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        refreshOutline();
    }

    public void refreshOutline() {
        refreshChildren();
        refreshVisuals();

        for (final Object child : getChildren()) {
            final AbstractOutlineEditPart part = (AbstractOutlineEditPart) child;
            part.refreshOutline();
        }
    }

    @Override
    public void refresh() {}

    /**
     * {@inheritDoc}
     */
    @Override
    final public void refreshVisuals() {
        refreshOutlineVisuals();
    }

    protected ERDiagram getDiagram() {
        return (ERDiagram) getRoot().getContents().getModel();
    }

    protected Category getCurrentCategory() {
        return getDiagram().getCurrentCategory();
    }

    abstract protected void refreshOutlineVisuals();

    protected void execute(final Command command) {
        getViewer().getEditDomain().getCommandStack().execute(command);
    }
}
