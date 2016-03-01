package org.insightech.er.editor.controller.command.diagram_contents.not_element.tablespace;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceSet;

public class DeleteTablespaceCommand extends AbstractCommand {

    private final TablespaceSet tablespaceSet;

    private final Tablespace tablespace;

    public DeleteTablespaceCommand(final ERDiagram diagram, final Tablespace tablespace) {
        tablespaceSet = diagram.getDiagramContents().getTablespaceSet();
        this.tablespace = tablespace;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        tablespaceSet.remove(tablespace);
        tablespaceSet.refresh();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        tablespaceSet.addObject(tablespace);
        tablespaceSet.refresh();
    }
}
