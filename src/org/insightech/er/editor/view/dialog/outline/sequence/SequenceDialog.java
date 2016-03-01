package org.insightech.er.editor.view.dialog.outline.sequence;

import java.math.BigDecimal;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.db.DBManager;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.db.impl.db2.DB2DBManager;
import org.insightech.er.db.impl.h2.H2DBManager;
import org.insightech.er.db.impl.hsqldb.HSQLDBDBManager;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;

public class SequenceDialog extends AbstractDialog {

    private final int NUMBER_TEXT_SIZE = -1;

    private Text nameText;

    private Text schemaText;

    private Text incrementText;

    private Text minValueText;

    private Text maxValueText;

    private Text startText;

    private Text cacheText;

    private Button nocacheCheckBox;

    private Button cycleCheckBox;

    private Button orderCheckBox;

    private Text descriptionText;

    private Combo dataTypeCombo;

    private Text decimalSizeText;

    private final Sequence sequence;

    private Sequence result;

    private final ERDiagram diagram;

    public SequenceDialog(final Shell parentShell, final Sequence sequence, final ERDiagram diagram) {
        super(parentShell);

        this.sequence = sequence;
        this.diagram = diagram;
    }

    @Override
    protected void initLayout(final GridLayout layout) {
        super.initLayout(layout);

        layout.numColumns = 5;
    }

    @Override
    protected void initialize(final Composite composite) {
        final String database = diagram.getDatabase();

        nameText = CompositeFactory.createText(this, composite, "label.sequence.name", 4, false, true);
        schemaText = CompositeFactory.createText(this, composite, "label.schema", 4, false, true);

        if (DB2DBManager.ID.equals(diagram.getDatabase())) {
            dataTypeCombo = CompositeFactory.createReadOnlyCombo(this, composite, "Data Type", 2);
            dataTypeCombo.add("BIGINT");
            dataTypeCombo.add("INTEGER");
            dataTypeCombo.add("SMALLINT");
            dataTypeCombo.add("DECIMAL(p)");

            decimalSizeText = CompositeFactory.createNumText(this, composite, "Size", 1, 30, false);
            decimalSizeText.setEnabled(false);

        } else if (HSQLDBDBManager.ID.equals(database)) {
            dataTypeCombo = CompositeFactory.createReadOnlyCombo(this, composite, "Data Type", 4);
            dataTypeCombo.add("BIGINT");
            dataTypeCombo.add("INTEGER");

        }

        incrementText = CompositeFactory.createNumText(this, composite, "Increment", 4, NUMBER_TEXT_SIZE, true);

        if (!H2DBManager.ID.equals(database)) {
            startText = CompositeFactory.createNumText(this, composite, "Start", 4, NUMBER_TEXT_SIZE, true);

            minValueText = CompositeFactory.createNumText(this, composite, "MinValue", 4, NUMBER_TEXT_SIZE, true);

            maxValueText = CompositeFactory.createNumText(this, composite, "MaxValue", 4, NUMBER_TEXT_SIZE, true);
        }

        if (!HSQLDBDBManager.ID.equals(diagram.getDatabase())) {
            if (DB2DBManager.ID.equals(diagram.getDatabase())) {
                cacheText = CompositeFactory.createNumText(this, composite, "Cache", 1, NUMBER_TEXT_SIZE, true);
                nocacheCheckBox = CompositeFactory.createCheckbox(this, composite, "nocache", false, 3);
            } else {
                cacheText = CompositeFactory.createNumText(this, composite, "Cache", 4, NUMBER_TEXT_SIZE, true);

            }
        }

        if (!H2DBManager.ID.equals(database)) {
            cycleCheckBox = CompositeFactory.createCheckbox(this, composite, "Cycle", false, 5);
        }

        if (DB2DBManager.ID.equals(diagram.getDatabase())) {
            orderCheckBox = CompositeFactory.createCheckbox(this, composite, "Order", false, 5);
        }

        descriptionText = CompositeFactory.createTextArea(this, composite, "label.description", -1, 100, 4, true);
    }

    @Override
    protected String getErrorMessage() {
        if (!DBManagerFactory.getDBManager(diagram).isSupported(DBManager.SUPPORT_SEQUENCE)) {
            return "error.sequence.not.supported";
        }

        String text = nameText.getText().trim();
        if (text.equals("")) {
            return "error.sequence.name.empty";
        }

        if (!Check.isAlphabet(text)) {
            if (diagram.getDiagramContents().getSettings().isValidatePhysicalName()) {
                return "error.sequence.name.not.alphabet";
            }
        }

        text = schemaText.getText();

        if (!Check.isAlphabet(text)) {
            return "error.schema.not.alphabet";
        }

        text = incrementText.getText();

        if (!text.equals("")) {
            try {
                Integer.parseInt(text);

            } catch (final NumberFormatException e) {
                return "error.sequence.increment.degit";
            }
        }

        if (minValueText != null) {
            text = minValueText.getText();

            if (!text.equals("")) {
                try {
                    Long.parseLong(text);

                } catch (final NumberFormatException e) {
                    return "error.sequence.minValue.degit";
                }
            }
        }

        if (maxValueText != null) {
            text = maxValueText.getText();

            if (!text.equals("")) {
                try {
                    new BigDecimal(text);

                } catch (final NumberFormatException e) {
                    return "error.sequence.maxValue.degit";
                }
            }
        }

        if (startText != null) {
            text = startText.getText();

            if (!text.equals("")) {
                try {
                    Long.parseLong(text);

                } catch (final NumberFormatException e) {
                    return "error.sequence.start.degit";
                }
            }
        }

        if (cacheText != null) {
            text = cacheText.getText();

            if (!text.equals("")) {
                try {
                    final int cache = Integer.parseInt(text);
                    if (DB2DBManager.ID.equals(diagram.getDatabase())) {
                        if (cache < 2) {
                            return "error.sequence.cache.min2";
                        }
                    } else {
                        if (cache < 1) {
                            return "error.sequence.cache.min1";
                        }
                    }
                } catch (final NumberFormatException e) {
                    return "error.sequence.cache.degit";
                }
            }
        }

        if (decimalSizeText != null) {
            text = decimalSizeText.getText();

            if (!text.equals("")) {

                try {
                    final int size = Integer.parseInt(text);
                    if (size < 0) {
                        return "error.sequence.size.zero";
                    }

                } catch (final NumberFormatException e) {
                    return "error.sequence.size.degit";
                }
            }
        }

        return null;
    }

