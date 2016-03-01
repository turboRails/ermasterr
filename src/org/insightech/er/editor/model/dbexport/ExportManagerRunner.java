package org.insightech.er.editor.model.dbexport;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.insightech.er.editor.model.progress_monitor.EclipseProgressMonitor;

public class ExportManagerRunner implements IRunnableWithProgress {

    private final ExportWithProgressManager exportManager;

    private Exception exception;

    public ExportManagerRunner(final ExportWithProgressManager exportManager) {
        this.exportManager = exportManager;
    }

    @Override
    public void run(final IProgressMonitor monitor) {
        try {
            exportManager.run(new EclipseProgressMonitor(monitor));

        } catch (final Exception e) {
            exception = e;
        }
    }

    public Exception getException() {
        return exception;
    }

}
