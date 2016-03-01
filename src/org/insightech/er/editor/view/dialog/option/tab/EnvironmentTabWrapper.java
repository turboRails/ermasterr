package org.insightech.er.editor.view.dialog.option.tab;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.common.dialog.ValidatableTabWrapper;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.settings.Environment;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.dialog.option.OptionSettingDialog;
import org.insightech.er.util.Check;

public class EnvironmentTabWrapper extends ValidatableTabWrapper {

    private List environmentList;

    private Text nameText;

    private Button addButton;

    private Button editButton;

    private Button deleteButton;

    private final Settings settings;

    private static final int LIST_HEIGHT = 230;

    public EnvironmentTabWrapper(final OptionSettingDialog dialog, final TabFolder parent, final Settings settings) {
        super(dialog, parent, "label.tablespace.environment");

        this.settings = settings;
    }

    @Override
    protected void initLayout(final GridLayout layout) {
        super.initLayout(layout);
        layout.numColumns = 3;
    }

    @Override
    public void initComposite() {
        createEnvironmentGroup(this);

        nameText = CompositeFactory.createText(null, this, null, 3, true, false);

        addButton = CompositeFactory.createSmallButton(this, "label.button.add");
        editButton = CompositeFactory.createSmallButton(this, "label.button.edit");
        deleteButton = CompositeFactory.createSmallButton(this, "label.button.delete");

        buttonEnabled(false);
        addButton.setEnabled(false);
    }

    private void createEnvironmentGroup(final Composite parent) {
        final GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalSpan = 3;
        gridData.heightHint = LIST_HEIGHT;

        environmentList = new List(parent, SWT.BORDER | SWT.V_SCROLL);
        environmentList.setLayoutData(gridData);
    }

    @Override
    protected void addListener() {
        environmentList.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final int targetIndex = environmentList.getSelectionIndex();
                if (targetIndex == -1) {
                    return;
                }

                final Environment environment = settings.getEnvironmentSetting().getEnvironments().get(targetIndex);
                nameText.setText(environment.getName());
                buttonEnabled(true);
            }
        });

        addButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final String name = nameText.getText().trim();
                if (!Check.isEmpty(name)) {
                    settings.getEnvironmentSetting().getEnvironments().add(new Environment(name));
                    setData();
                    environmentList.select(environmentList.getItemCount() - 1);
                }
            }
        });

        editButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final int targetIndex = environmentList.getSelectionIndex();
                if (targetIndex == -1) {
                    return;
                }

                final String name = nameText.getText().trim();
                if (!Check.isEmpty(name)) {
                    final Environment environment = settings.getEnvironmentSetting().getEnvironments().get(targetIndex);
                    environment.setName(name);
                    setData();
                    environmentList.select(targetIndex);
                }
            }
        });

        deleteButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final int targetIndex = environmentList.getSelectionIndex();
                if (targetIndex == -1) {
                    return;
                }

                settings.getEnvironmentSetting().getEnvironments().remove(targetIndex);
                setData();

                if (settings.getEnvironmentSetting().getEnvironments().size() > targetIndex) {
                    environmentList.select(targetIndex);
                    final Environment environment = settings.getEnvironmentSetting().getEnvironments().get(targetIndex);
                    nameText.setText(environment.getName());

                } else {
                    nameText.setText("");
                    buttonEnabled(false);
                }
            }
        });

        nameText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(final ModifyEvent e) {
                final String name = nameText.getText().trim();
                if (name.length() == 0) {
                    addButton.setEnabled(false);
                    editButton.setEnabled(false);

                } else {
                    addButton.setEnabled(true);
                    if (environmentList.getSelectionIndex() != -1) {
                        editButton.setEnabled(true);
                    } else {
                        editButton.setEnabled(false);
                    }
                }
            }
        });
    }

    private void buttonEnabled(boolean enabled) {
        editButton.setEnabled(enabled);

        if (environmentList.getItemCount() <= 1) {
            enabled = false;
        }
        deleteButton.setEnabled(enabled);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validatePage() throws InputException {}

    @Override
    public void setInitFocus() {
        environmentList.setFocus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setData() {
        super.setData();

        environmentList.removeAll();

        for (final Environment environment : settings.getEnvironmentSetting().getEnvironments()) {
            environmentList.add(environment.getName());
        }
    }

    @Override
    public void perfomeOK() {}

}
