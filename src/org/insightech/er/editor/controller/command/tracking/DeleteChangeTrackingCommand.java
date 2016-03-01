package org.insightech.er.editor.controller.command.tracking;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.tracking.ChangeTracking;
import org.insightech.er.editor.model.tracking.ChangeTrackingList;

/**
 * 変更履歴削除コマンド
 */
public class DeleteChangeTrackingCommand extends AbstractCommand {

    private final ERDiagram diagram;

    private final ChangeTracking changeTracking;

    private final int index;

    private final ChangeTrackingList changeTrackingList;

    /**
     * 変更履歴削除コマンドを作成します。
     * 
     * @param diagram
     * @param index
     */
    public DeleteChangeTrackingCommand(final ERDiagram diagram, final int index) {
        this.diagram = diagram;
        changeTrackingList = this.diagram.getChangeTrackingList();

        this.index = index;
        changeTracking = changeTrackingList.get(index);
    }

    /**
     * 変更履歴削除処理を実行する
     */
    @Override
    protected void doExecute() {
        changeTrackingList.removeChangeTracking(index);

        if (changeTrackingList.isCalculated()) {
            changeTrackingList.setCalculated(false);
            diagram.refresh();
        }
    }

    /**
     * 変更履歴削除処理を元に戻す
     */
    @Override
    protected void doUndo() {
        changeTrackingList.addChangeTracking(index, changeTracking);
    }

}
