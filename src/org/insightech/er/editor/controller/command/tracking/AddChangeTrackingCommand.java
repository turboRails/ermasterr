package org.insightech.er.editor.controller.command.tracking;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.tracking.ChangeTracking;

/**
 * 変更履歴追加コマンド
 */
public class AddChangeTrackingCommand extends AbstractCommand {

    private final ERDiagram diagram;

    // 変更履歴
    private final ChangeTracking changeTracking;

    /**
     * 変更履歴追加コマンドを作成します。
     * 
     * @param diagram
     * @param nodeElements
     */
    public AddChangeTrackingCommand(final ERDiagram diagram, final ChangeTracking changeTracking) {
        this.diagram = diagram;

        this.changeTracking = changeTracking;
    }

    /**
     * 変更履歴追加処理を実行する
     */
    @Override
    protected void doExecute() {
        diagram.getChangeTrackingList().addChangeTracking(changeTracking);
    }

    /**
     * 変更履歴追加処理を元に戻す
     */
    @Override
    protected void doUndo() {
        diagram.getChangeTrackingList().removeChangeTracking(changeTracking);
    }

}
