package org.insightech.er.editor.model.dbexport.ddl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.model.dbexport.AbstractExportManager;
import org.insightech.er.editor.model.progress_monitor.ProgressMonitor;
import org.insightech.er.editor.model.settings.export.ExportDDLSetting;
import org.insightech.er.util.io.FileUtils;

public class ExportToDDLManager extends AbstractExportManager {

    private final ExportDDLSetting exportDDLSetting;

    public ExportToDDLManager(final ExportDDLSetting exportDDLSetting) {
        super("dialog.message.export.ddl");
        this.exportDDLSetting = exportDDLSetting;
    }

    @Override
    protected int getTotalTaskCount() {
        return 2;
    }

    @Override
    protected void doProcess(final ProgressMonitor monitor) throws Exception {

        PrintWriter out = null;

        try {
            final DDLCreator ddlCreator = DBManagerFactory.getDBManager(diagram).getDDLCreator(diagram, exportDDLSetting.getCategory(), true);

            ddlCreator.init(exportDDLSetting.getEnvironment(), exportDDLSetting.getDdlTarget(), exportDDLSetting.getLineFeed());

            final File file = FileUtils.getFile(projectDir, exportDDLSetting.getDdlOutput());
            file.getParentFile().mkdirs();

            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), exportDDLSetting.getSrcFileEncoding())));

            monitor.subTaskWithCounter("writing drop ddl");

            out.print(ddlCreator.getDropDDL(diagram));

            monitor.worked(1);

            monitor.subTaskWithCounter("writing create ddl");

            out.print(ddlCreator.getCreateDDL(diagram));

            monitor.worked(1);

        } finally {
            if (out != null) {
                out.close();
            }
        }

    }

    @Override
    public File getOutputFileOrDir() {
        return FileUtils.getFile(projectDir, exportDDLSetting.getDdlOutput());
    }
}
