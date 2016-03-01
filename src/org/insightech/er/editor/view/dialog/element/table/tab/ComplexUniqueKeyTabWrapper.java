package org.insightech.er.editor.view.dialog.element.table.tab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.common.dialog.AbstractTabbedDialog;
import org.insightech.er.common.dialog.ValidatableTabWrapper;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;
import org.insightech.er.editor.model.diagram_contents.element.node.table.unique_key.CopyComplexUniqueKey;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;

public class ComplexUniqueKeyTabWrapper extends ValidatableTabWrapper {

    private final ERTable copyData;

    private Text nameText;

    private Combo complexUniqueKeyCombo;

    private Table columnTable;

    private Button addButton;

    private Button updateButton;

    private Button deleteButton;

    private final List<TableEditor> tableEditorList;

    private final Map<TableEditor, NormalColumn> editorColumnMap;

    public ComplexUniqueKeyTabWrapper(final AbstractTabbedDialog dialog, final TabFolder parent, final ERTable copyData) {
        super(dialog, parent, "label.complex.unique.key");

        this.copyData = copyData;
        tableEditorList = new ArrayList<TableEditor>();
        editorColumnMap = new HashMap<TableEditor, NormalColumn>();
    }

    @Override
    protected void initLayout(final GridLayout layout) {
        super.initLayout(layout);

        layout.numColumns = 2;
    }

