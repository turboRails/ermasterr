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
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.CopyColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.CopyGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GroupSet;

public class ChangeGroupCommand extends AbstractCommand {

    private final GroupSet groupSet;

    private final List<CopyGroup> oldCopyGroups;

    private final List<CopyGroup> newGroups;

    private final Map<TableView, List<Column>> oldColumnListMap;

    private final ERDiagram diagram;

    public ChangeGroupCommand(final ERDiagram diagram, final GroupSet groupSet, final List<CopyGroup> newGroups) {
        this.diagram = diagram;

        this.groupSet = groupSet;

        this.newGroups = newGroups;

        oldCopyGroups = new ArrayList<CopyGroup>();
        oldColumnListMap = new HashMap<TableView, List<Column>>();

        for (final ColumnGroup columnGroup : groupSet) {
            final CopyGroup oldCopyGroup = new CopyGroup(columnGroup);
            oldCopyGroups.add(oldCopyGroup);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        final ERDiagram diagram = this.diagram;

        groupSet.clear();
        oldColumnListMap.clear();

        for (final CopyGroup oldCopyColumnGroup : oldCopyGroups) {
            for (final NormalColumn column : oldCopyColumnGroup.getColumns()) {
                diagram.getDiagramContents().getDictionary().remove(((CopyColumn) column).getOriginalColumn());
            }
        }

        for (final CopyGroup newCopyColumnGroup : newGroups) {
            groupSet.add(newCopyColumnGroup.restructure(diagram));
        }

        for (final TableView tableView : this.diagram.getDiagramContents().getContents().getTableViewList()) {
            final List<Column> columns = tableView.getColumns();
            final List<Column> oldColumns = new ArrayList<Column>(columns);

            oldColumnListMap.put(tableView, oldColumns);

            for (final Iterator<Column> iter = columns.iterator(); iter.hasNext();) {
                final Column column = iter.next();

                if (column instanceof ColumnGroup) {
                    if (!groupSet.contains((ColumnGroup) column)) {
                        iter.remove();
                    }
                }
            }

            tableView.setColumns(columns);
        }

        this.diagram.refreshVisuals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        final ERDiagram diagram = this.diagram;

        groupSet.clear();

        for (final CopyGroup newCopyColumnGroup : newGroups) {
            for (final NormalColumn column : newCopyColumnGroup.getColumns()) {
                diagram.getDiagramContents().getDictionary().remove(((CopyColumn) column).getOriginalColumn());
            }
        }

        for (final CopyGroup copyGroup : oldCopyGroups) {
            final ColumnGroup group = copyGroup.restructure(diagram);
            groupSet.add(group);
        }

        for (final TableView tableView : oldColumnListMap.keySet()) {
            final List<Column> oldColumns = oldColumnListMap.get(tableView);
            tableView.setColumns(oldColumns);
        }

        this.diagram.refreshVisuals();
    }
}
