package org.insightech.er.test;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.db.impl.mysql.MySQLDBManager;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.testdata.TestData;
import org.insightech.er.editor.view.dialog.testdata.TestDataManageDialog;

public class TableTest {

    private final Display display = new Display();
    private final Shell shell = new Shell(display);

    public static void main(final String[] args) throws Exception {
        new ERDiagramActivator();
        new TableTest();
    }

    public TableTest() {
        initialize(shell);
    }

    private void initialize(final Composite parent) {
        final List<TestData> testDataList = new ArrayList<TestData>();
        final TestDataManageDialog dialog = new TestDataManageDialog(shell, new ERDiagram(MySQLDBManager.ID), testDataList);

        dialog.open();
    }
}
