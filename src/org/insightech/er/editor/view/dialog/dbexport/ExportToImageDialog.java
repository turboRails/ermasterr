package org.insightech.er.editor.view.dialog.dbexport;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.common.widgets.FileText;
import org.insightech.er.common.widgets.MultiLineCheckbox;
import org.insightech.er.editor.model.dbexport.ExportWithProgressManager;
import org.insightech.er.editor.model.dbexport.image.ExportToImageManager;
import org.insightech.er.editor.model.settings.ExportSetting;
import org.insightech.er.editor.model.settings.export.ExportImageSetting;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;
import org.insightech.er.util.io.FileUtils;

public class ExportToImageDialog extends AbstractExportDialog {

    private static final String DEFAULT_EXTENTION = ".png";

    private FileText outputFileText;

    // private DirectoryText categoryDirText;

    private MultiLineCheckbox withCategoryImageButton;

    private Label categoryLabel;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize(final Composite parent) {
        outputFileText = CompositeFactory.createFileText(true, this, parent, "label.output.file", getBaseDir(), getDefaultOutputFileName(DEFAULT_EXTENTION), new String[] {"*.png", "*.jpeg", "*.bmp"});

        CompositeFactory.createLabel(parent, "label.category");
        categoryLabel = CompositeFactory.createLabelAsValue(parent, "", 2);

        final Composite checkboxArea = this.createCheckboxArea(parent);

        withCategoryImageButton = CompositeFactory.createMultiLineCheckbox(this, checkboxArea, "label.output.category.image", false, 3);

        // CompositeFactory.createLabel(parent,
        // "label.output.category.image.dir");
        // this.categoryDirText = new DirectoryText(parent, SWT.BORDER, null);
        // this.categoryDirText.setLayoutData(gridData);

        createOpenAfterSavedButton(checkboxArea, false, 3);
    }

    @Override
    protected String getErrorMessage() {
        // this.categoryDirText.setEnabled(this.withCategoryImageButton
        // .getSelection());

        if (outputFileText.isBlank()) {
            return "error.output.file.is.empty";
        }

        return null;
    }

    @Override
    protected ExportWithProgressManager getExportWithProgressManager(final ExportSetting exportSetting) throws InputException {
        final ExportImageSetting exportImageSetting = exportSetting.getExportImageSetting();

        exportImageSetting.setOutputFilePath(outputFileText.getFilePath());
        exportImageSetting.setWithCategoryImage(withCategoryImageButton.getSelection());
        exportImageSetting.setOpenAfterSaved(openAfterSavedButton.getSelection());
        exportImageSetting.setCategory(diagram.getCurrentCategory());

        return new ExportToImageManager(exportImageSetting);
    }

    /**
     * {@inheritDoc}
     * 
     * @throws IOException
     */
    @Override
    protected void setData() {
        final ExportImageSetting exportImageSetting = settings.getExportSetting().getExportImageSetting();

        String outputFile = Format.null2blank(exportImageSetting.getOutputFilePath());

        if (Check.isEmpty(outputFile)) {
            outputFile = getDefaultOutputFilePath(DEFAULT_EXTENTION);
        }

        outputFileText.setText(FileUtils.getRelativeFilePath(getBaseDir(), outputFile));

        withCategoryImageButton.setSelection(exportImageSetting.isWithCategoryImage());
        openAfterSavedButton.setSelection(exportImageSetting.isOpenAfterSaved());

        setCategoryData(categoryLabel);
        if (diagram.getCurrentCategory() != null) {
            withCategoryImageButton.setEnabled(false);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTitle() {
        return "dialog.title.export.image";
    }

    @Override
    protected File openAfterSaved() {
        return FileUtils.getFile(getBaseDir(), settings.getExportSetting().getExportImageSetting().getOutputFilePath());
    }

    @Override
    protected boolean openWithExternalEditor() {
        return true;
    }

}
