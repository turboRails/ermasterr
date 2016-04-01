package org.insightech.er.editor.model.diagram_contents.element.connection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;

public class Relation extends ConnectionElement {

    private static final long serialVersionUID = 4456694342537711599L;

    public static final String PARENT_CARDINALITY_0_OR_1 = "0..1";

    public static final String PARENT_CARDINALITY_1 = "1";

    public static final String CHILD_CARDINALITY_1 = "1";

    private String name;

    private String onUpdateAction;

    private String onDeleteAction;

    private String parentCardinality;

    private String childCardinality;

    private boolean referenceForPK;

    private ComplexUniqueKey referencedComplexUniqueKey;

    private NormalColumn referencedColumn;

    private Relation original;

    public Relation() {
        this(false, null, null, true, false);
    }

    public Relation(final boolean referenceForPK, final ComplexUniqueKey referencedComplexUniqueKey, final NormalColumn referencedColumn, final boolean notnullParent, final boolean uniqueChild) {
        onUpdateAction = "RESTRICT";
        onDeleteAction = "RESTRICT";

        this.referenceForPK = referenceForPK;
        this.referencedComplexUniqueKey = referencedComplexUniqueKey;
        this.referencedColumn = referencedColumn;

        if (notnullParent) {
            parentCardinality = PARENT_CARDINALITY_1;
        } else {
            parentCardinality = PARENT_CARDINALITY_0_OR_1;
        }
        if (uniqueChild) {
            childCardinality = "1";
        } else {
            childCardinality = "1..n";
        }
    }

    private Relation getOriginal() {
        if (original != null) {
            return original.getOriginal();
        }

        return this;
    }

    public TableView getSourceTableView() {
        return (TableView) getSource();
    }

    public TableView getTargetTableView() {
        return (TableView) getTarget();
    }

    public void setTargetTableView(final TableView target) {
        this.setTargetTableView(target, null);
    }

    public void setTargetTableView(final TableView target, final List<NormalColumn> foreignKeyColumnList) {

        if (getTargetTableView() != null) {
            removeAllForeignKey();
        }

        super.setTarget(target);

        if (target != null) {
            final TableView sourceTable = (TableView) getSource();

            int i = 0;

            if (isReferenceForPK()) {
                for (final NormalColumn sourceColumn : ((ERTable) sourceTable).getPrimaryKeys()) {
                    final NormalColumn foreignKeyColumn = createForeiKeyColumn(sourceColumn, foreignKeyColumnList, i++);

                    target.addColumn(foreignKeyColumn);
                }

            } else if (referencedComplexUniqueKey != null) {
                for (final NormalColumn sourceColumn : referencedComplexUniqueKey.getColumnList()) {
                    final NormalColumn foreignKeyColumn = createForeiKeyColumn(sourceColumn, foreignKeyColumnList, i++);

                    target.addColumn(foreignKeyColumn);
                }

            } else {
                for (final NormalColumn sourceColumn : sourceTable.getNormalColumns()) {
                    if (sourceColumn == referencedColumn) {
                        final NormalColumn foreignKeyColumn = createForeiKeyColumn(sourceColumn, foreignKeyColumnList, i++);

                        target.addColumn(foreignKeyColumn);
                        break;
                    }
                }
            }
        }
    }

    private NormalColumn createForeiKeyColumn(final NormalColumn referencedColumn, final List<NormalColumn> foreignKeyColumnList, final int index) {
        final NormalColumn foreignKeyColumn = referencedColumn.createForeignKey(this, false);

        if (foreignKeyColumnList != null) {
            final NormalColumn data = foreignKeyColumnList.get(index);
            data.copyForeikeyData(foreignKeyColumn);
        }

        return foreignKeyColumn;
    }

    public void setTargetWithoutForeignKey(final TableView target) {
        super.setTarget(target);
    }

    public void setTargetTableWithExistingColumns(final ERTable target, final List<NormalColumn> referencedColumnList, final List<NormalColumn> foreignKeyColumnList) {

        super.setTarget(target);
    }

    public void delete(final boolean removeForeignKey, final Dictionary dictionary) {
        super.delete();

        for (final NormalColumn foreignKeyColumn : getForeignKeyColumns()) {
            foreignKeyColumn.removeReference(this);

            if (removeForeignKey) {
                if (foreignKeyColumn.getRelationList().isEmpty()) {
                    getTargetTableView().removeColumn(foreignKeyColumn);
                }

            } else {
                dictionary.add(foreignKeyColumn);
            }
        }
    }

    public List<NormalColumn> getForeignKeyColumns() {
        final List<NormalColumn> list = new ArrayList<NormalColumn>();

        if (getTargetTableView() != null) {
            for (final NormalColumn column : getTargetTableView().getNormalColumns()) {
                if (column.isForeignKey()) {
                    final NormalColumn foreignKeyColumn = column;
                    for (final Relation relation : foreignKeyColumn.getRelationList()) {
                        if (relation == getOriginal()) {
                            list.add(column);
                            break;
                        }
                    }
                }
            }
        }

        return list;
    }

    public String getName() {
        return name;
    }

    public String getOnDeleteAction() {
        return onDeleteAction;
    }

    public void setOnDeleteAction(final String onDeleteAction) {
        this.onDeleteAction = onDeleteAction;
    }

