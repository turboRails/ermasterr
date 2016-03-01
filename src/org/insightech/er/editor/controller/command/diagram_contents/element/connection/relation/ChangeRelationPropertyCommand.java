package org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;

public class ChangeRelationPropertyCommand extends AbstractCommand {

    private final Relation oldCopyRelation;

    private final Relation newCopyRelation;

    private final Relation relation;

    private final TableView oldTargetTable;

    private boolean isChildNotNull;

    private final Map<NormalColumn, Boolean> foreignKeyNotNullMap;

    public ChangeRelationPropertyCommand(final Relation relation, final Relation newCopyRelation) {
        this.relation = relation;
        oldCopyRelation = relation.copy();
        this.newCopyRelation = newCopyRelation;

        oldTargetTable = relation.getTargetTableView().copyData();

        foreignKeyNotNullMap = new HashMap<NormalColumn, Boolean>();

        if (Relation.PARENT_CARDINALITY_1.equals(newCopyRelation.getParentCardinality())) {
            isChildNotNull = true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        newCopyRelation.restructureRelationData(relation);

        if (newCopyRelation.isReferenceForPK()) {
            relation.setForeignKeyColumnForPK();

        } else if (newCopyRelation.getReferencedComplexUniqueKey() != null) {
            relation.setForeignKeyForComplexUniqueKey(newCopyRelation.getReferencedComplexUniqueKey());

        } else {
            relation.setForeignKeyColumn(newCopyRelation.getReferencedColumn());
        }

        for (final NormalColumn foreignKeyColumn : relation.getForeignKeyColumns()) {
            foreignKeyNotNullMap.put(foreignKeyColumn, foreignKeyColumn.isNotNull());

            foreignKeyColumn.setNotNull(isChildNotNull);
        }

        relation.getTarget().refresh();
        relation.getSource().refresh();
        relation.refreshVisuals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        oldCopyRelation.restructureRelationData(relation);

        relation.setReferenceForPK(oldCopyRelation.isReferenceForPK());
        relation.setReferencedComplexUniqueKey(oldCopyRelation.getReferencedComplexUniqueKey());
        relation.setReferencedColumn(oldCopyRelation.getReferencedColumn());

        oldTargetTable.restructureData(relation.getTargetTableView());

        for (final Entry<NormalColumn, Boolean> foreignKeyEntry : foreignKeyNotNullMap.entrySet()) {
            foreignKeyEntry.getKey().setNotNull(foreignKeyEntry.getValue());
        }

        relation.getTargetTableView().setDirty();

        relation.getTarget().refresh();
        relation.getSource().refresh();
        relation.refreshVisuals();
    }
}
