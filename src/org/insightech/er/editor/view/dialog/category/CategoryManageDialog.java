package org.insightech.er.editor.view.dialog.category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ResourceString;
import org.insightech.er.Resources;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.settings.CategorySetting;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;

public class CategoryManageDialog extends AbstractDialog {

    private static final int GROUP_HEIGHT = 350;

    private Table categoryTable = null;

    private Table nodeTable = null;

    private Button addCategoryButton;

    private Button updateCategoryButton;

    private Button deleteCategoryButton;

    private Text categoryNameText = null;

    private final ERDiagram diagram;

    private final CategorySetting categorySettings;

    private Map<Category, TableEditor> categoryCheckMap;

    private Map<NodeElement, TableEditor> nodeCheckMap;

    private Category targetCategory;

    private Button upButton;

    private Button downButton;

    public CategoryManageDialog(final Shell parentShell, final Settings settings, final ERDiagram diagram) {
        super(parentShell);

        this.diagram = diagram;
        categorySettings = settings.getCategorySetting();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize(final Composite composite) {
        createCategoryGroup(composite);
        createNodeGroup(composite);
    }

    private void createCategoryGroup(final Composite composite) {
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 4;
        gridLayout.verticalSpacing = Resources.VERTICAL_SPACING;

        final Group group = new Group(composite, SWT.NONE);
        group.setText(ResourceString.getResourceString("label.category.message"));
        group.setLayout(gridLayout);

        final GridData gridData = new GridData();
        gridData.heightHint = GROUP_HEIGHT;
        group.setLayoutData(gridData);

        CompositeFactory.fillLine(group, 5);

        final GridData tableGridData = new GridData();
        tableGridData.grabExcessVerticalSpace = true;
        tableGridData.verticalAlignment = GridData.FILL;
        tableGridData.horizontalSpan = 3;
        tableGridData.verticalSpan = 2;

        categoryTable = new Table(group, SWT.BORDER | SWT.FULL_SELECTION);
        categoryTable.setHeaderVisible(true);
        categoryTable.setLayoutData(tableGridData);
        categoryTable.setLinesVisible(true);

        upButton = CompositeFactory.createUpButton(group);
        downButton = CompositeFactory.createDownButton(group);

        categoryNameText = CompositeFactory.createText(this, group, null, 3, true, false);
        CompositeFactory.filler(group, 1);

        addCategoryButton = CompositeFactory.createSmallButton(group, "label.button.add");
        updateCategoryButton = CompositeFactory.createSmallButton(group, "label.button.update");
        deleteCategoryButton = CompositeFactory.createSmallButton(group, "label.button.delete");

        final TableColumn tableColumn = new TableColumn(categoryTable, SWT.NONE);
        tableColumn.setWidth(30);
        tableColumn.setResizable(false);
        final TableColumn tableColumn1 = new TableColumn(categoryTable, SWT.NONE);
        tableColumn1.setWidth(230);
        tableColumn1.setResizable(false);
        tableColumn1.setText(ResourceString.getResourceString("label.category.name"));
    }

    private void createNodeGroup(final Composite composite) {
        final Group group = new Group(composite, SWT.NONE);
        group.setLayout(new GridLayout());
        group.setText(ResourceString.getResourceString("label.category.object.message"));

        final GridData gridData = new GridData();
        gridData.heightHint = GROUP_HEIGHT;
        group.setLayoutData(gridData);

        CompositeFactory.fillLine(group, 5);

        final GridData tableGridData = new GridData();
        tableGridData.grabExcessVerticalSpace = true;
        tableGridData.verticalAlignment = GridData.FILL;

        nodeTable = new Table(group, SWT.BORDER | SWT.HIDE_SELECTION);
        nodeTable.setHeaderVisible(true);
        nodeTable.setLayoutData(tableGridData);
        nodeTable.setLinesVisible(true);

        final TableColumn tableColumn2 = new TableColumn(nodeTable, SWT.NONE);
        tableColumn2.setWidth(30);
        tableColumn2.setResizable(false);
        tableColumn2.setText("");
        final TableColumn tableColumn3 = new TableColumn(nodeTable, SWT.NONE);
        tableColumn3.setWidth(80);
        tableColumn3.setResizable(false);
        tableColumn3.setText(ResourceString.getResourceString("label.object.type"));
        final TableColumn tableColumn4 = new TableColumn(nodeTable, SWT.NONE);
        tableColumn4.setWidth(200);
        tableColumn4.setResizable(false);
        tableColumn4.setText(ResourceString.getResourceString("label.object.name"));
    }

    private void initCategoryTable() {
        categoryTable.removeAll();

        if (categoryCheckMap != null) {
            for (final TableEditor editor : categoryCheckMap.values()) {
                editor.getEditor().dispose();
                editor.dispose();
            }

            categoryCheckMap.clear();
        } else {
            categoryCheckMap = new HashMap<Category, TableEditor>();
        }

        for (final Category category : categorySettings.getAllCategories()) {
            final TableItem tableItem = new TableItem(categoryTable, SWT.NONE);

            final Button selectCheckButton = new Button(categoryTable, SWT.CHECK);
            selectCheckButton.pack();

            final TableEditor editor = new TableEditor(categoryTable);

            editor.minimumWidth = selectCheckButton.getSize().x;
            editor.horizontalAlignment = SWT.CENTER;
            editor.setEditor(selectCheckButton, tableItem, 0);

            tableItem.setText(1, category.getName());

            if (categorySettings.isSelected(category)) {
                selectCheckButton.setSelection(true);
            }

            categoryCheckMap.put(category, editor);

            if (targetCategory == category) {
                categoryTable.setSelection(tableItem);
            }
        }

        if (targetCategory != null) {
            initNodeList(targetCategory);
            updateCategoryButton.setEnabled(true);
            deleteCategoryButton.setEnabled(true);

        } else {
            deleteNodeList();
            updateCategoryButton.setEnabled(false);
            deleteCategoryButton.setEnabled(false);

        }
    }

    private void initNodeTable() {
        nodeTable.removeAll();

        nodeCheckMap = new HashMap<NodeElement, TableEditor>();

        for (final NodeElement nodeElement : diagram.getDiagramContents().getContents()) {
            final TableItem tableItem = new TableItem(nodeTable, SWT.NONE);

            final Button selectCheckButton = new Button(nodeTable, SWT.CHECK);
            selectCheckButton.pack();

            final TableEditor editor = new TableEditor(nodeTable);

            editor.minimumWidth = selectCheckButton.getSize().x;
            editor.horizontalAlignment = SWT.CENTER;
            editor.setEditor(selectCheckButton, tableItem, 0);

            tableItem.setText(1, ResourceString.getResourceString("label.object.type." + nodeElement.getObjectType()));
            tableItem.setText(2, Format.null2blank(nodeElement.getName()));

            nodeCheckMap.put(nodeElement, editor);
        }
    }

    private void initNodeList(final Category category) {
        categoryNameText.setText(category.getName());

        for (final NodeElement nodeElement : nodeCheckMap.keySet()) {
            final Button selectCheckButton = (Button) nodeCheckMap.get(nodeElement).getEditor();

            if (category.contains(nodeElement)) {
                selectCheckButton.setSelection(true);

            } else {
                selectCheckButton.setSelection(false);
            }
        }
    }

    private void deleteNodeList() {
        categoryNameText.setText("");

        nodeTable.removeAll();

        if (nodeCheckMap != null) {
            for (final TableEditor editor : nodeCheckMap.values()) {
                editor.getEditor().dispose();
                editor.dispose();
            }

            nodeCheckMap.clear();
        }
    }

    @Override
    protected void addListener() {
        super.addListener();

        categoryTable.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final int index = categoryTable.getSelectionIndex();
                if (index == -1) {
                    return;
                }

                validatePage();

                if (targetCategory == null) {
                    initNodeTable();

                    updateCategoryButton.setEnabled(true);
                    deleteCategoryButton.setEnabled(true);
                }

                targetCategory = categorySettings.getAllCategories().get(index);
                initNodeList(targetCategory);
            }
        });

        addCategoryButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final String name = categoryNameText.getText().trim();

                if (name.equals("")) {
                    return;
                }

                validatePage();

                if (targetCategory == null) {
                    initNodeTable();
                }

                final Category addedCategory = new Category();
                final int[] color = diagram.getDefaultColor();
                addedCategory.setColor(color[0], color[1], color[2]);
                addedCategory.setName(name);
                categorySettings.addCategoryAsSelected(addedCategory);
                targetCategory = addedCategory;

                initCategoryTable();
            }

        });

        updateCategoryButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final int index = categoryTable.getSelectionIndex();

                if (index == -1) {
                    return;
                }

                final String name = categoryNameText.getText().trim();

                if (name.equals("")) {
                    return;
                }

                validatePage();

                targetCategory.setName(name);

                initCategoryTable();
            }

        });

        deleteCategoryButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent event) {
                try {
                    int index = categoryTable.getSelectionIndex();

                    if (index == -1) {
                        return;
                    }

                    validatePage();

                    categorySettings.removeCategory(index);

                    if (categoryTable.getItemCount() > index + 1) {

                    } else if (categoryTable.getItemCount() != 0) {
                        index = categoryTable.getItemCount() - 2;

                    } else {
                        index = -1;
                    }

                    if (index != -1) {
                        targetCategory = categorySettings.getAllCategories().get(index);
                    } else {
                        targetCategory = null;
                    }

                    initCategoryTable();

                } catch (final Exception e) {
                    ERDiagramActivator.log(e);
                }
            }
        });

        upButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final int index = categoryTable.getSelectionIndex();

                if (index == -1 || index == 0) {
                    return;
                }

                validatePage();
                changeColumn(index - 1, index);
                initCategoryTable();
            }

        });

        downButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final int index = categoryTable.getSelectionIndex();

                if (index == -1 || index == categoryTable.getItemCount() - 1) {
                    return;
                }

                validatePage();
                changeColumn(index, index + 1);
                initCategoryTable();
            }

        });
    }

    public void changeColumn(final int index1, final int index2) {
        final List<Category> allCategories = categorySettings.getAllCategories();

        final Category category1 = allCategories.remove(index1);
        Category category2 = null;

        if (index1 < index2) {
            category2 = allCategories.remove(index2 - 1);
            allCategories.add(index1, category2);
            allCategories.add(index2, category1);

        } else if (index1 > index2) {
            category2 = allCategories.remove(index2);
            allCategories.add(index1 - 1, category2);
            allCategories.add(index2, category1);
        }
    }

    @Override
    protected String getTitle() {
        return "label.category";
    }

    @Override
    protected void perfomeOK() throws InputException {
        validatePage();
    }

    @Override
    protected void setData() {
        initCategoryTable();
    }

    @Override
    protected String getErrorMessage() {
        if (!Check.isEmpty(categoryNameText.getText())) {
            addCategoryButton.setEnabled(true);
        } else {
            addCategoryButton.setEnabled(false);
        }

        return null;
    }

    public void validatePage() {
        if (targetCategory != null) {
            final List<NodeElement> selectedNodeElementList = new ArrayList<NodeElement>();

            for (final NodeElement table : nodeCheckMap.keySet()) {
                final Button selectCheckButton = (Button) nodeCheckMap.get(table).getEditor();

                if (selectCheckButton.getSelection()) {
                    selectedNodeElementList.add(table);
                }
            }

            targetCategory.setContents(selectedNodeElementList);
        }

        final List<Category> selectedCategories = new ArrayList<Category>();

        for (final Category category : categorySettings.getAllCategories()) {
            final Button button = (Button) categoryCheckMap.get(category).getEditor();

            if (button.getSelection()) {
                selectedCategories.add(category);
            }
        }

        categorySettings.setSelectedCategories(selectedCategories);
    }
}
