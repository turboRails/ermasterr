package org.insightech.er.common.widgets;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Panel;
import java.io.File;
import java.nio.charset.Charset;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TreeNodeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;
import org.insightech.er.ResourceString;
import org.insightech.er.Resources;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.editor.view.dialog.dbimport.ViewLabelProvider;

public class CompositeFactory {

    public static Composite createComposite(final Composite parent, final int numColumns, final boolean withMargin) {
        final GridLayout gridLayout = new GridLayout();

        gridLayout.numColumns = numColumns;

        if (withMargin) {
            gridLayout.marginTop = Resources.MARGIN;
            gridLayout.marginBottom = Resources.MARGIN;
            gridLayout.marginRight = Resources.MARGIN;
            gridLayout.marginLeft = Resources.MARGIN;

        } else {
            gridLayout.marginTop = 0;
            gridLayout.marginBottom = 0;
            gridLayout.marginWidth = 0;
        }

        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(gridLayout);

        final GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;

        composite.setLayoutData(gridData);

        return composite;
    }

    public static Composite createChildComposite(final Composite parent, final int span, final int numColumns) {
        return createChildComposite(parent, -1, span, numColumns);
    }

    public static Composite createChildComposite(final Composite parent, final int height, final int span, final int numColumns) {
        final Composite composite = new Composite(parent, SWT.NONE);

        final GridData gridData = new GridData();
        gridData.horizontalSpan = span;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;

        if (height >= 0) {
            gridData.heightHint = height;
        }

        composite.setLayoutData(gridData);

        final GridLayout gridLayout = new GridLayout();
        gridLayout.marginWidth = 0;
        gridLayout.numColumns = numColumns;

        composite.setLayout(gridLayout);

        return composite;
    }

    public static SpinnerWithScale createSpinnerWithScale(final AbstractDialog dialog, final Composite composite, final String title, final int minimum, final int maximum) {
        return createSpinnerWithScale(dialog, composite, title, "%", minimum, maximum);
    }

    public static SpinnerWithScale createSpinnerWithScale(final AbstractDialog dialog, final Composite composite, final String title, final String unit, final int minimum, final int maximum) {
        if (title != null) {
            final Label label = new Label(composite, SWT.LEFT);
            label.setText(ResourceString.getResourceString(title));
        }

        final GridData scaleGridData = new GridData();
        scaleGridData.horizontalAlignment = GridData.FILL;
        scaleGridData.grabExcessHorizontalSpace = true;

        final Scale scale = new Scale(composite, SWT.NONE);
        scale.setLayoutData(scaleGridData);

        int diff = 0;

        if (minimum < 0) {
            scale.setMinimum(0);
            scale.setMaximum(-minimum + maximum);
            diff = minimum;

        } else {
            scale.setMinimum(minimum);
            scale.setMaximum(maximum);

        }

        scale.setPageIncrement((maximum - minimum) / 10);

        final GridData spinnerGridData = new GridData();

        final Spinner spinner = new Spinner(composite, SWT.RIGHT | SWT.BORDER);
        spinner.setLayoutData(spinnerGridData);
        spinner.setMinimum(minimum);
        spinner.setMaximum(maximum);

        final Label label = new Label(composite, SWT.NONE);
        label.setText(unit);

        ListenerAppender.addModifyListener(scale, spinner, diff, dialog);

        return new SpinnerWithScale(spinner, scale, diff);
    }

    public static Combo createReadOnlyCombo(final AbstractDialog dialog, final Composite composite, final String title) {
        return createReadOnlyCombo(dialog, composite, title, 1);
    }

    public static Combo createReadOnlyCombo(final AbstractDialog dialog, final Composite composite, final String title, final int span) {
        return createReadOnlyCombo(dialog, composite, title, span, -1);
    }

    public static Combo createReadOnlyCombo(final AbstractDialog dialog, final Composite composite, final String title, final int span, final int width) {
        final GridData gridData = new GridData();
        gridData.horizontalSpan = span;

        if (title != null) {
            gridData.horizontalIndent = Resources.INDENT;

            final Label label = new Label(composite, SWT.LEFT);

            final GridData labelGridData = new GridData();
            labelGridData.horizontalAlignment = SWT.LEFT;
            label.setLayoutData(labelGridData);
            label.setText(ResourceString.getResourceString(title));
        }

        if (width > 0) {
            gridData.widthHint = width;

        } else {
            gridData.horizontalAlignment = GridData.FILL;
            gridData.grabExcessHorizontalSpace = true;
        }

        final Combo combo = new Combo(composite, SWT.READ_ONLY);
        combo.setLayoutData(gridData);

        ListenerAppender.addComboListener(combo, dialog, false);

        return combo;
    }

