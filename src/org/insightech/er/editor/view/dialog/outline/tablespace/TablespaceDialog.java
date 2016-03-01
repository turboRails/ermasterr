package org.insightech.er.editor.view.dialog.outline.tablespace;

import java.util.List;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.Resources;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;
import org.insightech.er.editor.model.settings.Environment;
import org.insightech.er.util.Check;

public abstract class TablespaceDialog extends AbstractDialog {

    private Combo environmentCombo;

    private Text nameText;

    private Tablespace result;

    protected ERDiagram diagram;

    private Environment currentEnvironment;

    protected static final int NUM_TEXT_WIDTH = 60;

    public TablespaceDialog() {
        super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
    }

    public void init(final Tablespace tablespace, final ERDiagram diagram) {
        if (tablespace == null) {
            result = new Tablespace();

        } else {
            result = tablespace.clone();
        }

        this.diagram = diagram;
    }

    @Override
    protected void initialize(final Composite composite) {
        environmentCombo = CompositeFactory.createReadOnlyCombo(this, composite, "label.tablespace.environment", getNumColumns() - 1, -1);
        nameText = CompositeFactory.createText(this, composite, "label.tablespace.name", getNumColumns() - 1, Resources.DESCRIPTION_WIDTH, false, false);
    }

    @Override
    protected String getErrorMessage() {
        final String text = nameText.getText().trim();
        if (text.equals("")) {
            return "error.tablespace.name.empty";
        }

        if (!Check.isAlphabet(text)) {
            return "error.tablespace.name.not.alphabet";
        }

        return null;
    }

    @Override
    protected String getTitle() {
        return "dialog.title.tablespace";
    }

    @Override
    protected void perfomeOK() {
        result.setName(nameText.getText().trim());

        final TablespaceProperties tablespaceProperties = setTablespaceProperties();

        result.putProperties(currentEnvironment, tablespaceProperties);
    }

    protected abstract TablespaceProperties setTablespaceProperties();

    @Override
    protected void setData() {
        final List<Environment> environmentList = diagram.getDiagramContents().getSettings().getEnvironmentSetting().getEnvironments();

        for (final Environment environment : environmentList) {
            environmentCombo.add(environment.getName());
        }

        environmentCombo.select(0);
        currentEnvironment = environmentList.get(0);

        if (result.getName() != null) {
            nameText.setText(result.getName());
        }

        setPropertiesData();
    }

    private void setPropertiesData() {
        currentEnvironment = getSelectedEnvironment();

        final TablespaceProperties tablespaceProperties = result.getProperties(currentEnvironment, diagram);

        this.setData(tablespaceProperties);
    }

    protected abstract void setData(TablespaceProperties tablespaceProperties);

    public Tablespace getResult() {
        return result;
    }

    protected Environment getSelectedEnvironment() {
        final int index = environmentCombo.getSelectionIndex();

        final List<Environment> environmentList = diagram.getDiagramContents().getSettings().getEnvironmentSetting().getEnvironments();

        return environmentList.get(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addListener() {
        environmentCombo.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                perfomeOK();
                setPropertiesData();
            }

        });
    }

}
