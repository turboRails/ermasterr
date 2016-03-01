package org.insightech.er.editor.view.action.search;

import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.view.action.AbstractBaseAction;
import org.insightech.er.editor.view.dialog.search.SearchDialog;

public class SearchAction extends AbstractBaseAction {

    public static final String ID = SearchAction.class.getName();

    public SearchAction(final ERDiagramEditor editor) {
        super(ID, ResourceString.getResourceString("action.title.find"), editor);
        setActionDefinitionId("org.eclipse.ui.edit.findReplace");
        setImageDescriptor(ERDiagramActivator.getImageDescriptor(ImageKey.FIND));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(final Event event) {
        final ERDiagram diagram = getDiagram();

        final SearchDialog dialog = new SearchDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), getGraphicalViewer(), diagram);

        dialog.open();
    }

}
