package org.insightech.er.common.widgets;

import java.io.File;

import org.eclipse.swt.widgets.Composite;
import org.insightech.er.ERDiagramActivator;

public class FileText extends AbstractPathText {

    private String[] filterExtensions;

    private final String defaultFileName;

    private final boolean save;

    public FileText(final boolean save, final Composite parent, final File projectDir, final String defaultFileName, final String filterExtension) {
        this(save, parent, projectDir, defaultFileName, filterExtension, true);
    }

    public FileText(final boolean save, final Composite parent, final File projectDir, final String defaultFileName, final String filterExtension, final boolean indent) {
        this(save, parent, projectDir, defaultFileName, new String[] {filterExtension}, indent);
    }

    public FileText(final boolean save, final Composite parent, final File projectDir, final String defaultFileName, final String[] filterExtensions) {
        this(save, parent, projectDir, defaultFileName, filterExtensions, true);
    }

    public FileText(final boolean save, final Composite parent, final File projectDir, final String defaultFileName, final String[] filterExtensions, final boolean indent) {
        super(parent, projectDir, indent);

        this.filterExtensions = filterExtensions;
        this.defaultFileName = defaultFileName;
        this.save = save;
    }

    public void setFilterExtension(final String filterExtension) {
        filterExtensions = new String[] {filterExtension};
    }

    @Override
    protected String selectPathByDilaog() {
        return ERDiagramActivator.showSaveDialog(projectDir, defaultFileName, getFilePath(), filterExtensions, save);
    }

}
