package org.insightech.er.editor.model.dbexport.html.page_generator.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.html.page_generator.AbstractHtmlReportPageGenerator;
import org.insightech.er.editor.model.dbexport.html.part_generator.ImagePartGenerator;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;

public class CategoryHtmlReportPageGenerator extends AbstractHtmlReportPageGenerator {

    public CategoryHtmlReportPageGenerator(final Map<Object, Integer> idMap) {
        super(idMap);
    }

    @Override
    public String getType() {
        return "category";
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Object> getObjectList(final ERDiagram diagram) {
        final List list = diagram.getDiagramContents().getSettings().getCategorySetting().getSelectedCategories();

        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getContentArgs(final ERDiagram diagram, final Object object) throws IOException {
        final Category category = (Category) object;

        final List<TableView> usedTableList = category.getTableViewContents();

        final String usedTableTable = generateUsedTableTable(usedTableList);

        String imagePart = "";

        if (imageInfoSet != null) {
            final ImagePartGenerator imagePartGenerator = new ImagePartGenerator(idMap);

            imagePart = imagePartGenerator.generateImage(imageInfoSet.getImageInfo(category), "../");
        }

        return new String[] {imagePart, usedTableTable};
    }

    @Override
    public String getObjectName(final Object object) {
        final Category category = (Category) object;

        return category.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getObjectSummary(final Object object) {
        return null;
    }

}
