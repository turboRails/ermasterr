package org.insightech.er.editor.model.diagram_contents.not_element.dictionary;

import java.util.HashMap;
import java.util.Map;

import org.insightech.er.editor.model.ERDiagram;

public class UniqueWordDictionary {

    private final Map<UniqueWord, Word> dictionary;

    public UniqueWordDictionary() {
        dictionary = new HashMap<UniqueWord, Word>();
    }

    public void init(final ERDiagram diagram) {
        for (final Word word : diagram.getDiagramContents().getDictionary().getWordList()) {
            dictionary.put(new UniqueWord(word), word);
        }
    }

    public Word getUniqueWord(final Word word) {
        return this.getUniqueWord(word, true);
    }

    public Word getUniqueWord(final Word word, final boolean add) {
        if (word == null) {
            return null;
        }

        final UniqueWord uniqueWord = new UniqueWord(word);

        if (dictionary.get(uniqueWord) != null) {
            return dictionary.get(uniqueWord);

        } else {
            if (add) {
                dictionary.put(uniqueWord, word);

                return word;

            } else {
                return null;
            }
        }
    }

    private static class UniqueWord extends Word {

        private static final long serialVersionUID = 6795589487175949331L;

        public UniqueWord(final Word word) {
            super(word);
        }

        @Override
        public int hashCode() {
            final int PRIME = 31;
            int result = 1;
            result = PRIME * result + ((getTypeData() == null) ? 0 : getTypeData().hashCode());
            result = PRIME * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
            result = PRIME * result + ((getLogicalName() == null) ? 0 : getLogicalName().hashCode());
            result = PRIME * result + ((getPhysicalName() == null) ? 0 : getPhysicalName().hashCode());
            result = PRIME * result + ((getType() == null) ? 0 : getType().hashCode());
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final Word other = (Word) obj;
            if (getTypeData() == null) {
                if (other.getTypeData() != null)
                    return false;
            } else if (!getTypeData().equals(other.getTypeData()))
                return false;
            if (getDescription() == null) {
                if (other.getDescription() != null)
                    return false;
            } else if (!getDescription().equals(other.getDescription()))
                return false;
            if (getLogicalName() == null) {
                if (other.getLogicalName() != null)
                    return false;
            } else if (!getLogicalName().equals(other.getLogicalName()))
                return false;
            if (getPhysicalName() == null) {
                if (other.getPhysicalName() != null)
                    return false;
            } else if (!getPhysicalName().equals(other.getPhysicalName()))
                return false;
            if (getType() == null) {
                if (other.getType() != null)
                    return false;
            } else if (!getType().equals(other.getType()))
                return false;
            return true;
        }
    }

}
