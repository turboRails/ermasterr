package org.insightech.er.editor.controller.command.diagram_contents.not_element.group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GroupSet;

public class DeleteGroupCommand extends AbstractCommand {

    private final ERDiagram diagram;

    private final GroupSet groupSet;

    private final ColumnGroup columnGroup;

    private final Map<TableView, List<Column>> oldColumnListMap;

    public DeleteGroupCommand(final ERDiagram diagram, final ColumnGroup columnGroup) {
        groupSet = diagram.getDiagramContents().getGroups();
        this.columnGroup = columnGroup;
        this.diagram = diagram;

        oldColumnListMap = new HashMap<TableView, List<Column>>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        for (final NormalColumn column : columnGroup.getColumns()) {
            diagram.getDiagramContents().getDictionary().remove(column);
        }

        for (final TableView tableView : diagram.getDiagramContents().getContents().getTableViewList()) {
            final List<Column> columns = tableView.getColumns();
            final List<Column> oldColumns = new ArrayList<Column>(columns);

            oldColumnListMap.put(tableView, oldColumns);

            for (final Iterator<Column> iter = columns.iterator(); iter.hasNext();) {
                final Column column = iter.next();

                if (column instanceof ColumnGroup) {
                    if (column == columnGroup) {
                        iter.remove();
                    }
                }
            }

            tableView.setColumns(columns);
        }

        groupSet.remove(columnGroup);

        diagram.refreshVisuals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        for (final NormalColumn column : columnGroup.getColumns()) {
            diagram.getDiagramContents().getDictionary().add(column);
        }

        for (final TableView tableView : oldColumnListMap.keySet()) {
            final List<Column> oldColumns = oldColumnListMap.get(tableView);
            tableView.setColumns(oldColumns);
        }

        groupSet.add(columnGroup);

        diagram.refreshVisuals();
    }
}
