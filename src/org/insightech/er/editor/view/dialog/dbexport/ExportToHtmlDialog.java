package org.insightech.er.editor.view.dialog.dbexport;

import java.io.File;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.insightech.er.ResourceString;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.common.widgets.DirectoryText;
import org.insightech.er.common.widgets.MultiLineCheckbox;
import org.insightech.er.editor.model.dbexport.ExportWithProgressManager;
import org.insightech.er.editor.model.dbexport.html.ExportToHtmlManager;
import org.insightech.er.editor.model.settings.ExportSetting;
import org.insightech.er.editor.model.settings.export.ExportHtmlSetting;
import org.insightech.er.util.io.FileUtils;

public class ExportToHtmlDialog extends AbstractExportDialog {

    private DirectoryText outputDirText;

    private MultiLineCheckbox withImageButton;

    private MultiLineCheckbox withCategoryImageButton;

    // private Combo fileEncodingCombo;

    @Override
    protected void initLayout(final GridLayout layout) {
        super.initLayout(layout);

        layout.numColumns = 3;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize(final Composite parent) {
        outputDirText = CompositeFactory.createDirectoryText(this, parent, "label.output.dir", getBaseDir(), ResourceString.getResourceString("dialog.message.export.html.dir.select"));

        // this.fileEncodingCombo = CompositeFactory
        // .createFileEncodingCombo(this.diagramFile, this, parent,
        // "label.output.file.encoding", 2);

        final Composite checkboxArea = this.createCheckboxArea(parent);

        withImageButton = CompositeFactory.createMultiLineCheckbox(this, checkboxArea, "label.output.image.to.html", false, 1);

        withCategoryImageButton = CompositeFactory.createMultiLineCheckbox(this, checkboxArea, "label.output.image.to.html.category", false, 1);

        createOpenAfterSavedButton(checkboxArea, false, 1);
    }

    @Override
    protected String getErrorMessage() {
        // if (this.outputDirText.isBlank()) {
        // return "error.output.dir.is.empty";
        // }

        return null;
    }

    @Override
    protected ExportWithProgressManager getExportWithProgressManager(final ExportSetting exportSetting) throws InputException {

        final ExportHtmlSetting exportHtmlSetting = exportSetting.getExportHtmlSetting();

        exportHtmlSetting.setOutputDir(outputDirText.getFilePath());
        // exportHtmlSetting.setSrcFileEncoding(this.fileEncodingCombo.getText());
        exportHtmlSetting.setWithImage(withImageButton.getSelection());
        exportHtmlSetting.setWithCategoryImage(withCategoryImageButton.getSelection());
        exportHtmlSetting.setOpenAfterSaved(openAfterSavedButton.getSelection());

        return new ExportToHtmlManager(exportHtmlSetting);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setData() {
        final ExportHtmlSetting exportHtmlSetting = settings.getExportSetting().getExportHtmlSetting();

        outputDirText.setText(FileUtils.getRelativeFilePath(getBaseDir(), exportHtmlSetting.getOutputDir()));

        // if (!Check.isEmpty(exportHtmlSetting.getSrcFileEncoding())) {
        // this.fileEncodingCombo.setText(exportHtmlSetting
        // .getSrcFileEncoding());
        // }

        withImageButton.setSelection(exportHtmlSetting.isWithImage());
        withCategoryImageButton.setSelection(exportHtmlSetting.isWithCategoryImage());
        openAfterSavedButton.setSelection(exportHtmlSetting.isOpenAfterSaved());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTitle() {
        return "dialog.title.export.html";
    }

    @Override
    protected File openAfterSaved() {
        return ExportToHtmlManager.getIndexHtml(FileUtils.getFile(getBaseDir(), settings.getExportSetting().getExportHtmlSetting().getOutputDir()));
    }

    @Override
    protected boolean openWithExternalEditor() {
        return true;
    }

}
