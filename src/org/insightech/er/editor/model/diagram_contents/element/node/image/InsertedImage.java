package org.insightech.er.editor.model.diagram_contents.element.node.image;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.codec.binary.Base64;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.util.Format;
import org.insightech.er.util.io.IOUtils;

public class InsertedImage extends NodeElement implements Comparable<InsertedImage> {

    private static final long serialVersionUID = -2035035973213266486L;

    private String base64EncodedData;

    private int hue;

    private int saturation;

    private int brightness;

    private int alpha;

    private boolean fixAspectRatio;

    public InsertedImage() {
        alpha = 255;
        fixAspectRatio = true;
    }

    public String getBase64EncodedData() {
        return base64EncodedData;
    }

    public void setBase64EncodedData(final String base64EncodedData) {
        this.base64EncodedData = base64EncodedData;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getObjectType() {
        return "image";
    }

    public void setImageFilePath(final String imageFilePath) {
        InputStream in = null;

        try {
            in = new BufferedInputStream(new FileInputStream(imageFilePath));

            final byte[] data = IOUtils.toByteArray(in);

            final String encodedData = new String(Base64.encodeBase64(data));
            setBase64EncodedData(encodedData);

        } catch (final Exception e) {
            ERDiagramActivator.showExceptionDialog(e);

        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (final Exception e) {
                    ERDiagramActivator.showExceptionDialog(e);
                }
            }
        }
    }

    public int getHue() {
        return hue;
    }

    public void setHue(final int hue) {
        this.hue = hue;
    }

    public int getSaturation() {
        return saturation;
    }

    public void setSaturation(final int saturation) {
        this.saturation = saturation;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(final int brightness) {
        this.brightness = brightness;
    }

    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(final int alpha) {
        this.alpha = alpha;
    }

    public boolean isFixAspectRatio() {
        return fixAspectRatio;
    }

    public void setFixAspectRatio(final boolean fixAspectRatio) {
        this.fixAspectRatio = fixAspectRatio;
    }

    public void setDirty() {}

    @Override
    public int compareTo(final InsertedImage other) {
        int compareTo = 0;

        compareTo = Format.null2blank(base64EncodedData).compareTo(Format.null2blank(other.base64EncodedData));

        return compareTo;
    }
}
