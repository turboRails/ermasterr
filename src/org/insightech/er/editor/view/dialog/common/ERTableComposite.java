package org.insightech.er.editor.view.dialog.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.CopyColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.view.dialog.element.table.sub.QuickAddDialog;
import org.insightech.er.editor.view.dialog.word.column.AbstractColumnDialog;
import org.insightech.er.util.Format;

public class ERTableComposite extends Composite {

    private static final int DEFAULT_HEIGHT = 200;

    // private static final int KEY_WIDTH = 45;

    public static final int NAME_WIDTH = 150;

    private static final int TYPE_WIDTH = 130;

    // private static final int NOT_NULL_WIDTH = 90;

    public static final int UNIQUE_KEY_WIDTH = 90;

    private Table table;

    private Button columnAddButton;

    private Button columnEditButton;

    private Button columnDeleteButton;

    private Button upButton;

    private Button downButton;

    private Button quickAddButton;

    private final ERDiagram diagram;

    private final ERTable ertable;

    private List<Column> columnList;

    private final AbstractColumnDialog columnDialog;

    private final AbstractDialog parentDialog;

    private final Map<Column, TableEditor[]> columnNotNullCheckMap = new HashMap<Column, TableEditor[]>();

    private final boolean buttonDisplay;

    private final boolean checkboxEnabled;

    private final int height;

    private final ERTableCompositeHolder holder;

    public ERTableComposite(final ERTableCompositeHolder holder, final Composite parent, final ERDiagram diagram, final ERTable erTable, final List<Column> columnList, final AbstractColumnDialog columnDialog, final AbstractDialog parentDialog, final int horizontalSpan, final boolean buttonDisplay, final boolean checkboxEnabled) {
        this(holder, parent, diagram, erTable, columnList, columnDialog, parentDialog, horizontalSpan, buttonDisplay, checkboxEnabled, DEFAULT_HEIGHT);
    }

    public ERTableComposite(final ERTableCompositeHolder holder, final Composite parent, final ERDiagram diagram, final ERTable erTable, final List<Column> columnList, final AbstractColumnDialog columnDialog, final AbstractDialog parentDialog, final int horizontalSpan, final boolean buttonDisplay, final boolean checkboxEnabled, final int height) {
        super(parent, SWT.NONE);

        this.holder = holder;
        this.height = height;
        this.buttonDisplay = buttonDisplay;
        this.checkboxEnabled = checkboxEnabled;

        this.diagram = diagram;
        ertable = erTable;
        this.columnList = columnList;

        this.columnDialog = columnDialog;
        this.parentDialog = parentDialog;

        final GridData gridData = new GridData();
        gridData.horizontalSpan = horizontalSpan;
        gridData.grabExcessVerticalSpace = true;
        gridData.verticalAlignment = GridData.FILL;

        setLayoutData(gridData);

        createComposite();
        initComposite();
    }

    private void createComposite() {
        final GridLayout gridLayout = new GridLayout();
        gridLayout.marginWidth = 0;
        gridLayout.numColumns = 1;

        setLayout(gridLayout);

        createTable();

        if (buttonDisplay) {
            createButton();
            setButtonEnabled(false);
        }
    }

