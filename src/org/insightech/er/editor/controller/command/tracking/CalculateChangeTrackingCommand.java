package org.insightech.er.editor.controller.command.tracking;

import java.util.List;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeSet;
import org.insightech.er.editor.model.tracking.ChangeTrackingList;
import org.insightech.er.editor.model.tracking.RemovedNodeElement;
import org.insightech.er.editor.model.tracking.UpdatedNodeElement;

/**
 * 変更履歴計算コマンド
 */
public class CalculateChangeTrackingCommand extends AbstractCommand {

    private final ERDiagram diagram;

    private final NodeSet comparison;

    private final ChangeTrackingList changeTrackingList;

    private final List<NodeElement> oldAddedNodeElements;

    private final List<UpdatedNodeElement> oldUpdatedNodeElements;

    private final List<RemovedNodeElement> oldRemovedNodeElements;

    /**
     * 変更履歴計算コマンドを作成します。
     * 
     * @param diagram
     * @param comparison
     */
    public CalculateChangeTrackingCommand(final ERDiagram diagram, final NodeSet comparison) {
        this.diagram = diagram;
        this.comparison = comparison;

        changeTrackingList = this.diagram.getChangeTrackingList();

        oldAddedNodeElements = changeTrackingList.getAddedNodeElementSet();
        oldUpdatedNodeElements = changeTrackingList.getUpdatedNodeElementSet();
        oldRemovedNodeElements = changeTrackingList.getRemovedNodeElementSet();
    }

    /**
     * 変更履歴計算処理を実行する
     */
    @Override
    protected void doExecute() {
        changeTrackingList.calculateUpdatedNodeElementSet(comparison, diagram.getDiagramContents().getContents());
        diagram.refresh();
    }

    /**
     * 変更履歴計算処理を元に戻す
     */
    @Override
    protected void doUndo() {
        changeTrackingList.restore(oldAddedNodeElements, oldUpdatedNodeElements, oldRemovedNodeElements);
        diagram.refresh();
    }
}
