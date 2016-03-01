package org.insightech.er.editor.controller.command.diagram_contents.not_element.tablespace;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceSet;

public class EditTablespaceCommand extends AbstractCommand {

    private final TablespaceSet tablespaceSet;

    private final Tablespace tablespace;

    private final Tablespace oldTablespace;

    private final Tablespace newTablespace;

    public EditTablespaceCommand(final ERDiagram diagram, final Tablespace tablespace, final Tablespace newTablespace) {
        tablespaceSet = diagram.getDiagramContents().getTablespaceSet();
        this.tablespace = tablespace;
        oldTablespace = this.tablespace.clone();
        this.newTablespace = newTablespace;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        newTablespace.copyTo(tablespace);
        tablespaceSet.refresh();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        oldTablespace.copyTo(tablespace);
        tablespaceSet.refresh();
    }
}
