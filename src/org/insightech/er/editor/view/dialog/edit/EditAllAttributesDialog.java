package org.insightech.er.editor.view.dialog.edit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CenteredContentCellPaint;
import org.insightech.er.common.widgets.ListenerAppender;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.DiagramContents;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.CopyWord;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.TypeData;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.model.edit.CopyManager;
import org.insightech.er.editor.view.dialog.common.EditableTable;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;

public class EditAllAttributesDialog extends AbstractDialog implements EditableTable {

    private static final int KEY_WIDTH = 45;

    private static final int NAME_WIDTH = 150;

    private static final int TYPE_WIDTH = 100;

    private static final int NOT_NULL_WIDTH = 80;

    private static final int UNIQUE_KEY_WIDTH = 70;

    private Table attributeTable;

    private TableEditor tableEditor;

    private final ERDiagram diagram;

    private final DiagramContents copyContents;

    private final List<Column> columnList;

    private List<Word> wordList;

    private String errorMessage;

    // private Map<Column, TableEditor[]> columnNotNullCheckMap = new
    // HashMap<Column, TableEditor[]>();

    public EditAllAttributesDialog(final Shell parentShell, final ERDiagram diagram) {
        super(parentShell);
        this.diagram = diagram;

        final CopyManager copyManager = new CopyManager(null);

        copyContents = copyManager.copy(this.diagram.getDiagramContents());
        columnList = new ArrayList<Column>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getErrorMessage() {
        return errorMessage;
    }

    // @Override
    // protected void addListener() {
    // super.addListener();
    //
    // this.attributeTable.addListener(SWT.EraseItem, new Listener() {
    // public void handleEvent(Event event) {
    // // event.detail &= ~SWT.HOT;
    // if ((event.detail & SWT.SELECTED) == 0) {
    // Table table = (Table) event.widget;
    // TableItem item = (TableItem) event.item;
    //
    // if (item.getBackground().equals(
    // Resources.SELECTED_FOREIGNKEY_COLUMN)) {
    // int clientWidth = table.getClientArea().width;
    //
    // GC gc = event.gc;
    // // Color oldForeground = gc.getForeground();
    // Color oldBackground = gc.getBackground();
    //
    // gc.setBackground(Resources.SELECTED_FOREIGNKEY_COLUMN);
    // // gc.setForeground(colorForeground);
    // gc.fillRectangle(0, event.y, clientWidth, event.height);
    //
    // // gc.setForeground(oldForeground);
    // gc.setBackground(oldBackground);
    // // event.detail &= ~SWT.SELECTED;
    // }
    // }
    // }
    // });
    // }

    @Override
    protected String getTitle() {
        return "dialog.title.edit.all.attributes";
    }

    @Override
    protected void initialize(final Composite composite) {
        createTable(composite);
    }

    /**
     * This method initializes composite2
     */
    private void createTable(final Composite composite) {
        final GridData tableGridData = new GridData();
        tableGridData.horizontalSpan = 3;
        tableGridData.heightHint = 400;
        tableGridData.horizontalAlignment = GridData.FILL;
        tableGridData.grabExcessHorizontalSpace = true;

        attributeTable = new Table(composite, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION);
        attributeTable.setLayoutData(tableGridData);
        attributeTable.setHeaderVisible(true);
        attributeTable.setLinesVisible(true);

        final TableColumn columnLogicalName = new TableColumn(attributeTable, SWT.NONE);
        columnLogicalName.setWidth(NAME_WIDTH);
        columnLogicalName.setText(ResourceString.getResourceString("label.column.logical.name"));

        final TableColumn columnPhysicalName = new TableColumn(attributeTable, SWT.NONE);
        columnPhysicalName.setWidth(NAME_WIDTH);
        columnPhysicalName.setText(ResourceString.getResourceString("label.column.physical.name"));

        final TableColumn tableLogicalName = new TableColumn(attributeTable, SWT.NONE);
        tableLogicalName.setWidth(NAME_WIDTH);
        tableLogicalName.setText(ResourceString.getResourceString("label.table.logical.name"));

        final TableColumn tablePhysicalName = new TableColumn(attributeTable, SWT.NONE);
        tablePhysicalName.setWidth(NAME_WIDTH);
        tablePhysicalName.setText(ResourceString.getResourceString("label.table.physical.name"));

        final TableColumn tableWord = new TableColumn(attributeTable, SWT.NONE);
        tableWord.setWidth(NAME_WIDTH);
        tableWord.setText(ResourceString.getResourceString("label.word"));

        final TableColumn columnType = new TableColumn(attributeTable, SWT.NONE);
        columnType.setWidth(TYPE_WIDTH);
        columnType.setText(ResourceString.getResourceString("label.column.type"));

        final TableColumn columnLength = new TableColumn(attributeTable, SWT.RIGHT);
        columnLength.setWidth(TYPE_WIDTH);
        columnLength.setText(ResourceString.getResourceString("label.column.length"));

        final TableColumn columnDecimal = new TableColumn(attributeTable, SWT.RIGHT);
        columnDecimal.setWidth(TYPE_WIDTH);
        columnDecimal.setText(ResourceString.getResourceString("label.column.decimal"));

        final TableColumn columnKey = new TableColumn(attributeTable, SWT.CENTER);
        columnKey.setText("PK");
        columnKey.setWidth(KEY_WIDTH);
        new CenteredContentCellPaint(attributeTable, 8);

        final TableColumn columnForeignKey = new TableColumn(attributeTable, SWT.CENTER);
        columnForeignKey.setText("FK");
        columnForeignKey.setWidth(KEY_WIDTH);
        new CenteredContentCellPaint(attributeTable, 9);

        final TableColumn columnNotNull = new TableColumn(attributeTable, SWT.CENTER);
        columnNotNull.setWidth(NOT_NULL_WIDTH);
        columnNotNull.setText(ResourceString.getResourceString("label.not.null"));
        new CenteredContentCellPaint(attributeTable, 10);

        final TableColumn columnUnique = new TableColumn(attributeTable, SWT.CENTER);
        columnUnique.setWidth(UNIQUE_KEY_WIDTH);
        columnUnique.setText(ResourceString.getResourceString("label.unique.key"));
        new CenteredContentCellPaint(attributeTable, 11);

        tableEditor = new TableEditor(attributeTable);
        tableEditor.grabHorizontal = true;

        ListenerAppender.addTableEditListener(attributeTable, tableEditor, this);
    }

    private Combo createTypeCombo(final NormalColumn targetColumn) {
        final GridData gridData = new GridData();
        gridData.widthHint = 100;

        final Combo typeCombo = new Combo(attributeTable, SWT.READ_ONLY);
        initializeTypeCombo(typeCombo);
        typeCombo.setLayoutData(gridData);

        typeCombo.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent event) {
                validate();
            }

        });

        final SqlType sqlType = targetColumn.getType();

        final String database = diagram.getDatabase();

        if (sqlType != null && sqlType.getAlias(database) != null) {
            typeCombo.setText(sqlType.getAlias(database));
        }

        return typeCombo;
    }

    private void initializeTypeCombo(final Combo combo) {
        combo.setVisibleItemCount(20);

        combo.add("");

        final String database = diagram.getDatabase();

        for (final String alias : SqlType.getAliasList(database)) {
            combo.add(alias);
        }
    }

    protected Combo createWordCombo(final NormalColumn targetColumn) {
        final GridData gridData = new GridData();
        gridData.widthHint = 100;

        final Combo wordCombo = new Combo(attributeTable, SWT.READ_ONLY);
        initializeWordCombo(wordCombo);
        wordCombo.setLayoutData(gridData);
        setWordValue(wordCombo, targetColumn);

        return wordCombo;
    }

    private void initializeWordCombo(final Combo combo) {
        combo.setVisibleItemCount(20);

        combo.add("");

        wordList = copyContents.getDictionary().getWordList();
        Collections.sort(wordList);

        for (final Word word : wordList) {
            combo.add(Format.null2blank(word.getLogicalName()));
        }
    }

    private void setWordValue(final Combo combo, final NormalColumn targetColumn) {
        Word word = targetColumn.getWord();
        while (word instanceof CopyWord) {
            word = ((CopyWord) word).getOriginal();
        }

        if (word != null) {
            final int index = wordList.indexOf(word);

            combo.select(index + 1);
        }
    }

    @Override
    protected void perfomeOK() throws InputException {}

    @Override
    protected void setData() {
        for (final NodeElement nodeElement : copyContents.getContents()) {
            if (nodeElement instanceof ERTable) {
                final ERTable table = (ERTable) nodeElement;

                for (final Column column : table.getColumns()) {
                    final TableItem tableItem = new TableItem(attributeTable, SWT.NONE);
                    column2TableItem(table, column, tableItem);
                    columnList.add(column);
                }
            }
        }
    }

    private void column2TableItem(final ERTable table, final Column column, final TableItem tableItem) {
        // this.disposeCheckBox(column);

        if (table != null) {
            tableItem.setText(2, Format.null2blank(table.getLogicalName()));
            tableItem.setText(3, Format.null2blank(table.getPhysicalName()));
        }

        if (column instanceof NormalColumn) {

            final NormalColumn normalColumn = (NormalColumn) column;

            // if (normalColumn.isForeignKey()) {
            // tableItem.setBackground(Resources.SELECTED_FOREIGNKEY_COLUMN);
            //
            // } else {
            // tableItem.setBackground(ColorConstants.white);
            // }

            Color keyColor = ColorConstants.black;
            Color color = ColorConstants.black;

            if (normalColumn.isPrimaryKey() && normalColumn.isForeignKey()) {
                keyColor = ColorConstants.blue;
                color = ColorConstants.gray;

            } else if (normalColumn.isPrimaryKey()) {
                keyColor = ColorConstants.red;

            } else if (normalColumn.isForeignKey()) {
                keyColor = ColorConstants.darkGreen;
                color = ColorConstants.gray;
            }

            tableItem.setForeground(color);

            int colCount = 0;

            tableItem.setForeground(colCount, keyColor);
            tableItem.setText(colCount, Format.null2blank(normalColumn.getLogicalName()));

            colCount++;
            tableItem.setForeground(colCount, keyColor);
            tableItem.setText(colCount, Format.null2blank(normalColumn.getPhysicalName()));

            colCount++;
            tableItem.setForeground(colCount, ColorConstants.gray);

            colCount++;
            tableItem.setForeground(colCount, ColorConstants.gray);

            colCount++;
            if (normalColumn.getWord() != null) {
                tableItem.setText(colCount, Format.null2blank(normalColumn.getWord().getLogicalName()));
            }

            colCount++;
            final SqlType sqlType = normalColumn.getType();

            if (sqlType != null) {
                final String database = diagram.getDatabase();

                if (sqlType.getAlias(database) != null) {
                    tableItem.setText(colCount, sqlType.getAlias(database));
                } else {
                    tableItem.setText(colCount, "");
                }

            } else {
                tableItem.setText(colCount, "");
            }

            colCount++;
            if (normalColumn.getTypeData().getLength() != null) {
                tableItem.setText(colCount, normalColumn.getTypeData().getLength().toString());
            } else {
                tableItem.setText(colCount, "");
            }

            colCount++;
            if (normalColumn.getTypeData().getDecimal() != null) {
                tableItem.setText(colCount, normalColumn.getTypeData().getDecimal().toString());
            } else {
                tableItem.setText(colCount, "");
            }

            colCount++;
            if (normalColumn.isPrimaryKey()) {
                tableItem.setImage(colCount, ERDiagramActivator.getImage(ImageKey.PRIMARY_KEY));
            } else {
                tableItem.setImage(colCount, null);
            }

            colCount++;
            if (normalColumn.isForeignKey()) {
                tableItem.setImage(colCount, ERDiagramActivator.getImage(ImageKey.FOREIGN_KEY));

                // CLabel imageLabel = new CLabel(this.attributeTable,
                // SWT.NONE);
                // imageLabel.setImage(Activator.getImage(ImageKey.FOREIGN_KEY));
                // imageLabel.pack();
                // TableEditor editor = new TableEditor(this.attributeTable);
                // editor.minimumWidth = imageLabel.getSize().x;
                // editor.horizontalAlignment = SWT.CENTER;
                // editor.setEditor(imageLabel, tableItem, 9);

            } else {
                tableItem.setImage(colCount, null);
            }

            colCount++;
            if (normalColumn.isNotNull()) {
                if (normalColumn.isPrimaryKey()) {
                    tableItem.setImage(colCount, ERDiagramActivator.getImage(ImageKey.CHECK_GREY));
                } else {
                    tableItem.setImage(colCount, ERDiagramActivator.getImage(ImageKey.CHECK));
                }

            } else {
                tableItem.setImage(colCount, null);
            }

            colCount++;
            if (normalColumn.isUniqueKey()) {

                if (table != null && normalColumn.isRefered()) {
                    tableItem.setImage(colCount, ERDiagramActivator.getImage(ImageKey.CHECK_GREY));
                } else {
                    tableItem.setImage(colCount, ERDiagramActivator.getImage(ImageKey.CHECK));
                }

            } else {
                tableItem.setImage(colCount, null);
            }

            // this.setTableEditor(table, normalColumn, tableItem);

        } else {
            // group column

            // tableItem.setBackground(ColorConstants.white);
            tableItem.setForeground(ColorConstants.gray);

            // tableItem.setImage(2, Activator
            // .getImage(ImageKey.COLUMN_GROUP_IMAGE));

            tableItem.setText(0, column.getName());

            tableItem.setImage(8, null);
            tableItem.setImage(9, null);
        }
    }

    // private void disposeCheckBox(Column column) {
    // TableEditor[] oldEditors = this.columnNotNullCheckMap.get(column);
    //
    // if (oldEditors != null) {
    // for (TableEditor oldEditor : oldEditors) {
    // if (oldEditor.getEditor() != null) {
    // oldEditor.getEditor().dispose();
    // oldEditor.dispose();
    // }
    // }
    //
    // this.columnNotNullCheckMap.remove(column);
    // }
    // }

    // private void setTableEditor(ERTable table, final NormalColumn
    // normalColumn,
    // TableItem tableItem) {
    //
    // TableEditor[] editors = new TableEditor[] {
    // CompositeFactory.createCheckBoxTableEditor(tableItem,
    // normalColumn.isNotNull(), 10),
    // CompositeFactory.createCheckBoxTableEditor(tableItem,
    // normalColumn.isUniqueKey(), 11) };
    //
    // final Button notNullCheckButton = (Button) editors[0].getEditor();
    // final Button uniqueCheckButton = (Button) editors[1].getEditor();
    //
    // if (normalColumn.isPrimaryKey()) {
    // notNullCheckButton.setEnabled(false);
    // }
    //
    // if (table != null) {
    // if (normalColumn.isRefered()) {
    // uniqueCheckButton.setEnabled(false);
    // }
    // }
    //
    // this.columnNotNullCheckMap.put(normalColumn, editors);
    //
    // notNullCheckButton.addSelectionListener(new SelectionAdapter() {
    //
    // /**
    // * {@inheritDoc}
    // */
    // @Override
    // public void widgetSelected(SelectionEvent e) {
    // normalColumn.setNotNull(notNullCheckButton.getSelection());
    // super.widgetSelected(e);
    // }
    // });
    //
    // uniqueCheckButton.addSelectionListener(new SelectionAdapter() {
    //
    // /**
    // * {@inheritDoc}
    // */
    // @Override
    // public void widgetSelected(SelectionEvent e) {
    // normalColumn.setUniqueKey(uniqueCheckButton.getSelection());
    // super.widgetSelected(e);
    // }
    // });
    // }

    @Override
    public Control getControl(final Point xy) {
        final Column column = getColumn(xy);

        if (column instanceof NormalColumn) {
            final NormalColumn targetColumn = (NormalColumn) column;

            final String database = diagram.getDatabase();

            if (xy.x == 4) {
                if (targetColumn.isForeignKey()) {
                    return null;
                }
                return createWordCombo(targetColumn);
            }

            if (xy.x == 0 || xy.x == 1) {
                return new Text(attributeTable, SWT.BORDER);
            }
            if (xy.x == 6) {
                if (targetColumn.isForeignKey()) {
                    return null;
                }
                if (targetColumn.getType() != null && targetColumn.getType().isNeedLength(database)) {
                    return new Text(attributeTable, SWT.BORDER | SWT.RIGHT);
                }
            }
            if (xy.x == 7) {
                if (targetColumn.isForeignKey()) {
                    return null;
                }
                if (targetColumn.getType() != null && targetColumn.getType().isNeedDecimal(database)) {
                    return new Text(attributeTable, SWT.BORDER | SWT.RIGHT);
                }
            }

            if (xy.x == 5) {
                if (targetColumn.isForeignKey()) {
                    return null;
                }
                return createTypeCombo(targetColumn);
            }
        }

        return null;
    }

    private Column getColumn(final Point xy) {
        return columnList.get(xy.y);
    }

    @Override
    public void setData(final Point xy, final Control control) {
        errorMessage = null;

        final Column column = getColumn(xy);

        if (column instanceof NormalColumn) {
            final NormalColumn targetColumn = (NormalColumn) column;

            final String database = diagram.getDatabase();

            Word word = targetColumn.getWord();

            if (xy.x == 4) {
                if (targetColumn.isForeignKey()) {
                    return;
                }

                final int index = ((Combo) control).getSelectionIndex();

                final Dictionary dictionary = copyContents.getDictionary();
                dictionary.remove(targetColumn);

                if (index == 0) {
                    word = new Word(word);
                    word.setLogicalName("");

                } else {
                    final Word selectedWord = wordList.get(index - 1);
                    if (word != selectedWord) {
                        word = selectedWord;
                    }
                }

                targetColumn.setWord(word);
                dictionary.add(targetColumn);

                resetNormalColumn(targetColumn);

            } else {
                if (xy.x == 0) {
                    final String text = ((Text) control).getText();

                    if (targetColumn.isForeignKey()) {
                        targetColumn.setForeignKeyLogicalName(text);

                    } else {
                        word.setLogicalName(text);
                    }

                } else if (xy.x == 1) {
                    final String text = ((Text) control).getText();

                    if (!Check.isAlphabet(text)) {
                        if (diagram.getDiagramContents().getSettings().isValidatePhysicalName()) {
                            errorMessage = "error.column.physical.name.not.alphabet";
                            return;
                        }
                    }

                    if (targetColumn.isForeignKey()) {
                        targetColumn.setForeignKeyPhysicalName(text);

                    } else {

                        word.setPhysicalName(text);
                    }

                } else if (xy.x == 5) {
                    if (targetColumn.isForeignKey()) {
                        return;
                    }

                    final SqlType selectedType = SqlType.valueOf(database, ((Combo) control).getText());
                    word.setType(selectedType, word.getTypeData(), database);

                } else if (xy.x == 6) {
                    if (targetColumn.isForeignKey()) {
                        return;
                    }

                    final String text = ((Text) control).getText().trim();

                    try {
                        if (!text.equals("")) {
                            final int len = Integer.parseInt(text);
                            if (len < 0) {
                                errorMessage = "error.column.length.zero";
                                return;
                            }

                            final TypeData oldTypeData = word.getTypeData();
                            final TypeData newTypeData = new TypeData(Integer.parseInt(((Text) control).getText()), oldTypeData.getDecimal(), oldTypeData.isArray(), oldTypeData.getArrayDimension(), oldTypeData.isUnsigned(), oldTypeData.isZerofill(), oldTypeData.isBinary(), oldTypeData.getArgs(), oldTypeData.isCharSemantics());

                            word.setType(word.getType(), newTypeData, database);
                        }

                    } catch (final NumberFormatException e) {
                        errorMessage = "error.column.length.degit";
                        return;
                    }

                } else if (xy.x == 7) {
                    if (targetColumn.isForeignKey()) {
                        return;
                    }

                    final String text = ((Text) control).getText().trim();

                    try {
                        if (!text.equals("")) {
                            final int decimal = Integer.parseInt(text);
                            if (decimal < 0) {
                                errorMessage = "error.column.decimal.zero";
                                return;
                            }

                            final TypeData oldTypeData = word.getTypeData();
                            final TypeData newTypeData = new TypeData(oldTypeData.getLength(), decimal, oldTypeData.isArray(), oldTypeData.getArrayDimension(), oldTypeData.isUnsigned(), oldTypeData.isZerofill(), oldTypeData.isBinary(), oldTypeData.getArgs(), oldTypeData.isCharSemantics());

                            word.setType(word.getType(), newTypeData, database);
                        }

                    } catch (final NumberFormatException e) {
                        errorMessage = "error.column.decimal.degit";
                        return;
                    }
                }

                resetRowUse(word, targetColumn);
            }
        }

        return;
    }

    private void resetRowUse(final Word word, final NormalColumn targetColumn) {
        if (targetColumn.isForeignKey()) {
            resetNormalColumn(targetColumn);

        } else {
            for (int i = 0; i < columnList.size(); i++) {
                final Column column = columnList.get(i);
                if (column instanceof NormalColumn) {
                    final NormalColumn normalColumn = (NormalColumn) column;
                    if (word.equals(normalColumn.getWord())) {
                        resetNormalColumn(normalColumn);
                    }
                }
            }
        }
    }

    private void resetNormalColumn(final NormalColumn normalColumn) {
        for (int i = 0; i < columnList.size(); i++) {
            if (columnList.get(i) == normalColumn) {
                final TableItem tableItem = attributeTable.getItem(i);
                column2TableItem(null, normalColumn, tableItem);
                break;
            }
        }

        final List<NormalColumn> foreignKeyList = normalColumn.getForeignKeyList();

        for (final NormalColumn foreignKey : foreignKeyList) {
            resetNormalColumn(foreignKey);
        }
    }

    public DiagramContents getDiagramContents() {
        return copyContents;
    }

    @Override
    public void onDoubleClicked(final Point xy) {
        final Column column = getColumn(xy);

        if (column instanceof NormalColumn) {
            final NormalColumn normalColumn = (NormalColumn) column;

            if (normalColumn.isPrimaryKey()) {
                return;
            }

            if (normalColumn.isRefered()) {
                return;
            }

            if (xy.x == 10) {
                normalColumn.setNotNull(!normalColumn.isNotNull());
                resetNormalColumn(normalColumn);

            } else if (xy.x == 11) {
                normalColumn.setUniqueKey(!normalColumn.isUniqueKey());
                resetNormalColumn(normalColumn);
            }
        }
    }
}
