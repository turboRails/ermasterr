package org.insightech.er.editor.view.action.dbexport;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.model.settings.ExportSetting;
import org.insightech.er.editor.view.action.AbstractBaseAction;

public abstract class AbstractExportAction extends AbstractBaseAction {

    public AbstractExportAction(final String id, final String label, final ERDiagramEditor editor) {
        super(id, label, editor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(final Event event) throws Exception {
        this.save(getEditorPart(), getGraphicalViewer());
    }

    protected void save(final IEditorPart editorPart, final GraphicalViewer viewer) throws Exception {

        final String saveFilePath = getSaveFilePath(editorPart, viewer, getDiagram().getDiagramContents().getSettings().getExportSetting());
        if (saveFilePath == null) {
            return;
        }

        final File file = new File(saveFilePath);
        if (file.exists()) {
            final MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
            messageBox.setText(ResourceString.getResourceString("dialog.title.warning"));
            messageBox.setMessage(ResourceString.getResourceString(getConfirmOverrideMessage()));

            if (messageBox.open() == SWT.CANCEL) {
                return;
            }
        }

        this.save(editorPart, viewer, saveFilePath);
        refreshProject();
    }

    protected String getConfirmOverrideMessage() {
        return "dialog.message.update.file";
    }

    protected String getSaveFilePath(final IEditorPart editorPart, final GraphicalViewer viewer, final ExportSetting exportSetting) {

        final FileDialog fileDialog = new FileDialog(editorPart.getEditorSite().getShell(), SWT.SAVE);

        fileDialog.setFilterPath(getBasePath());

        final String[] filterExtensions = getFilterExtensions();
        fileDialog.setFilterExtensions(filterExtensions);

        final String fileName = getDiagramFileName(editorPart);

        fileDialog.setFileName(fileName);

        return fileDialog.open();
    }

    protected String getDiagramFileName(final IEditorPart editorPart) {
        final IFile file = ((IFileEditorInput) editorPart.getEditorInput()).getFile();
        final String fileName = file.getName();

        return fileName.substring(0, fileName.lastIndexOf(".")) + getDefaultExtension();
    }

    protected abstract String getDefaultExtension();

    protected String getSaveDirPath(final IEditorPart editorPart, final GraphicalViewer viewer) {

        final DirectoryDialog directoryDialog = new DirectoryDialog(editorPart.getEditorSite().getShell(), SWT.SAVE);

        directoryDialog.setFilterPath(getBasePath());

        return directoryDialog.open();
    }

    protected abstract String[] getFilterExtensions();

    protected abstract void save(IEditorPart editorPart, GraphicalViewer viewer, String saveFilePath) throws Exception;
}
