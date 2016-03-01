package org.insightech.er.db.impl.oracle.tablespace;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.ResourceString;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;
import org.insightech.er.editor.view.dialog.outline.tablespace.TablespaceDialog;
import org.insightech.er.editor.view.dialog.outline.tablespace.TablespaceSizeCaluculatorDialog;
import org.insightech.er.util.Format;

public class OracleTablespaceDialog extends TablespaceDialog {

    private Text dataFile;

    private Text fileSize;

    private Button autoExtend;

    private Text autoExtendSize;

    private Text autoExtendMaxSize;

    private Text minimumExtentSize;

    private Text initial;

    private Text next;

    private Text minExtents;

    private Text maxExtents;

    private Text pctIncrease;

    private Button logging;

    private Button offline;

    private Button temporary;

    private Button autoSegmentSpaceManagement;

    private Button calculatorButton;

    public OracleTablespaceDialog() {
        super();
    }

    @Override
    protected void initLayout(final GridLayout layout) {
        super.initLayout(layout);

        layout.numColumns = 6;
    }

    @Override
    protected void initialize(final Composite composite) {
        super.initialize(composite);

        dataFile = CompositeFactory.createText(this, composite, "label.tablespace.data.file", 1, 200, false, false);
        fileSize = CompositeFactory.createText(this, composite, "label.size", 1, NUM_TEXT_WIDTH, false, false);
        calculatorButton = new Button(composite, SWT.NONE);
        calculatorButton.setText(ResourceString.getResourceString("label.calculate"));

        CompositeFactory.filler(composite, 1);

        CompositeFactory.filler(composite, 1);
        CompositeFactory.createExampleLabel(composite, "label.tablespace.data.file.example");
        CompositeFactory.filler(composite, 1);
        CompositeFactory.createExampleLabel(composite, "label.tablespace.size.example", 2);
        CompositeFactory.filler(composite, 1);

        final Group autoExtendGroup = new Group(composite, SWT.NONE);
        final GridLayout autoExtendGroupLayout = new GridLayout();
        autoExtendGroupLayout.numColumns = 5;
        autoExtendGroup.setLayout(autoExtendGroupLayout);
        autoExtendGroup.setText(ResourceString.getResourceString("label.tablespace.auto.extend"));

        final GridData autoExtendGroupGridData = new GridData();
        autoExtendGroupGridData.horizontalSpan = getNumColumns();
        autoExtendGroupGridData.horizontalAlignment = GridData.FILL;
        autoExtendGroupGridData.grabExcessHorizontalSpace = true;
        autoExtendGroup.setLayoutData(autoExtendGroupGridData);

        autoExtend = CompositeFactory.createCheckbox(this, autoExtendGroup, "label.tablespace.auto.extend", false, 1);
        autoExtendSize = CompositeFactory.createText(this, autoExtendGroup, "label.size", 1, NUM_TEXT_WIDTH, false, false);
        autoExtendMaxSize = CompositeFactory.createText(this, autoExtendGroup, "label.max.size", 1, NUM_TEXT_WIDTH, false, false);
        CompositeFactory.filler(autoExtendGroup, 2);
        CompositeFactory.createExampleLabel(autoExtendGroup, "label.tablespace.size.example");
        CompositeFactory.filler(autoExtendGroup, 1);
        CompositeFactory.createExampleLabel(autoExtendGroup, "label.tablespace.size.example");

        minimumExtentSize = CompositeFactory.createText(this, composite, "label.tablespace.minimum.extent.size", 1, NUM_TEXT_WIDTH, false, false);
        CompositeFactory.filler(composite, 4);
        CompositeFactory.filler(composite, 1);
        CompositeFactory.createExampleLabel(composite, "label.tablespace.size.example");
        CompositeFactory.filler(composite, 4);

        final Group defaultStorageGroup = new Group(composite, SWT.NONE);
        final GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        defaultStorageGroup.setLayout(layout);
        defaultStorageGroup.setText("Default Storage");
        final GridData defaultStorageGroupGridData = new GridData();
        defaultStorageGroupGridData.horizontalSpan = getNumColumns();
        defaultStorageGroupGridData.horizontalAlignment = GridData.FILL;
        defaultStorageGroupGridData.grabExcessHorizontalSpace = true;
        defaultStorageGroup.setLayoutData(defaultStorageGroupGridData);

        initial = CompositeFactory.createText(this, defaultStorageGroup, "label.tablespace.initial", 1, NUM_TEXT_WIDTH, false, false);
        CompositeFactory.createExampleLabel(defaultStorageGroup, "ex) 1M");
        next = CompositeFactory.createText(this, defaultStorageGroup, "label.tablespace.next", 1, NUM_TEXT_WIDTH, false, false);
        CompositeFactory.createExampleLabel(defaultStorageGroup, "ex) 1M");
        minExtents = CompositeFactory.createText(this, defaultStorageGroup, "label.tablespace.min.extents", 1, NUM_TEXT_WIDTH, false, false);
        CompositeFactory.createExampleLabel(defaultStorageGroup, "ex) 1");
        maxExtents = CompositeFactory.createText(this, defaultStorageGroup, "label.tablespace.max.extents", 1, NUM_TEXT_WIDTH, false, false);
        CompositeFactory.createExampleLabel(defaultStorageGroup, "ex) 4096");
        pctIncrease = CompositeFactory.createText(this, defaultStorageGroup, "label.tablespace.pct.increase", 1, NUM_TEXT_WIDTH, false, false);
        CompositeFactory.createExampleLabel(defaultStorageGroup, "ex) 0");

        logging = createCheckbox(composite, "label.tablespace.logging");
        offline = createCheckbox(composite, "label.tablespace.offline");
        temporary = createCheckbox(composite, "label.tablespace.temporary");
        autoSegmentSpaceManagement = createCheckbox(composite, "label.tablespace.auto.segment.space.management");
    }

