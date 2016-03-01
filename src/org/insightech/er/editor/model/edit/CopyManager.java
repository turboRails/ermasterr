package org.insightech.er.editor.model.edit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.DiagramContents;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeSet;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.model_properties.ModelProperties;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableViewProperties;
import org.insightech.er.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.UniqueWordDictionary;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceSet;
import org.insightech.er.editor.model.settings.Settings;

public class CopyManager {

    private static NodeSet copyList = new NodeSet();

    private static int numberOfCopy;

    private Map<NodeElement, NodeElement> nodeElementMap;

    private UniqueWordDictionary uniqueWordDictionary;

    public CopyManager(final ERDiagram diagram) {
        if (diagram != null) {
            uniqueWordDictionary = new UniqueWordDictionary();
            uniqueWordDictionary.init(diagram);
        }
    }

    public static void copy(final NodeSet nodeElementList) {
        final CopyManager copyManager = new CopyManager(null);
        copyList = copyManager.copyNodeElementList(nodeElementList);
    }

    public static NodeSet paste(final ERDiagram diagram) {
        numberOfCopy++;
        final CopyManager copyManager = new CopyManager(diagram);
        return copyManager.copyNodeElementList(copyList);
    }

    public static void clear() {
        copyList.clear();
        numberOfCopy = 0;
    }

    public static boolean canCopy() {
        if (copyList != null && !copyList.isEmpty()) {
            return true;
        }

        return false;
    }

    public static int getNumberOfCopy() {
        return numberOfCopy;
    }

    public Map<NodeElement, NodeElement> getNodeElementMap() {
        return nodeElementMap;
    }

    public NodeSet copyNodeElementList(final NodeSet nodeElementList) {
        final NodeSet copyList = new NodeSet();

        nodeElementMap = new HashMap<NodeElement, NodeElement>();
        final Map<Column, Column> columnMap = new HashMap<Column, Column>();
        final Map<ComplexUniqueKey, ComplexUniqueKey> complexUniqueKeyMap = new HashMap<ComplexUniqueKey, ComplexUniqueKey>();

        // 選択されているノードのEditPartに対して処理を繰り返します
        for (final NodeElement nodeElement : nodeElementList) {

            if (nodeElement instanceof ModelProperties) {
                // モデルプロパティの場合、何もしません
                continue;

            } else if (nodeElement instanceof Category) {
                continue;
            }

            // ノードを複製して、コピー情報に追加します
            final NodeElement cloneNodeElement = nodeElement.clone();
            copyList.addNodeElement(cloneNodeElement);

            nodeElementMap.put(nodeElement, cloneNodeElement);

            if (nodeElement instanceof ERTable) {
                // ノードがテーブルの場合
                // 列とインデックスと複合一意キーを複製します。
                copyColumnAndIndex((ERTable) nodeElement, (ERTable) cloneNodeElement, columnMap, complexUniqueKeyMap);

            } else if (nodeElement instanceof View) {
                // ノードがビューの場合
                // 列を複製します。
                copyColumn((View) nodeElement, (View) cloneNodeElement, columnMap);
            }
        }

        // 複製後のノードに対して、接続を作りなおします
        final Map<ConnectionElement, ConnectionElement> connectionElementMap = new HashMap<ConnectionElement, ConnectionElement>();

        // 接続を張りなおします
        for (final NodeElement nodeElement : nodeElementMap.keySet()) {
            final NodeElement cloneNodeElement = nodeElementMap.get(nodeElement);

            // 複製元ノードに入ってくる接続を複製先に張りなおします
            replaceIncoming(nodeElement, cloneNodeElement, connectionElementMap, nodeElementMap);
        }

        // 外部キーの参照を作り直します
        for (final NodeElement nodeElement : nodeElementMap.keySet()) {

            if (nodeElement instanceof ERTable) {
                final ERTable table = (ERTable) nodeElement;

                // 複製元テーブルの列に対して処理を繰り返します
                for (final Column column : table.getColumns()) {
                    if (column instanceof NormalColumn) {
                        final NormalColumn oldColumn = (NormalColumn) column;

                        // 外部キーの場合
                        if (oldColumn.isForeignKey()) {
                            final NormalColumn newColumn = (NormalColumn) columnMap.get(oldColumn);
                            newColumn.renewRelationList();

                            for (final Relation oldRelation : oldColumn.getRelationList()) {

                                // 複製された関連の取得
                                final Relation newRelation = (Relation) connectionElementMap.get(oldRelation);

                                if (newRelation != null) {
                                    // 関連も複製されている場合

                                    final NormalColumn oldReferencedColumn = newRelation.getReferencedColumn();

                                    // ユニークキーを参照している場合
                                    if (oldReferencedColumn != null) {
                                        final NormalColumn newReferencedColumn = (NormalColumn) columnMap.get(oldReferencedColumn);

                                        newRelation.setReferencedColumn(newReferencedColumn);

                                    }

                                    final ComplexUniqueKey oldReferencedComplexUniqueKey = newRelation.getReferencedComplexUniqueKey();

                                    // 複合ユニークキーを参照している場合
                                    if (oldReferencedComplexUniqueKey != null) {
                                        final ComplexUniqueKey newReferencedComplexUniqueKey = complexUniqueKeyMap.get(oldReferencedComplexUniqueKey);
                                        if (newReferencedComplexUniqueKey != null) {
                                            newRelation.setReferencedComplexUniqueKey(newReferencedComplexUniqueKey);
                                        }
                                    }

                                    NormalColumn targetReferencedColumn = null;

                                    for (final NormalColumn referencedColumn : oldColumn.getReferencedColumnList()) {
                                        if (referencedColumn.getColumnHolder() == oldRelation.getSourceTableView()) {
                                            targetReferencedColumn = referencedColumn;
                                            break;
                                        }
                                    }
                                    final NormalColumn newReferencedColumn = (NormalColumn) columnMap.get(targetReferencedColumn);

                                    newColumn.removeReference(oldRelation);
                                    newColumn.addReference(newReferencedColumn, newRelation);

                                } else {
                                    // 複製先の列を外部キーではなく、通常の列に作り直します
                                    newColumn.removeReference(oldRelation);
                                }
                            }
                        }
                    }
                }

            }
        }

        return copyList;
    }

