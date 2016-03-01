package org.insightech.er.editor.view.dialog.word;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.db.impl.mysql.MySQLDBManager;
import org.insightech.er.db.impl.oracle.OracleDBManager;
import org.insightech.er.db.impl.postgres.PostgresDBManager;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.TypeData;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;

public abstract class AbstractWordDialog extends AbstractDialog {

    protected static int WIDTH = -1;

    protected Combo typeCombo;

    protected Text logicalNameText;

    protected Text physicalNameText;

    private String oldPhysicalName;

    protected Text lengthText;

    protected Text decimalText;

    protected Button arrayCheck;

    protected Text arrayDimensionText;

    protected Button unsignedCheck;

    protected Button zerofillCheck;

    protected Button binaryCheck;

    protected boolean add;

    protected Text descriptionText;

    protected Text argsText;

    protected Button byteSemanticsRadio;

    protected Button charSemanticsRadio;

    protected ERDiagram diagram;

    public AbstractWordDialog(final Shell parentShell, final ERDiagram diagram) {
        super(parentShell);

        this.diagram = diagram;
        oldPhysicalName = "";
    }

    public void setAdd(final boolean add) {
        this.add = add;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize(final Composite composite) {
        final Composite rootComposite = createRootComposite(composite);

        initializeComposite(rootComposite);
        initializeTypeCombo();

        physicalNameText.setFocus();
    }

    protected Composite createRootComposite(final Composite parent) {
        return CompositeFactory.createComposite(parent, getCompositeNumColumns(), false);
    }

    protected int getCompositeNumColumns() {
        return 6;
    }

    protected void initializeComposite(final Composite composite) {
        final int numColumns = getCompositeNumColumns();

        physicalNameText = CompositeFactory.createText(this, composite, "label.physical.name", numColumns - 1, WIDTH, false, true);

        logicalNameText = CompositeFactory.createText(this, composite, "label.logical.name", numColumns - 1, WIDTH, true, true);

        typeCombo = CompositeFactory.createReadOnlyCombo(this, composite, "label.column.type");

        lengthText = CompositeFactory.createNumText(this, composite, "label.column.length", 1, 30, false);
        lengthText.setEnabled(false);

        decimalText = CompositeFactory.createNumText(this, composite, "label.column.decimal", 1, 30, false);
        decimalText.setEnabled(false);

        if (PostgresDBManager.ID.equals(diagram.getDatabase())) {
            CompositeFactory.filler(composite, 1);

            final Composite typeOptionComposite = new Composite(composite, SWT.NONE);
            final GridData gridData = new GridData();
            gridData.horizontalSpan = getCompositeNumColumns() - 1;
            typeOptionComposite.setLayoutData(gridData);

            final GridLayout layout = new GridLayout();
            layout.numColumns = 5;
            typeOptionComposite.setLayout(layout);

            arrayCheck = CompositeFactory.createCheckbox(this, typeOptionComposite, "label.column.array", true);
            arrayCheck.setEnabled(true);
            arrayDimensionText = CompositeFactory.createNumText(this, typeOptionComposite, "label.column.array.dimension", 1, 30, false);
            arrayDimensionText.setEnabled(false);

            arrayCheck.addSelectionListener(new SelectionAdapter() {

                /**
                 * {@inheritDoc}
                 */
                @Override
                public void widgetSelected(final SelectionEvent e) {
                    arrayDimensionText.setEnabled(arrayCheck.getSelection());

                    super.widgetSelected(e);
                }
            });

        }

        if (MySQLDBManager.ID.equals(diagram.getDatabase())) {
            CompositeFactory.filler(composite, 1);

            Composite childComposite = CompositeFactory.createChildComposite(composite, 5, 3);

            unsignedCheck = CompositeFactory.createCheckbox(this, childComposite, "label.column.unsigned", true);
            unsignedCheck.setEnabled(false);

            zerofillCheck = CompositeFactory.createCheckbox(this, childComposite, "label.column.zerofill", false);
            zerofillCheck.setEnabled(false);

            binaryCheck = CompositeFactory.createCheckbox(this, childComposite, "label.column.binary", false);
            binaryCheck.setEnabled(false);

            CompositeFactory.filler(composite, 1);

            childComposite = CompositeFactory.createChildComposite(composite, 5, 3);
            CompositeFactory.createLabel(childComposite, "label.column.type.enum.set", 1, -1, true, true);
            argsText = CompositeFactory.createText(this, childComposite, null, 1, false, false);
            argsText.setEnabled(false);
        }

        if (OracleDBManager.ID.equals(diagram.getDatabase())) {
            CompositeFactory.filler(composite, 1);

            final Composite childComposite = CompositeFactory.createChildComposite(composite, 5, 2);

            byteSemanticsRadio = CompositeFactory.createRadio(this, childComposite, "label.column.byte", 1, true);
            byteSemanticsRadio.setEnabled(false);
            byteSemanticsRadio.setSelection(true);

            charSemanticsRadio = CompositeFactory.createRadio(this, childComposite, "label.column.char");
            charSemanticsRadio.setEnabled(false);
        }

        descriptionText = CompositeFactory.createTextArea(this, composite, "label.column.description", -1, 100, numColumns - 1, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final protected void setData() {
        initData();

        if (!add) {
            setWordData();
        }

        setEnabledBySqlType();
    }

    protected void initData() {}

    protected void setData(final String physicalName, final String logicalName, final SqlType sqlType, final TypeData typeData, final String description) {

        physicalNameText.setText(Format.toString(physicalName));
        logicalNameText.setText(Format.toString(logicalName));
        oldPhysicalName = physicalNameText.getText();

        if (sqlType != null) {
            final String database = diagram.getDatabase();

            if (sqlType.getAlias(database) != null) {
                typeCombo.setText(sqlType.getAlias(database));
            }

            if (!sqlType.isNeedLength(database)) {
                lengthText.setEnabled(false);
            }
            if (!sqlType.isNeedDecimal(database)) {
                decimalText.setEnabled(false);
            }

            if (unsignedCheck != null && !sqlType.isNumber()) {
                unsignedCheck.setEnabled(false);
            }

            if (zerofillCheck != null && !sqlType.isNumber()) {
                zerofillCheck.setEnabled(false);
            }

            if (binaryCheck != null && !sqlType.isFullTextIndexable()) {
                binaryCheck.setEnabled(false);
            }

            if (argsText != null) {
                if (sqlType.doesNeedArgs()) {
                    argsText.setEnabled(true);
                } else {
                    argsText.setEnabled(false);
                }
            }

        } else {
            lengthText.setEnabled(false);
            decimalText.setEnabled(false);
            if (unsignedCheck != null) {
                unsignedCheck.setEnabled(false);
            }
            if (zerofillCheck != null) {
                zerofillCheck.setEnabled(false);
            }
            if (binaryCheck != null) {
                binaryCheck.setEnabled(false);
            }
            if (argsText != null) {
                argsText.setEnabled(false);
            }
        }

        lengthText.setText(Format.toString(typeData.getLength()));
        decimalText.setText(Format.toString(typeData.getDecimal()));

        if (arrayDimensionText != null) {
            arrayCheck.setSelection(typeData.isArray());
            arrayDimensionText.setText(Format.toString(typeData.getArrayDimension()));
            arrayDimensionText.setEnabled(arrayCheck.getSelection());
        }

        if (unsignedCheck != null) {
            unsignedCheck.setSelection(typeData.isUnsigned());
        }

        if (zerofillCheck != null) {
            zerofillCheck.setSelection(typeData.isZerofill());
        }

        if (binaryCheck != null) {
            binaryCheck.setSelection(typeData.isBinary());
        }

        if (argsText != null) {
            argsText.setText(Format.null2blank(typeData.getArgs()));
        }

        descriptionText.setText(Format.toString(description));

        if (byteSemanticsRadio != null) {
            final boolean charSemantics = typeData.isCharSemantics();
            byteSemanticsRadio.setSelection(!charSemantics);
            charSemanticsRadio.setSelection(charSemantics);
        }
    }

    protected SqlType getSelectedType() {
        final String database = diagram.getDatabase();

        final SqlType selectedType = SqlType.valueOf(database, typeCombo.getText());

        return selectedType;
    }

    protected void setEnabledBySqlType() {
        final String database = diagram.getDatabase();

        final SqlType selectedType = SqlType.valueOf(database, typeCombo.getText());

        if (selectedType != null) {
            if (!selectedType.isNeedLength(database)) {
                lengthText.setEnabled(false);
            } else {
                lengthText.setEnabled(true);
            }

            if (!selectedType.isNeedDecimal(database)) {
                decimalText.setEnabled(false);
            } else {
                decimalText.setEnabled(true);
            }

            if (unsignedCheck != null) {
                if (!selectedType.isNumber()) {
                    unsignedCheck.setEnabled(false);
                } else {
                    unsignedCheck.setEnabled(true);
                }
            }

            if (zerofillCheck != null) {
                if (!selectedType.isNumber()) {
                    zerofillCheck.setEnabled(false);
                } else {
                    zerofillCheck.setEnabled(true);
                }
            }

            if (binaryCheck != null) {
                if (!selectedType.isFullTextIndexable()) {
                    binaryCheck.setEnabled(false);
                } else {
                    binaryCheck.setEnabled(true);
                }
            }

            if (argsText != null) {
                if (selectedType.doesNeedArgs()) {
                    argsText.setEnabled(true);
                } else {
                    argsText.setEnabled(false);
                }
            }

            if (charSemanticsRadio != null) {
                if (selectedType.isNeedCharSemantics(database)) {
                    byteSemanticsRadio.setEnabled(true);
                    charSemanticsRadio.setEnabled(true);

                } else {
                    byteSemanticsRadio.setEnabled(false);
                    charSemanticsRadio.setEnabled(false);
                }
            }
        }
    }

    @Override
    protected void addListener() {
        super.addListener();

        typeCombo.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent event) {
                setEnabledBySqlType();
            }

        });

        physicalNameText.addFocusListener(new FocusAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void focusLost(final FocusEvent e) {
                if (logicalNameText.getText().equals("")) {
                    logicalNameText.setText(physicalNameText.getText());
                }
            }
        });

