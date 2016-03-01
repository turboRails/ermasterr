package org.insightech.er.editor.model.diagram_contents.not_element.dictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;

public class Dictionary extends AbstractModel {

    private static final long serialVersionUID = -4476318682977312216L;

    private final Map<Word, List<NormalColumn>> wordMap;

    public Dictionary() {
        wordMap = new HashMap<Word, List<NormalColumn>>();
    }

    public void add(final NormalColumn column) {
        final Word word = column.getWord();

        if (word == null) {
            return;
        }

        List<NormalColumn> useColumns = wordMap.get(word);

        if (useColumns == null) {
            useColumns = new ArrayList<NormalColumn>();
            wordMap.put(word, useColumns);
        }

        if (!useColumns.contains(column)) {
            useColumns.add(column);
        }
    }

    public void remove(final NormalColumn column) {
        final Word word = column.getWord();

        if (word == null) {
            return;
        }

        final List<NormalColumn> useColumns = wordMap.get(word);

        if (useColumns != null) {
            useColumns.remove(column);
            if (useColumns.isEmpty()) {
                wordMap.remove(word);
            }
        }
    }

    public void remove(final TableView tableView) {
        for (final NormalColumn normalColumn : tableView.getNormalColumns()) {
            this.remove(normalColumn);
        }
    }

    public void clear() {
        wordMap.clear();
    }

    public List<Word> getWordList() {
        final List<Word> list = new ArrayList<Word>(wordMap.keySet());

        Collections.sort(list);

        return list;
    }

    public List<NormalColumn> getColumnList(final Word word) {
        return wordMap.get(word);
    }

    public void copyTo(final Word from, final Word to) {
        from.copyTo(to);
    }
}
