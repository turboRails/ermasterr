package org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.DeleteConnectionCommand;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;

public class DeleteRelationCommand extends DeleteConnectionCommand {

    private TableView oldTargetCopyTable;

    private final TableView oldTargetTable;

    private final TableView oldSourceTable;

    private final Relation relation;

    private Boolean removeForeignKey;

    private Map<NormalColumn, NormalColumn> foreignKeyReferencedColumnMap;

    public DeleteRelationCommand(final Relation relation, final Boolean removeForeignKey) {
        super(relation);

        this.relation = relation;
        oldTargetTable = relation.getTargetTableView();
        oldSourceTable = relation.getSourceTableView();

        this.removeForeignKey = removeForeignKey;

        foreignKeyReferencedColumnMap = new HashMap<NormalColumn, NormalColumn>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        if (oldTargetCopyTable == null) {
            for (final NormalColumn foreignKey : relation.getForeignKeyColumns()) {
                final NormalColumn referencedColumn = foreignKey.getReferencedColumn(relation);

                foreignKeyReferencedColumnMap.put(foreignKey, referencedColumn);
            }

            oldTargetCopyTable = oldTargetTable.copyData();
        }

        final Dictionary dictionary = oldTargetTable.getDiagram().getDiagramContents().getDictionary();

        relation.delete(removeForeignKey, dictionary);

        oldTargetTable.refresh();
        oldSourceTable.refreshSourceConnections();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        super.doUndo();

        for (final NormalColumn foreignKey : foreignKeyReferencedColumnMap.keySet()) {
            if (!removeForeignKey) {
                final Dictionary dictionary = oldTargetTable.getDiagram().getDiagramContents().getDictionary();
                dictionary.remove(foreignKey);
            }

            foreignKey.addReference(foreignKeyReferencedColumnMap.get(foreignKey), relation);
        }

        oldTargetCopyTable.restructureData(oldTargetTable);

        if (oldTargetTable == oldSourceTable) {
            //this.oldTargetTable.update();
            oldTargetTable.refresh();

        } else {
            oldTargetTable.refresh();
            oldSourceTable.refreshSourceConnections();
        }
    }

    @Override
    public boolean canExecute() {
        if (removeForeignKey == null) {
            if (relation.isReferedStrictly()) {
                if (isReferencedByMultiRelations()) {
                    ERDiagramActivator.showErrorDialog("dialog.message.referenced.by.multi.foreign.key");
                    return false;
                }

                removeForeignKey = false;

                foreignKeyReferencedColumnMap = new HashMap<NormalColumn, NormalColumn>();

                for (final NormalColumn foreignKey : relation.getForeignKeyColumns()) {
                    final NormalColumn referencedColumn = foreignKey.getReferencedColumn(relation);

                    foreignKeyReferencedColumnMap.put(foreignKey, referencedColumn);
                }

                return true;
            }

            if (ERDiagramActivator.showConfirmDialog("dialog.message.confirm.remove.foreign.key", SWT.YES, SWT.NO)) {
                removeForeignKey = true;

            } else {
                removeForeignKey = false;

                foreignKeyReferencedColumnMap = new HashMap<NormalColumn, NormalColumn>();

                for (final NormalColumn foreignKey : relation.getForeignKeyColumns()) {
                    final NormalColumn referencedColumn = foreignKey.getReferencedColumn(relation);

                    foreignKeyReferencedColumnMap.put(foreignKey, referencedColumn);
                }
            }
        }

        return true;
    }

    private boolean isReferencedByMultiRelations() {
        for (final NormalColumn foreignKeyColumn : relation.getForeignKeyColumns()) {
            for (final NormalColumn childForeignKeyColumn : foreignKeyColumn.getForeignKeyList()) {
                if (childForeignKeyColumn.getRelationList().size() >= 2) {
                    final Set<TableView> referencedTables = new HashSet<TableView>();

                    for (final Relation relation : childForeignKeyColumn.getRelationList()) {
                        referencedTables.add(relation.getSourceTableView());
                    }

                    if (referencedTables.size() >= 2) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

}