    private void createTable() {
        table = CompositeFactory.createTable(this, height, 3);

        table.addListener(SWT.PaintItem, new Listener() {

            @Override
            public void handleEvent(final Event event) {
                if (event.index == 0 || event.index == 1) {
                    final TableItem tableItem = (TableItem) event.item;

                    final Image tmpImage = (Image) tableItem.getData(String.valueOf(event.index));

                    if (tmpImage != null) {
                        final int tmpWidth = tableItem.getBounds(event.index).width;
                        final int tmpHeight = tableItem.getBounds().height;

                        int tmpX = tmpImage.getBounds().width;
                        tmpX = (tmpWidth / 2 - tmpX / 2);
                        int tmpY = tmpImage.getBounds().height;
                        tmpY = (tmpHeight / 2 - tmpY / 2);
                        if (tmpX <= 0)
                            tmpX = event.x;
                        else
                            tmpX += event.x;
                        if (tmpY <= 0)
                            tmpY = event.y;
                        else
                            tmpY += event.y;

                        event.gc.drawImage(tmpImage, tmpX, tmpY);
                    }
                }
            }
        });

        CompositeFactory.createTableColumn(table, "PK", -1, SWT.CENTER);
        CompositeFactory.createTableColumn(table, "FK", -1, SWT.CENTER);
        CompositeFactory.createTableColumn(table, "label.physical.name", NAME_WIDTH, SWT.NONE);
        CompositeFactory.createTableColumn(table, "label.logical.name", NAME_WIDTH, SWT.NONE);
        CompositeFactory.createTableColumn(table, "label.column.type", TYPE_WIDTH, SWT.NONE);
        CompositeFactory.createTableColumn(table, "label.not.null", -1, SWT.CENTER);
        CompositeFactory.createTableColumn(table, "label.unique.key", -1, SWT.CENTER);

        table.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final int index = table.getSelectionIndex();
                selectTable(index);

                final Column selectedColumn = columnList.get(index);
                if (selectedColumn instanceof ColumnGroup) {
                    holder.selectGroup((ColumnGroup) selectedColumn);
                }
            }
        });

        // [ermasterr] to open column editor when the space key pressed
        table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == SWT.MouseHover) {
                    Column targetColumn = getTargetColumn();
                    if (targetColumn == null || !(targetColumn instanceof CopyColumn)) {
                        return;
                    }
                    addOrEditColumn((CopyColumn) targetColumn, false);
                }
            }
        });

        if (buttonDisplay) {
            table.addMouseListener(new MouseAdapter() {

                /**
                 * {@inheritDoc}
                 */
                @Override
                public void mouseDoubleClick(final MouseEvent e) {
                    final Column targetColumn = getTargetColumn();

                    if (targetColumn == null || !(targetColumn instanceof CopyColumn)) {
                        return;
                    }

                    addOrEditColumn((CopyColumn) targetColumn, false);
                }
            });
        }

        table.pack();
    }

    /**
     * This method initializes composite2
     */
    private void createButton() {
        final Composite buttonComposite = CompositeFactory.createChildComposite(this, 1, 8);

        columnAddButton = CompositeFactory.createSmallButton(buttonComposite, "label.button.add");

        columnAddButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                addOrEditColumn(null, true);
            }
        });

        columnEditButton = CompositeFactory.createSmallButton(buttonComposite, "label.button.edit");

        columnEditButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final Column targetColumn = getTargetColumn();

                if (targetColumn == null || !(targetColumn instanceof CopyColumn)) {
                    return;
                }

                addOrEditColumn((CopyColumn) targetColumn, false);
            }

        });

        columnDeleteButton = CompositeFactory.createSmallButton(buttonComposite, "label.button.delete");

        columnDeleteButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                int index = table.getSelectionIndex();

                removeColumn();

                if (index >= table.getItemCount()) {
                    index = table.getItemCount() - 1;
                }

                selectTable(index);
            }

        });

        CompositeFactory.filler(buttonComposite, 1, 30);

        upButton = CompositeFactory.createSmallButton(buttonComposite, "label.up.arrow");

        upButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                upColumn();
            }

        });

        downButton = CompositeFactory.createSmallButton(buttonComposite, "label.down.arrow");

        downButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                downColumn();
            }

        });

        CompositeFactory.filler(buttonComposite, 1, 30);

        quickAddButton = new Button(buttonComposite, SWT.NONE);
        quickAddButton.setText(ResourceString.getResourceString("label.button.quick.add"));

        quickAddButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final QuickAddDialog dialog = new QuickAddDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), diagram);
                if (dialog.open() == IDialogConstants.OK_ID) {
                    final List<NormalColumn> columnList = dialog.getColumnList();

                    for (final NormalColumn column : columnList) {
                        addTableData(column, true);
                    }
                }
            }

        });

        quickAddButton.setEnabled(true);
    }

    private void initComposite() {
        if (columnList != null) {
            for (final Column column : columnList) {
                final TableItem tableItem = new TableItem(table, SWT.NONE);
                column2TableItem(column, tableItem);
            }
        }
    }

    private void disposeCheckBox(final Column column) {
        final TableEditor[] oldEditors = columnNotNullCheckMap.get(column);

        if (oldEditors != null) {
            for (final TableEditor oldEditor : oldEditors) {
                if (oldEditor.getEditor() != null) {
                    oldEditor.getEditor().dispose();
                    oldEditor.dispose();
                }
            }

            columnNotNullCheckMap.remove(column);
        }
    }

    private void column2TableItem(final Column column, final TableItem tableItem) {
        disposeCheckBox(column);

        if (column instanceof NormalColumn) {
            // tableItem.setBackground(ColorConstants.white);

            final NormalColumn normalColumn = (NormalColumn) column;

            if (normalColumn.isPrimaryKey()) {
                tableItem.setData("0", ERDiagramActivator.getImage(ImageKey.PRIMARY_KEY));
            } else {
                tableItem.setData("0", null);
            }

            if (normalColumn.isForeignKey()) {
                tableItem.setData("1", ERDiagramActivator.getImage(ImageKey.FOREIGN_KEY));
            } else {
                tableItem.setData("1", null);
            }

            tableItem.setText(2, Format.null2blank(normalColumn.getPhysicalName()));
            tableItem.setText(3, Format.null2blank(normalColumn.getLogicalName()));

            final SqlType sqlType = normalColumn.getType();

            tableItem.setText(4, Format.formatType(sqlType, normalColumn.getTypeData(), diagram.getDatabase(), true));

            setTableEditor(normalColumn, tableItem);

        } else {
            // tableItem.setBackground(ColorConstants.white);
            tableItem.setData("0", ERDiagramActivator.getImage(ImageKey.GROUP));
            tableItem.setData("1", null);
            tableItem.setText(2, column.getName());
            tableItem.setText(3, "");
            tableItem.setText(4, "");
        }

    }

    private void setTableEditor(final NormalColumn normalColumn, final TableItem tableItem) {

        final Button notNullCheckButton = new Button(table, SWT.CHECK);
        notNullCheckButton.pack();

        final Button uniqueCheckButton = new Button(table, SWT.CHECK);
        uniqueCheckButton.pack();

        final TableEditor[] editors = new TableEditor[2];

        editors[0] = new TableEditor(table);

        editors[0].minimumWidth = notNullCheckButton.getSize().x;
        editors[0].horizontalAlignment = SWT.CENTER;
        editors[0].setEditor(notNullCheckButton, tableItem, 5);

        editors[1] = new TableEditor(table);

        editors[1].minimumWidth = uniqueCheckButton.getSize().x;
        editors[1].horizontalAlignment = SWT.CENTER;
        editors[1].setEditor(uniqueCheckButton, tableItem, 6);

        if (normalColumn.isNotNull()) {
            notNullCheckButton.setSelection(true);
        } else {
            notNullCheckButton.setSelection(false);
        }
        if (normalColumn.isUniqueKey()) {
            uniqueCheckButton.setSelection(true);
        } else {
            uniqueCheckButton.setSelection(false);
        }

        if (normalColumn.isPrimaryKey()) {
            notNullCheckButton.setEnabled(false);
        }

        if (ertable != null) {
            if (normalColumn.isRefered()) {
                uniqueCheckButton.setEnabled(false);
            }
        }

        columnNotNullCheckMap.put(normalColumn, editors);

        if (checkboxEnabled) {
            notNullCheckButton.addSelectionListener(new SelectionAdapter() {

                /**
                 * {@inheritDoc}
                 */
                @Override
                public void widgetSelected(final SelectionEvent e) {
                    final boolean notnull = notNullCheckButton.getSelection();

                    setNotNull(normalColumn, notnull);

                    super.widgetSelected(e);
                }
            });

            uniqueCheckButton.addSelectionListener(new SelectionAdapter() {

                /**
                 * {@inheritDoc}
                 */
                @Override
                public void widgetSelected(final SelectionEvent e) {
                    normalColumn.setUniqueKey(uniqueCheckButton.getSelection());
                    super.widgetSelected(e);
                }
            });

        } else {
            notNullCheckButton.setEnabled(false);
            uniqueCheckButton.setEnabled(false);
        }
    }

    private void setNotNull(final NormalColumn normalColumn, final boolean notnull) {
        normalColumn.setNotNull(notnull);

        if (ertable != null) {
            for (final NormalColumn anotherColumn : ertable.getNormalColumns()) {
                if (anotherColumn.isForeignKey()) {
                    final Relation anotherColumnsRelation = anotherColumn.getRelationList().get(0);

                    for (final Relation relation : normalColumn.getRelationList()) {
                        if (anotherColumnsRelation == relation) {
                            ((Button) columnNotNullCheckMap.get(anotherColumn)[0].getEditor()).setSelection(notnull);
                            anotherColumn.setNotNull(notnull);

                            break;
                        }
                    }
                }
            }
        }
    }

    private void addTableData(final NormalColumn column, final boolean add) {
        final int index = table.getSelectionIndex();

        TableItem tableItem = null;
        CopyColumn copyColumn = null;

        if (add) {
            tableItem = new TableItem(table, SWT.NONE);

            copyColumn = new CopyColumn(column);
            columnList.add(copyColumn);

        } else {
            tableItem = table.getItem(index);

            copyColumn = (CopyColumn) columnList.get(index);
            NormalColumn.copyData(column, copyColumn);

            setNotNull(copyColumn, copyColumn.isNotNull());
        }

        column2TableItem(copyColumn, tableItem);

        parentDialog.validate();
    }

    public void addTableData(final ColumnGroup column) {
        TableItem tableItem = null;
        tableItem = new TableItem(table, SWT.NONE);

        columnList.add(column);
        column2TableItem(column, tableItem);

        parentDialog.validate();
    }

    private void removeColumn() {
        final int index = table.getSelectionIndex();

        if (index != -1) {
            final Column column = columnList.get(index);

            if (column instanceof NormalColumn) {
                final NormalColumn normalColumn = (NormalColumn) column;

                if (normalColumn.isForeignKey()) {
                    setMessage(ResourceString.getResourceString("error.foreign.key.not.deleteable"));

                } else {
                    if (ertable != null && normalColumn.isRefered()) {
                        setMessage(ResourceString.getResourceString("error.reference.key.not.deleteable"));

                    } else {
                        removeColumn(index);
                    }
                }

            } else {
                this.removeColumn(index);
            }
        }

        parentDialog.validate();
    }

    public void removeColumn(final int index) {
        Column column = columnList.get(index);

        table.remove(index);

        columnList.remove(index);

        disposeCheckBox(column);

        for (int i = index; i < table.getItemCount(); i++) {
            final TableItem tableItem = table.getItem(i);
            column = columnList.get(i);

            disposeCheckBox(column);

            if (column instanceof NormalColumn) {
                setTableEditor((NormalColumn) column, tableItem);
            }
        }
    }

    private CopyColumn getTargetColumn() {
        CopyColumn column = null;

        final int index = table.getSelectionIndex();

        if (index != -1) {
            final Column targetColumn = columnList.get(index);

            if (targetColumn instanceof CopyColumn) {
                column = (CopyColumn) targetColumn;
            }
        }

        return column;
    }

    private void setMessage(final String message) {
        final MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_ERROR | SWT.OK);
        messageBox.setText(ResourceString.getResourceString("dialog.title.error"));
        messageBox.setMessage(message);
        messageBox.open();
    }

    private void upColumn() {
        final int index = table.getSelectionIndex();

        if (index != -1 && index != 0) {
            changeColumn(index - 1, index);
            table.setSelection(index - 1);
        }
    }

    private void downColumn() {
        final int index = table.getSelectionIndex();

        if (index != -1 && index != table.getItemCount() - 1) {
            changeColumn(index, index + 1);
            table.setSelection(index + 1);
        }
    }

    private void changeColumn(final int index1, final int index2) {
        final Column column1 = columnList.remove(index1);
        Column column2 = null;

        if (index1 < index2) {
            column2 = columnList.remove(index2 - 1);
            columnList.add(index1, column2);
            columnList.add(index2, column1);

        } else if (index1 > index2) {
            column2 = columnList.remove(index2);
            columnList.add(index1 - 1, column2);
            columnList.add(index2, column1);
        }

        final TableItem[] tableItems = table.getItems();

        column2TableItem(column1, tableItems[index2]);
        column2TableItem(column2, tableItems[index1]);
    }

    private void addOrEditColumn(final CopyColumn targetColumn, final boolean add) {
        boolean foreignKey = false;
        boolean isRefered = false;

        if (targetColumn != null) {
            foreignKey = targetColumn.isForeignKey();
            if (ertable != null) {
                isRefered = targetColumn.isRefered();
            }
        }
        columnDialog.setTargetColumn(targetColumn, foreignKey, isRefered);

        if (columnDialog.open() == IDialogConstants.OK_ID) {
            final NormalColumn column = columnDialog.getColumn();
            addTableData(column, add);
        }
    }

    public void setColumnList(final List<Column> columnList) {
        table.removeAll();

        if (this.columnList != null) {
            for (final Column column : this.columnList) {
                disposeCheckBox(column);
            }
        }

        this.columnList = columnList;

        initComposite();
    }

    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);

        if (buttonDisplay) {
            columnAddButton.setEnabled(enabled);
            columnEditButton.setEnabled(false);
            columnDeleteButton.setEnabled(false);
            upButton.setEnabled(false);
            downButton.setEnabled(false);
            quickAddButton.setEnabled(enabled);
        }
    }

    private void setButtonEnabled(final boolean enabled) {
        if (buttonDisplay) {
            columnEditButton.setEnabled(enabled);
            columnDeleteButton.setEnabled(enabled);
            upButton.setEnabled(enabled);
            downButton.setEnabled(enabled);
        }
    }

    private void selectTable(final int index) {
        table.select(index);

        if (index >= 0) {
            setButtonEnabled(true);
        } else {
            setButtonEnabled(false);
        }
    }

}
