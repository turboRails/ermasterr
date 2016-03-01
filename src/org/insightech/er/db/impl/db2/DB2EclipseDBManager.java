package org.insightech.er.db.impl.db2;

import org.eclipse.swt.widgets.Composite;
import org.insightech.er.db.EclipseDBManagerBase;
import org.insightech.er.db.impl.db2.tablespace.DB2TablespaceDialog;
import org.insightech.er.editor.view.dialog.element.table_view.tab.AdvancedComposite;
import org.insightech.er.editor.view.dialog.outline.tablespace.TablespaceDialog;

public class DB2EclipseDBManager extends EclipseDBManagerBase {

    @Override
    public String getId() {
        return DB2DBManager.ID;
    }

    @Override
    public AdvancedComposite createAdvancedComposite(final Composite composite) {
        return new DB2AdvancedComposite(composite);
    }

    @Override
    public TablespaceDialog createTablespaceDialog() {
        return new DB2TablespaceDialog();
    }

}
