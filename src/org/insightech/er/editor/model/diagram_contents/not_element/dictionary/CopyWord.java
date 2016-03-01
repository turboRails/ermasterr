package org.insightech.er.editor.model.diagram_contents.not_element.dictionary;

public class CopyWord extends Word {

    private static final long serialVersionUID = 5610038803601000225L;

    private Word original;

    public CopyWord(final Word original) {
        super(original);
        this.original = original;
    }

    public Word restructure(final Dictionary dictionary) {
        dictionary.copyTo(this, original);
        return original;
    }

    public Word getOriginal() {
        return original;
    }

    @Override
    public void copyTo(final Word to) {
        super.copyTo(to);
        if (to instanceof CopyWord) {
            ((CopyWord) to).original = original;
        }
    }

    public void setOriginal(final Word original) {
        this.original = original;
    }

}
