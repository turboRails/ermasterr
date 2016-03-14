package org.insightech.er.editor.model.dbexport.html.page_generator;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.html.ExportToHtmlManager;
import org.insightech.er.editor.model.dbexport.image.ImageInfoSet;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;
import org.insightech.er.util.Format;

public abstract class AbstractHtmlReportPageGenerator implements HtmlReportPageGenerator {

    protected Map<Object, Integer> idMap;

    protected ImageInfoSet imageInfoSet;

    public AbstractHtmlReportPageGenerator(final Map<Object, Integer> idMap) {
        this.idMap = idMap;
    }

    @Override
    public void setImageInfoSet(final ImageInfoSet imageInfoSet) {
        this.imageInfoSet = imageInfoSet;
    }

    @Override
    public String getObjectId(final Object object) {
        Integer id = idMap.get(object);

        if (id == null) {
            id = new Integer(idMap.size());
            idMap.put(object, id);
        }

        return String.valueOf(id);
    }

    @Override
    public String getPageTitle() {
        return ResourceString.getResourceString("html.report.page.title." + getType());
    }

    @Override
    public String generatePackageFrame(final ERDiagram diagram) throws IOException {
        final String template = ExportToHtmlManager.getTemplate("types/package-frame/package-frame_template.html");

        final Object[] args = {getPageTitle(), generatePackageFrameTable(diagram)};
        return MessageFormat.format(template, args);
    }

    private String generatePackageFrameTable(final ERDiagram diagram) throws IOException {
        final StringBuilder sb = new StringBuilder();

        final String template = ExportToHtmlManager.getTemplate("types/package-frame/package-frame_row_template.html");

        for (final Object object : getObjectList(diagram)) {
            final Object[] args = getPackageFrameRowArgs(object);
            final String row = MessageFormat.format(template, args);

            sb.append(row);
        }

        return sb.toString();
    }

    public String[] getPackageFrameRowArgs(final Object object) {
        return new String[] {getObjectId(object), getObjectName(object)};
    }

    @Override
    public abstract List<Object> getObjectList(ERDiagram diagram);

    @Override
    public String generatePackageSummary(final HtmlReportPageGenerator prevPageGenerator, final HtmlReportPageGenerator nextPageGenerator, final ERDiagram diagram) throws IOException {
        final String template = ExportToHtmlManager.getTemplate("types/package-summary/package-summary_template.html");

        String prevPage = "<b>" + ResourceString.getResourceString("html.report.prev.object.type") + "</b>";

        if (prevPageGenerator != null) {
            prevPage = "<a HREF=\"../" + prevPageGenerator.getType() + "/package-summary.html\" >" + prevPage + "</a>";
        }

        String nextPage = "<b>" + ResourceString.getResourceString("html.report.next.object.type") + "</b>";

        if (nextPageGenerator != null) {
            nextPage = "<a HREF=\"../" + nextPageGenerator.getType() + "/package-summary.html\" >" + nextPage + "</a>";
        }

        final Object[] args = {getPageTitle(), prevPage, nextPage, generatePackageSummaryTable(diagram)};

        return MessageFormat.format(template, args);
    }

    private String generatePackageSummaryTable(final ERDiagram diagram) throws IOException {
        final StringBuilder sb = new StringBuilder();

        final String template = ExportToHtmlManager.getTemplate("types/package-summary/package-summary_row_template.html");

        for (final Object object : getObjectList(diagram)) {
            final Object[] args = getPackageSummaryRowArgs(object);
            final String row = MessageFormat.format(template, args);

            sb.append(row);
        }

        return sb.toString();
    }

    public String[] getPackageSummaryRowArgs(final Object object) {
        return new String[] {getObjectId(object), Format.null2blank(getObjectName(object)), Format.null2blank(getObjectSummary(object))};
    }