    @Override
    protected TablespaceProperties setTablespaceProperties() {
        final OracleTablespaceProperties properties = new OracleTablespaceProperties();

        properties.setAutoExtend(autoExtend.getSelection());
        properties.setAutoExtendMaxSize(autoExtendMaxSize.getText().trim());
        properties.setAutoExtendSize(autoExtendSize.getText().trim());
        properties.setAutoSegmentSpaceManagement(autoSegmentSpaceManagement.getSelection());
        properties.setDataFile(dataFile.getText().trim());
        properties.setFileSize(fileSize.getText().trim());
        properties.setInitial(initial.getText().trim());
        properties.setLogging(logging.getSelection());
        properties.setMaxExtents(maxExtents.getText().trim());
        properties.setMinExtents(minExtents.getText().trim());
        properties.setMinimumExtentSize(minimumExtentSize.getText().trim());
        properties.setNext(next.getText().trim());
        properties.setOffline(offline.getSelection());
        properties.setPctIncrease(pctIncrease.getText().trim());
        properties.setTemporary(temporary.getSelection());

        return properties;
    }

    @Override
    protected void setData(final TablespaceProperties tablespaceProperties) {
        if (tablespaceProperties instanceof OracleTablespaceProperties) {
            final OracleTablespaceProperties properties = (OracleTablespaceProperties) tablespaceProperties;

            autoExtend.setSelection(properties.isAutoExtend());
            autoExtendMaxSize.setText(Format.toString(properties.getAutoExtendMaxSize()));
            autoExtendSize.setText(Format.toString(properties.getAutoExtendSize()));
            autoSegmentSpaceManagement.setSelection(properties.isAutoSegmentSpaceManagement());
            dataFile.setText(Format.toString(properties.getDataFile()));
            fileSize.setText(Format.toString(properties.getFileSize()));
            initial.setText(Format.toString(properties.getInitial()));
            logging.setSelection(properties.isLogging());
            maxExtents.setText(Format.toString(properties.getMaxExtents()));
            minExtents.setText(Format.toString(properties.getMinExtents()));
            minimumExtentSize.setText(Format.toString(properties.getMinimumExtentSize()));
            next.setText(Format.toString(properties.getNext()));
            offline.setSelection(properties.isOffline());
            pctIncrease.setText(Format.toString(properties.getPctIncrease()));
            temporary.setSelection(properties.isTemporary());
        }

        setAutoExtendEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getErrorMessage() {
        final String errorMessage = super.getErrorMessage();
        if (errorMessage != null) {
            return errorMessage;
        }

        if (autoExtend.getSelection()) {
            final String text = autoExtendSize.getText().trim();
            if (text.equals("")) {
                return "error.tablespace.auto.extend.size.empty";
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addListener() {
        super.addListener();

        autoExtend.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                setAutoExtendEnabled();
            }

        });

        calculatorButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final TablespaceSizeCaluculatorDialog dialog = new TablespaceSizeCaluculatorDialog();
                dialog.init(diagram);
                dialog.open();
            }

        });
    }

    private void setAutoExtendEnabled() {
        final boolean enabled = autoExtend.getSelection();
        autoExtendSize.setEnabled(enabled);
        autoExtendMaxSize.setEnabled(enabled);
    }
}
