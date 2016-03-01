package org.insightech.er.editor.controller.command.diagram_contents.element.node.table_view;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;

public class AddWordCommand extends AbstractCommand {

    private final TableView tableView;

    private final Dictionary dictionary;

    private final Word word;

    private final NormalColumn column;

    private final int index;

    public AddWordCommand(final TableView tableView, final Word word, final int index) {
        this.tableView = tableView;
        this.word = word;
        this.index = index;

        dictionary = this.tableView.getDiagram().getDiagramContents().getDictionary();

        column = new NormalColumn(this.word, true, false, false, false, null, null, null, null, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        tableView.addColumn(index, column);
        dictionary.add(column);

        tableView.refresh();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        tableView.removeColumn(column);
        dictionary.remove(column);

        tableView.refresh();
    }

}