    public static Combo createCombo(final AbstractDialog dialog, final Composite composite, final String title) {
        return createCombo(dialog, composite, title, 1);
    }

    public static Combo createCombo(final AbstractDialog dialog, final Composite composite, final String title, final int span) {
        if (title != null) {
            final Label label = new Label(composite, SWT.LEFT);
            label.setText(ResourceString.getResourceString(title));
        }

        final GridData gridData = new GridData();
        gridData.horizontalSpan = span;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.horizontalIndent = Resources.INDENT;
        gridData.grabExcessHorizontalSpace = true;

        final Combo combo = new Combo(composite, SWT.NONE);
        combo.setLayoutData(gridData);

        ListenerAppender.addComboListener(combo, dialog, false);

        return combo;
    }

    public static Combo createFileEncodingCombo(final String defaultCharset, final AbstractDialog dialog, final Composite composite, final String title, final int span) {
        final Combo fileEncodingCombo = createReadOnlyCombo(dialog, composite, title, span, -1);

        for (final Charset charset : Charset.availableCharsets().values()) {
            fileEncodingCombo.add(charset.displayName());
        }

        fileEncodingCombo.setText(defaultCharset);

        return fileEncodingCombo;
    }

    public static Text createText(final AbstractDialog dialog, final Composite composite, final String title, final boolean imeOn, final boolean indent) {
        return createText(dialog, composite, title, 1, imeOn, indent);
    }

    public static Text createText(final AbstractDialog dialog, final Composite composite, final String title, final int span, final boolean imeOn, final boolean indent) {
        return createText(dialog, composite, title, span, -1, imeOn, indent);
    }

    public static Text createText(final AbstractDialog dialog, final Composite composite, final String title, final int span, final int width, final boolean imeOn, final boolean indent) {
        return createText(dialog, composite, title, span, width, SWT.BORDER, imeOn, indent);
    }

    public static Text createNumText(final AbstractDialog dialog, final Composite composite, final String title) {
        return createNumText(dialog, composite, title, -1);
    }

    public static Text createNumText(final AbstractDialog dialog, final Composite composite, final String title, final boolean indent) {
        return createNumText(dialog, composite, title, 1, -1, indent);
    }

    public static Text createNumText(final AbstractDialog dialog, final Composite composite, final String title, final int width) {
        return createNumText(dialog, composite, title, 1, width);
    }

    public static Text createNumText(final AbstractDialog dialog, final Composite composite, final String title, final int span, final int width) {
        return createNumText(dialog, composite, title, span, width, false);
    }

    public static Text createNumText(final AbstractDialog dialog, final Composite composite, final String title, final int span, final int width, final boolean indent) {
        return createText(dialog, composite, title, span, width, SWT.BORDER | SWT.RIGHT, false, indent);
    }

    public static Text createText(final AbstractDialog dialog, final Composite composite, final String title, final int span, final int width, final int style, final boolean imeOn, final boolean indent) {
        if (title != null) {
            final Label label = new Label(composite, SWT.NONE);
            if (indent) {
                final GridData labelGridData = new GridData();
                labelGridData.horizontalAlignment = SWT.LEFT;
                label.setLayoutData(labelGridData);
            }

            label.setText(ResourceString.getResourceString(title));
        }

        final GridData textGridData = new GridData();
        textGridData.horizontalSpan = span;
        if (indent) {
            textGridData.horizontalIndent = Resources.INDENT;
        }

        if (width > 0) {
            textGridData.widthHint = width;

        } else {
            textGridData.horizontalAlignment = GridData.FILL;
            textGridData.grabExcessHorizontalSpace = true;
        }

        final Text text = new Text(composite, style);
        text.setLayoutData(textGridData);

        ListenerAppender.addTextListener(text, dialog, imeOn);

        return text;
    }

    public static Label createExampleLabel(final Composite composite, final String title) {
        return createExampleLabel(composite, title, -1);
    }

