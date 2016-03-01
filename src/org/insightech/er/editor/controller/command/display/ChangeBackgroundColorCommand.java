package org.insightech.er.editor.controller.command.display;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ViewableModel;

public class ChangeBackgroundColorCommand extends AbstractCommand {

    private final ViewableModel model;

    private final int red;

    private final int green;

    private final int blue;

    private int[] oldColor;

    public ChangeBackgroundColorCommand(final ViewableModel model, final int red, final int green, final int blue) {
        this.model = model;

        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        oldColor = model.getColor();

        model.setColor(red, green, blue);

        model.refreshVisuals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        if (oldColor == null) {
            oldColor = new int[3];
            oldColor[0] = 255;
            oldColor[1] = 255;
            oldColor[2] = 255;
        }

        model.setColor(oldColor[0], oldColor[1], oldColor[2]);

        model.refreshVisuals();
    }
}
