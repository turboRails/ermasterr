package org.insightech.er.editor.view.property_source;

import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.insightech.er.editor.ERDiagramMultiPageEditor;
import org.insightech.er.editor.controller.editpart.element.ERDiagramEditPart;
import org.insightech.er.editor.controller.editpart.element.node.ERTableEditPart;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;

public class ERDiagramPropertySourceProvider implements IPropertySourceProvider {

    private final ERDiagramMultiPageEditor editor;

    public ERDiagramPropertySourceProvider(final ERDiagramMultiPageEditor editor) {
        this.editor = editor;
    }

    @Override
    public IPropertySource getPropertySource(final Object object) {
        if (object instanceof ERDiagramEditPart) {
            final ERDiagram diagram = (ERDiagram) ((ERDiagramEditPart) object).getModel();
            return new ERDiagramPropertySource(editor, diagram);

        } else if (object instanceof ERTableEditPart) {
            final ERTable table = (ERTable) ((ERTableEditPart) object).getModel();
            return new ERTablePropertySource(editor, table);

        }

        return null;
    }
}
