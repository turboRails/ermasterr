package org.insightech.er.editor.model.diagram_contents.element.node.table.unique_key;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.util.Format;

public class ComplexUniqueKey extends AbstractModel {

    private static final long serialVersionUID = -3970737521746421701L;

    private List<NormalColumn> columnList;

    private String uniqueKeyName;

    public ComplexUniqueKey(final String uniqueKeyName) {
        this.uniqueKeyName = uniqueKeyName;
        columnList = new ArrayList<NormalColumn>();
    }

    public String getUniqueKeyName() {
        return uniqueKeyName;
    }

    public List<NormalColumn> getColumnList() {
        return columnList;
    }

    public void addColumn(final NormalColumn column) {
        columnList.add(column);
    }

    public void setColumnList(final List<NormalColumn> columnList) {
        this.columnList = columnList;
    }

    public void setUniqueKeyName(final String uniqueKeyName) {
        this.uniqueKeyName = uniqueKeyName;
    }

    public boolean isRemoved(final List<NormalColumn> tableColumnList) {
        for (final NormalColumn normalColumn : columnList) {
            if (!tableColumnList.contains(normalColumn)) {
                return true;
            }
        }

        return false;
    }

    public String getLabel() {
        final StringBuilder sb = new StringBuilder();

        sb.append(Format.null2blank(uniqueKeyName));
        sb.append(" (");
        boolean first = true;
        for (final NormalColumn normalColumn : getColumnList()) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append(normalColumn.getName());
        }
        sb.append(")");

        return sb.toString();
    }

    public boolean isReferenced(final ERTable table) {
        boolean isReferenced = false;

        ComplexUniqueKey target = this;
        if (target instanceof CopyComplexUniqueKey) {
            target = ((CopyComplexUniqueKey) target).getOriginal();
        }

        for (final Relation relation : table.getOutgoingRelations()) {
            if (relation.getReferencedComplexUniqueKey() == target) {
                isReferenced = true;
                break;
            }
        }

        return isReferenced;
    }
}
