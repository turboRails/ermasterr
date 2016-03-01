package org.insightech.er.editor.controller.editpart.element;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;

public abstract class AbstractModelEditPart extends AbstractGraphicalEditPart implements PropertyChangeListener {

    private static Logger logger = Logger.getLogger(AbstractModelEditPart.class.getName());

    private static final boolean DEBUG = false;

    /**
     * {@inheritDoc}
     */
    @Override
    public void activate() {
        super.activate();

        final AbstractModel model = (AbstractModel) getModel();
        model.addPropertyChangeListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deactivate() {
        final AbstractModel model = (AbstractModel) getModel();
        model.removePropertyChangeListener(this);

        super.deactivate();
    }

    protected ERDiagram getDiagram() {
        return (ERDiagram) getRoot().getContents().getModel();
    }

    protected Category getCurrentCategory() {
        return getDiagram().getCurrentCategory();
    }

    protected void executeCommand(final Command command) {
        getViewer().getEditDomain().getCommandStack().execute(command);
    }

    @Override
    public final void propertyChange(final PropertyChangeEvent event) {
        try {
            if (DEBUG) {
                logger.log(Level.INFO, this.getClass().getName() + ":" + event.getPropertyName() + ":" + event.toString());
            }

            doPropertyChange(event);

        } catch (final Exception e) {
            ERDiagramActivator.showExceptionDialog(e);
        }
    }

    protected void doPropertyChange(final PropertyChangeEvent event) {
        if (event.getPropertyName().equals("refreshVisuals")) {
            refreshVisuals();

        } else if (event.getPropertyName().equals("refresh")) {
            refresh();

        }
    }

    @Override
    public void refresh() {
        refreshChildren();
        refreshVisuals();

        refreshSourceConnections();
        refreshTargetConnections();
    }

}
