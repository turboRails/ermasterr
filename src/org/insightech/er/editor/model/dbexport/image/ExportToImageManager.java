package org.insightech.er.editor.model.dbexport.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ResourceString;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.editor.controller.editpart.element.ERDiagramEditPartFactory;
import org.insightech.er.editor.controller.editpart.element.PagableFreeformRootEditPart;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.AbstractExportManager;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.progress_monitor.ProgressMonitor;
import org.insightech.er.editor.model.settings.export.ExportImageSetting;
import org.insightech.er.util.ImageUtils;
import org.insightech.er.util.io.FileUtils;

public class ExportToImageManager extends AbstractExportManager {

    private final ExportImageSetting exportImageSetting;

    public ExportToImageManager(final ExportImageSetting exportImageSetting) {
        super("dialog.message.export.image");

        this.exportImageSetting = exportImageSetting;
    }

    public static ImageInfoSet outputImage(final ERDiagram diagram, final Category category, final File projectDir, final ProgressMonitor monitor) throws Exception {
        final ExportImageSetting exportImageSetting = new ExportImageSetting();
        exportImageSetting.setOutputFilePath(null);
        exportImageSetting.setWithCategoryImage(false);
        exportImageSetting.setCategory(category);

        final ExportToImageManager imageManager = new ExportToImageManager(exportImageSetting);
        imageManager.init(diagram, projectDir);

        return imageManager.createImageInfoSet(monitor);
    }

    public static ImageInfoSet outputImage(final File outputDir, final ProgressMonitor monitor, final ERDiagram diagram, final boolean withCategoryImage) throws Exception {
        final ExportImageSetting exportImageSetting = new ExportImageSetting();
        exportImageSetting.setOutputFilePath("er.png");
        exportImageSetting.setWithCategoryImage(withCategoryImage);

        final ExportToImageManager imageManager = new ExportToImageManager(exportImageSetting);
        imageManager.init(diagram, outputDir);

        return imageManager.createImageInfoSet(monitor);
    }

    public static int countTask(final ERDiagram diagram, final boolean withCategoryImage, final boolean outputToFile) {
        int imageNum = 1;

        if (withCategoryImage) {
            imageNum += diagram.getDiagramContents().getSettings().getCategorySetting().getSelectedCategories().size();
        }

        if (outputToFile) {
            return imageNum * 2;

        } else {
            return imageNum;
        }
    }

    @Override
    protected int getTotalTaskCount() {
        return countTask(diagram, exportImageSetting.isWithCategoryImage(), true);
    }

    @Override
    protected void doProcess(final ProgressMonitor monitor) throws Exception {
        createImageInfoSet(monitor);
    }

    private ImageInfoSet createImageInfoSet(final ProgressMonitor monitor) throws Exception {
        final String mainFileName = exportImageSetting.getOutputFilePath();

        final int format = ImageUtils.getFormatType(mainFileName);

        if (mainFileName != null && format == -1) {
            throw new InputException("dialog.message.export.image.not.supported");
        }

        ImageInfoSet imageInfoSet = null;
        final Display display = ERDiagramActivator.getDisplay();

        try {
            final ImageInfo diagramImageInfo = this.outputImage(monitor, display, exportImageSetting.getCategory(), format, exportImageSetting.getOutputFilePath());
            imageInfoSet = new ImageInfoSet(diagramImageInfo);

            if (exportImageSetting.isWithCategoryImage()) {
                for (final Category category : categoryList) {
                    final ImageInfo imageInfo = this.outputImage(monitor, display, category, format, getFileNameForCategoryImage(imageInfoSet, category));

                    imageInfoSet.addImageInfo(category, imageInfo);
                }
            }

        } catch (final Exception e) {
            if (e.getCause() instanceof OutOfMemoryError) {
                throw new InputException("dialog.message.export.image.out.of.memory");
            }

            throw e;
        }

        return imageInfoSet;
    }

    private ImageInfo outputImage(final ProgressMonitor monitor, final Display display, final Category category, final int format, final String fileName) throws Exception {

        GraphicalViewer viewer = null;
        ImageInfo imageInfo = null;

        try {
            String name = "All";
            if (category != null) {
                name = "category - " + category.getName();
            }
            monitor.subTaskWithCounter(ResourceString.getResourceString("dialog.message.export.image.creating") + " : " + name);

            viewer = createGraphicalViewer(display, diagram);
            imageInfo = createImage(display, viewer, format, fileName, diagram, category);

            monitor.worked(1);

            if (fileName != null) {
                monitor.subTaskWithCounter(ResourceString.getResourceString("dialog.message.export.image.output") + " : " + fileName);

                writeToFile(imageInfo);
                monitor.worked(1);

            } else {
                imageInfo.toImageData();
            }

            return imageInfo;

        } finally {
            if (imageInfo != null) {
                imageInfo.dispose();
            }
            if (viewer != null && viewer.getContents() != null) {
                viewer.getContents().deactivate();
            }
        }

    }

