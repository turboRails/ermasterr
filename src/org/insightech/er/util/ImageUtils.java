package org.insightech.er.util;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;

public class ImageUtils {

    public static int getFormatType(final String saveFilePath) {
        int format = -1;

        if (saveFilePath == null) {
            return format;
        }

        final int index = saveFilePath.lastIndexOf(".");
        String ext = null;
        if (index != -1 && index != saveFilePath.length() - 1) {
            ext = saveFilePath.substring(index + 1, saveFilePath.length());
        } else {
            ext = "";
        }

        if (ext.equalsIgnoreCase("jpeg") || ext.equalsIgnoreCase("jpg")) {
            format = SWT.IMAGE_JPEG;

        } else if (ext.equalsIgnoreCase("bmp")) {
            format = SWT.IMAGE_BMP;

        } else if (ext.equalsIgnoreCase("png")) {
            format = SWT.IMAGE_PNG;

        }

        return format;
    }

    public static String toFormatName(final int format) {
        if (SWT.IMAGE_JPEG == format) {
            return "jpg";

        } else if (SWT.IMAGE_BMP == format) {
            return "bmp";
        }

        return "png";
    }

    public static void drawAtBufferedImage(final BufferedImage bimg, final Image image, final int x, final int y) throws InterruptedException {

        final ImageData data = image.getImageData();

        for (int i = 0; i < image.getBounds().width; i++) {

            for (int j = 0; j < image.getBounds().height; j++) {
                final int tmp = 4 * (j * image.getBounds().width + i);

                if (data.data.length > tmp + 2) {
                    final int r = 0xff & data.data[tmp + 2];
                    final int g = 0xff & data.data[tmp + 1];
                    final int b = 0xff & data.data[tmp];

                    bimg.setRGB(i + x, j + y, 0xFF << 24 | r << 16 | g << 8 | b << 0);
                }
            }
        }
    }

    public static BufferedImage convertToBufferedImage(final Image image) {
        final ImageData data = image.getImageData();

        ColorModel colorModel = null;
        final PaletteData palette = data.palette;
        if (palette.isDirect) {
            colorModel = new DirectColorModel(data.depth, palette.redMask, palette.greenMask, palette.blueMask);
            final BufferedImage bufferedImage = new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(data.width, data.height), false, null);
            for (int y = 0; y < data.height; y++) {
                for (int x = 0; x < data.width; x++) {
                    final int pixel = data.getPixel(x, y);
                    final RGB rgb = palette.getRGB(pixel);
                    bufferedImage.setRGB(x, y, rgb.red << 16 | rgb.green << 8 | rgb.blue);
                }
            }
            return bufferedImage;
        } else {
            final RGB[] rgbs = palette.getRGBs();
            final byte[] red = new byte[rgbs.length];
            final byte[] green = new byte[rgbs.length];
            final byte[] blue = new byte[rgbs.length];
            for (int i = 0; i < rgbs.length; i++) {
                final RGB rgb = rgbs[i];
                red[i] = (byte) rgb.red;
                green[i] = (byte) rgb.green;
                blue[i] = (byte) rgb.blue;
            }
            if (data.transparentPixel != -1) {
                colorModel = new IndexColorModel(data.depth, rgbs.length, red, green, blue, data.transparentPixel);
            } else {
                colorModel = new IndexColorModel(data.depth, rgbs.length, red, green, blue);
            }
            final BufferedImage bufferedImage = new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(data.width, data.height), false, null);
            final WritableRaster raster = bufferedImage.getRaster();
            final int[] pixelArray = new int[1];
            for (int y = 0; y < data.height; y++) {
                for (int x = 0; x < data.width; x++) {
                    final int pixel = data.getPixel(x, y);
                    pixelArray[0] = pixel;
                    raster.setPixel(x, y, pixelArray);
                }
            }
            return bufferedImage;
        }
    }
}
