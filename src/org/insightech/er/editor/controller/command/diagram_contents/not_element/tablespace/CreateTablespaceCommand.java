package org.insightech.er.editor.controller.command.diagram_contents.not_element.tablespace;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceSet;

public class CreateTablespaceCommand extends AbstractCommand {

    private final TablespaceSet tablespaceSet;

    private final Tablespace tablespace;

    public CreateTablespaceCommand(final ERDiagram diagram, final Tablespace tablespace) {
        tablespaceSet = diagram.getDiagramContents().getTablespaceSet();
        this.tablespace = tablespace;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        tablespaceSet.addObject(tablespace);
        tablespaceSet.refresh();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        tablespaceSet.remove(tablespace);
        tablespaceSet.refresh();
    }
}
