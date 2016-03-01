package org.insightech.er.editor.view.drag_drop;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.dnd.AbstractTransferDragSourceListener;
import org.eclipse.gef.dnd.TemplateTransfer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;

public class ERDiagramTransferDragSourceListener extends AbstractTransferDragSourceListener {

    public static final String REQUEST_TYPE_MOVE_COLUMN = "move column";

    public static final String REQUEST_TYPE_MOVE_COLUMN_GROUP = "move column group";

    public static final String REQUEST_TYPE_ADD_COLUMN_GROUP = "add column group";

    public static final String MOVE_COLUMN_GROUP_PARAM_PARENT = "parent";

    public static final String MOVE_COLUMN_GROUP_PARAM_GROUP = "group";

    public static final String REQUEST_TYPE_ADD_WORD = "add word";

    private final EditPartViewer dragSourceViewer;

    public ERDiagramTransferDragSourceListener(final EditPartViewer dragSourceViewer, final Transfer xfer) {
        super(dragSourceViewer, xfer);

        this.dragSourceViewer = dragSourceViewer;
    }

    @Override
    public void dragStart(final DragSourceEvent dragsourceevent) {
        super.dragStart(dragsourceevent);

        final Object target = getTargetModel(dragsourceevent);

        if (target != null && target == dragSourceViewer.findObjectAt(new Point(dragsourceevent.x, dragsourceevent.y)).getModel()) {
            final TemplateTransfer transfer = (TemplateTransfer) getTransfer();
            transfer.setObject(createTransferData(dragsourceevent));

        } else {
            dragsourceevent.doit = false;
        }
    }

    @Override
    public void dragSetData(final DragSourceEvent event) {
        event.data = createTransferData(event);
    }

    private Object getTargetModel(final DragSourceEvent event) {
        final List editParts = dragSourceViewer.getSelectedEditParts();
        if (editParts.size() != 1) {
            // ドラッグアンドドロップは選択されているオブジェクトが１つのときのみ可能とする
            return null;
        }

        final EditPart editPart = (EditPart) editParts.get(0);

        final Object model = editPart.getModel();
        if (model instanceof NormalColumn || model instanceof ColumnGroup || model instanceof Word) {
            return model;
        }

        return null;
    }

    private Object createTransferData(final DragSourceEvent event) {
        final List editParts = dragSourceViewer.getSelectedEditParts();
        if (editParts.size() != 1) {
            // ドラッグアンドドロップは選択されているオブジェクトが１つのときのみ可能とする
            return null;
        }

        final EditPart editPart = (EditPart) editParts.get(0);

        final Object model = editPart.getModel();

        if (model instanceof NormalColumn) {
            final NormalColumn normalColumn = (NormalColumn) model;
            if (normalColumn.getColumnHolder() instanceof ColumnGroup) {
                final Map<String, Object> map = new HashMap<String, Object>();
                map.put(MOVE_COLUMN_GROUP_PARAM_PARENT, editPart.getParent().getModel());
                map.put(MOVE_COLUMN_GROUP_PARAM_GROUP, normalColumn.getColumnHolder());

                return map;
            }

            return model;

        } else if (model instanceof ColumnGroup) {
            final Map<String, Object> map = new HashMap<String, Object>();
            map.put(MOVE_COLUMN_GROUP_PARAM_PARENT, editPart.getParent().getModel());
            map.put(MOVE_COLUMN_GROUP_PARAM_GROUP, model);

            return map;

        } else if (model instanceof Word) {
            return model;
        }

        return null;
    }

}
