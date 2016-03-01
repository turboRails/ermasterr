package org.insightech.er.common.dialog;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.ListenerAppender;

public abstract class AbstractTabbedDialog extends AbstractDialog {

    private TabFolder tabFolder;

    private List<ValidatableTabWrapper> tabWrapperList;

    public AbstractTabbedDialog(final Shell parentShell) {
        super(parentShell);
    }

    protected void createTabFolder(final Composite parent) {
        final GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.verticalAlignment = GridData.FILL;
        gridData.horizontalAlignment = GridData.FILL;

        tabFolder = new TabFolder(parent, SWT.NONE);
        tabFolder.setLayoutData(gridData);

        tabWrapperList = createTabWrapperList(tabFolder);

        for (final ValidatableTabWrapper tab : tabWrapperList) {
            tab.init();
        }

        ListenerAppender.addTabListener(tabFolder, tabWrapperList);

        tabWrapperList.get(0).setInitFocus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getErrorMessage() {
        try {
            for (final ValidatableTabWrapper tabWrapper : tabWrapperList) {
                tabWrapper.validatePage();
            }

        } catch (final InputException e) {
            return e.getMessage();
        }

        return null;
    }

    @Override
    protected void perfomeOK() throws InputException {
        for (final ValidatableTabWrapper tab : tabWrapperList) {
            tab.perfomeOK();
        }
    }

    @Override
    protected void setData() {}

    protected abstract List<ValidatableTabWrapper> createTabWrapperList(TabFolder tabFolder);

    public void resetTabs() {
        for (final ValidatableTabWrapper tab : tabWrapperList) {
            // tab.setVisible(false);
            tab.reset();
            // tab.setVisible(true);
        }
    }

}
