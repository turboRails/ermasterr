package org.insightech.er.editor.view.dialog.dbexport;

import java.io.File;
import java.util.List;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.insightech.er.ResourceString;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.common.widgets.FileText;
import org.insightech.er.common.widgets.MultiLineCheckbox;
import org.insightech.er.editor.model.dbexport.ExportWithProgressManager;
import org.insightech.er.editor.model.dbexport.excel.ExportToExcelManager;
import org.insightech.er.editor.model.settings.ExportSetting;
import org.insightech.er.editor.model.settings.export.ExportExcelSetting;
import org.insightech.er.preference.PreferenceInitializer;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;
import org.insightech.er.util.io.FileUtils;

public class ExportToExcelDialog extends AbstractExportDialog {

    // private static final String DEFAULT_IMAGE_EXTENTION = ".png";

    private Combo templateCombo;

    private FileText templateFileText;

    private FileText outputExcelFileText;

    // private FileText outputImageFileText;

    // private Combo categoryCombo;
    private Label categoryLabel;

    private MultiLineCheckbox useLogicalNameAsSheetNameButton;

    private MultiLineCheckbox outputImageButton;

    private Button selectTemplateFromRegistryRadio;

    private Button selectTemplateFromFilesRadio;

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
        outputExcelFileText = CompositeFactory.createFileText(true, this, parent, "label.output.excel.file", getBaseDir(), getDefaultOutputFileName(".xls"), "*.xls");

        CompositeFactory.createLabel(parent, "label.category");
        categoryLabel = CompositeFactory.createLabelAsValue(parent, "", 2);

        createTemplateGroup(parent);

        // this.categoryCombo = CompositeFactory.createReadOnlyCombo(this,
        // parent,
        // "label.category", 2);
        // this.initCategoryCombo(this.categoryCombo);

        final Composite checkboxArea = this.createCheckboxArea(parent, false);

        outputImageButton = CompositeFactory.createMultiLineCheckbox(this, checkboxArea, "label.output.image.to.excel", false, 1);

        // CompositeFactory.createLabel(parent, "label.output.image.file");
        // this.outputImageFileText = new FileText(parent, this.getProjectDir(),
        // this.getDefaultOutputFileName(DEFAULT_IMAGE_EXTENTION),
        // new String[] { "*.png", "*.jpeg" });

        useLogicalNameAsSheetNameButton = CompositeFactory.createMultiLineCheckbox(this, checkboxArea, "label.use.logical.name.as.sheet.name", false, 1);

