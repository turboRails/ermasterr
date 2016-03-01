package org.insightech.er.editor.model.dbexport.html.page_generator.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.html.page_generator.AbstractHtmlReportPageGenerator;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.util.Format;

public class ViewHtmlReportPageGenerator extends AbstractHtmlReportPageGenerator {

    public ViewHtmlReportPageGenerator(final Map<Object, Integer> idMap) {
        super(idMap);
    }

    @Override
    public String getType() {
        return "view";
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Object> getObjectList(final ERDiagram diagram) {
        final List list = diagram.getDiagramContents().getContents().getViewSet().getList();

        return list;
    }

    /**
     * {@inheritDoc}
     * 
     * @throws IOException
     */
    @Override
    public String[] getContentArgs(final ERDiagram diagram, final Object object) throws IOException {
        final View view = (View) object;

        final String description = Format.null2blank(view.getDescription());

        final List<NormalColumn> normalColumnList = view.getExpandedColumns();

        final String attributeTable = generateAttributeTable(diagram, normalColumnList);

        final List<NormalColumn> foreignKeyList = new ArrayList<NormalColumn>();
        for (final NormalColumn normalColumn : normalColumnList) {
            if (normalColumn.isForeignKey()) {
                foreignKeyList.add(normalColumn);
            }
        }

        final String foreignKeyTable = generateForeignKeyTable(foreignKeyList);

        final String attributeDetailTable = generateAttributeDetailTable(diagram, normalColumnList);

        return new String[] {Format.null2blank(description), Format.null2blank(view.getPhysicalName()), Format.null2blank(view.getSql()), attributeTable, foreignKeyTable, attributeDetailTable};
    }

    @Override
    public String getObjectName(final Object object) {
        final View view = (View) object;

        return view.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getObjectSummary(final Object object) {
        final View view = (View) object;

        return view.getDescription();
    }
}
