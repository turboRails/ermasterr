package org.insightech.er.editor.model.progress_monitor;

import org.eclipse.core.runtime.IProgressMonitor;

public class EclipseProgressMonitor implements ProgressMonitor {

    private final IProgressMonitor progressMonitor;

    private int totalCount;

    private int currentCount;

    public EclipseProgressMonitor(final IProgressMonitor progressMonitor) {
        this.progressMonitor = progressMonitor;
    }

    @Override
    public void beginTask(final String message, final int totalCount) {
        this.totalCount = totalCount;
        progressMonitor.beginTask(message, totalCount);
    }

    @Override
    public void worked(final int count) throws InterruptedException {
        currentCount += count;

        progressMonitor.worked(count);

        if (isCanceled()) {
            throw new InterruptedException("Cancel has been requested.");
        }
    }

    @Override
    public boolean isCanceled() {
        return progressMonitor.isCanceled();
    }

    @Override
    public void done() {
        progressMonitor.done();
    }

    @Override
    public void subTask(final String message) {
        progressMonitor.subTask(message);
    }

    @Override
    public void subTaskWithCounter(final String message) {
        subTask("(" + getCurrentCount() + "/" + getTotalCount() + ") " + message);
    }

    @Override
    public int getTotalCount() {
        return totalCount;
    }

    @Override
    public int getCurrentCount() {
        return currentCount;
    }

}
