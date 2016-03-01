package org.insightech.er.editor.controller.command.diagram_contents.not_element.dictionary;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;

public class EditWordCommand extends AbstractCommand {

    private final Word oldWord;

    private final Word word;

    private final Word newWord;

    private final ERDiagram diagram;

    private final Dictionary dictionary;

    public EditWordCommand(final Word word, final Word newWord, final ERDiagram diagram) {
        oldWord = new Word(word.getPhysicalName(), word.getLogicalName(), word.getType(), word.getTypeData().clone(), word.getDescription(), diagram.getDatabase());
        this.diagram = diagram;
        this.word = word;
        this.newWord = newWord;

        dictionary = this.diagram.getDiagramContents().getDictionary();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        dictionary.copyTo(newWord, word);
        diagram.refreshVisuals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        dictionary.copyTo(oldWord, word);
        diagram.refreshVisuals();
    }

}
