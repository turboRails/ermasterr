package org.insightech.er.editor.view.dialog.element.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.insightech.er.common.dialog.ValidatableTabWrapper;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.view.dialog.element.table_view.TableViewDialog;
import org.insightech.er.editor.view.dialog.element.table_view.tab.AdvancedTabWrapper;
import org.insightech.er.editor.view.dialog.element.table_view.tab.DescriptionTabWrapper;
import org.insightech.er.editor.view.dialog.element.view.tab.AttributeTabWrapper;
import org.insightech.er.editor.view.dialog.element.view.tab.SqlTabWrapper;

public class ViewDialog extends TableViewDialog {

    private final View copyData;

    public ViewDialog(final Shell parentShell, final EditPartViewer viewer, final View copyData) {
        super(parentShell, viewer);

        this.copyData = copyData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTitle() {
        return "dialog.title.view";
    }

    @Override
    protected List<ValidatableTabWrapper> createTabWrapperList(final TabFolder tabFolder) {
        final List<ValidatableTabWrapper> list = new ArrayList<ValidatableTabWrapper>();

        list.add(new AttributeTabWrapper(this, tabFolder, copyData));
        list.add(new SqlTabWrapper(this, tabFolder, copyData));
        list.add(new DescriptionTabWrapper(this, tabFolder, copyData));
        list.add(new AdvancedTabWrapper(this, tabFolder, copyData));

        return list;
    }
}