    @Override
    protected String getTitle() {
        return "dialog.title.sequence";
    }

    @Override
    protected void perfomeOK() throws InputException {
        result = new Sequence();

        result.setName(nameText.getText().trim());
        result.setSchema(schemaText.getText().trim());

        Integer increment = null;
        Long minValue = null;
        BigDecimal maxValue = null;
        Long start = null;
        Integer cache = null;

        String text = incrementText.getText();
        if (!text.equals("")) {
            increment = Integer.valueOf(text);
        }

        if (minValueText != null) {
            text = minValueText.getText();
            if (!text.equals("")) {
                minValue = Long.valueOf(text);
            }
        }

        if (maxValueText != null) {
            text = maxValueText.getText();
            if (!text.equals("")) {
                maxValue = new BigDecimal(text);
            }
        }

        text = startText.getText();
        if (!text.equals("")) {
            start = Long.valueOf(text);
        }

        if (cacheText != null) {
            text = cacheText.getText();
            if (!text.equals("")) {
                cache = Integer.valueOf(text);
            }
        }

        result.setIncrement(increment);
        result.setMinValue(minValue);
        result.setMaxValue(maxValue);
        result.setStart(start);
        result.setCache(cache);

        if (nocacheCheckBox != null) {
            result.setNocache(nocacheCheckBox.getSelection());
        }

        if (cycleCheckBox != null) {
            result.setCycle(cycleCheckBox.getSelection());
        }

        if (orderCheckBox != null) {
            result.setOrder(orderCheckBox.getSelection());
        }

        result.setDescription(descriptionText.getText().trim());

        if (dataTypeCombo != null) {
            result.setDataType(dataTypeCombo.getText());
            int decimalSize = 0;
            try {
                decimalSize = Integer.parseInt(decimalSizeText.getText().trim());
            } catch (final NumberFormatException e) {}
            result.setDecimalSize(decimalSize);
        }
    }

    @Override
    protected void setData() {
        if (sequence != null) {
            nameText.setText(Format.toString(sequence.getName()));
            schemaText.setText(Format.toString(sequence.getSchema()));
            incrementText.setText(Format.toString(sequence.getIncrement()));
            if (minValueText != null) {
                minValueText.setText(Format.toString(sequence.getMinValue()));
            }
            if (maxValueText != null) {
                maxValueText.setText(Format.toString(sequence.getMaxValue()));
            }
            if (startText != null) {
                startText.setText(Format.toString(sequence.getStart()));
            }
            if (cacheText != null) {
                cacheText.setText(Format.toString(sequence.getCache()));
            }
            if (nocacheCheckBox != null) {
                nocacheCheckBox.setSelection(sequence.isNocache());
            }
            if (cycleCheckBox != null) {
                cycleCheckBox.setSelection(sequence.isCycle());
            }
            if (orderCheckBox != null) {
                orderCheckBox.setSelection(sequence.isOrder());
            }

            descriptionText.setText(Format.toString(sequence.getDescription()));

            if (dataTypeCombo != null) {
                final String dataType = Format.toString(sequence.getDataType());
                dataTypeCombo.setText(dataType);
                if (dataType.equals("DECIMAL(p)") && decimalSizeText != null) {
                    decimalSizeText.setEnabled(true);
                    decimalSizeText.setText(Format.toString(sequence.getDecimalSize()));
                }
            }
        }
    }

    public Sequence getResult() {
        return result;
    }

    @Override
    protected void addListener() {
        super.addListener();

        if (dataTypeCombo != null && decimalSizeText != null) {
            dataTypeCombo.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(final SelectionEvent e) {
                    final String dataType = dataTypeCombo.getText();

                    if (dataType.equals("DECIMAL(p)")) {
                        decimalSizeText.setEnabled(true);

                    } else {
                        decimalSizeText.setEnabled(false);
                    }
                }

            });
        }

        if (nocacheCheckBox != null) {
            nocacheCheckBox.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(final SelectionEvent e) {
                    if (nocacheCheckBox.getSelection()) {
                        cacheText.setEnabled(false);

                    } else {
                        cacheText.setEnabled(true);
                    }

                }

            });
        }
    }

}
