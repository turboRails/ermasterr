package org.insightech.er.editor.model.dbexport.testdata;

import java.io.File;
import java.util.List;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.AbstractExportManager;
import org.insightech.er.editor.model.dbexport.testdata.impl.DBUnitFlatXmlTestDataCreator;
import org.insightech.er.editor.model.dbexport.testdata.impl.DBUnitTestDataCreator;
import org.insightech.er.editor.model.dbexport.testdata.impl.DBUnitXLSTestDataCreator;
import org.insightech.er.editor.model.dbexport.testdata.impl.SQLTestDataCreator;
import org.insightech.er.editor.model.progress_monitor.ProgressMonitor;
import org.insightech.er.editor.model.settings.export.ExportTestDataSetting;
import org.insightech.er.editor.model.testdata.TestData;

public class ExportToTestDataManager extends AbstractExportManager {

    private final ExportTestDataSetting exportTestDataSetting;

    private final List<TestData> testDataList;

    public ExportToTestDataManager(final ExportTestDataSetting exportTestDataSetting, final List<TestData> testDataList) {
        super("dialog.message.export.testdata");

        this.exportTestDataSetting = exportTestDataSetting;
        this.testDataList = testDataList;
    }

    @Override
    protected int getTotalTaskCount() {
        return testDataList.size();
    }

    @Override
    public void doProcess(final ProgressMonitor monitor) throws Exception {
        for (final TestData testData : testDataList) {
            monitor.subTaskWithCounter("writing : " + testData.getName());

            exportTestData(diagram, exportTestDataSetting, testData);

            monitor.worked(1);
        }
    }

    public void exportTestData(final ERDiagram diagram, final ExportTestDataSetting exportTestDataSetting, final TestData testData) throws Exception {
        TestDataCreator testDataCreator = null;

        final int format = exportTestDataSetting.getExportFormat();

        if (format == TestData.EXPORT_FORMT_DBUNIT) {
            testDataCreator = new DBUnitTestDataCreator(exportTestDataSetting.getExportFileEncoding());

        } else if (format == TestData.EXPORT_FORMT_DBUNIT_FLAT_XML) {
            testDataCreator = new DBUnitFlatXmlTestDataCreator(exportTestDataSetting.getExportFileEncoding());

        } else if (format == TestData.EXPORT_FORMT_SQL) {
            testDataCreator = new SQLTestDataCreator();

        } else if (format == TestData.EXPORT_FORMT_DBUNIT_XLS) {
            testDataCreator = new DBUnitXLSTestDataCreator();

        }

        testDataCreator.init(testData, projectDir);
        testDataCreator.write(exportTestDataSetting, diagram);
    }

    @Override
    public File getOutputFileOrDir() {
        return new File(exportTestDataSetting.getExportFilePath());
    }

}
