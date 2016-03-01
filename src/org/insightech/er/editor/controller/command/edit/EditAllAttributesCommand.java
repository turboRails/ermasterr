package org.insightech.er.editor.controller.command.edit;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.DiagramContents;

/**
 * DiagramContents の置換コマンド
 */
public class EditAllAttributesCommand extends AbstractCommand {

    private final ERDiagram diagram;

    private final DiagramContents oldDiagramContents;

    private final DiagramContents newDiagramContents;

    /**
     * 置換コマンドを作成します。
     * 
     * @param diagram
     * @param nodeElements
     * @param columnGroups
     */
    public EditAllAttributesCommand(final ERDiagram diagram, final DiagramContents newDiagramContents) {
        this.diagram = diagram;

        oldDiagramContents = this.diagram.getDiagramContents();
        this.newDiagramContents = newDiagramContents;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        diagram.replaceContents(newDiagramContents);
        diagram.refresh();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        diagram.replaceContents(oldDiagramContents);
        diagram.refresh();
    }

}
