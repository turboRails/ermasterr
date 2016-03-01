package org.insightech.er.editor.controller.editpart.outline.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.tools.SelectEditPartTracker;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ImageKey;
import org.insightech.er.editor.controller.editpart.DeleteableEditPart;
import org.insightech.er.editor.controller.editpart.element.node.ERTableEditPart;
import org.insightech.er.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.insightech.er.editor.controller.editpolicy.element.node.NodeElementComponentEditPolicy;
import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.dialog.element.table.TableDialog;

public class TableOutlineEditPart extends AbstractOutlineEditPart implements DeleteableEditPart {

    /**
     * {@inheritDoc}
     */
    @Override
    protected List getModelChildren() {
        final List<AbstractModel> children = new ArrayList<AbstractModel>();

        final ERTable table = (ERTable) getModel();

        final List<Relation> relationList = new ArrayList<Relation>();

        final Category category = getCurrentCategory();

        for (final Relation relation : table.getIncomingRelations()) {
            if (category == null || category.contains(relation.getSource())) {
                relationList.add(relation);
            }
        }

        Collections.sort(relationList);

        children.addAll(relationList);
        children.addAll(table.getIndexes());

        return children;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void refreshOutlineVisuals() {
        final ERTable model = (ERTable) getModel();

        final ERDiagram diagram = (ERDiagram) getRoot().getContents().getModel();

        String name = null;

        final int viewMode = diagram.getDiagramContents().getSettings().getOutlineViewMode();

        if (viewMode == Settings.VIEW_MODE_PHYSICAL) {
            if (model.getPhysicalName() != null) {
                name = model.getPhysicalName();

            } else {
                name = "";
            }

        } else if (viewMode == Settings.VIEW_MODE_LOGICAL) {
            if (model.getLogicalName() != null) {
                name = model.getLogicalName();

            } else {
                name = "";
            }

        } else {
            if (model.getLogicalName() != null) {
                name = model.getLogicalName();

            } else {
                name = "";
            }

            name += "/";

            if (model.getPhysicalName() != null) {
                name += model.getPhysicalName();

            }
        }

        setWidgetText(diagram.filter(name));
        setWidgetImage(ERDiagramActivator.getImage(ImageKey.TABLE));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new NodeElementComponentEditPolicy());
        // this.installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performRequest(final Request request) {
        final ERTable table = (ERTable) getModel();
        final ERDiagram diagram = (ERDiagram) getRoot().getContents().getModel();

        if (request.getType().equals(RequestConstants.REQ_OPEN)) {
            final ERTable copyTable = table.copyData();

            final TableDialog dialog = new TableDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), getViewer(), copyTable);

            if (dialog.open() == IDialogConstants.OK_ID) {
                final CompoundCommand command = ERTableEditPart.createChangeTablePropertyCommand(diagram, table, copyTable);

                execute(command.unwrap());
            }
        }

        super.performRequest(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DragTracker getDragTracker(final Request req) {
        return new SelectEditPartTracker(this);
    }

    @Override
    public boolean isDeleteable() {
        return true;
    }
}
