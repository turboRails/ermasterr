package org.insightech.er.db.impl.h2;

import org.eclipse.swt.widgets.Composite;
import org.insightech.er.db.EclipseDBManagerBase;
import org.insightech.er.editor.view.dialog.element.table_view.tab.AdvancedComposite;
import org.insightech.er.editor.view.dialog.outline.tablespace.TablespaceDialog;

public class H2EclipseDBManager extends EclipseDBManagerBase {

    @Override
    public String getId() {
        return H2DBManager.ID;
    }

    @Override
    public AdvancedComposite createAdvancedComposite(final Composite composite) {
        return new H2AdvancedComposite(composite);
    }

    @Override
    public TablespaceDialog createTablespaceDialog() {
        return null;
    }

}
