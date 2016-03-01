package org.insightech.er.editor.model.diagram_contents.element.node.table.unique_key;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.CopyColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;

public class CopyComplexUniqueKey extends ComplexUniqueKey {

    private static final long serialVersionUID = 4099783813887218599L;

    private ComplexUniqueKey originalComplexUniqueKey;

    public CopyComplexUniqueKey(final ComplexUniqueKey original, final List<Column> copyColumns) {
        super(original.getUniqueKeyName());

        originalComplexUniqueKey = original;

        for (final NormalColumn originalColumn : original.getColumnList()) {
            for (final Column column : copyColumns) {
                if (column instanceof CopyColumn) {
                    final CopyColumn copyColumn = (CopyColumn) column;

                    if (copyColumn.getOriginalColumn().equals(originalColumn)) {
                        addColumn(copyColumn);
                        break;
                    }
                }
            }
        }
    }

    public ComplexUniqueKey restructure() {
        if (originalComplexUniqueKey == null) {
            originalComplexUniqueKey = new ComplexUniqueKey(getUniqueKeyName());
        }

        final List<NormalColumn> normalColumns = new ArrayList<NormalColumn>();

        for (NormalColumn column : getColumnList()) {
            final CopyColumn copyColumn = (CopyColumn) column;
            column = copyColumn.getOriginalColumn();
            normalColumns.add(column);
        }

        originalComplexUniqueKey.setColumnList(normalColumns);
        originalComplexUniqueKey.setUniqueKeyName(getUniqueKeyName());

        return originalComplexUniqueKey;
    }

    public ComplexUniqueKey getOriginal() {
        return originalComplexUniqueKey;
    }
}
