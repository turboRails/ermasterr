package org.insightech.er.editor.view.dialog.element.relation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ResourceString;
import org.insightech.er.Resources;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;
import org.insightech.er.editor.view.dialog.element.relation.RelationDialog.ColumnComboInfo;
import org.insightech.er.util.Format;

public class RelationByExistingColumnsDialog extends AbstractDialog {

    private Combo columnCombo;

    private Table comparisonTable;

    private final ERTable source;

    private ColumnComboInfo columnComboInfo;

    private final List<NormalColumn> candidateForeignKeyColumns;

    private List<NormalColumn> referencedColumnList;

    private final List<NormalColumn> foreignKeyColumnList;

    private final Map<NormalColumn, List<NormalColumn>> referencedMap;

    private boolean referenceForPK;

    private ComplexUniqueKey referencedComplexUniqueKey;

    private NormalColumn referencedColumn;

    private final List<TableEditor> tableEditorList;

    private final Map<TableEditor, List<NormalColumn>> editorReferencedMap;

    private final Map<Relation, Set<NormalColumn>> foreignKeySetMap;

    public RelationByExistingColumnsDialog(final Shell parentShell, final ERTable source, final List<NormalColumn> candidateForeignKeyColumns, final Map<NormalColumn, List<NormalColumn>> referencedMap, final Map<Relation, Set<NormalColumn>> foreignKeySetMap) {
        super(parentShell);

        this.source = source;
        referencedColumnList = new ArrayList<NormalColumn>();
        foreignKeyColumnList = new ArrayList<NormalColumn>();

        this.candidateForeignKeyColumns = candidateForeignKeyColumns;
        this.referencedMap = referencedMap;
        this.foreignKeySetMap = foreignKeySetMap;

        tableEditorList = new ArrayList<TableEditor>();
        editorReferencedMap = new HashMap<TableEditor, List<NormalColumn>>();
    }

