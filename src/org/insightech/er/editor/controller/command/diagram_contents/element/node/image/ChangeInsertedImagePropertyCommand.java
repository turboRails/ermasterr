package org.insightech.er.editor.controller.command.diagram_contents.element.node.image;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.image.InsertedImage;

public class ChangeInsertedImagePropertyCommand extends AbstractCommand {

    protected InsertedImage insertedImage;

    protected InsertedImage oldInsertedImage;

    protected InsertedImage newInsertedImage;

    public ChangeInsertedImagePropertyCommand(final ERDiagram diagram, final InsertedImage insertedImage, final InsertedImage newInsertedImage, final InsertedImage oldInsertedImage) {
        this.insertedImage = insertedImage;
        this.oldInsertedImage = oldInsertedImage;
        this.newInsertedImage = newInsertedImage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        insertedImage.setHue(newInsertedImage.getHue());
        insertedImage.setSaturation(newInsertedImage.getSaturation());
        insertedImage.setBrightness(newInsertedImage.getBrightness());
        insertedImage.setFixAspectRatio(newInsertedImage.isFixAspectRatio());
        insertedImage.setAlpha(newInsertedImage.getAlpha());

        insertedImage.refresh();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        insertedImage.setHue(oldInsertedImage.getHue());
        insertedImage.setSaturation(oldInsertedImage.getSaturation());
        insertedImage.setBrightness(oldInsertedImage.getBrightness());
        insertedImage.setFixAspectRatio(oldInsertedImage.isFixAspectRatio());
        insertedImage.setAlpha(oldInsertedImage.getAlpha());

        insertedImage.refreshVisuals();
    }
}
