package org.insightech.er.editor.controller.command.edit;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.search.ReplaceManager;
import org.insightech.er.editor.model.search.ReplaceResult;

public class ReplaceCommand extends AbstractCommand {

    private final int type;

    private final Object object;

    private final String keyword;

    private final String replaceWord;

    private ReplaceResult result;

    private final ERDiagram diagram;

    public ReplaceCommand(final ERDiagram diagram, final int type, final Object object, final String keyword, final String replaceWord) {
        this.diagram = diagram;

        this.type = type;
        this.object = object;
        this.keyword = keyword;
        this.replaceWord = replaceWord;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        result = ReplaceManager.replace(type, object, keyword, replaceWord, diagram.getDatabase());

        diagram.refreshVisuals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        if (result != null) {
            ReplaceManager.undo(type, object, result.getOriginal(), diagram.getDatabase());

            diagram.refreshVisuals();
        }
    }

}
