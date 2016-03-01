package org.insightech.er.editor.model.diagram_contents.element.node.table.column;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.TypeData;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.util.Check;

public class NormalColumn extends Column {

    private static final long serialVersionUID = -3177788331933357906L;

    private Word word;

    private String foreignKeyPhysicalName;

    private String foreignKeyLogicalName;

    private String foreignKeyDescription;

    private boolean notNull;

    private boolean primaryKey;

    private boolean uniqueKey;

    private boolean autoIncrement;

    private String defaultValue;

    private String constraint;

    private String uniqueKeyName;

    private Sequence autoIncrementSetting;

    private String characterSet;

    private String collation;

    /** 親が2つある外部キーは、通常ないが、親の大元が同じ参照キーの場合はありえる. */
    private List<NormalColumn> referencedColumnList = new ArrayList<NormalColumn>();

    private List<Relation> relationList = new ArrayList<Relation>();

    public NormalColumn(final Word word, final boolean notNull, final boolean primaryKey, final boolean uniqueKey, final boolean autoIncrement, final String defaultValue, final String constraint, final String uniqueKeyName, final String characterSet, final String collation) {
        this.word = word;

        init(notNull, primaryKey, uniqueKey, autoIncrement, defaultValue, constraint, uniqueKeyName, characterSet, collation);

        autoIncrementSetting = new Sequence();
    }

    protected NormalColumn(final NormalColumn from) {
        referencedColumnList.addAll(from.referencedColumnList);
        relationList.addAll(from.relationList);

        foreignKeyPhysicalName = from.foreignKeyPhysicalName;
        foreignKeyLogicalName = from.foreignKeyLogicalName;
        foreignKeyDescription = from.foreignKeyDescription;

        init(from.notNull, from.primaryKey, from.uniqueKey, from.autoIncrement, from.defaultValue, from.constraint, from.uniqueKeyName, from.characterSet, from.collation);

        word = from.word;

        autoIncrementSetting = (Sequence) from.autoIncrementSetting.clone();
    }

    private NormalColumn() {}

    /**
     * 外部キーを作成します
     * 
     * @param from
     * @param referencedColumn
     * @param relation
     * @param primaryKey
     *            主キーかどうか
     */
    public NormalColumn createForeignKey(final Relation relation, final boolean primaryKey) {
        final NormalColumn newColumn = new NormalColumn();

        newColumn.word = null;

        newColumn.referencedColumnList.add(this);
        newColumn.relationList.add(relation);

        copyData(this, newColumn);

        newColumn.primaryKey = primaryKey;
        newColumn.autoIncrement = false;

        newColumn.autoIncrementSetting = new Sequence();

        return newColumn;
    }

    protected void init(final boolean notNull, final boolean primaryKey, final boolean uniqueKey, final boolean autoIncrement, final String defaultValue, final String constraint, final String uniqueKeyName, final String characterSet, final String collation) {

        this.notNull = notNull;
        this.primaryKey = primaryKey;
        this.uniqueKey = uniqueKey;
        this.autoIncrement = autoIncrement;

        this.defaultValue = defaultValue;
        this.constraint = constraint;

        this.uniqueKeyName = uniqueKeyName;

        this.characterSet = characterSet;
        this.collation = collation;
    }

    public NormalColumn getFirstReferencedColumn() {
        if (referencedColumnList.isEmpty()) {
            return null;
        }
        return referencedColumnList.get(0);
    }

    public NormalColumn getReferencedColumn(final Relation relation) {
        for (final NormalColumn referencedColumn : referencedColumnList) {
            if (referencedColumn.getColumnHolder() == relation.getSourceTableView()) {
                return referencedColumn;
            }
        }
        return null;
    }

    public String getLogicalName() {
        if (getFirstReferencedColumn() != null) {
            if (!Check.isEmpty(foreignKeyLogicalName)) {
                return foreignKeyLogicalName;

            } else {
                return getFirstReferencedColumn().getLogicalName();
            }
        }

        return word.getLogicalName();
    }

