package org.insightech.er.editor.view.dialog.word.column;

import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.ResourceString;
import org.insightech.er.Resources;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.CopyColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.CopyWord;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.TypeData;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.view.dialog.word.AbstractWordDialog;
import org.insightech.er.util.Format;

public abstract class AbstractColumnDialog extends AbstractWordDialog {

    protected Combo wordCombo;

    private Text wordFilterText;

    protected CopyColumn targetColumn;

    protected NormalColumn returnColumn;

    protected Word returnWord;

    private List<Word> wordList;

    protected boolean foreignKey;

    protected boolean isRefered;

    public AbstractColumnDialog(final Shell parentShell, final ERDiagram diagram) {
        super(parentShell, diagram);
    }

    public void setTargetColumn(final CopyColumn targetColumn, final boolean foreignKey, final boolean isRefered) {
        this.targetColumn = targetColumn;
        this.foreignKey = foreignKey;
        this.isRefered = isRefered;

        if (this.targetColumn == null) {
            setAdd(true);
        } else {
            setAdd(false);
        }
    }

    private void createWordFilter(final Composite composite) {
        final Composite filterComposite = new Composite(composite, SWT.NONE);

        final GridData gridData = new GridData();
        gridData.horizontalSpan = 4;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;

        filterComposite.setLayoutData(gridData);

        final GridLayout layout = new GridLayout();
        layout.numColumns = 2;

        filterComposite.setLayout(layout);

        final FontData fontData = Display.getCurrent().getSystemFont().getFontData()[0];
        final Font font = Resources.getFont(fontData.getName(), 7, SWT.NORMAL);

        final Label label = new Label(filterComposite, SWT.NONE);
        label.setText(ResourceString.getResourceString("label.filter"));
        label.setFont(font);

        final GridData textGridData = new GridData();
        textGridData.widthHint = 50;

        wordFilterText = new Text(filterComposite, SWT.BORDER);
        wordFilterText.setLayoutData(textGridData);
        wordFilterText.setFont(font);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initializeComposite(final Composite composite) {
        final int numColumns = getCompositeNumColumns();

        wordCombo = CompositeFactory.createReadOnlyCombo(null, composite, "label.word", numColumns - 1 - 4, WIDTH);
        createWordFilter(composite);
        wordCombo.setVisibleItemCount(20);

        wordCombo.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent event) {
                final int index = wordCombo.getSelectionIndex();
                if (index != 0) {
                    final Word word = wordList.get(index - 1);
                    setWordData(word);
                }

                validate();
                setEnabledBySqlType();
            }

        });

        super.initializeComposite(composite);
    }

    @Override
    protected void initData() {
        super.initData();

        initializeWordCombo(null);
    }

    private void setWordData(final Word word) {
        this.setData(word.getPhysicalName(), word.getLogicalName(), word.getType(), word.getTypeData(), word.getDescription());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setWordData() {
        this.setData(targetColumn.getPhysicalName(), targetColumn.getLogicalName(), targetColumn.getType(), targetColumn.getTypeData(), targetColumn.getDescription());

        setWordValue();
    }

    private void initializeWordCombo(final String filterString) {
        wordCombo.removeAll();

        wordCombo.add("");

        wordList = diagram.getDiagramContents().getDictionary().getWordList();

        for (final Iterator<Word> iter = wordList.iterator(); iter.hasNext();) {
            final Word word = iter.next();

            final String name = Format.null2blank(word.getLogicalName());

            if (filterString != null && name.indexOf(filterString) == -1) {
                iter.remove();

            } else {
                wordCombo.add(name);

            }
        }
    }

    private void setWordValue() {
        Word word = targetColumn.getWord();
        while (word instanceof CopyWord) {
            word = ((CopyWord) word).getOriginal();
        }

        if (word != null) {
            final int index = wordList.indexOf(word);

            wordCombo.select(index + 1);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void perfomeOK() {
        String text = lengthText.getText();
        Integer length = null;
        if (!text.equals("")) {
            final int len = Integer.parseInt(text);
            length = Integer.valueOf(len);
        }

        text = decimalText.getText();

        Integer decimal = null;
        if (!text.equals("")) {
            final int len = Integer.parseInt(text);
            decimal = Integer.valueOf(len);
        }

        boolean array = false;
        Integer arrayDimension = null;

        if (arrayDimensionText != null) {
            text = arrayDimensionText.getText();

            if (!text.equals("")) {
                final int len = Integer.parseInt(text);
                arrayDimension = Integer.valueOf(len);
            }

            array = arrayCheck.getSelection();
        }

        boolean unsigned = false;

        if (unsignedCheck != null) {
            unsigned = unsignedCheck.getSelection();
        }

        boolean zerofill = false;

        if (zerofillCheck != null) {
            zerofill = zerofillCheck.getSelection();
        }

        boolean binary = false;

        if (binaryCheck != null) {
            binary = binaryCheck.getSelection();
        }

        boolean charSemantics = false;

        if (charSemanticsRadio != null) {
            charSemantics = charSemanticsRadio.getSelection();
        }

        final String physicalName = physicalNameText.getText();
        final String logicalName = logicalNameText.getText();
        final String description = descriptionText.getText();
        String args = null;

        if (argsText != null) {
            args = argsText.getText();
        }

        final String database = diagram.getDatabase();

        final SqlType selectedType = SqlType.valueOf(database, typeCombo.getText());

        final TypeData typeData = new TypeData(length, decimal, array, arrayDimension, unsigned, zerofill, binary, args, charSemantics);

        final int wordIndex = wordCombo.getSelectionIndex();

        CopyWord word = null;
        if (wordIndex > 0) {
            word = new CopyWord(wordList.get(wordIndex - 1));

            if (!"".equals(physicalName)) {
                word.setPhysicalName(physicalName);
            }
            if (!"".equals(logicalName)) {
                word.setLogicalName(logicalName);
            }
            word.setDescription(description);

            word.setType(selectedType, typeData, database);

        } else {
            word = new CopyWord(new Word(physicalName, logicalName, selectedType, typeData, description, database));
        }

        returnWord = word;
    }

    public NormalColumn getColumn() {
        return returnColumn;
    }

    @Override
    protected void addListener() {
        super.addListener();

        wordFilterText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent modifyevent) {
                final String filterString = wordFilterText.getText();
                initializeWordCombo(filterString);
            }

        });
    }

}
