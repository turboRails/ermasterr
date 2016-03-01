package org.insightech.er.editor.model.diagram_contents.not_element.group;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.CopyColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.CopyWord;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;

public class CopyGroup extends ColumnGroup {

    private static final long serialVersionUID = 8453816730649838482L;

    private ColumnGroup original;

    public CopyGroup(final ColumnGroup original) {
        super();

        this.original = original;

        setGroupName(this.original.getGroupName());

        for (final NormalColumn fromColumn : this.original.getColumns()) {
            final CopyColumn copyColumn = new CopyColumn(fromColumn);
            if (fromColumn.getWord() != null) {
                copyColumn.setWord(new CopyWord(fromColumn.getWord()));
            }
            addColumn(copyColumn);
        }
    }

    public ColumnGroup restructure(final ERDiagram diagram) {
        if (original == null) {
            original = new ColumnGroup();
        }

        this.restructure(diagram, original);

        return original;
    }

    private void restructure(final ERDiagram diagram, final ColumnGroup to) {
        Dictionary dictionary = null;

        if (diagram != null) {
            dictionary = diagram.getDiagramContents().getDictionary();
            for (final NormalColumn toColumn : to.getColumns()) {
                dictionary.remove(toColumn);
            }
        }

        to.setGroupName(getGroupName());

        final List<NormalColumn> columns = new ArrayList<NormalColumn>();

        for (final NormalColumn fromColumn : getColumns()) {
            // グループの更新ボタンを押した場合、
            final CopyColumn copyColumn = (CopyColumn) fromColumn;
            final CopyWord copyWord = copyColumn.getWord();

            if (copyWord != null) {
                Word originalWord = copyColumn.getOriginalWord();

                if (dictionary != null) {
                    dictionary.copyTo(copyWord, originalWord);

                } else {
                    while (originalWord instanceof CopyWord) {
                        originalWord = ((CopyWord) originalWord).getOriginal();
                    }

                    //originalWord = new CopyWord(originalWord);
                    //copyWord.copyTo(originalWord);
                    copyWord.setOriginal(originalWord);
                }
            }

            NormalColumn restructuredColumn = copyColumn.getRestructuredColumn();

            if (to instanceof CopyGroup) {
                if (!(restructuredColumn instanceof CopyColumn)) {
                    final Word restructuredWord = restructuredColumn.getWord();

                    restructuredColumn = new CopyColumn(restructuredColumn);

                    if (restructuredWord != null && !(restructuredWord instanceof CopyWord)) {
                        restructuredColumn.setWord(new CopyWord(restructuredWord));
                    }
                }
            }

            columns.add(restructuredColumn);

            if (dictionary != null) {
                dictionary.add(restructuredColumn);
            }

        }

        to.setColumns(columns);
    }

}
