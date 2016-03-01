package org.insightech.er.editor.view.dialog.word.column.real;

import org.eclipse.swt.widgets.Shell;
import org.insightech.er.editor.model.ERDiagram;

public class GroupColumnDialog extends AbstractRealColumnDialog {

    public GroupColumnDialog(final Shell parentShell, final ERDiagram diagram) {
        super(parentShell, diagram);
    }

    protected int getStyle(final int style) {
        return style;
    }

    @Override
    protected String getTitle() {
        return "dialog.title.group.column";
    }

}