    @Override
    public void initComposite() {
        complexUniqueKeyCombo = CompositeFactory.createReadOnlyCombo(null, this, "label.complex.unique.key");

        nameText = CompositeFactory.createText(null, this, "label.unique.key.name", false, true);

        CompositeFactory.fillLine(this);

        columnTable = CompositeFactory.createTable(this, 200, 2);

        final TableColumn tableColumn = CompositeFactory.createTableColumn(columnTable, "label.unique.key", -1, SWT.CENTER);
        tableColumn.setResizable(false);

        CompositeFactory.createTableColumn(columnTable, "label.column.name", -1, SWT.NONE);

        final Composite buttonComposite = CompositeFactory.createChildComposite(this, 2, 3);

        addButton = CompositeFactory.createSmallButton(buttonComposite, "label.button.add");
        updateButton = CompositeFactory.createSmallButton(buttonComposite, "label.button.update");
        deleteButton = CompositeFactory.createSmallButton(buttonComposite, "label.button.delete");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validatePage() throws InputException {}

    @Override
    public void setInitFocus() {
        complexUniqueKeyCombo.setFocus();
    }

    @Override
    public void restruct() {
        columnTable.removeAll();

        disposeTableEditor();

        for (final NormalColumn normalColumn : copyData.getNormalColumns()) {
            final TableItem tableItem = new TableItem(columnTable, SWT.NONE);

            final TableEditor tableEditor = CompositeFactory.createCheckBoxTableEditor(tableItem, false, 0);
            tableEditorList.add(tableEditor);
            editorColumnMap.put(tableEditor, normalColumn);

            tableItem.setText(1, Format.null2blank(normalColumn.getName()));
        }

        setComboData();
        setButtonStatus(false);
        nameText.setText("");

        columnTable.getColumns()[1].pack();
    }

    @Override
    public void dispose() {
        disposeTableEditor();
        super.dispose();
    }

    private void disposeTableEditor() {
        for (final TableEditor tableEditor : tableEditorList) {
            tableEditor.getEditor().dispose();
            tableEditor.dispose();
        }

        tableEditorList.clear();
        editorColumnMap.clear();
    }

    @Override
    protected void addListener() {
        super.addListener();

        complexUniqueKeyCombo.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                checkSelectedKey();
            }
        });

        addButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                final String name = nameText.getText().trim();

                if (!"".equals(name)) {
                    if (!Check.isAlphabet(name)) {
                        ERDiagramActivator.showErrorDialog("error.unique.key.name.not.alphabet");
                        return;
                    }
                }

                final List<NormalColumn> columnList = new ArrayList<NormalColumn>();

                for (final TableEditor tableEditor : tableEditorList) {
                    final Button checkBox = (Button) tableEditor.getEditor();
                    if (checkBox.getSelection()) {
                        columnList.add(editorColumnMap.get(tableEditor));
                    }
                }

                if (columnList.isEmpty()) {
                    ERDiagramActivator.showErrorDialog("error.not.checked.complex.unique.key.columns");
                    return;
                }

                if (contains(columnList) != null) {
                    ERDiagramActivator.showErrorDialog("error.already.exist.complex.unique.key");
                    return;
                }

                final ComplexUniqueKey complexUniqueKey = new CopyComplexUniqueKey(new ComplexUniqueKey(name), null);
                complexUniqueKey.setColumnList(columnList);
                copyData.getComplexUniqueKeyList().add(complexUniqueKey);
                addComboData(complexUniqueKey);
                complexUniqueKeyCombo.select(complexUniqueKeyCombo.getItemCount() - 1);
                setButtonStatus(true);
            }

        });

        updateButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                final int index = complexUniqueKeyCombo.getSelectionIndex();
                if (index == -1) {
                    return;
                }

                final String name = nameText.getText().trim();

                if (!Check.isAlphabet(name)) {
                    ERDiagramActivator.showErrorDialog("error.unique.key.name.not.alphabet");
                    return;
                }

                final ComplexUniqueKey complexUniqueKey = copyData.getComplexUniqueKeyList().get(index);

                final List<NormalColumn> columnList = new ArrayList<NormalColumn>();

                for (final TableEditor tableEditor : tableEditorList) {
                    final Button checkBox = (Button) tableEditor.getEditor();
                    if (checkBox.getSelection()) {
                        columnList.add(editorColumnMap.get(tableEditor));
                    }
                }

                if (columnList.isEmpty()) {
                    ERDiagramActivator.showErrorDialog("error.not.checked.complex.unique.key.columns");
                    return;
                }

                final ComplexUniqueKey sameKey = contains(columnList);
                if (sameKey != null && sameKey != complexUniqueKey) {
                    ERDiagramActivator.showErrorDialog("error.already.exist.complex.unique.key");
                    return;
                }

                complexUniqueKey.setUniqueKeyName(name);
                complexUniqueKey.setColumnList(columnList);
                complexUniqueKeyCombo.remove(index);
                complexUniqueKeyCombo.add(complexUniqueKey.getLabel(), index);
                complexUniqueKeyCombo.select(index);
            }
        });

        deleteButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                final int index = complexUniqueKeyCombo.getSelectionIndex();
                if (index == -1) {
                    return;
                }

                complexUniqueKeyCombo.remove(index);
                copyData.getComplexUniqueKeyList().remove(index);

                if (index < copyData.getComplexUniqueKeyList().size()) {
                    complexUniqueKeyCombo.select(index);
                } else {
                    complexUniqueKeyCombo.select(index - 1);
                }

                checkSelectedKey();
            }
        });
    }

    private void checkSelectedKey() {
        final int index = complexUniqueKeyCombo.getSelectionIndex();

        ComplexUniqueKey complexUniqueKey = null;
        String name = null;

        if (index != -1) {
            complexUniqueKey = copyData.getComplexUniqueKeyList().get(index);
            name = complexUniqueKey.getUniqueKeyName();

            setButtonStatus(true);

        } else {
            setButtonStatus(false);
        }

        nameText.setText(Format.null2blank(name));

        for (final TableEditor tableEditor : tableEditorList) {
            final Button checkbox = (Button) tableEditor.getEditor();

            final NormalColumn column = editorColumnMap.get(tableEditor);
            if (complexUniqueKey != null && complexUniqueKey.getColumnList().contains(column)) {
                checkbox.setSelection(true);
            } else {
                checkbox.setSelection(false);
            }
        }
    }

    public ComplexUniqueKey contains(final List<NormalColumn> columnList) {
        for (final ComplexUniqueKey complexUniqueKey : copyData.getComplexUniqueKeyList()) {
            if (columnList.size() == complexUniqueKey.getColumnList().size()) {
                boolean exist = true;
                for (final NormalColumn column : columnList) {
                    if (!complexUniqueKey.getColumnList().contains(column)) {
                        exist = false;
                        break;
                    }
                }

                if (exist) {
                    return complexUniqueKey;
                }
            }
        }

        return null;
    }

    private void setComboData() {
        complexUniqueKeyCombo.removeAll();

        for (final Iterator<ComplexUniqueKey> iter = copyData.getComplexUniqueKeyList().iterator(); iter.hasNext();) {
            final ComplexUniqueKey complexUniqueKey = iter.next();

            if (complexUniqueKey.isRemoved(copyData.getNormalColumns())) {
                iter.remove();
            } else {
                addComboData(complexUniqueKey);
            }
        }
    }

    private void addComboData(final ComplexUniqueKey complexUniqueKey) {
        complexUniqueKeyCombo.add(complexUniqueKey.getLabel());
    }

    private void setButtonStatus(boolean enabled) {
        if (enabled) {
            if (copyData.getComplexUniqueKeyList().get(complexUniqueKeyCombo.getSelectionIndex()).isReferenced(copyData)) {
                enabled = false;
            }
        }

        updateButton.setEnabled(enabled);
        deleteButton.setEnabled(enabled);
    }

    @Override
    public void perfomeOK() {}
}
