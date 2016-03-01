package org.insightech.er.editor.view.dialog.outline.tablespace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.common.widgets.ListenerAppender;
import org.insightech.er.db.DBManager;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.db.sqltype.SqlTypeManager;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.view.dialog.common.EditableTable;
import org.insightech.er.util.Format;

public class TablespaceSizeCaluculatorDialog extends AbstractDialog implements EditableTable {

    private static final int NAME_WIDTH = 200;

    private static final int NUM_WIDTH = 50;

    private static final int TABLE_NUM_WIDTH = 100;

    private static final int INDENT = 30;

    private Table tableTable;

    private TableEditor tableEditor;

    private String errorMessage;

    private ERDiagram diagram;

    private List<ERTable> tableList;

    private Map<ERTable, Integer> tableNumMap;

    private Integer kcbh;

    private Integer ub4;

    private Integer ktbbh;

    private Integer ktbit;

    private Integer kdbh;

    private Integer kdbt;

    private Integer ub1;

    private Integer sb2;

    private Integer dbBlockSize;

    private Text kcbhText;

    private Text ub4Text;

    private Text ktbbhText;

    private Text ktbitText;

    private Text kdbhText;

    private Text kdbtText;

    private Text ub1Text;

    private Text sb2Text;

    private Text dbBlockSizeText;

    Button restoreDefaultButton1;

    Button restoreDefaultButton2;

    private final int initrans = 1;

    private final int pctfree = 10;

    private Text tablespaceSizeText;

    private void setDefault() {
        kcbh = 20;
        ub4 = 4;
        ktbbh = 48;
        ktbit = 24;
        kdbh = 14;
        kdbt = 4;
        ub1 = 1;
        sb2 = 2;

        dbBlockSize = 8192;
    }

    public TablespaceSizeCaluculatorDialog() {
        super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
    }

    @Override
    protected void initLayout(final GridLayout layout) {
        super.initLayout(layout);

        layout.numColumns = 4;
    }

    public void init(final ERDiagram diagram) {
        this.diagram = diagram;
        tableList = new ArrayList<ERTable>(this.diagram.getDiagramContents().getContents().getTableSet().getList());
        Collections.sort(tableList);

        tableNumMap = new HashMap<ERTable, Integer>();
    }

    @Override
    protected void initialize(final Composite composite) {
        CompositeFactory.createLabel(composite, "label.tablespace.size.calculate.1", 3);

        restoreDefaultButton1 = new Button(composite, SWT.NONE);
        restoreDefaultButton1.setText(ResourceString.getResourceString("label.restore.default"));

        CompositeFactory.filler(composite, 1, INDENT);
        kcbhText = CompositeFactory.createNumText(this, composite, "KCBH", 1, NUM_WIDTH);
        CompositeFactory.filler(composite, 1);

        CompositeFactory.filler(composite, 1, INDENT);
        ub4Text = CompositeFactory.createNumText(this, composite, "UB4", 1, NUM_WIDTH);
        CompositeFactory.filler(composite, 1);

        CompositeFactory.filler(composite, 1, INDENT);
        ktbbhText = CompositeFactory.createNumText(this, composite, "KTBBH", 1, NUM_WIDTH);
        CompositeFactory.filler(composite, 1);

        CompositeFactory.filler(composite, 1, INDENT);
        ktbitText = CompositeFactory.createNumText(this, composite, "KTBIT", 1, NUM_WIDTH);
        CompositeFactory.filler(composite, 1);

        CompositeFactory.filler(composite, 1);
        kdbhText = CompositeFactory.createNumText(this, composite, "KDBH", 1, NUM_WIDTH);
        CompositeFactory.filler(composite, 1);

        CompositeFactory.filler(composite, 1, INDENT);
        kdbtText = CompositeFactory.createNumText(this, composite, "KDBT", 1, NUM_WIDTH);
        CompositeFactory.filler(composite, 1);

        CompositeFactory.filler(composite, 1);
        ub1Text = CompositeFactory.createNumText(this, composite, "UB1", 1, NUM_WIDTH);
        CompositeFactory.filler(composite, 1);

        CompositeFactory.filler(composite, 1, INDENT);
        sb2Text = CompositeFactory.createNumText(this, composite, "SB2", 1, NUM_WIDTH);
        CompositeFactory.filler(composite, 1);

        CompositeFactory.filler(composite, 4);

        CompositeFactory.createLabel(composite, "label.tablespace.size.calculate.2", 3);
        restoreDefaultButton2 = new Button(composite, SWT.NONE);
        restoreDefaultButton2.setText(ResourceString.getResourceString("label.restore.default"));

        CompositeFactory.filler(composite, 1, INDENT);
        dbBlockSizeText = CompositeFactory.createNumText(this, composite, "DB_BLOCK_SIZE", 1, NUM_WIDTH);
        CompositeFactory.filler(composite, 1);

        CompositeFactory.filler(composite, 4);

        CompositeFactory.createLabel(composite, "label.tablespace.size.calculate.3", 4);

        CompositeFactory.filler(composite, 4);

        final GridData tableGridData = new GridData();
        tableGridData.horizontalSpan = 4;
        tableGridData.horizontalAlignment = GridData.FILL;
        tableGridData.grabExcessHorizontalSpace = true;
        tableGridData.heightHint = 100;

        tableTable = new Table(composite, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION);
        tableTable.setLayoutData(tableGridData);
        tableTable.setHeaderVisible(true);
        tableTable.setLinesVisible(true);

        final TableColumn tableLogicalName = new TableColumn(tableTable, SWT.NONE);
        tableLogicalName.setWidth(NAME_WIDTH);
        tableLogicalName.setText(ResourceString.getResourceString("label.table.logical.name"));

        final TableColumn num = new TableColumn(tableTable, SWT.RIGHT);
        num.setWidth(TABLE_NUM_WIDTH);
        num.setText(ResourceString.getResourceString("label.record.num"));

        tableEditor = new TableEditor(tableTable);
        tableEditor.grabHorizontal = true;

        CompositeFactory.createLabel(composite, "label.tablespace.size.calculated", 2);

        tablespaceSizeText = new Text(composite, SWT.BORDER | SWT.READ_ONLY | SWT.RIGHT);
        final GridData textGridData = new GridData();
        textGridData.horizontalAlignment = GridData.FILL;
        textGridData.grabExcessHorizontalSpace = true;
        tablespaceSizeText.setLayoutData(textGridData);

        CompositeFactory.filler(composite, 1);
    }

