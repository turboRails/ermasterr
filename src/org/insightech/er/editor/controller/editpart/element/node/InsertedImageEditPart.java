package org.insightech.er.editor.controller.editpart.element.node;

import java.io.ByteArrayInputStream;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.EditPolicy;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.image.ChangeInsertedImagePropertyCommand;
import org.insightech.er.editor.controller.editpolicy.element.node.NodeElementComponentEditPolicy;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.image.InsertedImage;
import org.insightech.er.editor.view.dialog.element.InsertedImageDialog;
import org.insightech.er.editor.view.figure.InsertedImageFigure;

public class InsertedImageEditPart extends NodeElementEditPart implements IResizable {

    private Image image;

    private ImageData imageData;

    /**
     * {@inheritDoc}
     */
    @Override
    protected IFigure createFigure() {
        final InsertedImage model = (InsertedImage) getModel();

        final byte[] data = Base64.decodeBase64((model.getBase64EncodedData().getBytes()));
        final ByteArrayInputStream in = new ByteArrayInputStream(data);

        imageData = new ImageData(in);
        changeImage();

        final InsertedImageFigure figure = new InsertedImageFigure(image, model.isFixAspectRatio(), model.getAlpha());
        figure.setMinimumSize(new Dimension(1, 1));

        return figure;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deactivate() {
        super.deactivate();

        if (image != null && !image.isDisposed()) {
            image.dispose();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new NodeElementComponentEditPolicy());

        super.createEditPolicies();
    }

    @Override
    protected void doRefreshVisuals() {
        changeImage();

        final InsertedImageFigure figure = (InsertedImageFigure) getFigure();
        final InsertedImage model = (InsertedImage) getModel();

        figure.setImg(image, model.isFixAspectRatio(), model.getAlpha());
    }

    private void changeImage() {
        final InsertedImage model = (InsertedImage) getModel();

        final ImageData newImageData = new ImageData(imageData.width, imageData.height, imageData.depth, imageData.palette);

        for (int x = 0; x < imageData.width; x++) {
            for (int y = 0; y < imageData.height; y++) {
                final RGB rgb = imageData.palette.getRGB(imageData.getPixel(x, y));
                final float[] hsb = rgb.getHSB();

                if (model.getHue() != 0) {
                    hsb[0] = model.getHue() & 360;
                }

                hsb[1] = hsb[1] + (model.getSaturation() / 100f);
                if (hsb[1] > 1.0f) {
                    hsb[1] = 1.0f;
                } else if (hsb[1] < 0) {
                    hsb[1] = 0f;
                }

                hsb[2] = hsb[2] + (model.getBrightness() / 100f);
                if (hsb[2] > 1.0f) {
                    hsb[2] = 1.0f;

                } else if (hsb[2] < 0) {
                    hsb[2] = 0f;
                }

                final RGB newRGB = new RGB(hsb[0], hsb[1], hsb[2]);

                final int pixel = imageData.palette.getPixel(newRGB);

                newImageData.setPixel(x, y, pixel);
            }
        }

        if (image != null && !image.isDisposed()) {
            image.dispose();
        }

        image = new Image(Display.getDefault(), newImageData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performRequestOpen() {
        final InsertedImage insertedImage = (InsertedImage) getModel();

        final InsertedImage oldInsertedImage = (InsertedImage) insertedImage.clone();

        final ERDiagram diagram = getDiagram();

        final InsertedImageDialog dialog = new InsertedImageDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), insertedImage);

        if (dialog.open() == IDialogConstants.OK_ID) {
            final ChangeInsertedImagePropertyCommand command = new ChangeInsertedImagePropertyCommand(diagram, insertedImage, dialog.getNewInsertedImage(), oldInsertedImage);

            executeCommand(command);

        } else {
            final ChangeInsertedImagePropertyCommand command = new ChangeInsertedImagePropertyCommand(diagram, insertedImage, oldInsertedImage, oldInsertedImage);
            command.execute();

        }
    }
}
