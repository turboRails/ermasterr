package org.insightech.er.editor.controller.editpart.element.node;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.db.impl.oracle.OracleDBManager;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.table_view.ChangeTableViewPropertyCommand;
import org.insightech.er.editor.controller.command.diagram_contents.not_element.sequence.CreateSequenceCommand;
import org.insightech.er.editor.controller.command.diagram_contents.not_element.sequence.DeleteSequenceCommand;
import org.insightech.er.editor.controller.command.diagram_contents.not_element.trigger.CreateTriggerCommand;
import org.insightech.er.editor.controller.command.diagram_contents.not_element.trigger.DeleteTriggerCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.CopyColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.SequenceSet;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.TriggerSet;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.dialog.element.table.TableDialog;
import org.insightech.er.editor.view.figure.table.TableFigure;
import org.insightech.er.util.Check;

public class ERTableEditPart extends TableViewEditPart implements IResizable {

    /**
     * {@inheritDoc}
     */
    @Override
    protected IFigure createFigure() {
        final ERDiagram diagram = getDiagram();
        final Settings settings = diagram.getDiagramContents().getSettings();

        final TableFigure figure = new TableFigure(settings.getTableStyle());

        this.changeFont(figure);

        return figure;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performRequestOpen() {
        final ERTable table = (ERTable) getModel();
        final ERDiagram diagram = getDiagram();

        final ERTable copyTable = table.copyData();

        final TableDialog dialog = new TableDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), getViewer(), copyTable);

        if (dialog.open() == IDialogConstants.OK_ID) {
            final CompoundCommand command = createChangeTablePropertyCommand(diagram, table, copyTable);

            executeCommand(command.unwrap());
        }
    }

    // // @Override
    // public void doRefreshVisuals() {
    // super.doRefreshVisuals();
    // // this.refreshSelfRelationVisuals();
    // }

    // private void refreshSelfRelationVisuals() {
    // for (int i = 0; i < this.getSourceConnections().size(); i++) {
    // AbstractERDiagramConnectionEditPart connectionEditPart =
    // (AbstractERDiagramConnectionEditPart) this
    // .getSourceConnections().get(i);
    //
    // if (connectionEditPart.getSource() == connectionEditPart
    // .getTarget()) {
    // connectionEditPart.refreshVisuals();
    // }
    // }
    // }

    public static CompoundCommand createChangeTablePropertyCommand(final ERDiagram diagram, final ERTable table, final ERTable copyTable) {
        final CompoundCommand command = new CompoundCommand();

        final ChangeTableViewPropertyCommand changeTablePropertyCommand = new ChangeTableViewPropertyCommand(table, copyTable);
        command.add(changeTablePropertyCommand);

        final String tableName = copyTable.getPhysicalName();

        if (OracleDBManager.ID.equals(diagram.getDatabase()) && !Check.isEmpty(tableName)) {
            final NormalColumn autoIncrementColumn = copyTable.getAutoIncrementColumn();

            if (autoIncrementColumn != null) {
                final String columnName = autoIncrementColumn.getPhysicalName();

                if (!Check.isEmpty(columnName)) {
                    final String triggerName = "TRI_" + tableName + "_" + columnName;
                    final String sequenceName = "SEQ_" + tableName + "_" + columnName;

                    final TriggerSet triggerSet = diagram.getDiagramContents().getTriggerSet();
                    final SequenceSet sequenceSet = diagram.getDiagramContents().getSequenceSet();

                    if (!triggerSet.contains(triggerName) || !sequenceSet.contains(sequenceName)) {
                        if (ERDiagramActivator.showConfirmDialog("dialog.message.confirm.create.autoincrement.trigger")) {
                            if (!triggerSet.contains(triggerName)) {
                                // トリガーの作成
                                final Trigger trigger = new Trigger();
                                trigger.setName(triggerName);
                                trigger.setSql("BEFORE INSERT ON " + tableName + "\r\nFOR EACH ROW" + "\r\nBEGIN" + "\r\n\tSELECT " + sequenceName + ".nextval\r\n\tINTO :new." + columnName + "\r\n\tFROM dual;" + "\r\nEND");

                                final CreateTriggerCommand createTriggerCommand = new CreateTriggerCommand(diagram, trigger);
                                command.add(createTriggerCommand);
                            }

                            if (!sequenceSet.contains(sequenceName)) {
                                // シーケンスの作成
                                final Sequence sequence = new Sequence();
                                sequence.setName(sequenceName);
                                sequence.setStart(1L);
                                sequence.setIncrement(1);

                                final CreateSequenceCommand createSequenceCommand = new CreateSequenceCommand(diagram, sequence);
                                command.add(createSequenceCommand);
                            }
                        }
                    }
                }
            }

            final NormalColumn oldAutoIncrementColumn = table.getAutoIncrementColumn();

            if (oldAutoIncrementColumn != null) {
                final String oldTableName = table.getPhysicalName();
                if (autoIncrementColumn == null || ((CopyColumn) autoIncrementColumn).getOriginalColumn() != oldAutoIncrementColumn
                        || !tableName.equals(oldTableName)) {   // [ermasterr] Add condition for when table name changed
                    final String columnName = oldAutoIncrementColumn.getPhysicalName();

                    if (!Check.isEmpty(columnName)) {
                        final String triggerName = "TRI_" + oldTableName + "_" + columnName;
                        final String sequenceName = "SEQ_" + oldTableName + "_" + columnName;

                        final TriggerSet triggerSet = diagram.getDiagramContents().getTriggerSet();
                        final SequenceSet sequenceSet = diagram.getDiagramContents().getSequenceSet();

                        if (triggerSet.contains(triggerName) || sequenceSet.contains(sequenceName)) {
                            if (ERDiagramActivator.showConfirmDialog("dialog.message.confirm.remove.autoincrement.trigger")) {

                                // トリガーの削除
                                final Trigger trigger = triggerSet.get(triggerName);

                                if (trigger != null) {
                                    final DeleteTriggerCommand deleteTriggerCommand = new DeleteTriggerCommand(diagram, trigger);
                                    command.add(deleteTriggerCommand);
                                }

                                // シーケンスの削除
                                final Sequence sequence = sequenceSet.get(sequenceName);

                                if (sequence != null) {
                                    final DeleteSequenceCommand deleteSequenceCommand = new DeleteSequenceCommand(diagram, sequence);
                                    command.add(deleteSequenceCommand);
                                }
                            }
                        }
                    }
                }
            }
        }

        return command;
    }

}