    @Override
    protected void initLayout(final GridLayout layout) {
        super.initLayout(layout);

        layout.verticalSpacing = Resources.VERTICAL_SPACING;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize(final Composite composite) {
        final GridData gridData = new GridData();
        gridData.horizontalSpan = 2;

        final Label label = new Label(composite, SWT.NONE);
        label.setLayoutData(gridData);
        label.setText(ResourceString.getResourceString("dialog.message.create.relation.by.existing.columns"));

        CompositeFactory.fillLine(composite, 10);

        createColumnCombo(composite);

        CompositeFactory.fillLine(composite, 10);

        createComparisonTable(composite);
    }

    /**
     * This method initializes combo
     */
    private void createColumnCombo(final Composite composite) {
        columnCombo = CompositeFactory.createReadOnlyCombo(this, composite, "label.reference.column");
        columnCombo.setVisibleItemCount(20);
    }

    private void createComparisonTable(final Composite composite) {
        final GridData tableGridData = new GridData();
        tableGridData.horizontalSpan = 2;
        tableGridData.heightHint = 100;
        tableGridData.horizontalAlignment = GridData.FILL;
        tableGridData.grabExcessHorizontalSpace = true;

        comparisonTable = new Table(composite, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION);
        comparisonTable.setLayoutData(tableGridData);
        comparisonTable.setHeaderVisible(true);
        comparisonTable.setLinesVisible(true);

        composite.pack();

        final int width = comparisonTable.getBounds().width;

        final TableColumn referencedColumn = new TableColumn(comparisonTable, SWT.NONE);
        referencedColumn.setWidth(width / 2);
        referencedColumn.setText(ResourceString.getResourceString("label.reference.column"));

        final TableColumn foreignKeyColumn = new TableColumn(comparisonTable, SWT.NONE);
        foreignKeyColumn.setWidth(width / 2);
        foreignKeyColumn.setText(ResourceString.getResourceString("label.foreign.key"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setData() {
        columnComboInfo = RelationDialog.setReferencedColumnComboData(columnCombo, source);

        columnCombo.select(0);

        createComparisonTableRows();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void perfomeOK() {
        final int index = columnCombo.getSelectionIndex();

        if (index < columnComboInfo.complexUniqueKeyStartIndex) {
            referenceForPK = true;

        } else if (index < columnComboInfo.columnStartIndex) {
            final ComplexUniqueKey complexUniqueKey = source.getComplexUniqueKeyList().get(index - columnComboInfo.complexUniqueKeyStartIndex);

            referencedComplexUniqueKey = complexUniqueKey;

        } else {
            referencedColumn = columnComboInfo.candidateColumns.get(index - columnComboInfo.columnStartIndex);
        }

        for (final TableEditor tableEditor : tableEditorList) {
            final NormalColumn foreignKeyColumn = getSelectedColumn(tableEditor);
            foreignKeyColumnList.add(foreignKeyColumn);
        }
    }

    private NormalColumn getSelectedColumn(final TableEditor tableEditor) {
        final Combo foreignKeyCombo = (Combo) tableEditor.getEditor();
        final int foreignKeyComboIndex = foreignKeyCombo.getSelectionIndex();
        int startIndex = 1;

        NormalColumn foreignKeyColumn = null;

        final List<NormalColumn> foreignKeyList = editorReferencedMap.get(tableEditor);
        if (foreignKeyList != null) {
            if (foreignKeyComboIndex <= foreignKeyList.size()) {
                foreignKeyColumn = foreignKeyList.get(foreignKeyComboIndex - startIndex);
            } else {
                startIndex += foreignKeyList.size();
            }
        }

        if (foreignKeyColumn == null) {
            foreignKeyColumn = candidateForeignKeyColumns.get(foreignKeyComboIndex - startIndex);
        }

        return foreignKeyColumn;
    }

    @Override
    protected String getErrorMessage() {
        final Set<NormalColumn> selectedColumns = new HashSet<NormalColumn>();

        for (final TableEditor tableEditor : tableEditorList) {
            final Combo foreignKeyCombo = (Combo) tableEditor.getEditor();
            final int index = foreignKeyCombo.getSelectionIndex();

            if (index == 0) {
                return "error.foreign.key.not.selected";
            }

            final NormalColumn selectedColumn = getSelectedColumn(tableEditor);
            if (selectedColumns.contains(selectedColumn)) {
                return "error.foreign.key.must.be.different";
            }

            selectedColumns.add(selectedColumn);
        }

        if (existForeignKeySet(selectedColumns)) {
            return "error.foreign.key.already.exist";
        }

        return null;
    }

    private boolean existForeignKeySet(final Set<NormalColumn> columnSet) {
        boolean exist = false;

        for (final Set<NormalColumn> foreignKeySet : foreignKeySetMap.values()) {
            if (foreignKeySet.size() == columnSet.size()) {
                exist = true;

                for (final NormalColumn normalColumn : columnSet) {
                    if (!foreignKeySet.contains(normalColumn)) {
                        exist = false;
                        continue;
                    }
                }

                break;
            }
        }

        return exist;
    }

    @Override
    protected void addListener() {
        super.addListener();

        columnCombo.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                comparisonTable.removeAll();
                disposeTableEditor();
                createComparisonTableRows();
                validate();
            }

        });

        comparisonTable.addListener(SWT.MeasureItem, new Listener() {

            @Override
            public void handleEvent(final Event event) {
                event.height = columnCombo.getSize().y;;
            }

        });
    }

    private void createComparisonTableRows() {
        try {
            final int index = columnCombo.getSelectionIndex();

            if (index < columnComboInfo.complexUniqueKeyStartIndex) {
                referencedColumnList = source.getPrimaryKeys();

            } else if (index < columnComboInfo.columnStartIndex) {
                final ComplexUniqueKey complexUniqueKey = source.getComplexUniqueKeyList().get(index - columnComboInfo.complexUniqueKeyStartIndex);

                referencedColumnList = complexUniqueKey.getColumnList();

            } else {
                final NormalColumn referencedColumn = columnComboInfo.candidateColumns.get(index - columnComboInfo.columnStartIndex);

                referencedColumnList = new ArrayList<NormalColumn>();
                referencedColumnList.add(referencedColumn);
            }

            for (final NormalColumn referencedColumn : referencedColumnList) {
                column2TableItem(referencedColumn);
            }

        } catch (final Exception e) {
            ERDiagramActivator.showExceptionDialog(e);
        }
    }

    private void column2TableItem(final NormalColumn referencedColumn) {
        final TableItem tableItem = new TableItem(comparisonTable, SWT.NONE);

        tableItem.setText(0, Format.null2blank(referencedColumn.getLogicalName()));

        final List<NormalColumn> foreignKeyList = referencedMap.get(referencedColumn.getRootReferencedColumn());

        final TableEditor tableEditor = new TableEditor(comparisonTable);
        tableEditor.grabHorizontal = true;

        tableEditor.setEditor(createForeignKeyCombo(foreignKeyList), tableItem, 1);
        tableEditorList.add(tableEditor);
        editorReferencedMap.put(tableEditor, foreignKeyList);
    }

    protected Combo createForeignKeyCombo(final List<NormalColumn> foreignKeyList) {
        final Combo foreignKeyCombo = CompositeFactory.createReadOnlyCombo(this, comparisonTable, null);

        foreignKeyCombo.add("");

        if (foreignKeyList != null) {
            for (final NormalColumn normalColumn : foreignKeyList) {
                foreignKeyCombo.add(Format.toString(normalColumn.getName()));
            }
        }

        for (final NormalColumn normalColumn : candidateForeignKeyColumns) {
            foreignKeyCombo.add(Format.toString(normalColumn.getName()));
        }

        if (foreignKeyCombo.getItemCount() > 0) {
            foreignKeyCombo.select(0);
        }

        return foreignKeyCombo;
    }

    @Override
    public boolean close() {
        disposeTableEditor();

        return super.close();
    }

    private void disposeTableEditor() {
        for (final TableEditor tableEditor : tableEditorList) {
            tableEditor.getEditor().dispose();
            tableEditor.dispose();
        }

        tableEditorList.clear();
        editorReferencedMap.clear();
    }

    public List<NormalColumn> getReferencedColumnList() {
        return referencedColumnList;
    }

    public List<NormalColumn> getForeignKeyColumnList() {
        return foreignKeyColumnList;
    }

    public boolean isReferenceForPK() {
        return referenceForPK;
    }

    public ComplexUniqueKey getReferencedComplexUniqueKey() {
        return referencedComplexUniqueKey;
    }

    public NormalColumn getReferencedColumn() {
        return referencedColumn;
    }

    @Override
    protected String getTitle() {
        return "dialog.title.relation";
    }
}
