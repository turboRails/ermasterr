package org.insightech.er.editor.controller.command.category;

import java.util.List;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.settings.CategorySetting;

public class ChangeShowReferredTablesCommand extends AbstractCommand {

    private final ERDiagram diagram;

    private final boolean oldShowReferredTables;

    private final boolean newShowReferredTables;

    private final CategorySetting categorySettings;

    public ChangeShowReferredTablesCommand(final ERDiagram diagram, final boolean isShowReferredTables) {
        this.diagram = diagram;
        categorySettings = this.diagram.getDiagramContents().getSettings().getCategorySetting();

        newShowReferredTables = isShowReferredTables;
        oldShowReferredTables = categorySettings.isFreeLayout();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        categorySettings.setShowReferredTables(newShowReferredTables);
        refreshReferredNodeElementList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        categorySettings.setShowReferredTables(oldShowReferredTables);
        refreshReferredNodeElementList();
    }

    private void refreshReferredNodeElementList() {
        final List<NodeElement> nodeElementList = diagram.getCurrentCategory().getContents();

        for (final NodeElement nodeElement : nodeElementList) {
            for (final ConnectionElement connection : nodeElement.getIncomings()) {
                final NodeElement referredNodeElement = connection.getSource();

                if (nodeElementList.contains(referredNodeElement)) {
                    continue;
                }

                referredNodeElement.refreshVisuals();
                connection.refreshVisuals();
            }
        }
    }
}
