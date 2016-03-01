package org.insightech.er.editor.view.property_source;

import org.eclipse.gef.commands.Command;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramMultiPageEditor;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.table_view.ChangeTableViewPropertyCommand;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;

public class ERTablePropertySource extends AbstractPropertySource {

    private final ERTable table;

    public ERTablePropertySource(final ERDiagramMultiPageEditor editor, final ERTable table) {
        super(editor);
        this.table = table;
    }

    @Override
    public Object getEditableValue() {
        return table;
    }

    @Override
    public IPropertyDescriptor[] getPropertyDescriptors() {
        return new IPropertyDescriptor[] {new TextPropertyDescriptor("physicalName", ResourceString.getResourceString("label.physical.name")), new TextPropertyDescriptor("logicalName", ResourceString.getResourceString("label.logical.name"))};
    }

    @Override
    public Object getPropertyValue(final Object id) {
        if (id.equals("physicalName")) {
            return table.getPhysicalName() != null ? table.getPhysicalName() : "";
        }
        if (id.equals("logicalName")) {
            return table.getLogicalName() != null ? table.getLogicalName() : "";
        }
        return null;
    }

    @Override
    public boolean isPropertySet(final Object id) {
        if (id.equals("physicalName")) {
            return true;
        }
        if (id.equals("logicalName")) {
            return true;
        }
        return false;
    }

    @Override
    protected Command createSetPropertyCommand(final Object id, final Object value) {
        final ERTable copyTable = table.copyData();

        if (id.equals("physicalName")) {
            copyTable.setPhysicalName(String.valueOf(value));

        } else if (id.equals("logicalName")) {
            copyTable.setLogicalName(String.valueOf(value));
        }

        final ChangeTableViewPropertyCommand command = new ChangeTableViewPropertyCommand(table, copyTable);

        return command;
    }

}
