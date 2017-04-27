package org.insightech.er.db.impl.informix;

import org.eclipse.swt.widgets.Composite;
import org.insightech.er.db.EclipseDBManagerBase;
import org.insightech.er.editor.view.dialog.element.table_view.tab.AdvancedComposite;
import org.insightech.er.editor.view.dialog.outline.tablespace.TablespaceDialog;

public class InformixEclipseDBManager extends EclipseDBManagerBase {

	@Override
	public String getId() {
		return InformixDBManager.ID;
	}

	@Override
	public AdvancedComposite createAdvancedComposite(Composite composite) {
		return new InformixAdvancedComposite(composite);
	}

	@Override
	public TablespaceDialog createTablespaceDialog()
	{
		return null;
	}
}
