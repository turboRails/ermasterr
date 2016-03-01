package org.insightech.er.editor.model.dbexport.testdata.impl;

import java.util.Map;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.testdata.RepeatTestData;
import org.insightech.er.editor.model.testdata.RepeatTestDataDef;
import org.insightech.er.editor.persistent.impl.PersistentXmlImpl;
import org.insightech.er.util.Format;

public class DBUnitFlatXmlTestDataCreator extends AbstractTextTestDataCreator {

    private final String encoding;

    public DBUnitFlatXmlTestDataCreator(final String encoding) {
        this.encoding = encoding;
    }

    @Override
    protected void writeDirectTestData(final ERTable table, final Map<NormalColumn, String> data, final String database) {
        final StringBuilder sb = new StringBuilder();

        sb.append("\t<");
        sb.append(table.getNameWithSchema(database));

        for (final NormalColumn column : table.getExpandedColumns()) {
            final String value = Format.null2blank(data.get(column));

            if (value != null && !"null".equals(value.toLowerCase())) {
                sb.append(" ");
                sb.append(column.getPhysicalName());
                sb.append("=\"");
                sb.append(PersistentXmlImpl.escape(value));
                sb.append("\"");
            }
        }

        sb.append("/>\r\n");

        out.print(sb.toString());
    }

    @Override
    protected void writeRepeatTestData(final ERTable table, final RepeatTestData repeatTestData, final String database) {
        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < repeatTestData.getTestDataNum(); i++) {
            sb.append("\t<");
            sb.append(table.getNameWithSchema(database));

            for (final NormalColumn column : table.getExpandedColumns()) {
                final RepeatTestDataDef repeatTestDataDef = repeatTestData.getDataDef(column);

                final String value = getMergedRepeatTestDataValue(i, repeatTestDataDef, column);

                if (value != null && !"null".equals(value.toLowerCase())) {
                    sb.append(" ");
                    sb.append(column.getPhysicalName());
                    sb.append("=\"");

                    sb.append(PersistentXmlImpl.escape(value));
                    sb.append("\"");
                }
            }

            sb.append("/>\r\n");
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
    protected void writeTableHeader(final ERDiagram diagram, final ERTable table) {}

    @Override
    protected void writeTableFooter(final ERTable table) {}

    @Override
    protected String getFileExtention() {
        return ".xml";
    }

}
