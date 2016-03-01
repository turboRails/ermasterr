package org.insightech.er.common.widgets;

import java.io.File;

import org.eclipse.swt.widgets.Composite;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.util.io.FileUtils;

public class DirectoryText extends AbstractPathText {

    private final String message;

    public DirectoryText(final Composite parent, final File projectDir, final String message) {
        this(parent, projectDir, message, true);
    }

    public DirectoryText(final Composite parent, final File projectDir, final String message, final boolean indent) {
        super(parent, projectDir, indent);

        this.message = message;
    }

    @Override
    protected String selectPathByDilaog() {
        final String filePath = FileUtils.getFile(projectDir, getFilePath()).getAbsolutePath();

        return ERDiagramActivator.showDirectoryDialog(filePath, message);
    }

}