    public String getPhysicalName() {
        if (getFirstReferencedColumn() != null) {
            if (!Check.isEmpty(foreignKeyPhysicalName)) {
                return foreignKeyPhysicalName;

            } else {
                return getFirstReferencedColumn().getPhysicalName();
            }
        }

        return word.getPhysicalName();
    }

    public String getDescription() {
        if (getFirstReferencedColumn() != null) {
            if (!Check.isEmpty(foreignKeyDescription)) {
                return foreignKeyDescription;

            } else {
                return getFirstReferencedColumn().getDescription();
            }
        }

        return word.getDescription();
    }

    public String getForeignKeyLogicalName() {
        return foreignKeyLogicalName;
    }

    public String getForeignKeyPhysicalName() {
        return foreignKeyPhysicalName;
    }

    public String getForeignKeyDescription() {
        return foreignKeyDescription;
    }

    public SqlType getType() {
        if (getFirstReferencedColumn() != null) {
            final SqlType type = getFirstReferencedColumn().getType();

            if (SqlType.valueOfId(SqlType.SQL_TYPE_ID_SERIAL).equals(type)) {
                return SqlType.valueOfId(SqlType.SQL_TYPE_ID_INTEGER);
            } else if (SqlType.valueOfId(SqlType.SQL_TYPE_ID_BIG_SERIAL).equals(type)) {
                return SqlType.valueOfId(SqlType.SQL_TYPE_ID_BIG_INT);
            }

            return type;
        }

        return word.getType();
    }

    public TypeData getTypeData() {
        if (getFirstReferencedColumn() != null) {
            return getFirstReferencedColumn().getTypeData();
        }

        return word.getTypeData();
    }

