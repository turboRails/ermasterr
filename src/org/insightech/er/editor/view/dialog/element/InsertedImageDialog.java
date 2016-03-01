package org.insightech.er.editor.view.dialog.element;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.common.widgets.SpinnerWithScale;
import org.insightech.er.editor.model.diagram_contents.element.node.image.InsertedImage;

public class InsertedImageDialog extends AbstractDialog {

    private SpinnerWithScale hueSpinner;

    private SpinnerWithScale saturationSpinner;

    private SpinnerWithScale brightnessSpinner;

    private SpinnerWithScale alphaSpinner;

    private Button fixAspectRatioCheckbox;

    private final InsertedImage insertedImage;

    private InsertedImage newInsertedImage;

    public InsertedImageDialog(final Shell parentShell, final InsertedImage insertedImage) {
        super(parentShell);

        this.insertedImage = insertedImage;
    }

    @Override
    protected void initLayout(final GridLayout layout) {
        super.initLayout(layout);

        layout.numColumns = 4;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize(final Composite composite) {
        hueSpinner = CompositeFactory.createSpinnerWithScale(this, composite, "label.image.hue", "", 0, 360);
        // this.hueScale.setPageIncrement(10);

        saturationSpinner = CompositeFactory.createSpinnerWithScale(this, composite, "label.image.saturation", -100, 100);

        brightnessSpinner = CompositeFactory.createSpinnerWithScale(this, composite, "label.image.brightness", -100, 100);

        alphaSpinner = CompositeFactory.createSpinnerWithScale(this, composite, "label.image.alpha", 0, 255);

        CompositeFactory.fillLine(composite);

        fixAspectRatioCheckbox = CompositeFactory.createCheckbox(this, composite, "label.image.fix.aspect.ratio", false, 3);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getErrorMessage() {
        insertedImage.setHue(hueSpinner.getSelection());
        insertedImage.setSaturation(saturationSpinner.getSelection());
        insertedImage.setBrightness(brightnessSpinner.getSelection());
        insertedImage.setAlpha(alphaSpinner.getSelection());

        insertedImage.setFixAspectRatio(fixAspectRatioCheckbox.getSelection());

        insertedImage.setDirty();

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void perfomeOK() {
        newInsertedImage = new InsertedImage();
        newInsertedImage.setHue(hueSpinner.getSelection());
        newInsertedImage.setSaturation(saturationSpinner.getSelection());
        newInsertedImage.setBrightness(brightnessSpinner.getSelection());
        newInsertedImage.setAlpha(alphaSpinner.getSelection());
        newInsertedImage.setFixAspectRatio(fixAspectRatioCheckbox.getSelection());
    }

    @Override
    protected String getTitle() {
        return "dialog.title.image.information";
    }

    @Override
    protected void setData() {
        hueSpinner.setSelection(insertedImage.getHue());
        saturationSpinner.setSelection(insertedImage.getSaturation());
        brightnessSpinner.setSelection(insertedImage.getBrightness());
        alphaSpinner.setSelection(insertedImage.getAlpha());
        fixAspectRatioCheckbox.setSelection(insertedImage.isFixAspectRatio());
    }

    public InsertedImage getNewInsertedImage() {
        return newInsertedImage;
    }

}
