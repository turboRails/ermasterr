package org.insightech.er.editor.model.dbexport.image;

import java.util.HashMap;
import java.util.Map;

import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;

public class ImageInfoSet {

    private static final int MAX_NAME_LENGTH = 26;

    private final ImageInfo diagramImageInfo;

    private final Map<Category, ImageInfo> categoryImageInfoMap;

    private final Map<String, Integer> fileNameMap = new HashMap<String, Integer>();

    public ImageInfoSet(final ImageInfo diagramImageInfo) {
        this.diagramImageInfo = diagramImageInfo;
        categoryImageInfoMap = new HashMap<Category, ImageInfo>();
    }

    public ImageInfo getDiagramImageInfo() {
        return diagramImageInfo;
    }

    public void addImageInfo(final Category category, final ImageInfo imageInfo) {
        categoryImageInfoMap.put(category, imageInfo);
    }

    public ImageInfo getImageInfo(final Category category) {
        return categoryImageInfoMap.get(category);
    }

    public String decideFileName(String name, final String extension) {
        if (name.length() > MAX_NAME_LENGTH) {
            name = name.substring(0, MAX_NAME_LENGTH);
        }

        String fileName = null;

        Integer sameNameNum = fileNameMap.get(name.toUpperCase());
        if (sameNameNum == null) {
            sameNameNum = 0;
            fileName = name;

        } else {
            do {
                sameNameNum++;
                fileName = name + "(" + sameNameNum + ")";
            } while (fileNameMap.containsKey(fileName.toUpperCase()));
        }

        fileNameMap.put(name.toUpperCase(), sameNameNum);

        return fileName + extension;
    }

}
