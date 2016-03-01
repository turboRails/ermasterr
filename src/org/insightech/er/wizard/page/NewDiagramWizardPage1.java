package org.insightech.er.wizard.page;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.persistent.Persistent;

public class NewDiagramWizardPage1 extends WizardNewFileCreationPage {

    private ERDiagram diagram;

    private static final String EXTENSION = ".erm";

    public NewDiagramWizardPage1(final IStructuredSelection selection) {
        super(ResourceString.getResourceString("wizard.new.diagram.title"), selection);

        setTitle(ResourceString.getResourceString("wizard.new.diagram.title"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createControl(final Composite parent) {
        super.createControl(parent);

        setFileName("newfile");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean validatePage() {
        boolean valid = super.validatePage();
        if (valid) {
            final String fileName = getFileName();
            if (fileName.indexOf(".") != -1 && !fileName.endsWith(EXTENSION)) {
                setErrorMessage(ResourceString.getResourceString("error.erm.extension"));
                valid = false;
            }
        }
        if (valid) {
            String fileName = getFileName();
            if (fileName.indexOf(".") == -1) {
                fileName = fileName + EXTENSION;
            }
            final IWorkspace workspace = ResourcesPlugin.getWorkspace();
            final IWorkspaceRoot root = workspace.getRoot();

            final IPath containerPath = getContainerFullPath();
            final IPath newFilePath = containerPath.append(fileName);

            if (root.getFile(newFilePath).exists()) {
                setErrorMessage("'" + fileName + "' " + ResourceString.getResourceString("error.file.already.exists"));
                valid = false;
            }
        }

        if (valid) {
            this.setMessage(ResourceString.getResourceString("wizard.new.diagram.message"));
        }

        return valid;
    }

    public void createERDiagram(final String database) {
        diagram = new ERDiagram(database);
        diagram.init();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected InputStream getInitialContents() {
        final Persistent persistent = Persistent.getInstance();

        try {
            final InputStream in = persistent.createInputStream(diagram);
            return in;

        } catch (final IOException e) {
            ERDiagramActivator.showExceptionDialog(e);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IFile createNewFile() {
        final String fileName = getFileName();
        if (fileName.indexOf(".") == -1) {
            setFileName(fileName + EXTENSION);
        }

        return super.createNewFile();
    }

}
