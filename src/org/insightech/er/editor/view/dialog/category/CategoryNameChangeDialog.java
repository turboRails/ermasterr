package org.insightech.er.editor.view.dialog.category;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;

public class CategoryNameChangeDialog extends AbstractDialog {

    private Text categoryNameText = null;

    private final Category targetCategory;

    private String categoryName;

    public CategoryNameChangeDialog(final Shell parentShell, final Category category) {
        super(parentShell);
        targetCategory = category;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize(final Composite composite) {
        categoryNameText = CompositeFactory.createText(this, composite, "label.category.name", true, true);
    }

    @Override
    protected String getTitle() {
        return "dialog.title.change.category.name";
    }

    @Override
    protected void perfomeOK() throws InputException {}

    @Override
    protected void setData() {
        categoryNameText.setText(targetCategory.getName());
    }

    @Override
    protected String getErrorMessage() {
        final String text = categoryNameText.getText().trim();

        if ("".equals(text)) {
            return "error.category.name.empty";
        }

        categoryName = text;

        return null;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