    /**
     * 複製元ノードに入ってくる接続を複製先に張りなおします
     */
    private static void replaceIncoming(final NodeElement from, final NodeElement to, final Map<ConnectionElement, ConnectionElement> connectionElementMap, final Map<NodeElement, NodeElement> nodeElementMap) {
        final List<ConnectionElement> cloneIncomings = new ArrayList<ConnectionElement>();

        // 複製元ノードに入ってくる接続に対して処理を繰り返します
        for (final ConnectionElement incoming : from.getIncomings()) {
            final NodeElement oldSource = incoming.getSource();

            // 接続元の複製を取得します
            final NodeElement newSource = nodeElementMap.get(oldSource);

            // 接続元も複製されている場合
            if (newSource != null) {

                // 接続を複製します。
                final ConnectionElement cloneIncoming = incoming.clone();

                cloneIncoming.setSourceAndTarget(newSource, to);

                connectionElementMap.put(incoming, cloneIncoming);

                cloneIncomings.add(cloneIncoming);

                newSource.addOutgoing(cloneIncoming);
            }
        }

        to.setIncoming(cloneIncomings);
    }

    /**
     * 列とインデックスの情報を複製します。
     * 
     * @param from
     *            元のテーブル
     * @param to
     *            複製されたテーブル
     * @param columnMap
     *            キー：元の列、値：複製後の列
     */
    private void copyColumnAndIndex(final ERTable from, final ERTable to, final Map<Column, Column> columnMap, final Map<ComplexUniqueKey, ComplexUniqueKey> complexUniqueKeyMap) {
        copyColumn(from, to, columnMap);
        copyIndex(from, to, columnMap);
        copyComplexUniqueKey(from, to, columnMap, complexUniqueKeyMap);
    }

