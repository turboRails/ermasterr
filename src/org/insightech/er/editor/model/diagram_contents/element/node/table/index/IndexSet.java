package org.insightech.er.editor.model.diagram_contents.element.node.table.index;

import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ObjectListModel;

public class IndexSet extends AbstractModel implements ObjectListModel {

    private static final long serialVersionUID = 3691276015432133679L;

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getName() {
        return ResourceString.getResourceString("label.object.type.index_list");
    }

    @Override
    public String getObjectType() {
        return "list";
    }
}
