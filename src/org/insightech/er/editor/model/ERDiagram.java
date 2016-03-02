package org.insightech.er.editor.model;

import java.util.Locale;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.insightech.er.editor.ERDiagramMultiPageEditor;
import org.insightech.er.editor.model.diagram_contents.DiagramContents;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GlobalGroupSet;
import org.insightech.er.editor.model.settings.DBSetting;
import org.insightech.er.editor.model.settings.PageSetting;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.model.tracking.ChangeTrackingList;

public class ERDiagram extends ViewableModel {

    private static final long serialVersionUID = 8729319470770699498L;

    private ChangeTrackingList changeTrackingList;

    private DiagramContents diagramContents;

    private ERDiagramMultiPageEditor editor;

    private int[] defaultColor;

    private boolean tooltip;

    private boolean disableSelectColumn;

    private boolean snapToGrid;

    private Category currentCategory;

    private int pageIndex;

    private double zoom = 1.0d;

    private int x;

    private int y;

    private DBSetting dbSetting;

    private PageSetting pageSetting;

    public Point mousePoint = new Point();

    public ERDiagram(final String database) {
        diagramContents = new DiagramContents();
        diagramContents.getSettings().setDatabase(database);
        pageSetting = new PageSetting();

        setDefaultColor(128, 128, 192);
        setColor(255, 255, 255);

        if (Display.getCurrent() != null) {
            final FontData fontData = Display.getCurrent().getSystemFont().getFontData()[0];
            setFontName(fontData.getName());
        }
    }

    public void clear() {
        diagramContents.clear();
        changeTrackingList.clear();

        diagramContents.setColumnGroups(GlobalGroupSet.load());
    }

    public void init() {
        diagramContents.setColumnGroups(GlobalGroupSet.load());

        final Settings settings = getDiagramContents().getSettings();

        if (Locale.JAPANESE.getLanguage().equals(Locale.getDefault().getLanguage())) {
            settings.getTranslationSetting().setUse(true);
            settings.getTranslationSetting().selectDefault();
        }

        settings.getModelProperties().init();
    }

    public void addNewContent(final NodeElement element) {
        element.setColor(defaultColor[0], defaultColor[1], defaultColor[2]);
        element.setFontName(getFontName());
        element.setFontSize(getFontSize());

        addContent(element);
    }

    public void addContent(final NodeElement element) {
        element.setDiagram(this);

        diagramContents.getContents().addNodeElement(element);

        if (element instanceof TableView) {
            for (final NormalColumn normalColumn : ((TableView) element).getNormalColumns()) {
                getDiagramContents().getDictionary().add(normalColumn);
            }
        }

    }

    public void removeContent(final NodeElement element) {
        diagramContents.getContents().remove(element);

        if (element instanceof TableView) {
            diagramContents.getDictionary().remove((TableView) element);
        }

        for (final Category category : diagramContents.getSettings().getCategorySetting().getAllCategories()) {
            category.getContents().remove(element);
        }
    }

    public void replaceContents(final DiagramContents newDiagramContents) {
        diagramContents = newDiagramContents;
    }

    public String getDatabase() {
        return getDiagramContents().getSettings().getDatabase();
    }

    public void setSettings(final Settings settings) {
        getDiagramContents().setSettings(settings);
        // [ermaster-fast] Avoid expensive initialization, disable category tabs
        //editor.initCategoryPages();
    }

    public void setCurrentCategoryPageName() {
        editor.setCurrentCategoryPageName();
    }

    public void addCategory(final Category category) {
        category.setColor(defaultColor[0], defaultColor[1], defaultColor[2]);
        category.setFontName(getFontName());
        category.setFontSize(getFontSize());

        getDiagramContents().getSettings().getCategorySetting().addCategoryAsSelected(category);
        // [ermaster-fast] Avoid expensive initialization, disable category tabs
        //editor.initCategoryPages();
    }

    public void removeCategory(final Category category) {
        getDiagramContents().getSettings().getCategorySetting().removeCategory(category);
        // [ermaster-fast] Avoid expensive initialization, disable category tabs
        //editor.initCategoryPages();
    }

    public void restoreCategories() {
        // [ermaster-fast] Avoid expensive initialization, disable category tabs
        //editor.initCategoryPages();
    }

    public void change() {}

    public ChangeTrackingList getChangeTrackingList() {
        if (changeTrackingList == null) {
            changeTrackingList = new ChangeTrackingList();
        }
        return changeTrackingList;
    }

    public DiagramContents getDiagramContents() {
        return diagramContents;
    }

    public void setEditor(final ERDiagramMultiPageEditor editor) {
        this.editor = editor;
    }

    public int[] getDefaultColor() {
        return defaultColor;
    }

    public RGB getDefaultColorAsGRB() {
        final RGB rgb = new RGB(defaultColor[0], defaultColor[1], defaultColor[2]);

        return rgb;
    }

    public void setDefaultColor(final int red, final int green, final int blue) {
        defaultColor = new int[3];
        defaultColor[0] = red;
        defaultColor[1] = green;
        defaultColor[2] = blue;
    }

    public void setCurrentCategory(final Category currentCategory, final int pageIndex) {
        this.currentCategory = currentCategory;
        this.pageIndex = pageIndex;
    }

    public Category getCurrentCategory() {
        return currentCategory;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public boolean isTooltip() {
        return tooltip;
    }

    public void setTooltip(final boolean tooltip) {
        this.tooltip = tooltip;
    }

    public double getZoom() {
        return zoom;
    }

    public void setZoom(final double zoom) {
        this.zoom = zoom;
    }

    public void setLocation(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public DBSetting getDbSetting() {
        return dbSetting;
    }

    public void setDbSetting(final DBSetting dbSetting) {
        this.dbSetting = dbSetting;
    }

    public PageSetting getPageSetting() {
        return pageSetting;
    }

    public void setPageSetting(final PageSetting pageSetting) {
        this.pageSetting = pageSetting;
    }

    public ERDiagramMultiPageEditor getEditor() {
        return editor;
    }

    public String filter(final String str) {
        if (str == null) {
            return str;
        }

        final Settings settings = getDiagramContents().getSettings();

        if (settings.isCapital()) {
            return str.toUpperCase();
        }

        return str;
    }

    public boolean isDisableSelectColumn() {
        return disableSelectColumn;
    }

    public void setDisableSelectColumn(final boolean disableSelectColumn) {
        this.disableSelectColumn = disableSelectColumn;
    }

    public boolean isSnapToGrid() {
        return snapToGrid;
    }

    public void setSnapToGrid(final boolean snapToGrid) {
        this.snapToGrid = snapToGrid;
    }

    public void refreshChildren() {
        if (isUpdateable()) {
            this.firePropertyChange("refreshChildren", null, null);
        }
    }

    public void refreshConnection() {
        if (isUpdateable()) {
            this.firePropertyChange("refreshConnection", null, null);
        }
    }

    public void refreshOutline() {
        if (isUpdateable()) {
            this.firePropertyChange("refreshOutline", null, null);
        }
    }

    public void refreshSettings() {
        if (isUpdateable()) {
            this.firePropertyChange("refreshSettings", null, null);
        }
    }

    public void refreshWithConnection() {
        if (isUpdateable()) {
            this.firePropertyChange("refreshWithConnection", null, null);
        }
    }

    public void refreshCategories() {
        for (final Category category : getDiagramContents().getSettings().getCategorySetting().getSelectedCategories()) {
            category.refreshVisuals();
        }
    }

}
