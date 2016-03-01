package org.insightech.er.editor.model.dbexport.html.page_generator;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.html.ExportToHtmlManager;
import org.insightech.er.editor.model.dbexport.html.part_generator.ImagePartGenerator;
import org.insightech.er.editor.model.dbexport.image.ImageInfoSet;

public class OverviewHtmlReportPageGenerator {

    private final Map<Object, Integer> idMap;

    public OverviewHtmlReportPageGenerator(final Map<Object, Integer> idMap) {
        this.idMap = idMap;
    }

    public String getObjectId(final Object object) {
        Integer id = idMap.get(object);

        if (id == null) {
            id = new Integer(idMap.size());
            idMap.put(object, id);
        }

        return String.valueOf(id);
    }

    public String generateFrame(final List<HtmlReportPageGenerator> htmlReportPageGeneratorList) throws IOException {
        final String template = ExportToHtmlManager.getTemplate("overview/overview-frame_template.html");

        final Object[] args = {generateFrameTable(htmlReportPageGeneratorList)};
        return MessageFormat.format(template, args);
    }

    private String generateFrameTable(final List<HtmlReportPageGenerator> htmlReportPageGeneratorList) throws IOException {
        final StringBuilder sb = new StringBuilder();

        final String template = ExportToHtmlManager.getTemplate("overview/overview-frame_row_template.html");

        for (final HtmlReportPageGenerator pageGenerator : htmlReportPageGeneratorList) {
            final Object[] args = {pageGenerator.getType(), pageGenerator.getPageTitle()};
            final String row = MessageFormat.format(template, args);

            sb.append(row);
        }

        return sb.toString();
    }

    public String generateSummary(final ImageInfoSet imageInfoSet, final List<HtmlReportPageGenerator> htmlReportPageGeneratorList) throws IOException {

        final String template = ExportToHtmlManager.getTemplate("overview/overview-summary_template.html");

        String imagePart = "";

        if (imageInfoSet != null) {
            final ImagePartGenerator imagePartGenerator = new ImagePartGenerator(idMap);

            imagePart = imagePartGenerator.generateImage(imageInfoSet.getDiagramImageInfo(), "");
        }

        final Object[] args = {imagePart, generateSummaryTable(htmlReportPageGeneratorList)};

        return MessageFormat.format(template, args);
    }

    private String generateSummaryTable(final List<HtmlReportPageGenerator> htmlReportPageGeneratorList) throws IOException {
        final StringBuilder sb = new StringBuilder();

        final String template = ExportToHtmlManager.getTemplate("overview/overview-summary_row_template.html");

        for (final HtmlReportPageGenerator pageGenerator : htmlReportPageGeneratorList) {
            final Object[] args = {pageGenerator.getType(), pageGenerator.getPageTitle()};
            final String row = MessageFormat.format(template, args);

            sb.append(row);
        }

        return sb.toString();
    }

    public String generateAllClasses(final ERDiagram diagram, final List<HtmlReportPageGenerator> htmlReportPageGeneratorList) throws IOException {
        final String template = ExportToHtmlManager.getTemplate("allclasses_template.html");

        final Object[] args = {generateAllClassesTable(diagram, htmlReportPageGeneratorList)};

        return MessageFormat.format(template, args);
    }

    private String generateAllClassesTable(final ERDiagram diagram, final List<HtmlReportPageGenerator> htmlReportPageGeneratorList) throws IOException {
        final StringBuilder sb = new StringBuilder();

        final String template = ExportToHtmlManager.getTemplate("allclasses_row_template.html");

        for (int i = 0; i < htmlReportPageGeneratorList.size(); i++) {
            final HtmlReportPageGenerator pageGenerator = htmlReportPageGeneratorList.get(i);

            for (final Object object : pageGenerator.getObjectList(diagram)) {
                final Object[] args = {pageGenerator.getType() + "/" + pageGenerator.getObjectId(object) + ".html", pageGenerator.getObjectName(object)};
                final String row = MessageFormat.format(template, args);

                sb.append(row);
            }
        }

        return sb.toString();
    }

    public int countAllClasses(final ERDiagram diagram, final List<HtmlReportPageGenerator> htmlReportPageGeneratorList) {
        int count = 0;

        for (int i = 0; i < htmlReportPageGeneratorList.size(); i++) {
            final HtmlReportPageGenerator pageGenerator = htmlReportPageGeneratorList.get(i);
            count += pageGenerator.getObjectList(diagram).size();
        }

        return count;
    }
}
