package org.insightech.er.editor.model.dbexport.html.page_generator.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.html.page_generator.AbstractHtmlReportPageGenerator;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.util.Format;

public class IndexHtmlReportPageGenerator extends AbstractHtmlReportPageGenerator {

    public IndexHtmlReportPageGenerator(final Map<Object, Integer> idMap) {
        super(idMap);
    }

    @Override
    public String getType() {
        return "index";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Object> getObjectList(final ERDiagram diagram) {
        final List<Object> list = new ArrayList<Object>();

        for (final NodeElement nodeElement : diagram.getDiagramContents().getContents()) {
            if (nodeElement instanceof ERTable) {
                final ERTable table = (ERTable) nodeElement;
                list.addAll(table.getIndexes());
            }
        }

        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getContentArgs(final ERDiagram diagram, final Object object) throws IOException {
        final Index index = (Index) object;

        final ERTable table = index.getTable();

        final String description = Format.null2blank(index.getDescription());
        final String tableId = getObjectId(table);
        final String tableName = Format.null2blank(table.getName());

        final String unique = getUniqueString(index);

        final List<NormalColumn> normalColumnList = index.getColumns();
        final List<Boolean> descs = index.getDescs();

        final String indexAttribute = generateIndexAttributeTable(table, normalColumnList, descs);

        return new String[] {description, tableId, tableName, this.getType(index), unique, indexAttribute};
    }

    private String getType(final Index index) {
        if (index.isFullText()) {
            return "FULLTEXT";
        }

        return Format.null2blank(index.getType());
    }

    @Override
    public String getObjectName(final Object object) {
        final Index index = (Index) object;

        return index.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getObjectSummary(final Object object) {
        final Index index = (Index) object;

        return index.getDescription();
    }

    private String getUniqueString(final Index index) {
        if (!index.isNonUnique()) {
            return "UNIQUE";
        } else {
            return "";
        }
    }
}
