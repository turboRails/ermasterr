package org.insightech.er.editor.view.contributor;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.model.ERDiagram;

public class ERDiagramMultiPageEditorActionBarContributor extends MultiPageEditorActionBarContributor {

    private ZoomComboContributionItem zoomComboContributionItem;

    public ERDiagramMultiPageEditorActionBarContributor() {}

    @Override
    public void setActivePage(final IEditorPart activeEditor) {
        final IActionBars actionBars = getActionBars();

        actionBars.clearGlobalActionHandlers();
        actionBars.getToolBarManager().removeAll();

        final ERDiagramEditor editor = (ERDiagramEditor) activeEditor;

        final ERDiagramActionBarContributor activeContributor = editor.getActionBarContributor();
        if (zoomComboContributionItem == null) {
            zoomComboContributionItem = new ZoomComboContributionItem(getPage());
        }

        activeContributor.setActiveEditor(editor);

        final EditPart editPart = editor.getGraphicalViewer().getContents();
        final ERDiagram diagram = (ERDiagram) editPart.getModel();

        activeContributor.contributeToToolBar(diagram, actionBars.getToolBarManager(), zoomComboContributionItem);

        final ZoomComboContributionItem item = (ZoomComboContributionItem) getActionBars().getToolBarManager().find(GEFActionConstants.ZOOM_TOOLBAR_WIDGET);
        if (item != null) {
            final ZoomManager zoomManager = (ZoomManager) editor.getAdapter(ZoomManager.class);
            item.setZoomManager(zoomManager);
        }

        actionBars.updateActionBars();
    }

}
