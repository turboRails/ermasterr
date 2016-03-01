package org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.view.dialog.element.relation.RelationByExistingColumnsDialog;

public class CreateRelationByExistingColumnsCommand extends AbstractCreateRelationCommand {

    private Relation relation;

    private List<NormalColumn> referencedColumnList;

    private List<NormalColumn> foreignKeyColumnList;

    private final List<Boolean> notNullList;

    private boolean notNull;

    private boolean unique;

    private final List<Word> wordList;

    public CreateRelationByExistingColumnsCommand() {
        super();
        wordList = new ArrayList<Word>();
        notNullList = new ArrayList<Boolean>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        final ERTable sourceTable = (ERTable) source.getModel();
        final TableView targetTable = (TableView) target.getModel();

        relation.setSource(sourceTable);
        relation.setTargetWithoutForeignKey(targetTable);

        for (int i = 0; i < foreignKeyColumnList.size(); i++) {
            final NormalColumn foreignKeyColumn = foreignKeyColumnList.get(i);

            wordList.add(foreignKeyColumn.getWord());

            sourceTable.getDiagram().getDiagramContents().getDictionary().remove(foreignKeyColumn);

            foreignKeyColumn.addReference(referencedColumnList.get(i), relation);
            foreignKeyColumn.setWord(null);

            foreignKeyColumn.setNotNull(notNull);
        }

        relation.getSource().refreshSourceConnections();
        relation.getTarget().refresh();

        // targetTable.setDirty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        final ERTable sourceTable = (ERTable) source.getModel();

        relation.setSource(null);
        relation.setTargetWithoutForeignKey(null);

        for (int i = 0; i < foreignKeyColumnList.size(); i++) {
            final NormalColumn foreignKeyColumn = foreignKeyColumnList.get(i);

            foreignKeyColumn.setNotNull(notNullList.get(i));
            foreignKeyColumn.removeReference(relation);
            foreignKeyColumn.setWord(wordList.get(i));

            sourceTable.getDiagram().getDiagramContents().getDictionary().add(foreignKeyColumn);
        }

        // targetTable.setDirty();

        getSourceModel().refreshSourceConnections();
        getTargetModel().refresh();
    }

    public boolean selectColumns() {
        if (target == null) {
            return false;
        }

        final ERTable sourceTable = (ERTable) source.getModel();
        final TableView targetTable = (TableView) target.getModel();

        final Map<NormalColumn, List<NormalColumn>> referencedMap = new HashMap<NormalColumn, List<NormalColumn>>();
        final Map<Relation, Set<NormalColumn>> foreignKeySetMap = new HashMap<Relation, Set<NormalColumn>>();

        for (final NormalColumn normalColumn : targetTable.getNormalColumns()) {
            final NormalColumn rootReferencedColumn = normalColumn.getRootReferencedColumn();
            if (rootReferencedColumn != null) {
                List<NormalColumn> foreignKeyList = referencedMap.get(rootReferencedColumn);

                if (foreignKeyList == null) {
                    foreignKeyList = new ArrayList<NormalColumn>();
                    referencedMap.put(rootReferencedColumn, foreignKeyList);
                }

                foreignKeyList.add(normalColumn);

                for (final Relation relation : normalColumn.getRelationList()) {
                    Set<NormalColumn> foreignKeySet = foreignKeySetMap.get(relation);
                    if (foreignKeySet == null) {
                        foreignKeySet = new HashSet<NormalColumn>();
                        foreignKeySetMap.put(relation, foreignKeySet);
                    }

                    foreignKeySet.add(normalColumn);
                }
            }
        }

        final List<NormalColumn> candidateForeignKeyColumns = new ArrayList<NormalColumn>();

        for (final NormalColumn column : targetTable.getNormalColumns()) {
            if (!column.isForeignKey()) {
                candidateForeignKeyColumns.add(column);
            }
        }

        if (candidateForeignKeyColumns.isEmpty()) {
            ERDiagramActivator.showErrorDialog("error.no.candidate.of.foreign.key.exist");
            return false;
        }

        final RelationByExistingColumnsDialog dialog = new RelationByExistingColumnsDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), sourceTable, candidateForeignKeyColumns, referencedMap, foreignKeySetMap);

        if (dialog.open() == IDialogConstants.OK_ID) {
            notNull = false;
            unique = false;
            notNullList.clear();

            for (final NormalColumn foreignKeyColumn : dialog.getForeignKeyColumnList()) {
                notNullList.add(foreignKeyColumn.isNotNull());

                if (foreignKeyColumn.isNotNull()) {
                    notNull = true;
                }
                if (foreignKeyColumn.isUniqueKey() || foreignKeyColumn.isSinglePrimaryKey()) {
                    unique = true;
                }
            }

            relation = new Relation(dialog.isReferenceForPK(), dialog.getReferencedComplexUniqueKey(), dialog.getReferencedColumn(), notNull, unique);
            referencedColumnList = dialog.getReferencedColumnList();
            foreignKeyColumnList = dialog.getForeignKeyColumnList();

        } else {
            return false;
        }

        return true;
    }
}
