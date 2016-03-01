package org.insightech.er.editor.controller.command.tracking;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.tracking.ChangeTrackingList;

/**
 * 変更履歴計算コマンド
 */
public class ResetChangeTrackingCommand extends AbstractCommand {

    private final ERDiagram diagram;
    private final ChangeTrackingList changeTrackingList;

    private final boolean oldCalculated;

    public ResetChangeTrackingCommand(final ERDiagram diagram) {
        this.diagram = diagram;
        changeTrackingList = this.diagram.getChangeTrackingList();
        oldCalculated = changeTrackingList.isCalculated();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        changeTrackingList.setCalculated(false);
        diagram.refresh();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        changeTrackingList.setCalculated(oldCalculated);
        diagram.refresh();
    }
}
