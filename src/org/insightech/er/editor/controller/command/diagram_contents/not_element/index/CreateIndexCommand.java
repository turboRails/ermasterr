package org.insightech.er.editor.controller.command.diagram_contents.not_element.index;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.IndexSet;

public class CreateIndexCommand extends AbstractCommand {

    private final ERTable table;

    private final List<Index> oldIndexList;

    private final List<Index> newIndexList;

    private final IndexSet indexSet;

    public CreateIndexCommand(final ERDiagram diagram, final Index newIndex) {
        table = newIndex.getTable();
        indexSet = diagram.getDiagramContents().getIndexSet();

        oldIndexList = newIndex.getTable().getIndexes();
        newIndexList = new ArrayList<Index>(oldIndexList);

        newIndexList.add(newIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        table.setIndexes(newIndexList);
        indexSet.refresh();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        table.setIndexes(oldIndexList);
        indexSet.refresh();
    }
}
