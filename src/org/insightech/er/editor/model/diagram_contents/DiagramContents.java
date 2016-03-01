package org.insightech.er.editor.model.diagram_contents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.insightech.er.editor.model.diagram_contents.element.node.NodeSet;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.IndexSet;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GroupSet;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.SequenceSet;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceSet;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.TriggerSet;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.model.testdata.TestData;

public class DiagramContents {

    private Settings settings;

    private NodeSet contents;

    private GroupSet groups;

    private Dictionary dictionary;

    private SequenceSet sequenceSet;

    private TriggerSet triggerSet;

    private final IndexSet indexSet;

    private final TablespaceSet tablespaceSet;

    private List<TestData> testDataList;

    public DiagramContents() {
        settings = new Settings();
        contents = new NodeSet();
        groups = new GroupSet();
        dictionary = new Dictionary();
        sequenceSet = new SequenceSet();
        triggerSet = new TriggerSet();
        indexSet = new IndexSet();
        tablespaceSet = new TablespaceSet();

        testDataList = new ArrayList<TestData>();
    }

    public void clear() {
        contents.clear();
        groups.clear();
        dictionary.clear();
        sequenceSet.clear();
        triggerSet.clear();
        tablespaceSet.clear();
        testDataList.clear();
    }

    public void sort() {
        contents.sort();
        groups.sort();
        sequenceSet.sort();
        triggerSet.sort();
        tablespaceSet.sort();
        Collections.sort(testDataList);
    }

    public NodeSet getContents() {
        return contents;
    }

    public void setContents(final NodeSet contents) {
        this.contents = contents;
    }

    public GroupSet getGroups() {
        return groups;
    }

    public void setColumnGroups(final GroupSet groups) {
        this.groups = groups;
        for (final ColumnGroup group : groups) {
            for (final NormalColumn normalColumn : group.getColumns()) {
                dictionary.add(normalColumn);
            }
        }
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    public void setDictionary(final Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public SequenceSet getSequenceSet() {
        return sequenceSet;
    }

    public void setSequenceSet(final SequenceSet sequenceSet) {
        this.sequenceSet = sequenceSet;
    }

    public TriggerSet getTriggerSet() {
        return triggerSet;
    }

    public void setTriggerSet(final TriggerSet triggerSet) {
        this.triggerSet = triggerSet;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(final Settings settings) {
        this.settings = settings;
    }

    public IndexSet getIndexSet() {
        return indexSet;
    }

    public TablespaceSet getTablespaceSet() {
        return tablespaceSet;
    }

    public List<TestData> getTestDataList() {
        return testDataList;
    }

    public void setTestDataList(final List<TestData> testDataList) {
        this.testDataList = testDataList;
    }

}
