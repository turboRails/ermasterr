package org.insightech.er.common.widgets;

import java.io.File;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.Resources;
import org.insightech.er.util.io.FileUtils;

public abstract class AbstractPathText {

    // private String PROJECT_BASE_STRING = "<project_dir>" + File.separator;

    private final Text text;

    private final Button openBrowseButton;

    protected File projectDir;

    public AbstractPathText(final Composite parent, final File argProjectDir, final boolean indent) {
        text = new Text(parent, SWT.BORDER);
        projectDir = argProjectDir;

        final GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gridData.grabExcessHorizontalSpace = true;

        if (indent) {
            gridData.horizontalIndent = Resources.INDENT;
        }

        text.setLayoutData(gridData);

        openBrowseButton = new Button(parent, SWT.LEFT);
        openBrowseButton.setText(" " + JFaceResources.getString("openBrowse") + " ");

        openBrowseButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                String saveFilePath = selectPathByDilaog();

                if (saveFilePath != null) {
                    saveFilePath = FileUtils.getRelativeFilePath(projectDir, saveFilePath);
                    setText(saveFilePath);
                }
            }
        });
    }

    protected abstract String selectPathByDilaog();

    public void setLayoutData(final Object layoutData) {
        text.setLayoutData(layoutData);
    }

    public void setText(final String text) {
        // if (!FileUtils.isAbsolutePath(text)) {
        // text = PROJECT_BASE_STRING + Format.null2blank(text);
        // }

        this.text.setText(text);
        this.text.setSelection(text.length());
    }

    public boolean isBlank() {
        if (text.getText().trim().length() == 0) {
            return true;
        }

        return false;
    }

    public String getFilePath() {
        final String path = text.getText().trim();
        // if (path.startsWith(PROJECT_BASE_STRING)) {
        // path = path.substring(PROJECT_BASE_STRING.length());
        // }

        return path;
    }

    public void addModifyListener(final ModifyListener listener) {
        text.addModifyListener(listener);
    }

    public void setEnabled(final boolean enabled) {
        text.setEnabled(enabled);
        openBrowseButton.setEnabled(enabled);
    }

}
