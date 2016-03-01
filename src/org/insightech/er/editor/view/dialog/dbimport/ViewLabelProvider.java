package org.insightech.er.editor.view.dialog.dbimport;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.swt.graphics.Image;
import org.insightech.er.editor.model.StringObjectModel;
import org.insightech.er.editor.model.dbimport.DBObject;
import org.insightech.er.editor.model.testdata.TestData;

public class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {

    @Override
    public String getText(final Object element) {
        final TreeNode treeNode = (TreeNode) element;

        final Object value = treeNode.getValue();
        if (value instanceof DBObject) {
            final DBObject dbObject = (DBObject) value;
            return dbObject.getName();

        } else if (value instanceof StringObjectModel) {
            final StringObjectModel object = (StringObjectModel) value;
            return object.getName();

        } else if (value instanceof TestData) {
            final TestData testData = (TestData) value;
            return testData.getName();

        }

        return value.toString();
    }

    @Override
    public Image getColumnImage(final Object element, final int columnIndex) {
        return null;
    }

    @Override
    public String getColumnText(final Object element, final int columnIndex) {
        return "xxx";
    }

}