    @Override
    public String generateContent(final ERDiagram diagram, final Object object, final Object prevObject, final Object nextObject) throws IOException {
        final String template = ExportToHtmlManager.getTemplate("types/contents_template.html");

        final String pageTitle = getPageTitle();

        String prevPage = "<b>" + ResourceString.getResourceString("html.report.prev.of") + pageTitle + "</b>";

        if (prevObject != null) {
            prevPage = "<a HREF=\"" + getObjectId(prevObject) + ".html\" >" + prevPage + "</a>";
        }

        String nextPage = "<b>" + ResourceString.getResourceString("html.report.next.of") + pageTitle + "</b>";

        if (nextObject != null) {
            nextPage = "<a HREF=\"" + getObjectId(nextObject) + ".html\" >" + nextPage + "</a>";
        }

        String mainTemplate = ExportToHtmlManager.getTemplate("types/main/" + getType() + "_template.html");

        final Object[] contentArgs = getContentArgs(diagram, object);

        mainTemplate = MessageFormat.format(mainTemplate, contentArgs);

        final Object[] args = new String[] {getObjectName(object), pageTitle, prevPage, nextPage, mainTemplate, getObjectId(object)};

        return MessageFormat.format(template, args);
    }

    public abstract String getObjectSummary(Object object);

    public abstract String[] getContentArgs(ERDiagram diagram, Object object) throws IOException;

    protected String generateAttributeTable(final ERDiagram diagram, final List<NormalColumn> normalColumnList) throws IOException {
        final StringBuilder sb = new StringBuilder();

        final String template = ExportToHtmlManager.getTemplate("types/attribute_row_template.html");

        for (final NormalColumn normalColumn : normalColumnList) {
            String type = null;
            if (normalColumn.getType() != null) {
                type = Format.formatType(normalColumn.getType(), normalColumn.getTypeData(), diagram.getDatabase(), true);
            } else {
                type = "";
            }

            final Object[] args = {getObjectId(normalColumn), getPKString(normalColumn), getForeignKeyString(normalColumn), Format.null2blank(normalColumn.getLogicalName()), Format.null2blank(normalColumn.getPhysicalName()), type, getUniqueString(normalColumn), getNotNullString(normalColumn)};

            final String row = MessageFormat.format(template, args);

            sb.append(row);
        }

        return sb.toString();
    }

    public String generateAttributeDetailTable(final ERDiagram diagram, final List<NormalColumn> normalColumnList) throws IOException {
        final StringBuilder sb = new StringBuilder();

        final String template = ExportToHtmlManager.getTemplate("types/attribute_detail_row_template.html");

        for (final NormalColumn normalColumn : normalColumnList) {
            String type = null;

            if (normalColumn.getType() != null) {
                type = Format.formatType(normalColumn.getType(), normalColumn.getTypeData(), diagram.getDatabase(), true);
            } else {
                type = "";
            }

            final Object[] args = {getObjectId(normalColumn), getPKString(normalColumn), getForeignKeyString(normalColumn), Format.null2blank(normalColumn.getLogicalName()), Format.null2blank(normalColumn.getPhysicalName()), Format.null2blank(normalColumn.getDescription()), String.valueOf(normalColumn.isUniqueKey()).toUpperCase(), String.valueOf(normalColumn.isNotNull()).toUpperCase(), type, String.valueOf(normalColumn.isAutoIncrement()).toUpperCase(), Format.null2blank(normalColumn.getDefaultValue()), Format.null2blank(normalColumn.getConstraint())};
            final String row = MessageFormat.format(template, args);

            sb.append(row);
        }

        return sb.toString();
    }

    public String generateUsedTableTable(final List<TableView> tableList) throws IOException {
        final StringBuilder sb = new StringBuilder();

        final String template = ExportToHtmlManager.getTemplate("types/use_table_row_template.html");

        for (final TableView table : tableList) {
            final Object[] args = {getObjectId(table), table.getObjectType(), Format.null2blank(table.getPhysicalName()), Format.null2blank(table.getLogicalName())};
            final String row = MessageFormat.format(template, args);

            sb.append(row);
        }

        return sb.toString();
    }

    public String generateIndexAttributeTable(final ERTable table, final List<NormalColumn> normalColumnList, final List<Boolean> descs) throws IOException {
        final StringBuilder sb = new StringBuilder();

        final String template = ExportToHtmlManager.getTemplate("types/index_attribute_row_template.html");

        int i = 0;

        for (final NormalColumn normalColumn : normalColumnList) {
            final String tableId = getObjectId(table);
            final String columnId = getObjectId(normalColumn);
            final String columnPhysicalName = Format.null2blank(normalColumn.getPhysicalName());
            final String columnLogicalName = Format.null2blank(normalColumn.getLogicalName());
            final Boolean desc = descs.get(i);
            String descStr = null;
            if (desc != null) {
                if (desc.booleanValue()) {
                    descStr = "DESC";

                } else {
                    descStr = "ASC";
                }

            } else {
                descStr = "";
            }

            final Object[] args = {tableId, columnId, columnPhysicalName, columnLogicalName, descStr};
            final String row = MessageFormat.format(template, args);

            sb.append(row);
            i++;
        }

        return sb.toString();
    }

