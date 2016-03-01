package org.insightech.er.preference.page.translation;

import org.eclipse.swt.widgets.Composite;
import org.insightech.er.preference.PreferenceInitializer;
import org.insightech.er.preference.editor.FileListEditor;

public class TranslationFileListEditor extends FileListEditor {

    public TranslationFileListEditor(final String name, final String labelText, final Composite parent) {
        super(name, labelText, parent, "*.txt");
    }

    @Override
    protected String getStorePath(final String name) {
        return PreferenceInitializer.getTranslationPath(name);
    }

}