    public static Label createExampleLabel(final Composite composite, final String title, final int span) {
        final Label label = new Label(composite, SWT.NONE);
        label.setText(ResourceString.getResourceString(title));

        if (span > 0) {
            final GridData gridData = new GridData();
            gridData.horizontalSpan = span;
            label.setLayoutData(gridData);
        }

        final FontData fontData = Display.getCurrent().getSystemFont().getFontData()[0];
        final Font font = Resources.getFont(fontData.getName(), 8);
        label.setFont(font);

        return label;
    }

    public static void filler(final Composite composite, final int span) {
        filler(composite, span, -1);
    }

    public static void filler(final Composite composite, final int span, final int width) {
        final GridData gridData = new GridData();
        gridData.horizontalSpan = span;
        gridData.heightHint = 1;

        if (width > 0) {
            gridData.widthHint = width;
        }

        final Label label = new Label(composite, SWT.NONE);
        label.setLayoutData(gridData);
    }

    public static void fillLine(final Composite composite) {
        fillLine(composite, -1);
    }

    public static void fillLine(final Composite composite, final int height) {
        final GridData gridData = new GridData();
        gridData.horizontalSpan = ((GridLayout) composite.getLayout()).numColumns;
        if (height != -1) {
            gridData.heightHint = height;
        }

        final Label label = new Label(composite, SWT.NONE);
        label.setLayoutData(gridData);
    }

    public static Label separater(final Composite composite) {
        return separater(composite, -1);
    }

    public static Label separater(final Composite composite, final int span) {
        final Label label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
        final GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gridData.heightHint = 1;

        // gridData.horizontalIndent = Resources.INDENT;

        if (span > 0) {
            gridData.horizontalSpan = span;

        } else {
            gridData.horizontalSpan = ((GridLayout) composite.getLayout()).numColumns;
        }

        label.setLayoutData(gridData);

        return label;
    }

    public static Label createLabelAsValue(final Composite composite, final String title, final int span) {
        final Label label = new Label(composite, SWT.NONE);
        label.setText(ResourceString.getResourceString(title));

        final GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalIndent = Resources.INDENT;
        gridData.horizontalSpan = span;

        label.setLayoutData(gridData);

        return label;
    }

    public static Label createLeftLabel(final Composite composite, final String title, final int span) {
        return createLabel(composite, title, span, -1, true, false);
    }

    public static Label createLabel(final Composite composite, final String title) {
        return createLabel(composite, title, -1);
    }

    public static Label createLabel(final Composite composite, final String title, final int span) {
        return createLabel(composite, title, span, -1);
    }

    public static Label createLabel(final Composite composite, final String title, final int span, final int width) {
        return createLabel(composite, title, span, width, true, false);
    }

    public static Label createLabel(final Composite composite, final String title, final int span, final int width, final boolean leftAlign, final boolean indent) {
        final Label label = new Label(composite, SWT.NONE);
        label.setText(ResourceString.getResourceString(title));

        final GridData gridData = new GridData();
        if (indent) {
            gridData.horizontalIndent = Resources.INDENT;
        }
        if (leftAlign) {
            gridData.horizontalAlignment = SWT.LEFT;

        } else {
            gridData.horizontalAlignment = SWT.RIGHT;
        }

        if (span > 0 || width > 0) {
            if (span > 0) {
                gridData.horizontalSpan = span;
            }
            if (width > 0) {
                gridData.widthHint = width;
            }
        }

        label.setLayoutData(gridData);

        return label;
    }

    public static Button createCheckbox(final AbstractDialog dialog, final Composite composite, final String title, final boolean indent) {
        return createCheckbox(dialog, composite, title, indent, -1);
    }

    public static Button createCheckbox(final AbstractDialog dialog, final Composite composite, final String title, final boolean indent, final int span) {
        final Button checkbox = new Button(composite, SWT.CHECK);
        checkbox.setText(ResourceString.getResourceString(title));

        final GridData gridData = new GridData();

        if (span != -1) {
            gridData.horizontalSpan = span;
        }
        if (indent) {
            gridData.horizontalIndent = Resources.INDENT;
        }

        checkbox.setLayoutData(gridData);

        ListenerAppender.addCheckBoxListener(checkbox, dialog);

        return checkbox;
    }

    public static MultiLineCheckbox createMultiLineCheckbox(final AbstractDialog dialog, final Composite composite, final String title, final boolean indent, final int span) {
        return new MultiLineCheckbox(dialog, composite, title, indent, span);
    }

