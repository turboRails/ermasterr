package org.insightech.er.editor.model.dbexport.html.page_generator.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.html.page_generator.AbstractHtmlReportPageGenerator;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.util.Format;

public class TableHtmlReportPageGenerator extends AbstractHtmlReportPageGenerator {

    public TableHtmlReportPageGenerator(final Map<Object, Integer> idMap) {
        super(idMap);
    }

    @Override
    public String getType() {
        return "table";
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Object> getObjectList(final ERDiagram diagram) {
        final List list = diagram.getDiagramContents().getContents().getTableSet().getList();

        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getContentArgs(final ERDiagram diagram, final Object object) throws IOException {
        final ERTable table = (ERTable) object;

        final String description = table.getDescription();

        final List<NormalColumn> normalColumnList = table.getExpandedColumns();

        final String attributeTable = generateAttributeTable(diagram, normalColumnList);

        final List<NormalColumn> foreignKeyList = new ArrayList<NormalColumn>();
        for (final NormalColumn normalColumn : normalColumnList) {
            if (normalColumn.isForeignKey()) {
                foreignKeyList.add(normalColumn);
            }
        }

        final String foreignKeyTable = generateForeignKeyTable(foreignKeyList);

        final List<NormalColumn> referencedKeyList = new ArrayList<NormalColumn>();
        for (final Relation relation : table.getOutgoingRelations()) {
            referencedKeyList.addAll(relation.getForeignKeyColumns());
        }

        final String referencedKeyTable = generateReferenceKeyTable(referencedKeyList);

        final String complexUniqueKeyMatrix = generateComplexUniqueKeyMatrix(table.getComplexUniqueKeyList(), normalColumnList);

        final List<Index> indexList = table.getIndexes();

        final String indexSummaryTable = generateIndexSummaryTable(indexList);

        final String indexMatrix = generateIndexMatrix(indexList, normalColumnList);

        final String attributeDetailTable = generateAttributeDetailTable(diagram, normalColumnList);

        return new String[] {Format.null2blank(description), Format.null2blank(table.getPhysicalName()), Format.null2blank(table.getConstraint()), attributeTable, foreignKeyTable, referencedKeyTable, complexUniqueKeyMatrix, indexSummaryTable, indexMatrix, attributeDetailTable};
    }

    @Override
    public String getObjectName(final Object object) {
        final ERTable table = (ERTable) object;

        return table.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getObjectSummary(final Object object) {
        final ERTable table = (ERTable) object;

        return table.getDescription();
    }
}
