package org.insightech.er.editor.view.dialog.option.tab;

import org.eclipse.swt.widgets.TabFolder;
import org.insightech.er.common.dialog.ValidatableTabWrapper;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.db.EclipseDBManagerFactory;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.dialog.element.table_view.tab.AdvancedComposite;
import org.insightech.er.editor.view.dialog.option.OptionSettingDialog;

public class AdvancedTabWrapper extends ValidatableTabWrapper {

    private final Settings settings;

    private final ERDiagram diagram;

    private AdvancedComposite composite;

    public AdvancedTabWrapper(final OptionSettingDialog dialog, final TabFolder parent, final Settings settings, final ERDiagram diagram) {
        super(dialog, parent, "label.advanced.settings");

        this.diagram = diagram;
        this.settings = settings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validatePage() throws InputException {
        composite.validate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initComposite() {
        if (composite != null) {
            composite.dispose();
        }

        composite = EclipseDBManagerFactory.getEclipseDBManager(settings.getDatabase()).createAdvancedComposite(this);
        composite.initialize(dialog, settings.getTableViewProperties(), diagram, null);

        this.pack();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInitFocus() {
        composite.setInitFocus();
    }

    @Override
    public void reset() {
        init();
    }

    @Override
    public void perfomeOK() {}
}
