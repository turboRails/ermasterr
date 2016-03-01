package org.insightech.er.editor.view.action.printer;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.print.PrintGraphicalViewerOperation;
import org.eclipse.gef.ui.actions.PrintAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.insightech.er.editor.ERDiagramEditor;

public class PrintImageAction extends PrintAction {

    public PrintImageAction(final ERDiagramEditor part) {
        super(part);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        GraphicalViewer viewer;
        viewer = getWorkbenchPart().getAdapter(GraphicalViewer.class);

        final PrintDialog dialog = new PrintDialog(viewer.getControl().getShell(), SWT.NULL);
        final PrinterData data = dialog.open();

        if (data != null) {
            final Printer printer = new Printer(data);
            final PrintGraphicalViewerOperation op = new PrintERDiagramOperation(printer, viewer);

            op.run(getWorkbenchPart().getTitle());
        }
    }

}