    @Override
    protected String getErrorMessage() {
        if (errorMessage == null) {
            calculate();
        }
        return errorMessage;
    }

    @Override
    protected String getTitle() {
        return "dialog.title.tablespace.size.calculator";
    }

    @Override
    protected void perfomeOK() {}

    @Override
    protected void setData() {
        for (final ERTable table : tableList) {
            final TableItem tableItem = new TableItem(tableTable, SWT.NONE);
            column2TableItem(table, tableNumMap.get(table), tableItem);
        }

        setDefault();

        setParameterData1();
        setParameterData2();
    }

    private void setParameterData1() {
        kcbhText.setText(Format.toString(kcbh));
        ub4Text.setText(Format.toString(ub4));
        ktbbhText.setText(Format.toString(ktbbh));
        ktbitText.setText(Format.toString(ktbit));
        kdbhText.setText(Format.toString(kdbh));
        kdbtText.setText(Format.toString(kdbt));
        ub1Text.setText(Format.toString(ub1));
        sb2Text.setText(Format.toString(sb2));
    }

    private void setParameterData2() {
        dbBlockSizeText.setText(Format.toString(dbBlockSize));
    }

    private void column2TableItem(final ERTable table, final Integer num, final TableItem tableItem) {
        if (table != null) {
            tableItem.setText(0, Format.null2blank(table.getLogicalName()));
        }
        tableItem.setText(1, Format.toString(num));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addListener() {
        ListenerAppender.addTableEditListener(tableTable, tableEditor, this);

        restoreDefaultButton1.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                setParameterData1();
                calculate();
            }

        });

        restoreDefaultButton2.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                setParameterData2();
                calculate();
            }

        });
    }

    @Override
    public Control getControl(final Point xy) {
        if (xy.x == 1) {
            return new Text(tableTable, SWT.BORDER | SWT.RIGHT);
        }

        return null;
    }

    @Override
    public void setData(final Point xy, final Control control) {
        errorMessage = null;

        final String text = ((Text) control).getText().trim();

        try {
            if (!text.equals("")) {
                final int num = Integer.parseInt(text);
                if (num < 0) {
                    errorMessage = "error.record.num.zero";
                    return;
                }

                tableNumMap.put(tableList.get(xy.y), num);

                final TableItem tableItem = tableTable.getItem(xy.y);
                column2TableItem(null, num, tableItem);
            }

        } catch (final NumberFormatException e) {
            errorMessage = "error.record.num.degit";
            return;
        }
    }

    private void calculate() {
        final double bytesOfBlockHeader = getValue(kcbhText) + getValue(ub4Text) + getValue(ktbbhText) + ((initrans - 1) * getValue(ktbitText)) + getValue(kdbhText);

        // 20 + 4 + 48 + (1-1) * 24 + 14;

        // this.kcbh = 20;
        // this.ub4 = 4;
        // this.ktbbh = 48;
        // this.ktbit = 24;
        // this.kdbh = 14;
        // this.kdbt = 4;
        // this.ub1 = 1;
        // this.sb2 = 2;

        final double bytesOfDataPerBlock = Math.ceil((getValue(dbBlockSizeText) - bytesOfBlockHeader) * (1 - (pctfree / 100))) + getValue(kdbtText);

        int total = 0;

        for (final ERTable table : tableList) {
            final double bytesPerRow = 3 * getValue(ub1Text) + getTotalColumnSize(table) + getValue(sb2Text);

            final double rowNumPerBlock = Math.floor(bytesOfDataPerBlock / bytesPerRow);
            Integer recordNum = tableNumMap.get(table);
            if (recordNum == null) {
                recordNum = 0;
            }

            final double totalBlockNum = Math.ceil(recordNum / rowNumPerBlock);
            final int totalBytes = (int) (totalBlockNum * getValue(dbBlockSizeText));

            total += totalBytes;
        }

        tablespaceSizeText.setText(Format.toString(total));
    }

    private int getTotalColumnSize(final ERTable table) {
        int total = 0;

        final DBManager dbManager = DBManagerFactory.getDBManager(diagram);
        final SqlTypeManager manager = dbManager.getSqlTypeManager();

        for (final NormalColumn column : table.getExpandedColumns()) {
            total += manager.getByteLength(column.getType(), column.getTypeData().getLength(), column.getTypeData().getDecimal());
        }

        return total;
    }

    private double getValue(final Text text) {
        double value = 0;

        try {
            value = Double.parseDouble(text.getText());
        } catch (final NumberFormatException e) {}

        return value;
    }

    @Override
    public void onDoubleClicked(final Point xy) {}
}
