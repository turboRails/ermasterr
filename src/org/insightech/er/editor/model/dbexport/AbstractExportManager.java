package org.insightech.er.editor.model.dbexport;

import java.io.File;
import java.util.List;

import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.progress_monitor.ProgressMonitor;

public abstract class AbstractExportManager implements ExportWithProgressManager {

    protected ERDiagram diagram;

    protected List<Category> categoryList;

    protected File projectDir;

    private final String taskMessage;

    public AbstractExportManager(final String taskMessage) {
        this.taskMessage = taskMessage;
    }

    @Override
    public void init(final ERDiagram diagram, final File projectDir) throws Exception {
        this.diagram = diagram;
        this.diagram.getDiagramContents().sort();

        categoryList = this.diagram.getDiagramContents().getSettings().getCategorySetting().getSelectedCategories();

        this.projectDir = projectDir;
    }

    @Override
    public void run(final ProgressMonitor monitor) throws Exception {
        final int totalTaskCount = getTotalTaskCount();

        monitor.beginTask(ResourceString.getResourceString(taskMessage), totalTaskCount);

        doProcess(monitor);

        monitor.done();
    }

    protected abstract int getTotalTaskCount();

    protected abstract void doProcess(ProgressMonitor monitor) throws Exception;

}
