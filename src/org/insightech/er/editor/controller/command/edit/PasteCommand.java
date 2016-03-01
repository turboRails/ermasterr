package org.insightech.er.editor.controller.command.edit;

import java.util.ArrayList;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.Bendpoint;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeSet;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GroupSet;

public class PasteCommand extends AbstractCommand {

    private final ERDiagram diagram;

    private final GraphicalViewer viewer;

    // 貼り付け対象の一覧
    private final NodeSet nodeElements;

    // 貼り付け時に追加するグループ列の一覧
    private final GroupSet columnGroups;

    private final Category category;

    /**
     * 貼り付けコマンドを作成します。
     * 
     * @param editor
     * @param nodeElements
     */
    public PasteCommand(final ERDiagramEditor editor, final NodeSet nodeElements, final int x, final int y) {
        viewer = editor.getGraphicalViewer();
        diagram = (ERDiagram) viewer.getContents().getModel();
        category = diagram.getCurrentCategory();

        this.nodeElements = nodeElements;

        columnGroups = new GroupSet();

        final GroupSet groupSet = diagram.getDiagramContents().getGroups();

        // 貼り付け対象に対して処理を繰り返します
        for (final NodeElement nodeElement : nodeElements) {
            nodeElement.setLocation(new Location(nodeElement.getX() + x, nodeElement.getY() + y, nodeElement.getWidth(), nodeElement.getHeight()));

            for (final ConnectionElement connection : nodeElement.getIncomings()) {
                for (final Bendpoint bendpoint : connection.getBendpoints()) {
                    bendpoint.transform(x, y);
                }
            }

            // 貼り付け対象がテーブルの場合
            if (nodeElement instanceof ERTable) {

                final ERTable table = (ERTable) nodeElement;

                // 列に対して処理を繰り返します
                for (final Column column : new ArrayList<Column>(table.getColumns())) {

                    // 列がグループ列の場合
                    if (column instanceof ColumnGroup) {
                        final ColumnGroup group = (ColumnGroup) column;

                        // この図のグループ列でない場合
                        if (!groupSet.contains(group)) {
                            // 対象のグループ列に追加します。
                            columnGroups.add(group);

                        } else {
                            if (groupSet.findSame(group) == null) {
                                final ColumnGroup equalColumnGroup = groupSet.find(group);

                                table.replaceColumnGroup(group, equalColumnGroup);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 貼り付け処理を実行する
     */
    @Override
    protected void doExecute() {
        final GroupSet columnGroupSet = diagram.getDiagramContents().getGroups();

        // 図にノードを追加します。
        for (final NodeElement nodeElement : nodeElements) {
            if (category != null) {
                category.add(nodeElement);
            }
            diagram.addContent(nodeElement);
        }

        // グループ列を追加します。
        for (final ColumnGroup columnGroup : columnGroups) {
            columnGroupSet.add(columnGroup);

            for (final NormalColumn normalColumn : columnGroup.getColumns()) {
                diagram.getDiagramContents().getDictionary().add(normalColumn);
            }
        }

        diagram.refreshChildren();

        // 貼り付けられたテーブルを選択状態にします。
        setFocus();
    }

    /**
     * 貼り付け処理を元に戻す
     */
    @Override
    protected void doUndo() {
        final GroupSet columnGroupSet = diagram.getDiagramContents().getGroups();

        // 図からノードを削除します。
        for (final NodeElement nodeElement : nodeElements) {
            if (category != null) {
                category.remove(nodeElement);
            }
            diagram.removeContent(nodeElement);
        }

        // グループ列を削除します。
        for (final ColumnGroup columnGroup : columnGroups) {
            columnGroupSet.remove(columnGroup);

            for (final NormalColumn normalColumn : columnGroup.getColumns()) {
                diagram.getDiagramContents().getDictionary().remove(normalColumn);
            }
        }

        diagram.refreshChildren();
    }

    /**
     * 貼り付けられたテーブルを選択状態にします。
     */
    private void setFocus() {
        // 貼り付けられたテーブルを選択状態にします。
        for (final NodeElement nodeElement : nodeElements) {
            final EditPart editPart = (EditPart) viewer.getEditPartRegistry().get(nodeElement);

            viewer.getSelectionManager().appendSelection(editPart);
        }
    }
}
