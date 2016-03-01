package org.insightech.er.editor.view.dialog.element.view.tab;

import org.eclipse.swt.widgets.TabFolder;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.view.dialog.element.table_view.tab.AbstractAttributeTabWrapper;
import org.insightech.er.editor.view.dialog.element.view.ViewDialog;
import org.insightech.er.editor.view.dialog.word.column.AbstractColumnDialog;
import org.insightech.er.editor.view.dialog.word.column.ViewColumnDialog;

public class AttributeTabWrapper extends AbstractAttributeTabWrapper {

    private final View copyData;

    public AttributeTabWrapper(final ViewDialog viewDialog, final TabFolder parent, final View copyData) {
        super(viewDialog, parent, copyData);

        this.copyData = copyData;
    }

    @Override
    protected AbstractColumnDialog createColumnDialog() {
        return new ViewColumnDialog(getShell(), copyData);
    }

    @Override
    protected String getGroupAddButtonLabel() {
        return "label.button.add.group.to.view";
    }
}
