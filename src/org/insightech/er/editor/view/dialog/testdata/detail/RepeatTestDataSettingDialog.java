package org.insightech.er.editor.view.dialog.testdata.detail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.testdata.RepeatTestDataDef;
import org.insightech.er.editor.view.dialog.testdata.detail.tab.RepeatTestDataTabWrapper;
import org.insightech.er.util.Format;

public class RepeatTestDataSettingDialog extends AbstractDialog {

    private static final int LABEL_WIDTH = 90;

    private static final int NUM_WIDTH = 50;

    private StackLayout stackLayout;
    private Composite cardPanel;

    private Composite nonePanel;
    private Composite templatePanel;
    private Composite foreignKeyPanel;
    private Composite enumPanel;

    private Combo columnCombo;

    private Combo typeCombo;

    private Label repeatNumLabel;

    private Text repeatNum;

    private Text template;

    private Text from;

    private Text to;

    private Text increment;

    private Text selects;

    private RepeatTestDataDef dataDef;

    private final RepeatTestDataTabWrapper repeatTestDataTabWrapper;

    private int columnIndex;

    private NormalColumn normalColumn;

    private final ERTable table;

    private boolean createContents = false;

    public RepeatTestDataSettingDialog(final Shell parentShell, final int columnIndex, final RepeatTestDataTabWrapper repeatTestDataTabWrapper, final ERTable table) {
        super(parentShell);

        this.repeatTestDataTabWrapper = repeatTestDataTabWrapper;
        this.table = table;
        this.columnIndex = columnIndex;
    }

    @Override
    protected void initialize(final Composite composite) {
        CompositeFactory.createLabel(composite, "label.column", 1, LABEL_WIDTH);
        columnCombo = CompositeFactory.createReadOnlyCombo(this, composite, null);

        CompositeFactory.createLabel(composite, "label.testdata.repeat.type", 1, LABEL_WIDTH);
        typeCombo = CompositeFactory.createReadOnlyCombo(this, composite, null);

        repeatNumLabel = CompositeFactory.createLabel(composite, "label.testdata.repeat.num", 1, LABEL_WIDTH);
        repeatNum = CompositeFactory.createNumText(this, composite, null);

        initCardPanel(composite);

        initTypeCombo();
        initColumnCombo();
    }

    private void initColumnCombo() {
        for (final NormalColumn normalColumn : table.getExpandedColumns()) {
            columnCombo.add(normalColumn.getName());
        }
    }

    private void initTypeCombo() {
        typeCombo.add(RepeatTestDataDef.getTypeLabel(RepeatTestDataDef.TYPE_NULL));
        typeCombo.add(RepeatTestDataDef.getTypeLabel(RepeatTestDataDef.TYPE_FORMAT));

        normalColumn = table.getExpandedColumns().get(columnIndex);

        if (normalColumn.isForeignKey()) {
            typeCombo.add(RepeatTestDataDef.getTypeLabel(RepeatTestDataDef.TYPE_FOREIGNKEY));
        }

        typeCombo.add(RepeatTestDataDef.getTypeLabel(RepeatTestDataDef.TYPE_ENUM));
    }

    private void initCardPanel(final Composite composite) {
        cardPanel = new Composite(composite, SWT.NONE);

        stackLayout = new StackLayout();
        stackLayout.marginHeight = 0;
        stackLayout.marginWidth = 0;

        cardPanel.setLayout(stackLayout);

        final GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.horizontalSpan = 2;
        gridData.horizontalIndent = 0;
        gridData.verticalIndent = 0;
        cardPanel.setLayoutData(gridData);

        initNonePanel();
        initTemplatePanel();
        initForeignKeyPanel();
        initEnumPanel();
    }

    private void initNonePanel() {
        nonePanel = new Composite(cardPanel, SWT.NONE);
    }

    private void initTemplatePanel() {
        templatePanel = new Composite(cardPanel, SWT.NONE);
        final GridLayout templatePanelLayout = new GridLayout(7, false);
        templatePanelLayout.marginHeight = 0;
        templatePanelLayout.marginWidth = 0;

        templatePanel.setLayout(templatePanelLayout);

        CompositeFactory.createLabel(templatePanel, "label.testdata.repeat.format", 1, LABEL_WIDTH);
        template = CompositeFactory.createText(this, templatePanel, null, 6, false, false);
        CompositeFactory.filler(templatePanel, 1);
        CompositeFactory.createExampleLabel(templatePanel, "label.testdata.repeat.comment", 6);

        CompositeFactory.filler(templatePanel, 1);
        CompositeFactory.createLabel(templatePanel, "label.testdata.repeat.start");
        from = CompositeFactory.createNumText(this, templatePanel, null, NUM_WIDTH);
        CompositeFactory.createLabel(templatePanel, "label.testdata.repeat.end");
        to = CompositeFactory.createNumText(this, templatePanel, null, NUM_WIDTH);
        CompositeFactory.createLabel(templatePanel, "label.testdata.repeat.increment");
        increment = CompositeFactory.createNumText(this, templatePanel, null, NUM_WIDTH);
    }