    public boolean isNotNull() {
        return notNull;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public boolean isSinglePrimaryKey() {
        if (isPrimaryKey()) {
            if (getColumnHolder() instanceof ERTable) {
                if (((ERTable) getColumnHolder()).getPrimaryKeySize() == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isUniqueKey() {
        return uniqueKey;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getConstraint() {
        return constraint;
    }

    public String getUniqueKeyName() {
        return uniqueKeyName;
    }

    public String getCharacterSet() {
        return characterSet;
    }

    public void setCharacterSet(final String characterSet) {
        this.characterSet = characterSet;
    }

    public String getCollation() {
        return collation;
    }

    public void setCollation(final String collation) {
        this.collation = collation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        String name = getLogicalName();

        if (Check.isEmpty(name)) {
            name = getPhysicalName();
        }

        return name;
    }

    public NormalColumn getRootReferencedColumn() {
        NormalColumn root = getFirstReferencedColumn();

        if (root != null) {
            while (root.getFirstReferencedColumn() != null) {
                root = root.getFirstReferencedColumn();
            }
        }

        return root;
    }

    public List<Relation> getOutgoingRelationList() {
        final List<Relation> outgoingRelationList = new ArrayList<Relation>();

        final ColumnHolder columnHolder = getColumnHolder();

        if (columnHolder instanceof ERTable) {
            final ERTable table = (ERTable) columnHolder;

            for (final Relation relation : table.getOutgoingRelations()) {
                if (relation.isReferenceForPK()) {
                    if (isPrimaryKey()) {
                        outgoingRelationList.add(relation);
                    }
                } else {
                    if (this == relation.getReferencedColumn()) {
                        outgoingRelationList.add(relation);
                    }
                }
            }
        }

        return outgoingRelationList;
    }

    public List<NormalColumn> getForeignKeyList() {
        final List<NormalColumn> foreignKeyList = new ArrayList<NormalColumn>();

        final ColumnHolder columnHolder = getColumnHolder();

        if (columnHolder instanceof ERTable) {
            final ERTable table = (ERTable) columnHolder;

            for (final Relation relation : table.getOutgoingRelations()) {
                boolean found = false;
                for (final NormalColumn column : relation.getTargetTableView().getNormalColumns()) {
                    if (column.isForeignKey()) {
                        for (final NormalColumn referencedColumn : column.referencedColumnList) {
                            if (referencedColumn == this) {
                                foreignKeyList.add(column);
                                found = true;
                                break;
                            }
                        }

                        if (found) {
                            break;
                        }
                    }
                }
            }
        }

        return foreignKeyList;
    }

    public List<Relation> getRelationList() {
        return relationList;
    }

    public void addReference(final NormalColumn referencedColumn, final Relation relation) {
        foreignKeyDescription = getDescription();
        foreignKeyLogicalName = getLogicalName();
        foreignKeyPhysicalName = getPhysicalName();

        referencedColumnList.add(referencedColumn);

        relationList.add(relation);

        copyData(this, this);

        word = null;
    }

    public void renewRelationList() {
        final List<Relation> newRelationList = new ArrayList<Relation>();
        newRelationList.addAll(relationList);

        relationList = newRelationList;
    }

    public void removeReference(final Relation relation) {
        relationList.remove(relation);

        if (relationList.isEmpty()) {
            NormalColumn temp = getFirstReferencedColumn();
            while (temp.isForeignKey()) {
                temp = temp.getFirstReferencedColumn();
            }

            word = temp.getWord();
            if (getPhysicalName() != word.getPhysicalName() || getLogicalName() != word.getLogicalName() || getDescription() != word.getDescription()) {
                word = new Word(word);

                word.setPhysicalName(getPhysicalName());
                word.setLogicalName(getLogicalName());
                word.setDescription(getDescription());
            }

            foreignKeyDescription = null;
            foreignKeyLogicalName = null;
            foreignKeyPhysicalName = null;

            referencedColumnList.clear();

            copyData(this, this);

        } else {
            for (final NormalColumn referencedColumn : referencedColumnList) {
                if (referencedColumn.getColumnHolder() == relation.getSourceTableView()) {
                    referencedColumnList.remove(referencedColumn);
                    break;
                }
            }
        }
    }

    public boolean isForeignKey() {
        if (!relationList.isEmpty()) {
            return true;
        }

        return false;
    }

    public boolean isRefered() {
        if (!(getColumnHolder() instanceof ERTable)) {
            return false;
        }

        boolean isRefered = false;

        final ERTable table = (ERTable) getColumnHolder();

        for (final Relation relation : table.getOutgoingRelations()) {
            if (!relation.isReferenceForPK()) {
                for (final NormalColumn foreignKeyColumn : relation.getForeignKeyColumns()) {

                    for (final NormalColumn referencedColumn : foreignKeyColumn.referencedColumnList) {
                        if (referencedColumn == this) {
                            isRefered = true;
                            break;
                        }
                    }

                    if (isRefered) {
                        break;
                    }
                }

                if (isRefered) {
                    break;
                }

            }
        }

        return isRefered;
    }

    public boolean isReferedStrictly() {
        if (!(getColumnHolder() instanceof ERTable)) {
            return false;
        }

        boolean isRefered = false;

        final ERTable table = (ERTable) getColumnHolder();

        for (final Relation relation : table.getOutgoingRelations()) {
            if (!relation.isReferenceForPK()) {
                for (final NormalColumn foreignKeyColumn : relation.getForeignKeyColumns()) {

                    for (final NormalColumn referencedColumn : foreignKeyColumn.referencedColumnList) {
                        if (referencedColumn == this) {
                            isRefered = true;
                            break;
                        }
                    }

                    if (isRefered) {
                        break;
                    }
                }

                if (isRefered) {
                    break;
                }

            } else {
                if (isPrimaryKey()) {
                    isRefered = true;
                    break;
                }
            }
        }

        return isRefered;
    }

    public Word getWord() {
        return word;
    }

    public boolean isFullTextIndexable() {
        return getType().isFullTextIndexable();
    }

    public static void copyData(final NormalColumn from, final NormalColumn to) {
        to.init(from.isNotNull(), from.isPrimaryKey(), from.isUniqueKey(), from.isAutoIncrement(), from.getDefaultValue(), from.getConstraint(), from.uniqueKeyName, from.characterSet, from.collation);

        to.autoIncrementSetting = (Sequence) from.autoIncrementSetting.clone();

        if (to.isForeignKey()) {
            final NormalColumn firstReferencedColumn = to.getFirstReferencedColumn();

            if (firstReferencedColumn.getPhysicalName() == null) {
                to.foreignKeyPhysicalName = from.getPhysicalName();

            } else {
                if (from.foreignKeyPhysicalName != null && !firstReferencedColumn.getPhysicalName().equals(from.foreignKeyPhysicalName)) {
                    to.foreignKeyPhysicalName = from.foreignKeyPhysicalName;

                } else if (!firstReferencedColumn.getPhysicalName().equals(from.getPhysicalName())) {
                    to.foreignKeyPhysicalName = from.getPhysicalName();

                } else {
                    to.foreignKeyPhysicalName = null;
                }
            }

            if (firstReferencedColumn.getLogicalName() == null) {
                to.foreignKeyLogicalName = from.getLogicalName();

            } else {
                if (from.foreignKeyLogicalName != null && !firstReferencedColumn.getLogicalName().equals(from.foreignKeyLogicalName)) {
                    to.foreignKeyLogicalName = from.foreignKeyLogicalName;

                } else if (!firstReferencedColumn.getLogicalName().equals(from.getLogicalName())) {
                    to.foreignKeyLogicalName = from.getLogicalName();

                } else {
                    to.foreignKeyLogicalName = null;

                }
            }

            if (firstReferencedColumn.getDescription() == null) {
                to.foreignKeyDescription = from.getDescription();

            } else {
                if (from.foreignKeyDescription != null && !firstReferencedColumn.getDescription().equals(from.foreignKeyDescription)) {
                    to.foreignKeyDescription = from.foreignKeyDescription;

                } else if (!firstReferencedColumn.getDescription().equals(from.getDescription())) {
                    to.foreignKeyDescription = from.getDescription();

                } else {
                    to.foreignKeyDescription = null;
                }
            }

        } else {
            from.word.copyTo(to.word);
        }

        to.setColumnHolder(from.getColumnHolder());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(", physicalName:" + getPhysicalName());
        sb.append(", logicalName:" + getLogicalName());

        return sb.toString();
    }

    public void setForeignKeyPhysicalName(final String physicalName) {
        foreignKeyPhysicalName = physicalName;
    }

    public void setForeignKeyLogicalName(final String logicalName) {
        foreignKeyLogicalName = logicalName;
    }

    public void setForeignKeyDescription(final String description) {
        foreignKeyDescription = description;
    }

    public void setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setConstraint(final String constraint) {
        this.constraint = constraint;
    }

    public void setUniqueKeyName(final String uniqueKeyName) {
        this.uniqueKeyName = uniqueKeyName;
    }

    public void setNotNull(final boolean notNull) {
        this.notNull = notNull;
    }

    public void setUniqueKey(final boolean uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public void setPrimaryKey(final boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public void setAutoIncrement(final boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public void setWord(final Word word) {
        this.word = word;
    }

    public Sequence getAutoIncrementSetting() {
        return autoIncrementSetting;
    }

    public void setAutoIncrementSetting(final Sequence autoIncrementSetting) {
        this.autoIncrementSetting = autoIncrementSetting;
    }

    public void copyForeikeyData(final NormalColumn to) {
        to.setConstraint(getConstraint());
        to.setForeignKeyDescription(getForeignKeyDescription());
        to.setForeignKeyLogicalName(getForeignKeyLogicalName());
        to.setForeignKeyPhysicalName(getForeignKeyPhysicalName());
        to.setNotNull(true);
        to.setUniqueKey(isUniqueKey());
        to.setPrimaryKey(isPrimaryKey());
        to.setAutoIncrement(isAutoIncrement());
        to.setCharacterSet(getCharacterSet());
        to.setCollation(getCollation());
    }

    public List<NormalColumn> getReferencedColumnList() {
        return referencedColumnList;
    }

    @Override
    public NormalColumn clone() {
        final NormalColumn clone = (NormalColumn) super.clone();

        clone.relationList = new ArrayList<Relation>(relationList);
        clone.referencedColumnList = new ArrayList<NormalColumn>(referencedColumnList);

        return clone;
    }

}
