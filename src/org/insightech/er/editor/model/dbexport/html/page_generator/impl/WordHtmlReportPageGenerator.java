package org.insightech.er.editor.model.dbexport.html.page_generator.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.html.page_generator.AbstractHtmlReportPageGenerator;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.ColumnHolder;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.util.Format;

public class WordHtmlReportPageGenerator extends AbstractHtmlReportPageGenerator {

    public WordHtmlReportPageGenerator(final Map<Object, Integer> idMap) {
        super(idMap);
    }

    @Override
    public String getType() {
        return "word";
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Object> getObjectList(final ERDiagram diagram) {
        final List list = diagram.getDiagramContents().getDictionary().getWordList();

        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getContentArgs(final ERDiagram diagram, final Object object) throws IOException {
        final Word word = (Word) object;

        final String logicalName = word.getLogicalName();
        final String physicalName = word.getPhysicalName();
        String type = "";
        if (word.getType() != null) {
            type = Format.formatType(word.getType(), word.getTypeData(), diagram.getDatabase(), true);
        }

        final String description = word.getDescription();

        final List<TableView> usedTableList = new ArrayList<TableView>();

        final List<NormalColumn> normalColumnList = diagram.getDiagramContents().getDictionary().getColumnList(word);
        for (final NormalColumn normalColumn : normalColumnList) {
            final ColumnHolder columnHolder = normalColumn.getColumnHolder();
            if (columnHolder instanceof TableView) {
                usedTableList.add((TableView) columnHolder);

            } else {
                final ColumnGroup columnGroup = (ColumnGroup) columnHolder;
                usedTableList.addAll(columnGroup.getUsedTalbeList(diagram));
            }
        }

        final String usedTableTable = generateUsedTableTable(usedTableList);

        return new String[] {logicalName, physicalName, type, description, usedTableTable};
    }

    @Override
    public String getObjectName(final Object object) {
        final Word word = (Word) object;

        return word.getLogicalName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getObjectSummary(final Object object) {
        return null;
    }
}
