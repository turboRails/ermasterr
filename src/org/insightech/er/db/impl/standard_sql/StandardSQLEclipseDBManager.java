package org.insightech.er.db.impl.standard_sql;

import org.eclipse.swt.widgets.Composite;
import org.insightech.er.db.EclipseDBManagerBase;
import org.insightech.er.editor.view.dialog.element.table_view.tab.AdvancedComposite;
import org.insightech.er.editor.view.dialog.outline.tablespace.TablespaceDialog;

public class StandardSQLEclipseDBManager extends EclipseDBManagerBase {

    @Override
    public String getId() {
        return StandardSQLDBManager.ID;
    }

    @Override
    public AdvancedComposite createAdvancedComposite(final Composite composite) {
        return new StandardSQLAdvancedComposite(composite);
    }

    @Override
    public TablespaceDialog createTablespaceDialog() {
        return null;
    }

}
