package org.insightech.er.editor.model.progress_monitor;

public class EmptyProgressMonitor implements ProgressMonitor {

    public EmptyProgressMonitor() {}

    @Override
    public void beginTask(final String message, final int counter) {}

    @Override
    public void worked(final int counter) {}

    @Override
    public boolean isCanceled() {
        return false;
    }

    @Override
    public void done() {}

    @Override
    public void subTask(final String message) {}

    @Override
    public int getTotalCount() {
        return 0;
    }

    @Override
    public int getCurrentCount() {
        return 0;
    }

    @Override
    public void subTaskWithCounter(final String message) {
        // TODO Auto-generated method stub

    }

}
