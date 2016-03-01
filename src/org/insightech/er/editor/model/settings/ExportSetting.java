package org.insightech.er.editor.model.settings;

import java.io.Serializable;
import java.util.Map;

import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.settings.export.ExportDDLSetting;
import org.insightech.er.editor.model.settings.export.ExportExcelSetting;
import org.insightech.er.editor.model.settings.export.ExportHtmlSetting;
import org.insightech.er.editor.model.settings.export.ExportImageSetting;
import org.insightech.er.editor.model.settings.export.ExportJavaSetting;
import org.insightech.er.editor.model.settings.export.ExportTestDataSetting;

public class ExportSetting implements Serializable, Cloneable {

    private static final long serialVersionUID = 3669486436464233526L;

    private ExportDDLSetting exportDDLSetting = new ExportDDLSetting();

    private ExportExcelSetting exportExcelSetting = new ExportExcelSetting();

    private ExportHtmlSetting exportHtmlSetting = new ExportHtmlSetting();

    private ExportImageSetting exportImageSetting = new ExportImageSetting();

    private ExportJavaSetting exportJavaSetting = new ExportJavaSetting();

    private ExportTestDataSetting exportTestDataSetting = new ExportTestDataSetting();

    public ExportDDLSetting getExportDDLSetting() {
        return exportDDLSetting;
    }

    public void setExportDDLSetting(final ExportDDLSetting exportDDLSetting) {
        this.exportDDLSetting = exportDDLSetting;
    }

    public ExportExcelSetting getExportExcelSetting() {
        return exportExcelSetting;
    }

    public void setExportExcelSetting(final ExportExcelSetting exportExcelSetting) {
        this.exportExcelSetting = exportExcelSetting;
    }

    public ExportHtmlSetting getExportHtmlSetting() {
        return exportHtmlSetting;
    }

    public void setExportHtmlSetting(final ExportHtmlSetting exportHtmlSetting) {
        this.exportHtmlSetting = exportHtmlSetting;
    }

    public ExportImageSetting getExportImageSetting() {
        return exportImageSetting;
    }

    public void setExportImageSetting(final ExportImageSetting exportImageSetting) {
        this.exportImageSetting = exportImageSetting;
    }

    public ExportJavaSetting getExportJavaSetting() {
        return exportJavaSetting;
    }

    public void setExportJavaSetting(final ExportJavaSetting exportJavaSetting) {
        this.exportJavaSetting = exportJavaSetting;
    }

    public ExportTestDataSetting getExportTestDataSetting() {
        return exportTestDataSetting;
    }

    public void setExportTestDataSetting(final ExportTestDataSetting exportTestDataSetting) {
        this.exportTestDataSetting = exportTestDataSetting;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((exportDDLSetting == null) ? 0 : exportDDLSetting.hashCode());
        result = prime * result + ((exportExcelSetting == null) ? 0 : exportExcelSetting.hashCode());
        result = prime * result + ((exportHtmlSetting == null) ? 0 : exportHtmlSetting.hashCode());
        result = prime * result + ((exportImageSetting == null) ? 0 : exportImageSetting.hashCode());
        result = prime * result + ((exportJavaSetting == null) ? 0 : exportJavaSetting.hashCode());
        result = prime * result + ((exportTestDataSetting == null) ? 0 : exportTestDataSetting.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ExportSetting other = (ExportSetting) obj;
        if (exportDDLSetting == null) {
            if (other.exportDDLSetting != null)
                return false;
        } else if (!exportDDLSetting.equals(other.exportDDLSetting))
            return false;
        if (exportExcelSetting == null) {
            if (other.exportExcelSetting != null)
                return false;
        } else if (!exportExcelSetting.equals(other.exportExcelSetting))
            return false;
        if (exportHtmlSetting == null) {
            if (other.exportHtmlSetting != null)
                return false;
        } else if (!exportHtmlSetting.equals(other.exportHtmlSetting))
            return false;
        if (exportImageSetting == null) {
            if (other.exportImageSetting != null)
                return false;
        } else if (!exportImageSetting.equals(other.exportImageSetting))
            return false;
        if (exportJavaSetting == null) {
            if (other.exportJavaSetting != null)
                return false;
        } else if (!exportJavaSetting.equals(other.exportJavaSetting))
            return false;
        if (exportTestDataSetting == null) {
            if (other.exportTestDataSetting != null)
                return false;
        } else if (!exportTestDataSetting.equals(other.exportTestDataSetting))
            return false;
        return true;
    }

    public ExportSetting clone(final Map<Category, Category> categoryCloneMap, final Map<Environment, Environment> environmentCloneMap) {
        try {
            final ExportSetting clone = (ExportSetting) super.clone();

            clone.setExportDDLSetting(exportDDLSetting.clone(categoryCloneMap, environmentCloneMap));
            clone.setExportExcelSetting(exportExcelSetting.clone(categoryCloneMap));
            clone.setExportHtmlSetting(exportHtmlSetting.clone());
            clone.setExportImageSetting(exportImageSetting.clone());
            clone.setExportJavaSetting(exportJavaSetting.clone());
            clone.setExportTestDataSetting(exportTestDataSetting.clone());

            return clone;

        } catch (final CloneNotSupportedException e) {
            return null;
        }
    }
}