    protected String generateForeignKeyTable(final List<NormalColumn> foreignKeyList) throws IOException {
        final StringBuilder sb = new StringBuilder();

        final String template = ExportToHtmlManager.getTemplate("types/foreign_key_row_template.html");

        // [ermaster-fast] distinct
        List<String> generatedIds = new ArrayList<>();
        for (final NormalColumn normalColumn : foreignKeyList) {
            for (final Relation relation : normalColumn.getRelationList()) {
                final TableView sourceTable = relation.getSourceTableView();

                String id = getObjectId(normalColumn);
                if (generatedIds.contains(id)) {
                    continue;
                }
                final Object[] args = {id, Format.null2blank(normalColumn.getName()), getObjectId(sourceTable), Format.null2blank(sourceTable.getName()), getObjectId(normalColumn.getReferencedColumn(relation)), Format.null2blank(normalColumn.getReferencedColumn(relation).getName()), relation.getOnUpdateAction(), relation.getOnDeleteAction(), Format.null2blank(relation.getParentCardinality()), Format.null2blank(relation.getChildCardinality())};

                final String row = MessageFormat.format(template, args);

                sb.append(row);
                generatedIds.add(id);
            }
        }

        return sb.toString();
    }

    protected String generateReferenceKeyTable(final List<NormalColumn> foreignKeyList) throws IOException {
        final StringBuilder sb = new StringBuilder();

        final String template = ExportToHtmlManager.getTemplate("types/foreign_key_row_template.html");

        // [ermaster-fast] distinct
        List<String> generatedIds = new ArrayList<>();
        for (final NormalColumn normalColumn : foreignKeyList) {
            for (final Relation relation : normalColumn.getRelationList()) {
                final TableView targetTable = relation.getTargetTableView();

                String id = getObjectId(normalColumn);
                if (generatedIds.contains(id)) {
                    continue;
                }
                final Object[] args = {getObjectId(normalColumn.getReferencedColumn(relation)), Format.null2blank(normalColumn.getReferencedColumn(relation).getName()), getObjectId(targetTable), Format.null2blank(targetTable.getName()), id, Format.null2blank(normalColumn.getName()), relation.getOnUpdateAction(), relation.getOnDeleteAction(), Format.null2blank(relation.getParentCardinality()), Format.null2blank(relation.getChildCardinality())};

                final String row = MessageFormat.format(template, args);

                sb.append(row);
                generatedIds.add(id);
            }
        }

        return sb.toString();
    }

    public String generateIndexSummaryTable(final List<Index> indexList) throws IOException {
        final StringBuilder sb = new StringBuilder();

        final String template = ExportToHtmlManager.getTemplate("types/index_summary_row_template.html");

        for (final Index index : indexList) {
            final String id = getObjectId(index);
            final String name = Format.null2blank(index.getName());
            final String type = Format.null2blank(index.getType());
            String unique = null;
            if (!index.isNonUnique()) {
                unique = "UNIQUE";
            } else {
                unique = "";
            }

            final Object[] args = {id, name, type, unique};
            final String row = MessageFormat.format(template, args);

            sb.append(row);
        }

        return sb.toString();
    }

