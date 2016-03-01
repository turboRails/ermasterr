package org.insightech.er.editor.controller.command.diagram_contents.element.node.category;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.settings.CategorySetting;

public class DeleteCategoryCommand extends AbstractCommand {

    private final ERDiagram diagram;

    private final CategorySetting categorySettings;

    private final Category category;

    private List<Category> oldAllCategories;

    private List<Category> oldSelectedCategories;

    public DeleteCategoryCommand(final ERDiagram diagram, final Category category) {
        this.diagram = diagram;
        categorySettings = diagram.getDiagramContents().getSettings().getCategorySetting();
        this.category = category;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        oldAllCategories = new ArrayList<Category>(categorySettings.getAllCategories());
        oldSelectedCategories = new ArrayList<Category>(categorySettings.getSelectedCategories());

        diagram.removeCategory(category);
        diagram.refreshChildren();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        categorySettings.setAllCategories(oldAllCategories);
        categorySettings.setSelectedCategories(oldSelectedCategories);
        diagram.restoreCategories();
        diagram.refreshChildren();
    }
}