    private void initForeignKeyPanel() {
        foreignKeyPanel = new Composite(cardPanel, SWT.NONE);
        foreignKeyPanel.setLayout(new GridLayout(2, false));
    }

    private void initEnumPanel() {
        enumPanel = new Composite(cardPanel, SWT.NONE);

        final GridLayout enumPanelLayout = new GridLayout(2, false);
        enumPanelLayout.marginHeight = 0;
        enumPanelLayout.marginWidth = 0;

        enumPanel.setLayout(enumPanelLayout);

        CompositeFactory.createLabel(enumPanel, "label.testdata.repeat.enum.values", 1, LABEL_WIDTH);
        selects = CompositeFactory.createTextArea(this, enumPanel, null, -1, 100, 1, false);
    }

    @Override
    protected Point getInitialLocation(final Point initialSize) {
        final Point location = super.getInitialLocation(initialSize);

        location.y = 70;

        return location;
    }

    @Override
    protected void setData() {
        initialized = false;

        normalColumn = table.getExpandedColumns().get(columnIndex);
        dataDef = repeatTestDataTabWrapper.getRepeatTestData().getDataDef(normalColumn);

        columnCombo.select(columnIndex);

        if (dataDef != null) {
            typeCombo.setText(dataDef.getTypeLabel());
            repeatNum.setText(Format.toString(dataDef.getRepeatNum()));
            template.setText(Format.toString(dataDef.getTemplate()));
            from.setText(Format.toString(dataDef.getFrom()));
            to.setText(Format.toString(dataDef.getTo()));
            increment.setText(Format.toString(dataDef.getIncrement()));

            final StringBuilder sb = new StringBuilder();
            for (final String str : dataDef.getSelects()) {
                sb.append(str);
                sb.append("\r\n");
            }
            selects.setText(sb.toString());

            setCardPanel(dataDef.getType());

        } else {
            repeatNum.setText("1");
            template.setText("value_%");
            from.setText("1");
            to.setText("5");
            increment.setText("1");

            selects.setText("value_1\r\nvalue_2\r\nvalue_3\r\nvalue_4\r\n");
        }

        initialized = true;
        // this.validate();
    }

    @Override
    protected String getTitle() {
        return "dialog.title.testdata.repetition.condition.setting";
    }

    @Override
    protected String getErrorMessage() {
        if (createContents) {
            dataDef = getRepeatTestDataDef();

            repeatTestDataTabWrapper.setRepeatTestDataDef(normalColumn, dataDef);
            repeatTestDataTabWrapper.initTableData();
        }

        return null;
    }

    @Override
    protected void perfomeOK() throws InputException {}

    private RepeatTestDataDef getRepeatTestDataDef() {
        final RepeatTestDataDef dataDef = new RepeatTestDataDef();

        dataDef.setType(typeCombo.getText());
        dataDef.setRepeatNum(getIntValue(repeatNum));
        dataDef.setTemplate(template.getText());
        dataDef.setFrom(from.getText());
        dataDef.setTo(to.getText());
        dataDef.setIncrement(increment.getText());

        final String str = selects.getText();
        final BufferedReader reader = new BufferedReader(new StringReader(str));
        String line = null;
        final List<String> lines = new ArrayList<String>();

        try {
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (final IOException e) {}

        dataDef.setSelects(lines.toArray(new String[lines.size()]));

        return dataDef;
    }

    private int getIntValue(final Text textField) {
        try {
            return Integer.parseInt(textField.getText().trim());

        } catch (final NumberFormatException e) {}

        return 0;
    }

    @Override
    protected void addListener() {
        super.addListener();

        typeCombo.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent selectionevent) {
                setCardPanel(RepeatTestDataDef.getType(typeCombo.getText()));
            }

        });

        columnCombo.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent selectionevent) {
                columnIndex = columnCombo.getSelectionIndex();
                setData();
            }

        });
    }

    private void setCardPanel(final String selectedType) {
        if (RepeatTestDataDef.TYPE_FORMAT.equals(selectedType)) {
            stackLayout.topControl = templatePanel;
            repeatNumLabel.setVisible(true);
            repeatNum.setVisible(true);
            cardPanel.layout();

        } else if (RepeatTestDataDef.TYPE_FOREIGNKEY.equals(selectedType)) {
            stackLayout.topControl = foreignKeyPanel;
            repeatNumLabel.setVisible(true);
            repeatNum.setVisible(true);
            cardPanel.layout();

        } else if (RepeatTestDataDef.TYPE_ENUM.equals(selectedType)) {
            stackLayout.topControl = enumPanel;
            repeatNumLabel.setVisible(true);
            repeatNum.setVisible(true);
            cardPanel.layout();

        } else {
            stackLayout.topControl = nonePanel;
            repeatNumLabel.setVisible(false);
            repeatNum.setVisible(false);
            cardPanel.layout();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createButtonsForButtonBar(final Composite parent) {
        createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, false);
    }

    @Override
    protected Control createContents(final Composite parent) {
        final Control control = super.createContents(parent);

        addListener();
        validate();

        createContents = true;

        return control;
    }
}
