package org.insightech.er.editor.view.dialog.dbexport;

import java.io.File;
import java.net.URI;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.insightech.er.ResourceString;
import org.insightech.er.Resources;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.common.widgets.MultiLineCheckbox;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ExportManagerRunner;
import org.insightech.er.editor.model.dbexport.ExportWithProgressManager;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.settings.ExportSetting;
import org.insightech.er.editor.model.settings.Settings;

public abstract class AbstractExportDialog extends AbstractDialog {

    protected MultiLineCheckbox openAfterSavedButton;

    protected Settings settings;

    protected ERDiagram diagram;

    private List<Category> categoryList;

    public AbstractExportDialog() {
        this(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
    }

    public AbstractExportDialog(final Shell parentShell) {
        super(parentShell);
    }

    public void init(final ERDiagram diagram) {
        this.diagram = diagram;

        settings = this.diagram.getDiagramContents().getSettings().clone();
        categoryList = settings.getCategorySetting().getSelectedCategories();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initLayout(final GridLayout layout) {
        super.initLayout(layout);

        layout.numColumns = 3;
        layout.verticalSpacing = Resources.VERTICAL_SPACING;
    }

    protected void createOpenAfterSavedButton(final Composite parent, final boolean indent, final int span) {
        openAfterSavedButton = CompositeFactory.createMultiLineCheckbox(this, parent, "label.open.after.saved", indent, span);
    }

    public Composite createCheckboxArea(final Composite parent) {
        return createCheckboxArea(parent, true);
    }

    public Composite createCheckboxArea(final Composite parent, final boolean separater) {
        if (separater) {
            CompositeFactory.fillLine(parent, 5);
            CompositeFactory.separater(parent);
        }

        final Composite checkboxArea = new Composite(parent, SWT.NONE);

        final int span = ((GridLayout) parent.getLayout()).numColumns;

        final GridData checkboxGridData = new GridData(SWT.FILL, SWT.LEFT, true, false, span, 1);
        // checkboxGridData.horizontalIndent = Resources.INDENT;
        checkboxArea.setLayoutData(checkboxGridData);

        final GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = 0;
        layout.marginLeft = 0;
        layout.marginRight = 0;
        layout.marginBottom = 0;
        layout.verticalSpacing = 0;
        checkboxArea.setLayout(layout);

        return checkboxArea;
    }

    public Settings getSettings() {
        return settings;
    }

    protected File getBaseDir() {
        return new File(diagram.getEditor().getBasePath());
    }

    protected String getDefaultOutputFilePath(final String extention) {
        final String diagramFilePath = diagram.getEditor().getDiagramFilePath();

        return diagramFilePath.substring(0, diagramFilePath.lastIndexOf(".")) + extention;
    }

    protected String getDefaultOutputFileName(final String extention) {
        final File file = new File(getDefaultOutputFilePath(extention));

        return file.getName();
    }

    @Override
    protected void perfomeOK() throws Exception {
        try {
            final ProgressMonitorDialog monitor = new ProgressMonitorDialog(getShell());

            final ExportWithProgressManager manager = getExportWithProgressManager(settings.getExportSetting());

            manager.init(diagram, getBaseDir());

            final ExportManagerRunner runner = new ExportManagerRunner(manager);

            monitor.run(true, true, runner);

            if (runner.getException() != null) {
                throw runner.getException();
            }

            if (openAfterSavedButton != null && openAfterSavedButton.getSelection()) {
                final File openAfterSaved = openAfterSaved();

                final URI uri = openAfterSaved.toURI();

                final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

                if (openWithExternalEditor()) {
                    IDE.openEditor(page, uri, IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID, true);

                } else {
                    final IFileStore fileStore = EFS.getStore(uri);
                    IDE.openEditorOnFileStore(page, fileStore);
                }
            }

            // there is a case in another project
            diagram.getEditor().refreshProject();

        } catch (final InterruptedException e) {
            throw new InputException();
        }
    }

    protected abstract ExportWithProgressManager getExportWithProgressManager(ExportSetting exportSetting) throws Exception;

    protected File openAfterSaved() {
        return null;
    }

    protected boolean openWithExternalEditor() {
        return false;
    }

    protected void initCategoryCombo(final Combo categoryCombo) {
        categoryCombo.add(ResourceString.getResourceString("label.all"));

        for (final Category category : categoryList) {
            categoryCombo.add(category.getName());
        }

        categoryCombo.setVisibleItemCount(20);
    }

    protected void setCategoryData(final Label categoryLabel) {
        String categoryName = ResourceString.getResourceString("label.all");
        if (diagram.getCurrentCategory() != null) {
            categoryName = diagram.getCurrentCategory().getName();
        }
        categoryLabel.setText(categoryName);
    }

    protected void setCategoryComboData(final Combo categoryCombo, final Category selectedCategory) {
        categoryCombo.select(0);

        if (selectedCategory != null) {
            for (int i = 0; i < categoryList.size(); i++) {
                final Category category = categoryList.get(i);

                if (selectedCategory.equals(category)) {
                    categoryCombo.select(i + 1);
                    break;
                }
            }
        }
    }

    protected Category getSelectedCategory(final Combo categoryCombo) {
        Category category = null;

        final int categoryIndex = categoryCombo.getSelectionIndex();

        if (categoryIndex != 0) {
            category = categoryList.get(categoryIndex - 1);
        }

        return category;
    }

}
