package org.insightech.er.editor.controller.command.settings;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.Settings;

public class ChangeSettingsCommand extends AbstractCommand {

    private final ERDiagram diagram;

    private final Settings oldSettings;

    private final Settings settings;

    private final boolean needRefresh;

    public ChangeSettingsCommand(final ERDiagram diagram, final Settings settings, final boolean needRefresh) {
        this.diagram = diagram;
        oldSettings = this.diagram.getDiagramContents().getSettings();
        this.settings = settings;
        this.needRefresh = needRefresh;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        diagram.setSettings(settings);

        if (needRefresh) {
            diagram.refreshSettings();
            diagram.getEditor().refreshPropertySheet();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        diagram.setSettings(oldSettings);

        if (needRefresh) {
            diagram.refreshSettings();
            diagram.getEditor().refreshPropertySheet();
        }
    }

}
