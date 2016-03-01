package org.insightech.er.test;

import java.io.FileNotFoundException;

import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ImageTest {

    private static Shell shell = new Shell();

    private static Image image;

    private static ImageFigure imageFigure;

    public static void main(final String[] args) throws FileNotFoundException {
        main();
    }

    private static void main() throws FileNotFoundException {
        try {
            shell.setSize(100, 100);

            final LightweightSystem lws = new LightweightSystem(shell);

            final IFigure panel = new Figure();
            panel.setLayoutManager(new ToolbarLayout());

            initialize(panel);

            lws.setContents(panel);

            shell.open();

            final Display display = Display.getDefault();
            while (!shell.isDisposed()) {
                if (!display.readAndDispatch())
                    display.sleep();
            }

        } finally {
            if (image != null) {
                image.dispose();
            }
        }
    }

    private static void initialize(final IFigure parent) throws FileNotFoundException {
        parent.add(createContents());
    }

    private static Figure createContents() {
        final Figure contents = new Figure();
        final XYLayout layout = new XYLayout();
        contents.setLayoutManager(layout);

        final Button button = new Button("Hello World");
        layout.setConstraint(button, new Rectangle(0, 0, -1, -1));
        contents.add(button);

        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent actionevent) {
                setBrightness();
            }
        });

        final String path = "C:\\Users\\Public\\Pictures\\Sample Pictures\\Oryx Antelope.jpg";
        image = new Image(Display.getDefault(), path);
        imageFigure = new ImageFigure(image);

        layout.setConstraint(imageFigure, new Rectangle(0, 30, -1, -1));

        contents.add(imageFigure);

        return contents;
    }

    private static void setBrightness() {
        if (image.isDisposed()) {
            return;
        }

        final ImageData imageData = image.getImageData();

        for (int x = 0; x < imageData.width; x++) {
            for (int y = 0; y < imageData.height; y++) {
                final RGB rgb = imageData.palette.getRGB(imageData.getPixel(x, y));
                final float[] hsb = rgb.getHSB();

                hsb[2] = hsb[2] + 0.1f;
                if (hsb[2] > 1.0f) {
                    hsb[2] = 1.0f;
                }

//				hsb[1] = hsb[1] - 0.1f;
//				if (hsb[1] < 0.0f) {
//					hsb[1] = 0.0f;
//				}
                final RGB newRGB = new RGB(hsb[0], hsb[1], hsb[2]);

                final int pixel = imageData.palette.getPixel(newRGB);
                imageData.setPixel(x, y, pixel);
            }
        }

        image.dispose();
        image = new Image(Display.getDefault(), imageData);

        imageFigure.setImage(image);
    }
}