    public String getOnUpdateAction() {
        return onUpdateAction;
    }

    public void setOnUpdateAction(final String onUpdateAction) {
        this.onUpdateAction = onUpdateAction;
    }

    public String getChildCardinality() {
        return childCardinality;
    }

    public void setChildCardinality(final String childCardinality) {
        this.childCardinality = childCardinality;
    }

    public String getParentCardinality() {
        return parentCardinality;
    }

    public void setParentCardinality(final String parentCardinality) {
        this.parentCardinality = parentCardinality;
    }

    public void setNotNullOfForeignKey(final boolean notnull) {
        if (notnull) {
            parentCardinality = PARENT_CARDINALITY_1;
        } else {
            parentCardinality = PARENT_CARDINALITY_0_OR_1;
        }
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Relation copy() {
        final Relation to = new Relation(isReferenceForPK(), getReferencedComplexUniqueKey(), getReferencedColumn(), true, true);

        to.setName(getName());
        to.setOnDeleteAction(getOnDeleteAction());
        to.setOnUpdateAction(getOnUpdateAction());
        to.setChildCardinality(getChildCardinality());
        to.setParentCardinality(getParentCardinality());

        to.source = getSourceTableView();
        to.target = getTargetTableView();

        to.original = this;

        return to;
    }

    public Relation restructureRelationData(final Relation to) {
        to.setName(getName());
        to.setOnDeleteAction(getOnDeleteAction());
        to.setOnUpdateAction(getOnUpdateAction());
        to.setChildCardinality(getChildCardinality());
        to.setParentCardinality(getParentCardinality());

        return to;
    }

    public boolean isReferenceForPK() {
        return referenceForPK;
    }

    public void setReferenceForPK(final boolean referenceForPK) {
        this.referenceForPK = referenceForPK;
    }

    public void setForeignKeyColumn(final NormalColumn sourceColumn) {
        if (referencedColumn == sourceColumn) {
            return;
        }

        removeAllForeignKey();

        final NormalColumn foreignKeyColumn = sourceColumn.createForeignKey(this, false);

        getTargetTableView().addColumn(foreignKeyColumn);

        referenceForPK = false;
        referencedColumn = sourceColumn;
        referencedComplexUniqueKey = null;
    }

    public void setForeignKeyForComplexUniqueKey(final ComplexUniqueKey complexUniqueKey) {
        if (referencedComplexUniqueKey == complexUniqueKey) {
            return;
        }

        removeAllForeignKey();

        for (final NormalColumn sourceColumn : complexUniqueKey.getColumnList()) {
            final NormalColumn foreignKeyColumn = sourceColumn.createForeignKey(this, false);

            getTargetTableView().addColumn(foreignKeyColumn);
        }

        referenceForPK = false;
        referencedColumn = null;
        referencedComplexUniqueKey = complexUniqueKey;
    }

    public void setForeignKeyColumnForPK() {
        if (referenceForPK) {
            return;
        }

        removeAllForeignKey();

        for (final NormalColumn sourceColumn : ((ERTable) getSourceTableView()).getPrimaryKeys()) {
            final NormalColumn foreignKeyColumn = sourceColumn.createForeignKey(this, false);

            getTargetTableView().addColumn(foreignKeyColumn);
        }

        referenceForPK = true;
        referencedColumn = null;
        referencedComplexUniqueKey = null;
    }

    private void removeAllForeignKey() {
        for (final Iterator iter = getTargetTableView().getColumns().iterator(); iter.hasNext();) {
            final Column column = (Column) iter.next();

            if (column instanceof NormalColumn) {
                final NormalColumn normalColumn = (NormalColumn) column;

                if (normalColumn.isForeignKey()) {
                    if (normalColumn.getRelationList().size() == 1 && normalColumn.getRelationList().get(0) == this) {
                        iter.remove();
                    }
                }
            }
        }

        getTargetTableView().setDirty();
    }

    public void setReferencedColumn(final NormalColumn referencedColumn) {
        this.referencedColumn = referencedColumn;
    }

    public NormalColumn getReferencedColumn() {
        return referencedColumn;
    }

    public void setReferencedComplexUniqueKey(final ComplexUniqueKey referencedComplexUniqueKey) {
        this.referencedComplexUniqueKey = referencedComplexUniqueKey;
    }

    public ComplexUniqueKey getReferencedComplexUniqueKey() {
        return referencedComplexUniqueKey;
    }

    public boolean isReferedStrictly() {
        for (final NormalColumn column : getForeignKeyColumns()) {
            if (column.isReferedStrictly()) {
                return true;
            }
        }

        return false;
    }

    public boolean isSelfRelation() {
        if (source == target) {
            return true;
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Relation clone() {
        final Relation clone = (Relation) super.clone();

        return clone;
    }

    @Override
    public int compareTo(ConnectionElement other) {
        if (!(other instanceof Relation)) {
            return 1;
        }

        Relation _other = (Relation) other;

        // [ermasterr] compare by name
        final String name1 = getName();
        final String name2 = _other.getName();

        if (name1 == null && name2 != null) {
            return 1;
        }
        if (!name1.equals(name2)) {
            return name1.compareTo(name2);
        }

        return super.compareTo(other);
        // [ermasterr] give up if same relation and same position
    }

}
