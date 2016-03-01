package org.insightech.er.editor.view.dialog.dbexport;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;
import org.insightech.er.ResourceString;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.common.widgets.DirectoryText;
import org.insightech.er.editor.model.StringObjectModel;
import org.insightech.er.editor.model.dbexport.ExportWithProgressManager;
import org.insightech.er.editor.model.dbexport.testdata.ExportToTestDataManager;
import org.insightech.er.editor.model.settings.ExportSetting;
import org.insightech.er.editor.model.settings.export.ExportTestDataSetting;
import org.insightech.er.editor.model.testdata.TestData;
import org.insightech.er.util.Check;

public class ExportToTestDataDialog extends AbstractExportDialog {

    private ContainerCheckedTreeViewer testDataTable;

    private Button formatSqlRadio;

    private Button formatDBUnitRadio;

    private Button formatDBUnitFlatXmlRadio;

    private Button formatDBUnitXlsRadio;

    private DirectoryText outputDirectoryText;

    private Combo fileEncodingCombo;

    private final List<TestData> testDataList;

    private final int targetIndex;

    public ExportToTestDataDialog(final List<TestData> testDataList) {
        this(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), testDataList, -1);
    }

    public ExportToTestDataDialog(final Shell parentShell, final List<TestData> testDataList, final int targetIndex) {
        super(parentShell);

        // from TestDataManagementDialog
        // testDataList is different from
        // diagram.getDiagramContents().getTestDataList()
        this.testDataList = testDataList;
        this.targetIndex = targetIndex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize(final Composite parent) {
        createTestDataTableGroup(parent);
        createFormatGroup(parent);
        createFileGroup(parent);
    }

    private void createTestDataTableGroup(final Composite parent) {
        testDataTable = CompositeFactory.createCheckedTreeViewer(this, parent, 100, 3);
    }

    private void createFormatGroup(final Composite parent) {
        final Group group = CompositeFactory.createGroup(parent, "label.format", 3, 2);

        formatSqlRadio = CompositeFactory.createRadio(this, group, "label.sql", 2);
        formatDBUnitRadio = CompositeFactory.createRadio(this, group, "label.dbunit", 2);
        formatDBUnitFlatXmlRadio = CompositeFactory.createRadio(this, group, "label.dbunit.flat.xml", 2);
        formatDBUnitXlsRadio = CompositeFactory.createRadio(this, group, "label.dbunit.xls", 2);

        CompositeFactory.fillLine(group);

        fileEncodingCombo = CompositeFactory.createFileEncodingCombo(diagram.getEditor().getDefaultCharset(), this, group, "label.output.file.encoding", 1);
    }

    private void createFileGroup(final Composite parent) {
        outputDirectoryText = CompositeFactory.createDirectoryText(this, parent, "label.output.dir", getBaseDir(), "");
    }

    @Override
    protected void addListener() {
        super.addListener();

        formatSqlRadio.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                fileEncodingCombo.setEnabled(true);
            }
        });

        formatDBUnitRadio.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                fileEncodingCombo.setEnabled(true);
            }
        });

        formatDBUnitFlatXmlRadio.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                fileEncodingCombo.setEnabled(true);
            }
        });

        formatDBUnitXlsRadio.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                fileEncodingCombo.setEnabled(false);
            }
        });

    }

    @Override
    protected String getErrorMessage() {

        if (testDataTable.getCheckedElements().length == 0) {
            return "error.testdata.not.selected";
        }

        // if (this.outputDirectoryText.isBlank()) {
        // return "error.output.dir.is.empty";
        // }

        if (!Charset.isSupported(fileEncodingCombo.getText())) {
            return "error.file.encoding.is.not.supported";
        }

        return null;
    }

    @Override
    protected ExportWithProgressManager getExportWithProgressManager(final ExportSetting exportSetting) {

        final ExportTestDataSetting exportTestDataSetting = exportSetting.getExportTestDataSetting();

        if (formatSqlRadio.getSelection()) {
            exportTestDataSetting.setExportFormat(TestData.EXPORT_FORMT_SQL);

        } else if (formatDBUnitRadio.getSelection()) {
            exportTestDataSetting.setExportFormat(TestData.EXPORT_FORMT_DBUNIT);

        } else if (formatDBUnitFlatXmlRadio.getSelection()) {
            exportTestDataSetting.setExportFormat(TestData.EXPORT_FORMT_DBUNIT_FLAT_XML);

        } else if (formatDBUnitXlsRadio.getSelection()) {
            exportTestDataSetting.setExportFormat(TestData.EXPORT_FORMT_DBUNIT_XLS);

        }

        exportTestDataSetting.setExportFilePath(outputDirectoryText.getFilePath());
        exportTestDataSetting.setExportFileEncoding(fileEncodingCombo.getText());

        final List<TestData> exportTestDataList = new ArrayList<TestData>();

        for (final Object selectedNode : testDataTable.getCheckedElements()) {
            final Object value = ((TreeNode) selectedNode).getValue();

            if (value instanceof TestData) {
                exportTestDataList.add((TestData) value);
            }
        }

        return new ExportToTestDataManager(exportTestDataSetting, exportTestDataList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setData() {
        final ExportTestDataSetting exportTestDataSetting = settings.getExportSetting().getExportTestDataSetting();

        setTestDataTable();

        if (targetIndex >= 0) {
            final TreeNode rootNode = ((TreeNode[]) testDataTable.getInput())[0];
            for (final TreeNode treeNode : rootNode.getChildren()) {
                if (treeNode.getValue() == testDataList.get(targetIndex)) {
                    testDataTable.setChecked(treeNode, true);
                }
            }

        } else {
            testDataTable.setCheckedElements((TreeNode[]) testDataTable.getInput());
        }

        fileEncodingCombo.setEnabled(true);

        if (exportTestDataSetting.getExportFormat() == TestData.EXPORT_FORMT_DBUNIT) {
            formatDBUnitRadio.setSelection(true);

        } else if (exportTestDataSetting.getExportFormat() == TestData.EXPORT_FORMT_DBUNIT_FLAT_XML) {
            formatDBUnitFlatXmlRadio.setSelection(true);

        } else if (exportTestDataSetting.getExportFormat() == TestData.EXPORT_FORMT_DBUNIT_XLS) {
            formatDBUnitXlsRadio.setSelection(true);
            fileEncodingCombo.setEnabled(false);

        } else {
            formatSqlRadio.setSelection(true);

        }

        String outputDirectoryPath = exportTestDataSetting.getExportFilePath();

        if (Check.isEmpty(outputDirectoryPath)) {
            outputDirectoryPath = "testdata";
        }

        outputDirectoryText.setText(outputDirectoryPath);

        fileEncodingCombo.setText(exportTestDataSetting.getExportFileEncoding());
    }

    private void setTestDataTable() {
        final List<TreeNode> treeNodeList = createTreeNodeList();

        final TreeNode[] treeNodes = treeNodeList.toArray(new TreeNode[treeNodeList.size()]);
        testDataTable.setInput(treeNodes);
        testDataTable.expandAll();
    }

    protected List<TreeNode> createTreeNodeList() {
        final List<TreeNode> treeNodeList = new ArrayList<TreeNode>();

        final TreeNode topNode = new TreeNode(new StringObjectModel(ResourceString.getResourceString("label.testdata")));
        treeNodeList.add(topNode);

        final List<TreeNode> nodeList = new ArrayList<TreeNode>();

        for (final TestData testData : testDataList) {
            final TreeNode objectNode = new TreeNode(testData);
            objectNode.setParent(topNode);

            nodeList.add(objectNode);
        }

        topNode.setChildren(nodeList.toArray(new TreeNode[nodeList.size()]));

        return treeNodeList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createButtonsForButtonBar(final Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, ResourceString.getResourceString("label.button.export"), true);
        createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTitle() {
        return "dialog.title.export.testdata";
    }

}
