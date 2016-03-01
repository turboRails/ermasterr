package org.insightech.er.db.impl.oracle;

import org.eclipse.swt.widgets.Composite;
import org.insightech.er.db.EclipseDBManagerBase;
import org.insightech.er.db.impl.oracle.tablespace.OracleTablespaceDialog;
import org.insightech.er.editor.view.dialog.element.table_view.tab.AdvancedComposite;
import org.insightech.er.editor.view.dialog.outline.tablespace.TablespaceDialog;

public class OracleEclipseDBManager extends EclipseDBManagerBase {

    @Override
    public String getId() {
        return OracleDBManager.ID;
    }

    @Override
    public AdvancedComposite createAdvancedComposite(final Composite composite) {
        return new OracleAdvancedComposite(composite);
    }

    @Override
    public TablespaceDialog createTablespaceDialog() {
        return new OracleTablespaceDialog();
    }

}
