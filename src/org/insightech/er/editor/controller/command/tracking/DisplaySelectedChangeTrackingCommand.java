package org.insightech.er.editor.controller.command.tracking;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.DiagramContents;

/**
 * 変更履歴の置換コマンド
 */
public class DisplaySelectedChangeTrackingCommand extends AbstractCommand {

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
    public DisplaySelectedChangeTrackingCommand(final ERDiagram diagram, final DiagramContents newDiagramContents) {
        this.diagram = diagram;

        oldDiagramContents = this.diagram.getDiagramContents();
        this.newDiagramContents = newDiagramContents;
    }

    /**
     * 置換処理を実行する
     */
    @Override
    protected void doExecute() {
        diagram.replaceContents(newDiagramContents);
        diagram.refresh();
    }

    /**
     * 置換処理を元に戻す
     */
    @Override
    protected void doUndo() {
        diagram.replaceContents(oldDiagramContents);
        diagram.refresh();
    }

}
