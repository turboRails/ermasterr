package org.insightech.er.editor.model.dbexport.testdata.impl;

import java.util.Map;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.testdata.RepeatTestData;
import org.insightech.er.editor.model.testdata.RepeatTestDataDef;
import org.insightech.er.editor.persistent.impl.PersistentXmlImpl;
import org.insightech.er.util.Format;

public class DBUnitTestDataCreator extends AbstractTextTestDataCreator {

    private final String encoding;

    public DBUnitTestDataCreator(final String encoding) {
        this.encoding = encoding;
    }

    @Override
    protected void writeDirectTestData(final ERTable table, final Map<NormalColumn, String> data, final String database) {
        final StringBuilder sb = new StringBuilder();

        sb.append("\t\t<row>\r\n");

        for (final NormalColumn column : table.getExpandedColumns()) {
            final String value = Format.null2blank(data.get(column));

            if (value == null || "null".equals(value.toLowerCase())) {
                sb.append("\t\t\t<null/>\r\n");

            } else {
                sb.append("\t\t\t<value>");
                sb.append(PersistentXmlImpl.escape(value));
                sb.append("</value>\r\n");
            }
        }

        sb.append("\t\t</row>\r\n");

        out.print(sb.toString());
    }

    @Override
    protected void writeRepeatTestData(final ERTable table, final RepeatTestData repeatTestData, final String database) {
        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < repeatTestData.getTestDataNum(); i++) {
            sb.append("\t\t<row>\r\n");

            for (final NormalColumn column : table.getExpandedColumns()) {

                final RepeatTestDataDef repeatTestDataDef = repeatTestData.getDataDef(column);

                final String value = getMergedRepeatTestDataValue(i, repeatTestDataDef, column);

                if (value == null || "null".equals(value.toLowerCase())) {
                    sb.append("\t\t\t<null/>\r\n");

                } else {
                    sb.append("\t\t\t<value>");
                    sb.append(PersistentXmlImpl.escape(value));
                    sb.append("</value>\r\n");

                }
            }

            sb.append("\t\t</row>\r\n");
        }

        out.print(sb.toString());
    }

    @Override
    protected String getHeader() {
        final StringBuilder sb = new StringBuilder();

        sb.append("<?xml version=\"1.0\" encoding=\"");
        sb.append(encoding);
        sb.append("\" ?>\r\n<dataset>\r\n");

        return sb.toString();
    }

    @Override
    protected String getFooter() {
        return "</dataset>";
    }

    @Override
    protected void writeTableHeader(final ERDiagram diagram, final ERTable table) {
        final StringBuilder sb = new StringBuilder();

        sb.append("\t<table name=\"");
        sb.append(table.getNameWithSchema(diagram.getDatabase()));
        sb.append("\">\r\n");

        for (final NormalColumn column : table.getExpandedColumns()) {
            sb.append("\t\t<column>");
            sb.append(column.getPhysicalName());
            sb.append("</column>\r\n");
        }

        out.print(sb.toString());
    }

    @Override
    protected void writeTableFooter(final ERTable table) {
        out.print("\t</table>\r\n");
    }

    @Override
    protected String getFileExtention() {
        return ".xml";
    }

}
