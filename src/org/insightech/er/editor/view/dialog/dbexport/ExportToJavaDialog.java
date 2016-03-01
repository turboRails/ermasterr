package org.insightech.er.editor.view.dialog.dbexport;

import java.nio.charset.Charset;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.common.widgets.DirectoryText;
import org.insightech.er.common.widgets.MultiLineCheckbox;
import org.insightech.er.editor.model.dbexport.ExportWithProgressManager;
import org.insightech.er.editor.model.dbexport.java.ExportToJavaManager;
import org.insightech.er.editor.model.settings.ExportSetting;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.model.settings.export.ExportJavaSetting;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;
import org.insightech.er.util.io.FileUtils;

public class ExportToJavaDialog extends AbstractExportDialog {

    private DirectoryText outputDirText;

    private Text packageText;

    private Text classNameSuffixText;

    private MultiLineCheckbox withHibernateButton;

    private Combo fileEncodingCombo;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize(final Composite parent) {
        packageText = CompositeFactory.createText(this, parent, "label.package.name", 2, false, true);

        classNameSuffixText = CompositeFactory.createText(this, parent, "label.class.name.suffix", 2, false, true);

        outputDirText = CompositeFactory.createDirectoryText(this, parent, "label.output.dir", getBaseDir(), "");

        fileEncodingCombo = CompositeFactory.createFileEncodingCombo(diagram.getEditor().getDefaultCharset(), this, parent, "label.output.file.encoding", 2);

        final Composite checkboxArea = this.createCheckboxArea(parent);

        withHibernateButton = CompositeFactory.createMultiLineCheckbox(this, checkboxArea, "label.with.hibernate", false, 2);
    }

    @Override
    protected String getErrorMessage() {
        // if (this.outputDirText.isBlank()) {
        // return "error.output.dir.is.empty";
        // }

        if (!Charset.isSupported(fileEncodingCombo.getText())) {
            return "error.file.encoding.is.not.supported";
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setData() {
        final Settings settings = diagram.getDiagramContents().getSettings();
        final ExportJavaSetting exportSetting = settings.getExportSetting().getExportJavaSetting();

        final String outputPath = exportSetting.getJavaOutput();

        outputDirText.setText(FileUtils.getRelativeFilePath(getBaseDir(), outputPath));

        packageText.setText(Format.null2blank(exportSetting.getPackageName()));
        classNameSuffixText.setText(Format.null2blank(exportSetting.getClassNameSuffix()));

        if (!Check.isEmpty(exportSetting.getSrcFileEncoding())) {
            fileEncodingCombo.setText(exportSetting.getSrcFileEncoding());
        }

        withHibernateButton.setSelection(exportSetting.isWithHibernate());
    }

    @Override
    protected ExportWithProgressManager getExportWithProgressManager(final ExportSetting exportSetting) {
        final ExportJavaSetting exportJavaSetting = exportSetting.getExportJavaSetting();

        exportJavaSetting.setJavaOutput(outputDirText.getFilePath());
        exportJavaSetting.setPackageName(packageText.getText());
        exportJavaSetting.setClassNameSuffix(classNameSuffixText.getText());
        exportJavaSetting.setSrcFileEncoding(fileEncodingCombo.getText());
        exportJavaSetting.setWithHibernate(withHibernateButton.getSelection());

        return new ExportToJavaManager(exportJavaSetting);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTitle() {
        return "dialog.title.export.java";
    }

}