    private void copyColumn(final TableView from, final TableView to, final Map<Column, Column> columnMap) {
        // 複製後の列の一覧
        final List<Column> cloneColumns = new ArrayList<Column>();

        // 元のテーブルの列に対して、処理を繰り返します。
        for (final Column column : from.getColumns()) {

            Column cloneColumn = null;

            if (column instanceof ColumnGroup) {
                // グループ列の場合
                // 複製は特にしません。
                cloneColumn = column;

            } else {
                // 普通の列の場合
                // 列を複製します。
                final NormalColumn cloneNormalColumn = (NormalColumn) column.clone();

                if (uniqueWordDictionary != null) {
                    final Word word = uniqueWordDictionary.getUniqueWord(cloneNormalColumn.getWord(), false);
                    if (word != null) {
                        cloneNormalColumn.setWord(word);
                    }
                }

                cloneColumn = cloneNormalColumn;
            }

            cloneColumns.add(cloneColumn);

            columnMap.put(column, cloneColumn);
        }

        // 複製後のテーブルに、複製後の列一覧を設定します。
        to.setColumns(cloneColumns);
    }

    private static void copyComplexUniqueKey(final ERTable from, final ERTable to, final Map<Column, Column> columnMap, final Map<ComplexUniqueKey, ComplexUniqueKey> complexUniqueKeyMap) {
        final List<ComplexUniqueKey> cloneComplexUniqueKeyList = new ArrayList<ComplexUniqueKey>();

        // 元のテーブルの複合一意キーに対して、処理を繰り返します。
        for (final ComplexUniqueKey complexUniqueKey : from.getComplexUniqueKeyList()) {

            // 複合一意キーを複製します。
            final ComplexUniqueKey cloneComplexUniqueKey = (ComplexUniqueKey) complexUniqueKey.clone();
            complexUniqueKeyMap.put(complexUniqueKey, cloneComplexUniqueKey);

            final List<NormalColumn> cloneColumns = new ArrayList<NormalColumn>();

            // 複製後の複合一意キーの列に対して、処理を繰り返します。
            for (final NormalColumn column : cloneComplexUniqueKey.getColumnList()) {
                // 複製後の列を取得して、複製後の複合一意キーの列一覧に追加します。
                cloneColumns.add((NormalColumn) columnMap.get(column));
            }

            // 複製後の複合一意キーに、複製後の複合一意キーの列一覧を設定します。
            cloneComplexUniqueKey.setColumnList(cloneColumns);

            cloneComplexUniqueKeyList.add(cloneComplexUniqueKey);
        }

        // 複製後のテーブルに、複製後のインデックス一覧を設定します。
        to.setComplexUniqueKeyList(cloneComplexUniqueKeyList);
    }

    private static void copyIndex(final ERTable from, final ERTable to, final Map<Column, Column> columnMap) {
        final List<Index> cloneIndexes = new ArrayList<Index>();

        // 元のテーブルのインデックスに対して、処理を繰り返します。
        for (final Index index : from.getIndexes()) {

            // インデックスを複製します。
            final Index cloneIndex = index.clone();

            final List<NormalColumn> cloneIndexColumns = new ArrayList<NormalColumn>();

            // 複製後のインデックスの列に対して、処理を繰り返します。
            for (final NormalColumn indexColumn : cloneIndex.getColumns()) {
                // 複製後の列を取得して、複製後のインデックス列一覧に追加します。
                cloneIndexColumns.add((NormalColumn) columnMap.get(indexColumn));
            }

            // 複製後のインデックスに、複製後のインデックス列一覧を設定します。
            cloneIndex.setColumns(cloneIndexColumns);

            cloneIndexes.add(cloneIndex);
        }

        // 複製後のテーブルに、複製後のインデックス一覧を設定します。
        to.setIndexes(cloneIndexes);
    }

    public DiagramContents copy(final DiagramContents originalDiagramContents) {
        final DiagramContents copyDiagramContents = new DiagramContents();

        copyDiagramContents.setContents(copyNodeElementList(originalDiagramContents.getContents()));
        final Map<NodeElement, NodeElement> nodeElementMap = getNodeElementMap();

        final Settings settings = originalDiagramContents.getSettings().clone();
        setSettings(nodeElementMap, settings);
        copyDiagramContents.setSettings(settings);

        setColumnGroup(copyDiagramContents, originalDiagramContents);

        copyDiagramContents.setSequenceSet(originalDiagramContents.getSequenceSet().clone());
        copyDiagramContents.setTriggerSet(originalDiagramContents.getTriggerSet().clone());

        setWord(copyDiagramContents, originalDiagramContents);
        setTablespace(copyDiagramContents, originalDiagramContents);

        return copyDiagramContents;
    }

