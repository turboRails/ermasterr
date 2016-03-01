package org.insightech.er.preference.page.template;

import org.eclipse.swt.widgets.Composite;
import org.insightech.er.preference.PreferenceInitializer;
import org.insightech.er.preference.editor.FileListEditor;

public class TemplateFileListEditor extends FileListEditor {

    public TemplateFileListEditor(final String name, final String labelText, final Composite parent) {
        super(name, labelText, parent, "*.xls");
    }

    @Override
    protected String getStorePath(final String name) {
        return PreferenceInitializer.getTemplatePath(name);
    }

}
