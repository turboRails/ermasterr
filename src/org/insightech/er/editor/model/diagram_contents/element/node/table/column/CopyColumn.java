package org.insightech.er.editor.model.diagram_contents.element.node.table.column;

import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.CopyWord;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;

public class CopyColumn extends NormalColumn {

    private static final long serialVersionUID = 5638703275130616851L;

    private final NormalColumn originalColumn;

    public CopyColumn(final NormalColumn originalColumn) {
        super(originalColumn);

        if (originalColumn == null) {
            throw new IllegalArgumentException("originalColumn is null.");
        }

        this.originalColumn = originalColumn;
    }

    public NormalColumn getRestructuredColumn() {
        final CopyWord copyWord = getWord();
        if (copyWord != null) {
            if (!(originalColumn instanceof CopyColumn)) {
                originalColumn.setWord(copyWord.getOriginal());
            }
        }

        copyData(this, originalColumn);

        return originalColumn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isForeignKey() {
        return originalColumn.isForeignKey();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRefered() {
        return originalColumn.isRefered();
    }

    public NormalColumn getOriginalColumn() {
        return originalColumn;
    }

    public Word getOriginalWord() {
        if (getWord() != null) {
            return getWord().getOriginal();
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        final NormalColumn originalColumn = getOriginalColumn();

        if (obj instanceof CopyColumn) {
            final CopyColumn copy = (CopyColumn) obj;
            obj = copy.getOriginalColumn();
        }

        return originalColumn.equals(obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CopyWord getWord() {
        return (CopyWord) super.getWord();
    }

}