        createOpenAfterSavedButton(checkboxArea, false, 1);
    }

    private void createTemplateGroup(final Composite parent) {
        final Group group = CompositeFactory.createGroup(parent, "label.template", 3, 2);

        selectTemplateFromRegistryRadio = CompositeFactory.createRadio(this, group, "label.select.from.registry", 2);
        templateCombo = CompositeFactory.createReadOnlyCombo(this, group, null, 2);
        initTemplateCombo();

        CompositeFactory.fillLine(group, 5);

        selectTemplateFromFilesRadio = CompositeFactory.createRadio(this, group, "label.select.from.file", 2);
        templateFileText = CompositeFactory.createFileText(false, this, group, null, getBaseDir(), null, "*.xls", false);
    }

    private void initTemplateCombo() {
        templateCombo.setVisibleItemCount(20);

        templateCombo.add(ResourceString.getResourceString("label.template.default.en"));
        templateCombo.add(ResourceString.getResourceString("label.template.default.ja"));

        final List<String> fileNames = PreferenceInitializer.getAllExcelTemplateFiles();

        for (final String fileName : fileNames) {
            final File file = new File(PreferenceInitializer.getTemplatePath(fileName));
            if (file.exists()) {
                templateCombo.add(fileName);
            }
        }
    }

    @Override
    protected void addListener() {
        super.addListener();

        selectTemplateFromRegistryRadio.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                templateCombo.setEnabled(true);
                templateFileText.setEnabled(false);
            }
        });

        selectTemplateFromFilesRadio.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                templateCombo.setEnabled(false);
                templateFileText.setEnabled(true);
            }
        });
    }

    @Override
    protected String getErrorMessage() {
        // this.outputImageFileText.setEnabled(this.outputImageButton
        // .getSelection());

        if (selectTemplateFromRegistryRadio.getSelection()) {
            if (isBlank(templateCombo)) {
                return "error.template.is.empty";
            }

        } else {
            if (templateFileText.isBlank()) {
                return "error.template.is.empty";
            }
        }

        if (outputExcelFileText.isBlank()) {
            return "error.output.excel.file.is.empty";
        }

        // if (this.outputImageButton.getSelection()
        // && this.outputImageFileText.isBlank()) {
        // return "error.output.image.file.is.empty";
        // }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setData() {
        final ExportExcelSetting exportExcelSetting = settings.getExportSetting().getExportExcelSetting();

        String outputExcel = Format.null2blank(exportExcelSetting.getExcelOutput());
        // String outputImage = Format.null2blank(exportExcelSetting
        // .getImageOutput());

        if ("".equals(outputExcel)) {
            outputExcel = getDefaultOutputFilePath(".xls");
        }

        // if ("".equals(outputImage)) {
        // outputImage = this
        // .getDefaultOutputFilePath(DEFAULT_IMAGE_EXTENTION);
        // }

        outputExcelFileText.setText(FileUtils.getRelativeFilePath(getBaseDir(), outputExcel));
        // this.outputImageFileText.setText(outputImage);

        // this.setCategoryComboData(this.categoryCombo,
        // exportExcelSetting.getCategory());
        setCategoryData(categoryLabel);

        useLogicalNameAsSheetNameButton.setSelection(exportExcelSetting.isUseLogicalNameAsSheet());
        outputImageButton.setSelection(exportExcelSetting.isPutERDiagramOnExcel());
        openAfterSavedButton.setSelection(exportExcelSetting.isOpenAfterSaved());

        setTemplateData(exportExcelSetting);

        final String excelTemplatePath = exportExcelSetting.getExcelTemplatePath();

        if (!Check.isEmpty(excelTemplatePath)) {
            templateFileText.setText(excelTemplatePath);
            selectTemplateFromFilesRadio.setSelection(true);
            templateCombo.setEnabled(false);

        } else {
            selectTemplateFromRegistryRadio.setSelection(true);
            templateFileText.setEnabled(false);

        }
    }

    private void setTemplateData(final ExportExcelSetting exportExcelSetting) {
        final String lang = exportExcelSetting.getUsedDefaultTemplateLang();

        if ("en".equals(lang)) {
            templateCombo.select(0);

        } else if ("ja".equals(lang)) {
            templateCombo.select(1);

        } else {
            templateCombo.select(0);

            final String template = exportExcelSetting.getExcelTemplate();

            for (int i = 2; i < templateCombo.getItemCount(); i++) {
                final String item = templateCombo.getItem(i);
                if (item.equals(template)) {
                    templateCombo.select(i);
                    break;
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTitle() {
        return "dialog.title.export.excel";
    }

    @Override
    protected ExportWithProgressManager getExportWithProgressManager(final ExportSetting exportSetting) throws Exception {

        final ExportExcelSetting exportExcelSetting = exportSetting.getExportExcelSetting();

        final String outputExcelFilePath = outputExcelFileText.getFilePath();

        // String outputImageFilePath = this.outputImageFileText.getFilePath();

        // this.outputExcelFile = new File(outputExcelFilePath);
        //
        // if (!outputExcelFile.isAbsolute()) {
        // outputExcelFile = new File(this.getProjectDir(),
        // outputExcelFilePath);
        // }
        //
        // File outputExcelDir = outputExcelFile.getParentFile();
        //
        // if (!outputExcelDir.exists()) {
        // if (!Activator.showConfirmDialog(ResourceString.getResourceString(
        // "dialog.message.create.parent.dir",
        // new String[] { outputExcelDir.getAbsolutePath() }))) {
        // throw new InputException();
        //
        // } else {
        // outputExcelDir.mkdirs();
        // }
        // }

        exportExcelSetting.setExcelOutput(outputExcelFilePath);
        // exportExcelSetting.setImageOutput(outputImageFilePath);

        exportExcelSetting.setUseLogicalNameAsSheet(useLogicalNameAsSheetNameButton.getSelection());
        exportExcelSetting.setPutERDiagramOnExcel(outputImageButton.getSelection());
        // exportExcelSetting.setCategory(this
        // .getSelectedCategory(this.categoryCombo));
        exportExcelSetting.setCategory(diagram.getCurrentCategory());
        exportExcelSetting.setOpenAfterSaved(openAfterSavedButton.getSelection());

        final int templateIndex = templateCombo.getSelectionIndex();

        String template = null;

        if (templateIndex == 0) {
            exportExcelSetting.setUsedDefaultTemplateLang("en");
        } else if (templateIndex == 1) {
            exportExcelSetting.setUsedDefaultTemplateLang("ja");
        } else {
            exportExcelSetting.setUsedDefaultTemplateLang(null);
            template = templateCombo.getText();
        }

        if (selectTemplateFromRegistryRadio.getSelection()) {
            exportExcelSetting.setExcelTemplate(template);

        } else {
            exportExcelSetting.setExcelTemplatePath(templateFileText.getFilePath());
        }

        return new ExportToExcelManager(exportExcelSetting);
    }

    @Override
    protected File openAfterSaved() {
        return FileUtils.getFile(getBaseDir(), settings.getExportSetting().getExportExcelSetting().getExcelOutput());
    }

    @Override
    protected boolean openWithExternalEditor() {
        return true;
    }

}
