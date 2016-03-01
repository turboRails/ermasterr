package org.insightech.er.editor.view.property_source;

import org.eclipse.gef.commands.Command;
import org.eclipse.ui.views.properties.IPropertySource;
import org.insightech.er.editor.ERDiagramMultiPageEditor;

public abstract class AbstractPropertySource implements IPropertySource {

    private final ERDiagramMultiPageEditor editor;

    private boolean processing = false;

    public AbstractPropertySource(final ERDiagramMultiPageEditor editor) {
        this.editor = editor;
    }

    @Override
    public void resetPropertyValue(final Object paramObject) {}

    @Override
    public synchronized void setPropertyValue(final Object id, final Object value) {
        if (!processing) {
            try {
                processing = true;

                final Command command = createSetPropertyCommand(id, value);

                if (command != null) {
                    editor.getActiveEditor().getGraphicalViewer().getEditDomain().getCommandStack().execute(command);
                }

            } finally {
                processing = false;
            }
        }
    }

    abstract protected Command createSetPropertyCommand(Object id, Object value);
}
