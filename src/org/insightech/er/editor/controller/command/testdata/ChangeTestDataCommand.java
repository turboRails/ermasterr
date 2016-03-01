package org.insightech.er.editor.controller.command.testdata;

import java.util.List;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.testdata.TestData;

public class ChangeTestDataCommand extends AbstractCommand {

    private final ERDiagram diagram;

    private final List<TestData> oldTestDataList;

    private final List<TestData> newTestDataList;

    public ChangeTestDataCommand(final ERDiagram diagram, final List<TestData> newTestDataList) {
        this.diagram = diagram;
        oldTestDataList = diagram.getDiagramContents().getTestDataList();
        this.newTestDataList = newTestDataList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        diagram.getDiagramContents().setTestDataList(newTestDataList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        diagram.getDiagramContents().setTestDataList(oldTestDataList);
    }

}
