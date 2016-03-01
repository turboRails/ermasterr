package org.insightech.er.editor.model.dbexport.html.part_generator;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;

import org.insightech.er.editor.model.dbexport.html.ExportToHtmlManager;
import org.insightech.er.editor.model.dbexport.image.ImageInfo;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;

public class ImagePartGenerator {

    private final Map<Object, Integer> idMap;

    private Category category;

    public ImagePartGenerator(final Map<Object, Integer> idMap) {
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

    public String generateImage(final ImageInfo imageInfo, final String relativePath) throws IOException {
        if (imageInfo.getPath() == null) {
            return "";
        }

        final String template = ExportToHtmlManager.getTemplate("overview/overview-summary_image_template.html");

        final String pathToImageFile = relativePath + ExportToHtmlManager.IMAGE_DIR + File.separator + imageInfo.getPath();

        final Object[] args = {pathToImageFile, generateImageMap(imageInfo.getTableLocationMap(), relativePath)};

        return MessageFormat.format(template, args);
    }

    private String generateImageMap(final Map<TableView, Location> tableLocationMap, final String relativePath) throws IOException {
        final StringBuilder sb = new StringBuilder();

        if (tableLocationMap != null) {
            final String template = ExportToHtmlManager.getTemplate("overview/overview-summary_image_map_template.html");

            for (final Map.Entry<TableView, Location> entry : tableLocationMap.entrySet()) {
                if (category == null || category.contains(entry.getKey())) {
                    final Location location = entry.getValue();

                    String pathToHtmlFile = entry.getKey().getObjectType() + "/" + getObjectId(entry.getKey()) + ".html";

                    pathToHtmlFile = relativePath + pathToHtmlFile;

                    final Object[] args = {String.valueOf(location.x), String.valueOf(location.y), String.valueOf(location.x + location.width), String.valueOf(location.y + location.height), pathToHtmlFile,};
                    final String row = MessageFormat.format(template, args);

                    sb.append(row);
                }
            }
        }

        return sb.toString();
    }
}
