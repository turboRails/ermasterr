package org.insightech.er.db.impl.sqlserver;

import org.eclipse.swt.widgets.Composite;
import org.insightech.er.db.EclipseDBManagerBase;
import org.insightech.er.db.impl.sqlserver.tablespace.SqlServerTablespaceDialog;
import org.insightech.er.editor.view.dialog.element.table_view.tab.AdvancedComposite;
import org.insightech.er.editor.view.dialog.outline.tablespace.TablespaceDialog;

public class SqlServerEclipseDBManager extends EclipseDBManagerBase {

    @Override
    public String getId() {
        return SqlServerDBManager.ID;
    }

    @Override
    public AdvancedComposite createAdvancedComposite(final Composite composite) {
        return new SqlServerAdvancedComposite(composite);
    }

    @Override
    public TablespaceDialog createTablespaceDialog() {
        return new SqlServerTablespaceDialog();
    }

}