    private static GraphicalViewer createGraphicalViewer(final Display display, final ERDiagram diagram) {

        final GraphicalViewer[] viewerHolder = new GraphicalViewer[1];

        display.syncExec(new Runnable() {

            @Override
            public void run() {
                final Shell shell = new Shell(display);
                shell.setLayout(new GridLayout(1, false));

                final ERDiagramEditPartFactory editPartFactory = new ERDiagramEditPartFactory();

                final GraphicalViewer viewer = new ScrollingGraphicalViewer();

                viewer.setControl(new FigureCanvas(shell));
                final ScalableFreeformRootEditPart rootEditPart = new PagableFreeformRootEditPart(diagram);
                viewer.setRootEditPart(rootEditPart);

                viewer.setEditPartFactory(editPartFactory);
                viewer.setContents(diagram);

                viewerHolder[0] = viewer;
            }

        });

        return viewerHolder[0];
    }

    private static ImageInfo createImage(final Display display, final GraphicalViewer viewer, final int format, final String path, final ERDiagram diagram, final Category category) throws InterruptedException {
        final Category currentCategory = diagram.getCurrentCategory();
        final int pageIndex = diagram.getPageIndex();

        try {
            diagram.setCurrentCategory(category, 0);

            final ImageInfo[] imageInfoHolder = new ImageInfo[1];

            display.syncExec(new Runnable() {

                @Override
                public void run() {
                    imageInfoHolder[0] = ImageInfo.createImage(viewer, format, path);
                }

            });

            return imageInfoHolder[0];

        } finally {
            diagram.setCurrentCategory(currentCategory, pageIndex);
        }

    }

    private void writeToFile(final ImageInfo imageInfo) throws IOException, InterruptedException {
        final Image img = imageInfo.getImage();
        final int format = imageInfo.getFormat();

        final File file = FileUtils.getFile(projectDir, imageInfo.getPath());
        file.getParentFile().mkdirs();

        // try {
        // ImageLoader imgLoader = new ImageLoader();
        // imgLoader.data = new ImageData[] { img.getImageData() };
        // imgLoader.save(file.getAbsolutePath(), format);
        //
        // } catch (SWTException e) {
        // if (format == SWT.IMAGE_PNG) {
        writePNGByAnotherWay(img, file.getAbsolutePath(), format);

        // } else {
        // throw e;
        // }
        // }
    }

    /*
     * Eclipse 3.2 では、 PNG が Unsupported or unrecognized format となるため、
     * 以下の代替方法を使用する ただし、この方法では上手く出力できない環境あり
     */
    private static void writePNGByAnotherWay(final Image image, final String saveFilePath, final int format) throws IOException, InterruptedException {

        // BufferedImage bufferedImage = new BufferedImage(
        // image.getBounds().width, image.getBounds().height,
        // BufferedImage.TYPE_INT_RGB);
        //
        // ImageUtils.drawAtBufferedImage(bufferedImage, image, 0, 0);

        final BufferedImage bufferedImage = ImageUtils.convertToBufferedImage(image);

        final String formatName = ImageUtils.toFormatName(format);

        ImageIO.write(bufferedImage, formatName, new File(saveFilePath));
    }

    @Override
    public File getOutputFileOrDir() {
        return FileUtils.getFile(projectDir, exportImageSetting.getOutputFilePath());
    }

    private String getFileNameForCategoryImage(final ImageInfoSet imageInfoSet, final Category category) {
        if (exportImageSetting.getOutputFilePath() == null) {
            return null;
        }

        String extension = "";

        final File mainFile = FileUtils.getFile(projectDir, exportImageSetting.getOutputFilePath());
        final String mainFilePath = mainFile.getAbsolutePath();

        final int index = mainFilePath.lastIndexOf(".");

        if (index != -1) {
            extension = mainFilePath.substring(index);
        }

        final File file = new File(mainFile.getParentFile(), "categories" + File.separator + imageInfoSet.decideFileName(category.getName(), extension));

        return FileUtils.getRelativeFilePath(projectDir, file.getAbsolutePath());
    }
}