    private void setSettings(final Map<NodeElement, NodeElement> nodeElementMap, final Settings settings) {
        for (final Category category : settings.getCategorySetting().getAllCategories()) {
            final List<NodeElement> newContents = new ArrayList<NodeElement>();
            for (final NodeElement nodeElement : category.getContents()) {
                newContents.add(nodeElementMap.get(nodeElement));
            }

            category.setContents(newContents);
        }
    }

    private void setColumnGroup(final DiagramContents copyDiagramContents, final DiagramContents originalDiagramContents) {

        final Map<ColumnGroup, ColumnGroup> columnGroupMap = new HashMap<ColumnGroup, ColumnGroup>();

        for (final ColumnGroup columnGroup : originalDiagramContents.getGroups()) {
            final ColumnGroup newColumnGroup = columnGroup.clone();
            copyDiagramContents.getGroups().add(newColumnGroup);

            columnGroupMap.put(columnGroup, newColumnGroup);
        }

        for (final TableView tableView : copyDiagramContents.getContents().getTableViewList()) {
            final List<Column> newColumns = new ArrayList<Column>();

            for (final Column column : tableView.getColumns()) {
                if (column instanceof ColumnGroup) {
                    newColumns.add(columnGroupMap.get(column));

                } else {
                    newColumns.add(column);
                }
            }

            tableView.setColumns(newColumns);
        }
    }

    private void setWord(final DiagramContents copyDiagramContents, final DiagramContents originalDiagramContents) {

        final Map<Word, Word> wordMap = new HashMap<Word, Word>();
        final Dictionary copyDictionary = copyDiagramContents.getDictionary();

        for (final Word word : originalDiagramContents.getDictionary().getWordList()) {
            final Word newWord = (Word) word.clone();
            wordMap.put(word, newWord);
        }

        for (final TableView tableView : copyDiagramContents.getContents().getTableViewList()) {
            for (final NormalColumn normalColumn : tableView.getNormalColumns()) {
                final Word oldWord = normalColumn.getWord();
                if (oldWord != null) {
                    final Word newWord = wordMap.get(oldWord);
                    normalColumn.setWord(newWord);

                    copyDictionary.add(normalColumn);
                }
            }
        }

        for (final ColumnGroup columnGroup : copyDiagramContents.getGroups()) {
            for (final NormalColumn normalColumn : columnGroup.getColumns()) {
                final Word oldWord = normalColumn.getWord();
                if (oldWord != null) {
                    final Word newWord = wordMap.get(oldWord);
                    normalColumn.setWord(newWord);

                    copyDictionary.add(normalColumn);
                }
            }
        }

    }

    private void setTablespace(final DiagramContents copyDiagramContents, final DiagramContents originalDiagramContents) {

        final Map<Tablespace, Tablespace> tablespaceMap = new HashMap<Tablespace, Tablespace>();
        final TablespaceSet copyTablespaceSet = copyDiagramContents.getTablespaceSet();

        for (final Tablespace tablespace : originalDiagramContents.getTablespaceSet()) {
            final Tablespace newTablespace = tablespace.clone();
            tablespaceMap.put(tablespace, newTablespace);

            copyTablespaceSet.addObject(newTablespace);
        }

        for (final TableView tableView : copyDiagramContents.getContents().getTableViewList()) {
            final TableViewProperties tableProperties = tableView.getTableViewProperties();
            final Tablespace oldTablespace = tableProperties.getTableSpace();

            final Tablespace newTablespace = tablespaceMap.get(oldTablespace);
            tableProperties.setTableSpace(newTablespace);
        }

        final TableViewProperties defaultTableProperties = copyDiagramContents.getSettings().getTableViewProperties();
        final Tablespace oldDefaultTablespace = defaultTableProperties.getTableSpace();

        final Tablespace newDefaultTablespace = tablespaceMap.get(oldDefaultTablespace);
        defaultTableProperties.setTableSpace(newDefaultTablespace);
    }
}