    public static Button createRadio(final AbstractDialog dialog, final Composite composite, final String title) {
        return createRadio(dialog, composite, title, -1);
    }

    public static Button createRadio(final AbstractDialog dialog, final Composite composite, final String title, final int span) {
        return createRadio(dialog, composite, title, span, false);
    }

    public static Button createRadio(final AbstractDialog dialog, final Composite composite, final String title, final int span, final boolean indent) {
        final Button radio = new Button(composite, SWT.RADIO);
        radio.setText(ResourceString.getResourceString(title));

        final GridData gridData = new GridData();

        if (span != -1) {
            gridData.horizontalSpan = span;
        }

        if (indent) {
            gridData.horizontalIndent = Resources.INDENT;
        }

        radio.setLayoutData(gridData);

        ListenerAppender.addCheckBoxListener(radio, dialog);

        return radio;
    }

    public static Text createTextArea(final AbstractDialog dialog, final Composite composite, final String title, final int width, final int height, final int span, final boolean imeOn) {
        return createTextArea(dialog, composite, title, width, height, span, true, imeOn, true);
    }

    public static Text createTextArea(final AbstractDialog dialog, final Composite composite, final String title, final int width, final int height, final int span, final boolean imeOn, final boolean indent) {
        return createTextArea(dialog, composite, title, width, height, span, true, imeOn, indent);
    }

