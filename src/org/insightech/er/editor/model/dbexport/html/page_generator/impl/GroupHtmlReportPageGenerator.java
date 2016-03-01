package org.insightech.er.editor.model.dbexport.html.page_generator.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.html.page_generator.AbstractHtmlReportPageGenerator;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;

public class GroupHtmlReportPageGenerator extends AbstractHtmlReportPageGenerator {

    public GroupHtmlReportPageGenerator(final Map<Object, Integer> idMap) {
        super(idMap);
    }

    @Override
    public String getType() {
        return "group";
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Object> getObjectList(final ERDiagram diagram) {
        final List list = diagram.getDiagramContents().getGroups().getGroupList();

        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getContentArgs(final ERDiagram diagram, final Object object) throws IOException {
        final ColumnGroup columnGroup = (ColumnGroup) object;

        final List<NormalColumn> normalColumnList = columnGroup.getColumns();

        final String attributeTable = generateAttributeTable(diagram, normalColumnList);

        final List<TableView> usedTableList = columnGroup.getUsedTalbeList(diagram);

        final String usedTableTable = generateUsedTableTable(usedTableList);

        final String attributeDetailTable = generateAttributeDetailTable(diagram, normalColumnList);

        return new String[] {attributeTable, usedTableTable, attributeDetailTable};
    }

    @Override
    public String getObjectName(final Object object) {
        final ColumnGroup columnGroup = (ColumnGroup) object;

        return columnGroup.getGroupName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getObjectSummary(final Object object) {
        return null;
    }

}
