package org.insightech.er.editor.view.dialog.translation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.model.settings.TranslationSetting;

public class TranslationManageDialog extends AbstractDialog {

    private static final int BUTTON_WIDTH = 60;

    private Table dictionaryTable = null;

    private final TranslationSetting translationSettings;

    private Map<String, TableEditor> translationCheckMap;

    private Button useButton;

    private final List<String> allTranslations;

    public TranslationManageDialog(final Shell parentShell, final Settings settings, final ERDiagram diagram) {
        super(parentShell);

        translationSettings = settings.getTranslationSetting();
        allTranslations = translationSettings.getAllTranslations();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize(final Composite composite) {
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;

        final GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;

        final Group group = new Group(composite, SWT.NONE);
        group.setLayoutData(gridData);
        group.setText(ResourceString.getResourceString("label.translation.message"));
        group.setLayout(gridLayout);

        useButton = new Button(group, SWT.CHECK);
        useButton.setText(ResourceString.getResourceString("label.translation.use"));

        final GridData tableGridData = new GridData();
        tableGridData.heightHint = 200;
        tableGridData.horizontalSpan = 1;
        tableGridData.verticalSpan = 2;
        tableGridData.horizontalAlignment = GridData.FILL;
        tableGridData.grabExcessHorizontalSpace = true;

        dictionaryTable = new Table(group, SWT.BORDER | SWT.FULL_SELECTION);
        dictionaryTable.setHeaderVisible(true);
        dictionaryTable.setLayoutData(tableGridData);
        dictionaryTable.setLinesVisible(true);

        final GridData upButtonGridData = new GridData();
        upButtonGridData.grabExcessHorizontalSpace = false;
        upButtonGridData.verticalAlignment = GridData.END;
        upButtonGridData.grabExcessVerticalSpace = true;
        upButtonGridData.widthHint = BUTTON_WIDTH;

        final GridData downButtonGridData = new GridData();
        downButtonGridData.grabExcessVerticalSpace = true;
        downButtonGridData.verticalAlignment = GridData.BEGINNING;
        downButtonGridData.widthHint = BUTTON_WIDTH;

        final GridData textGridData = new GridData();
        textGridData.widthHint = 150;

        final TableColumn tableColumn = new TableColumn(dictionaryTable, SWT.NONE);
        tableColumn.setWidth(30);
        tableColumn.setResizable(false);
        final TableColumn tableColumn1 = new TableColumn(dictionaryTable, SWT.NONE);
        tableColumn1.setWidth(230);
        tableColumn1.setResizable(false);
        tableColumn1.setText(ResourceString.getResourceString("label.translation.file.name"));
    }

    private void setUse(final boolean use) {
        dictionaryTable.setEnabled(use);
    }

    private void initTranslationTable() {
        dictionaryTable.removeAll();

        if (translationCheckMap != null) {
            for (final TableEditor editor : translationCheckMap.values()) {
                editor.getEditor().dispose();
                editor.dispose();
            }

            translationCheckMap.clear();
        } else {
            translationCheckMap = new HashMap<String, TableEditor>();
        }

        for (final String translation : allTranslations) {
            final TableItem tableItem = new TableItem(dictionaryTable, SWT.NONE);

            final Button selectCheckButton = new Button(dictionaryTable, SWT.CHECK);
            selectCheckButton.pack();

            final TableEditor editor = new TableEditor(dictionaryTable);

            editor.minimumWidth = selectCheckButton.getSize().x;
            editor.horizontalAlignment = SWT.CENTER;
            editor.setEditor(selectCheckButton, tableItem, 0);

            tableItem.setText(1, translation);

            if (translationSettings.isSelected(translation)) {
                selectCheckButton.setSelection(true);
            }

            translationCheckMap.put(translation, editor);
        }
    }

    @Override
    protected void addListener() {
        useButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                setUse(useButton.getSelection());
            }
        });
    }

    @Override
    protected String getTitle() {
        return "label.translation";
    }

    @Override
    protected void perfomeOK() throws InputException {
        validatePage();
    }

    @Override
    protected void setData() {
        initTranslationTable();
        useButton.setSelection(translationSettings.isUse());
        setUse(translationSettings.isUse());
    }

    @Override
    protected String getErrorMessage() {
        return null;
    }

    public void validatePage() {
        final List<String> selectedTranslations = new ArrayList<String>();

        for (final String translation : allTranslations) {
            final Button button = (Button) translationCheckMap.get(translation).getEditor();

            if (button.getSelection()) {
                selectedTranslations.add(translation);
            }
        }

        translationSettings.setSelectedTranslations(selectedTranslations);
        translationSettings.setUse(useButton.getSelection());
    }
}
