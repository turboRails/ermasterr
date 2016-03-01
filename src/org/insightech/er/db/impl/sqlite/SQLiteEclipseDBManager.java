package org.insightech.er.db.impl.sqlite;

import org.eclipse.swt.widgets.Composite;
import org.insightech.er.db.EclipseDBManagerBase;
import org.insightech.er.editor.view.dialog.element.table_view.tab.AdvancedComposite;
import org.insightech.er.editor.view.dialog.outline.tablespace.TablespaceDialog;

public class SQLiteEclipseDBManager extends EclipseDBManagerBase {

    @Override
    public String getId() {
        return SQLiteDBManager.ID;
    }

    @Override
    public AdvancedComposite createAdvancedComposite(final Composite composite) {
        return new SQLiteAdvancedComposite(composite);
    }

    @Override
    public TablespaceDialog createTablespaceDialog() {
        return null;
    }

}
