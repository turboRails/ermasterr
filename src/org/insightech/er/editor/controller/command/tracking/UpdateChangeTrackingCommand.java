package org.insightech.er.editor.controller.command.tracking;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.tracking.ChangeTracking;

/**
 * 変更履歴更新コマンド
 */
public class UpdateChangeTrackingCommand extends AbstractCommand {

    // 変更履歴
    private final ChangeTracking changeTracking;

    private final String oldComment;

    private final String newComment;

    /**
     * 変更履歴更新コマンドを作成します。
     * 
     * @param changeTracking
     * @param comment
     */
    public UpdateChangeTrackingCommand(final ChangeTracking changeTracking, final String comment) {
        this.changeTracking = changeTracking;

        oldComment = changeTracking.getComment();
        newComment = comment;
    }

    /**
     * 変更履歴更新処理を実行する
     */
    @Override
    protected void doExecute() {
        changeTracking.setComment(newComment);
    }

    /**
     * 変更履歴更新処理を元に戻す
     */
    @Override
    protected void doUndo() {
        changeTracking.setComment(oldComment);
    }

}
