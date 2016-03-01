package org.insightech.er.editor.model.diagram_contents.element.node.table.index;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.CopyColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;

public class CopyIndex extends Index {

    private static final long serialVersionUID = -7896024413398953097L;

    private Index originalIndex;

    public CopyIndex(final ERTable copyTable, final Index originalIndex, final List<Column> copyColumns) {
        super(copyTable, originalIndex.getName(), originalIndex.isNonUnique(), originalIndex.getType(), originalIndex.getDescription());

        this.originalIndex = originalIndex;

        final List<Boolean> descs = originalIndex.getDescs();

        int i = 0;

        for (final NormalColumn originalIndexColumn : originalIndex.getColumns()) {
            Boolean desc = Boolean.FALSE;

            if (descs.size() > i) {
                desc = descs.get(i);
            }

            if (copyColumns != null) {

                boolean isGroupColumn = true;

                for (final Column column : copyColumns) {
                    if (column instanceof CopyColumn) {
                        final CopyColumn copyColumn = (CopyColumn) column;

                        if (copyColumn.getOriginalColumn().equals(originalIndexColumn)) {
                            this.addColumn(copyColumn, desc);
                            isGroupColumn = false;
                            break;
                        }
                    }
                }

                if (isGroupColumn) {
                    this.addColumn(originalIndexColumn, desc);
                }

            } else {
                this.addColumn(originalIndexColumn, desc);
            }

            i++;
        }
    }

    public Index getRestructuredIndex(final ERTable originalTable) {
        if (originalIndex == null) {
            originalIndex = new Index(originalTable, getName(), isNonUnique(), getType(), getDescription());
        }

        copyData(this, originalIndex);

        final List<NormalColumn> indexColumns = new ArrayList<NormalColumn>();

        for (NormalColumn column : originalIndex.getColumns()) {
            if (column instanceof CopyColumn) {
                final CopyColumn copyColumn = (CopyColumn) column;
                column = copyColumn.getOriginalColumn();
            }
            indexColumns.add(column);
        }

        originalIndex.setColumns(indexColumns);
        originalIndex.setTable(originalTable);

        return originalIndex;
    }

    public static void copyData(final Index from, final Index to) {
        to.setName(from.getName());
        to.setNonUnique(from.isNonUnique());
        to.setFullText(from.isFullText());
        to.setType(from.getType());
        to.setDescription(from.getDescription());

        to.clearColumns();

        final List<Boolean> descs = from.getDescs();
        int i = 0;

        for (final NormalColumn column : from.getColumns()) {
            Boolean desc = Boolean.FALSE;

            if (descs.size() > i) {
                desc = descs.get(i);
            }
            to.addColumn(column, desc);
            i++;
        }

    }

}
