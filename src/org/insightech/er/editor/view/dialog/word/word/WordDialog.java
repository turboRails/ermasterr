package org.insightech.er.editor.view.dialog.word.word;

import org.eclipse.swt.widgets.Shell;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.TypeData;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.view.dialog.word.AbstractWordDialog;

public class WordDialog extends AbstractWordDialog {

    private final Word targetWord;

    private Word returnWord;

    public WordDialog(final Shell parentShell, final Word targetWord, final boolean add, final ERDiagram diagram) {
        super(parentShell, diagram);

        this.targetWord = targetWord;
    }

    @Override
    protected void setWordData() {
        this.setData(targetWord.getPhysicalName(), targetWord.getLogicalName(), targetWord.getType(), targetWord.getTypeData(), targetWord.getDescription());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getErrorMessage() {
        final String text = logicalNameText.getText().trim();
        if (text.equals("")) {
            return "error.column.logical.name.empty";
        }

        return super.getErrorMessage();
    }

    @Override
    protected void perfomeOK() {
        String text = lengthText.getText();
        Integer length = null;
        if (!text.equals("")) {
            final int len = Integer.parseInt(text);
            length = new Integer(len);
        }

        text = decimalText.getText();

        Integer decimal = null;
        if (!text.equals("")) {
            final int len = Integer.parseInt(text);
            decimal = new Integer(len);
        }

        boolean array = false;
        Integer arrayDimension = null;

        if (arrayDimensionText != null) {
            text = arrayDimensionText.getText();

            if (!text.equals("")) {
                final int len = Integer.parseInt(text);
                arrayDimension = new Integer(len);
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

        text = physicalNameText.getText();

        final String database = diagram.getDatabase();

        final SqlType selectedType = SqlType.valueOf(database, typeCombo.getText());

        String args = null;
        if (argsText != null) {
            args = argsText.getText();
        }

        final TypeData typeData = new TypeData(length, decimal, array, arrayDimension, unsigned, zerofill, binary, args, charSemantics);

        returnWord = new Word(physicalNameText.getText(), logicalNameText.getText(), selectedType, typeData, descriptionText.getText(), database);
    }

    public Word getWord() {
        return returnWord;
    }

    @Override
    protected String getTitle() {
        return "dialog.title.word";
    }

}
