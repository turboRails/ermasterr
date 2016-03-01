package org.insightech.er.editor.controller.command.display;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ViewableModel;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;

public class ChangeFontCommand extends AbstractCommand {

    private final ViewableModel viewableModel;

    private final String oldFontName;

    private final String newFontName;

    private final int oldFontSize;

    private final int newFontSize;

    public ChangeFontCommand(final ViewableModel viewableModel, final String fontName, final int fontSize) {
        this.viewableModel = viewableModel;

        oldFontName = viewableModel.getFontName();
        oldFontSize = viewableModel.getFontSize();

        newFontName = fontName;
        newFontSize = fontSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        viewableModel.setFontName(newFontName);
        viewableModel.setFontSize(newFontSize);

        viewableModel.refreshFont();

        if (viewableModel instanceof NodeElement) {
            // to expand categories including this element.
            ((NodeElement) viewableModel).refreshCategory();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        viewableModel.setFontName(oldFontName);
        viewableModel.setFontSize(oldFontSize);

        viewableModel.refreshFont();
    }
}