        physicalNameText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                final String logicalName = logicalNameText.getText();
                final String physicalName = physicalNameText.getText();

                if (oldPhysicalName.equals(logicalName) || logicalName.equals("")) {
                    logicalNameText.setText(physicalName);
                    oldPhysicalName = physicalName;
                }
            }
        });

        if (zerofillCheck != null) {
            zerofillCheck.addSelectionListener(new SelectionAdapter() {

                /**
                 * {@inheritDoc}
                 */
                @Override
                public void widgetSelected(final SelectionEvent e) {
                    if (zerofillCheck.getSelection()) {
                        unsignedCheck.setSelection(true);
                        unsignedCheck.setEnabled(false);

                    } else {
                        unsignedCheck.setSelection(false);
                        unsignedCheck.setEnabled(true);
                    }
                }
            });
        }

    }

    abstract protected void setWordData();

    private void initializeTypeCombo() {
        typeCombo.add("");

        final String database = diagram.getDatabase();

        for (final String alias : SqlType.getAliasList(database)) {
            typeCombo.add(alias);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getErrorMessage() {
        String text = physicalNameText.getText().trim();
        if (!Check.isAlphabet(text)) {
            if (diagram.getDiagramContents().getSettings().isValidatePhysicalName()) {
                return "error.column.physical.name.not.alphabet";
            }
        }

        final String logicalName = logicalNameText.getText().trim();
        if (Check.isEmpty(text) && Check.isEmpty(logicalName)) {
            return "error.column.name.empty";
        }

        if (lengthText.isEnabled()) {

            text = lengthText.getText();

            if (text.equals("")) {
                return "error.column.length.empty";

            } else {
                try {
                    final int len = Integer.parseInt(text);
                    if (len < 0) {
                        return "error.column.length.zero";
                    }

                } catch (final NumberFormatException e) {
                    return "error.column.length.degit";
                }
            }
        }

        if (decimalText.isEnabled()) {

            text = decimalText.getText();

            if (text.equals("")) {
                return "error.column.decimal.empty";

            } else {
                try {
                    final int len = Integer.parseInt(text);
                    if (len < 0) {
                        return "error.column.decimal.zero";
                    }

                } catch (final NumberFormatException e) {
                    return "error.column.decimal.degit";
                }
            }
        }

        if (arrayDimensionText != null) {
            text = arrayDimensionText.getText();

            if (!text.equals("")) {
                try {
                    final int len = Integer.parseInt(text);
                    if (len < 1) {
                        return "error.column.array.dimension.one";
                    }

                } catch (final NumberFormatException e) {
                    return "error.column.array.dimension.degit";
                }

            } else {
                if (arrayCheck.getSelection()) {
                    return "error.column.array.dimension.one";
                }
            }
        }

        final SqlType selectedType = SqlType.valueOf(diagram.getDatabase(), typeCombo.getText());

        if (selectedType != null && argsText != null) {
            text = argsText.getText();

            if (selectedType.doesNeedArgs()) {
                if (text.equals("")) {
                    return "error.column.type.enum.set";
                }
            }
        }

        return null;
    }

}