    public static Text createTextArea(final AbstractDialog dialog, final Composite composite, final String title, final int width, final int height, final int span, final boolean selectAll, final boolean imeOn, final boolean indent) {
        if (title != null) {
            final Label label = new Label(composite, SWT.NONE);

            final GridData labelGridData = new GridData();
            labelGridData.verticalAlignment = SWT.TOP;
            labelGridData.horizontalAlignment = SWT.LEFT;

            label.setLayoutData(labelGridData);

            label.setText(ResourceString.getResourceString(title));
        }

        final GridData textAreaGridData = new GridData();
        textAreaGridData.heightHint = height;
        textAreaGridData.horizontalSpan = span;

        if (width > 0) {
            textAreaGridData.widthHint = width;
        } else {
            textAreaGridData.horizontalAlignment = GridData.FILL;
            textAreaGridData.grabExcessHorizontalSpace = true;
        }

        if (title != null && indent) {
            textAreaGridData.horizontalIndent = Resources.INDENT;
        }

        final Text text = new Text(composite, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
        text.setLayoutData(textAreaGridData);

        ListenerAppender.addTextAreaListener(text, dialog, selectAll, imeOn);

        return text;
    }

    public static Table createTable(final Composite composite, final int height, final int span) {
        return createTable(composite, height, span, false);
    }

    public static Table createTable(final Composite composite, final int height, final int span, final boolean multi) {
        final GridData gridData = new GridData();
        gridData.horizontalSpan = span;
        gridData.heightHint = height;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;

        int style = SWT.SINGLE;
        if (multi) {
            style = SWT.MULTI;
        }

        final Table table = new Table(composite, style | SWT.BORDER | SWT.FULL_SELECTION);
        table.setLayoutData(gridData);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        return table;
    }

    public static Button createSmallButton(final Composite composite, final String text) {
        return createButton(composite, text, -1, Resources.SMALL_BUTTON_WIDTH);
    }

    public static Button createMiddleButton(final Composite composite, final String text) {
        return createButton(composite, text, -1, Resources.MIDDLE_BUTTON_WIDTH);
    }

    public static Button createLargeButton(final Composite composite, final String text) {
        return createButton(composite, text, -1, Resources.LARGE_BUTTON_WIDTH);
    }

    public static Button createLargeButton(final Composite composite, final String text, final int span) {
        return createButton(composite, text, span, Resources.LARGE_BUTTON_WIDTH);
    }

    public static Button createButton(final Composite composite, final String text, final int span, final int width) {
        final GridData gridData = new GridData();
        gridData.widthHint = width;

        if (span != -1) {
            gridData.horizontalSpan = span;
        }

        final Button button = new Button(composite, SWT.NONE);
        button.setText(ResourceString.getResourceString(text));
        button.setLayoutData(gridData);

        return button;
    }

    public static Button createFillButton(final Composite composite, final String text) {
        final GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;

        final Button button = new Button(composite, SWT.NONE);
        button.setText(ResourceString.getResourceString(text));
        button.setLayoutData(gridData);

        return button;
    }

    public static Button createAddButton(final Composite composite) {
        final GridData gridData = new GridData();
        gridData.grabExcessVerticalSpace = true;
        gridData.verticalAlignment = GridData.END;
        gridData.widthHint = Resources.BUTTON_ADD_REMOVE_WIDTH;

        final Button button = new Button(composite, SWT.NONE);
        button.setText(ResourceString.getResourceString("label.right.arrow"));
        button.setLayoutData(gridData);

        return button;
    }

    public static Button createRemoveButton(final Composite composite) {
        final GridData gridData = new GridData();
        gridData.grabExcessVerticalSpace = true;
        gridData.verticalAlignment = GridData.BEGINNING;
        gridData.widthHint = Resources.BUTTON_ADD_REMOVE_WIDTH;

        final Button button = new Button(composite, SWT.NONE);
        button.setText(ResourceString.getResourceString("label.left.arrow"));
        button.setLayoutData(gridData);

        return button;
    }

    public static Button createUpButton(final Composite composite) {
        final GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = false;
        gridData.verticalAlignment = GridData.END;
        gridData.grabExcessVerticalSpace = true;
        gridData.widthHint = Resources.SMALL_BUTTON_WIDTH;

        final Button button = new Button(composite, SWT.NONE);
        button.setText(ResourceString.getResourceString("label.up.arrow"));
        button.setLayoutData(gridData);

        return button;
    }

    public static Button createDownButton(final Composite composite) {
        final GridData gridData = new GridData();
        gridData.grabExcessVerticalSpace = true;
        gridData.verticalAlignment = GridData.BEGINNING;
        gridData.widthHint = Resources.SMALL_BUTTON_WIDTH;

        final Button button = new Button(composite, SWT.NONE);
        button.setText(ResourceString.getResourceString("label.down.arrow"));
        button.setLayoutData(gridData);

        return button;
    }

    public static TableEditor createCheckBoxTableEditor(final TableItem tableItem, final boolean selection, final int column) {
        final Table table = tableItem.getParent();

        final Button checkBox = new Button(table, SWT.CHECK);
        checkBox.pack();

        final TableEditor editor = new TableEditor(table);

        editor.minimumWidth = checkBox.getSize().x;
        editor.horizontalAlignment = SWT.CENTER;
        editor.setEditor(checkBox, tableItem, column);

        checkBox.setSelection(selection);

        return editor;
    }

    public static RowHeaderTable createRowHeaderTable(final Composite parent, final int width, final int height, final int rowHeaderWidth, final int rowHeight, final int span, final boolean iconEnable, final boolean editable) {
        final Composite composite = new Composite(parent, SWT.EMBEDDED);
        final GridData gridData = new GridData();
        gridData.horizontalSpan = span;
        gridData.widthHint = width;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.heightHint = height;
        composite.setLayoutData(gridData);

        return createRowHeaderTable(composite, width, height, rowHeaderWidth, rowHeight, iconEnable, editable);
    }

    private static RowHeaderTable createRowHeaderTable(final Composite composite, final int width, final int height, final int rowHeaderWidth, final int rowHeight, final boolean iconEnable, final boolean editable) {
        final Frame frame = SWT_AWT.new_Frame(composite);
        final FlowLayout frameLayout = new FlowLayout();
        frameLayout.setVgap(0);
        frame.setLayout(frameLayout);

        final Panel panel = new Panel();
        final FlowLayout panelLayout = new FlowLayout();
        panelLayout.setVgap(0);
        panel.setLayout(panelLayout);
        frame.add(panel);

        final RowHeaderTable table = new RowHeaderTable(width, height, rowHeaderWidth, rowHeight, iconEnable, editable);
        panel.add(table);

        return table;
    }

    public static Group createGroup(final Composite parent, final String title, final int span, final int numColumns) {
        return createGroup(parent, title, span, numColumns, 15);
    }

    public static Group createGroup(final Composite parent, final String title, final int span, final int numColumns, final int margin) {
        final GridData groupGridData = new GridData();
        groupGridData.horizontalAlignment = GridData.FILL;
        groupGridData.grabExcessHorizontalSpace = true;
        groupGridData.verticalAlignment = GridData.FILL;
        groupGridData.grabExcessVerticalSpace = true;
        groupGridData.horizontalSpan = span;

        final GridLayout groupLayout = new GridLayout();
        groupLayout.marginWidth = margin;
        groupLayout.marginHeight = margin;
        groupLayout.numColumns = numColumns;

        final Group group = new Group(parent, SWT.NONE);
        group.setText(ResourceString.getResourceString(title));
        group.setLayoutData(groupGridData);
        group.setLayout(groupLayout);

        return group;
    }

    public static FileText createFileText(final boolean save, final AbstractDialog dialog, final Composite parent, final String title, final File projectDir, final String defaultFileName, final String filterExtension) {
        return createFileText(save, dialog, parent, title, projectDir, defaultFileName, filterExtension, true);
    }

    public static FileText createFileText(final boolean save, final AbstractDialog dialog, final Composite parent, final String title, final File projectDir, final String defaultFileName, final String filterExtension, final boolean indent) {
        return createFileText(save, dialog, parent, title, projectDir, defaultFileName, new String[] {filterExtension}, indent);
    }

    public static FileText createFileText(final boolean save, final AbstractDialog dialog, final Composite parent, final String title, final File projectDir, final String defaultFileName, final String[] filterExtensions) {
        return createFileText(save, dialog, parent, title, projectDir, defaultFileName, filterExtensions, true);
    }

    public static FileText createFileText(final boolean save, final AbstractDialog dialog, final Composite parent, final String title, final File projectDir, final String defaultFileName, final String[] filterExtensions, final boolean indent) {
        if (title != null) {
            final Label label = new Label(parent, SWT.NONE);
            if (indent) {
                final GridData labelGridData = new GridData();
                labelGridData.horizontalAlignment = SWT.LEFT;
                label.setLayoutData(labelGridData);
            }

            label.setText(ResourceString.getResourceString(title));
        }

        final FileText fileText = new FileText(save, parent, projectDir, defaultFileName, filterExtensions, indent);

        ListenerAppender.addPathTextListener(fileText, dialog);

        return fileText;
    }

    public static DirectoryText createDirectoryText(final AbstractDialog dialog, final Composite parent, final String title, final File projectDir, final String message) {
        return createDirectoryText(dialog, parent, title, projectDir, message, true);
    }

    public static DirectoryText createDirectoryText(final AbstractDialog dialog, final Composite parent, final String title, final File projectDir, final String message, final boolean indent) {

        if (title != null) {
            final Label label = new Label(parent, SWT.NONE);
            if (indent) {
                final GridData labelGridData = new GridData();
                labelGridData.horizontalAlignment = SWT.LEFT;
                label.setLayoutData(labelGridData);
            }

            label.setText(ResourceString.getResourceString(title));
        }

        final DirectoryText directoryText = new DirectoryText(parent, projectDir, message, indent);

        ListenerAppender.addPathTextListener(directoryText, dialog);

        return directoryText;
    }

    public static ContainerCheckedTreeViewer createCheckedTreeViewer(final AbstractDialog dialog, final Composite parent, final int height, final int span) {
        final GridData gridData = new GridData();
        gridData.heightHint = height;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalSpan = span;

        final ContainerCheckedTreeViewer viewer = new ContainerCheckedTreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        final Tree tree = viewer.getTree();
        tree.setLayoutData(gridData);

        viewer.setContentProvider(new TreeNodeContentProvider());
        viewer.setLabelProvider(new ViewLabelProvider());

        if (dialog != null) {
            viewer.addCheckStateListener(new ICheckStateListener() {

                @Override
                public void checkStateChanged(final CheckStateChangedEvent event) {
                    dialog.validate();
                }

            });
        }

        return viewer;
    }

    public static Table createTable(final Composite parent, final int height) {
        final GridData gridData = new GridData();
        gridData.heightHint = height;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;

        final Table table = new Table(parent, SWT.FULL_SELECTION | SWT.BORDER);
        table.setHeaderVisible(true);
        table.setLayoutData(gridData);
        table.setLinesVisible(false);

        return table;
    }

    public static TableColumn createTableColumn(final Table table, final String title) {
        return createTableColumn(table, title, -1);
    }

    public static TableColumn createTableColumn(final Table table, final String title, final int width) {
        return createTableColumn(table, title, width, SWT.LEFT);
    }

    public static TableColumn createTableColumn(final Table table, final String title, final int width, final int align) {
        final TableColumn column = new TableColumn(table, align);

        column.setText(ResourceString.getResourceString(title));

        if (width >= 0) {
            column.setWidth(width);
        } else {
            column.pack();
        }

        return column;
    }
}
