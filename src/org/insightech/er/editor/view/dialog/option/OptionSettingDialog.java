package org.insightech.er.editor.view.dialog.option;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.insightech.er.common.dialog.AbstractTabbedDialog;
import org.insightech.er.common.dialog.ValidatableTabWrapper;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.dialog.option.tab.AdvancedTabWrapper;
import org.insightech.er.editor.view.dialog.option.tab.DBSelectTabWrapper;
import org.insightech.er.editor.view.dialog.option.tab.EnvironmentTabWrapper;
import org.insightech.er.editor.view.dialog.option.tab.OptionTabWrapper;

public class OptionSettingDialog extends AbstractTabbedDialog {

    private final Settings settings;

    private final ERDiagram diagram;

    public OptionSettingDialog(final Shell parentShell, final Settings settings, final ERDiagram diagram) {
        super(parentShell);

        this.diagram = diagram;
        this.settings = settings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize(final Composite composite) {
        createTabFolder(composite);
    }

    @Override
    protected String getTitle() {
        return "dialog.title.option";
    }

    @Override
    protected List<ValidatableTabWrapper> createTabWrapperList(final TabFolder tabFolder) {
        final List<ValidatableTabWrapper> list = new ArrayList<ValidatableTabWrapper>();

        list.add(new DBSelectTabWrapper(this, tabFolder, settings));
        list.add(new EnvironmentTabWrapper(this, tabFolder, settings));
        list.add(new AdvancedTabWrapper(this, tabFolder, settings, diagram));
        list.add(new OptionTabWrapper(this, tabFolder, settings));

        return list;
    }
}