    public String generateIndexMatrix(final List<Index> indexList, final List<NormalColumn> normalColumnList) throws IOException {

        if (indexList.isEmpty()) {
            return "";
        }

        String template = ExportToHtmlManager.getTemplate("types/index_matrix/index_matrix_template.html");

        final String headerTemplate = ExportToHtmlManager.getTemplate("types/index_matrix/index_matrix_header_column_template.html");

        final StringBuilder header = new StringBuilder();

        for (final Index index : indexList) {
            final String name = index.getName();

            final Object[] args = {name};
            final String column = MessageFormat.format(headerTemplate, args);

            header.append(column);
        }

        final String rowTemplate = ExportToHtmlManager.getTemplate("types/index_matrix/index_matrix_data_row_template.html");

        final String dataColumnTemplate = ExportToHtmlManager.getTemplate("types/index_matrix/index_matrix_data_column_template.html");

        final StringBuilder body = new StringBuilder();

        for (final NormalColumn normalColumn : normalColumnList) {
            final String name = normalColumn.getName();
            final StringBuilder rowContent = new StringBuilder();

            for (final Index index : indexList) {
                int no = 1;
                String noString = "";

                for (final NormalColumn indexColumn : index.getColumns()) {
                    if (indexColumn == normalColumn) {
                        noString = String.valueOf(no);
                        break;
                    }
                    no++;
                }

                final Object[] args = {noString};
                final String column = MessageFormat.format(dataColumnTemplate, args);

                rowContent.append(column);
            }

            final Object[] args = {name, rowContent.toString()};
            final String row = MessageFormat.format(rowTemplate, args);

            body.append(row);
        }

        template = MessageFormat.format(template, new Object[] {header.toString(), body.toString()});

        return template;
    }

    public String generateComplexUniqueKeyMatrix(final List<ComplexUniqueKey> complexUniqueKeyList, final List<NormalColumn> normalColumnList) throws IOException {

        String template = ExportToHtmlManager.getTemplate("types/complex_unique_key_matrix/complex_unique_key_matrix_template.html");

        final String headerTemplate = ExportToHtmlManager.getTemplate("types/complex_unique_key_matrix/complex_unique_key_matrix_header_column_template.html");

        final StringBuilder header = new StringBuilder();

        for (final ComplexUniqueKey complexUniqueKey : complexUniqueKeyList) {
            final String name = Format.null2blank(complexUniqueKey.getUniqueKeyName());

            final Object[] args = {name};
            final String column = MessageFormat.format(headerTemplate, args);

            header.append(column);
        }

        final String rowTemplate = ExportToHtmlManager.getTemplate("types/complex_unique_key_matrix/complex_unique_key_matrix_data_row_template.html");

        final String dataColumnTemplate = ExportToHtmlManager.getTemplate("types/complex_unique_key_matrix/complex_unique_key_matrix_data_column_template.html");

        final StringBuilder body = new StringBuilder();

        if (!complexUniqueKeyList.isEmpty()) {
            for (final NormalColumn normalColumn : normalColumnList) {
                final String name = normalColumn.getName();
                final StringBuilder rowContent = new StringBuilder();

                for (final ComplexUniqueKey complexUniqueKey : complexUniqueKeyList) {
                    int no = 1;
                    String noString = "";

                    for (final NormalColumn complexUniqueKeyColumn : complexUniqueKey.getColumnList()) {
                        if (complexUniqueKeyColumn == normalColumn) {
                            noString = String.valueOf(no);
                            break;
                        }
                        no++;
                    }

                    final Object[] args = {noString};
                    final String column = MessageFormat.format(dataColumnTemplate, args);

                    rowContent.append(column);
                }

                final Object[] args = {name, rowContent.toString()};
                final String row = MessageFormat.format(rowTemplate, args);

                body.append(row);
            }
        }

        template = MessageFormat.format(template, new Object[] {header.toString(), body.toString(), complexUniqueKeyList.size() + 1});

        return template;
    }

    private String getPKString(final NormalColumn normalColumn) {
        if (normalColumn.isPrimaryKey()) {
            return "<img src=\"../image/" + ExportToHtmlManager.ICON_FILES[0] + "\">";
        } else {
            return "";
        }
    }

    private String getForeignKeyString(final NormalColumn normalColumn) {
        if (normalColumn.isForeignKey()) {
            return "<img src=\"../image/" + ExportToHtmlManager.ICON_FILES[1] + "\">";
        } else {
            return "";
        }
    }

    private String getUniqueString(final NormalColumn normalColumn) {
        if (normalColumn.isUniqueKey()) {
            return "UNIQUE";
        } else {
            return "";
        }
    }

    private String getNotNullString(final NormalColumn normalColumn) {
        if (normalColumn.isNotNull()) {
            return "NOT NULL";
        } else {
            return "";
        }
    }

}
