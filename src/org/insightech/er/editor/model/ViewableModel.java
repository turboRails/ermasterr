package org.insightech.er.editor.model;

public abstract class ViewableModel extends AbstractModel {

    private static final long serialVersionUID = 5866202173090969615L;

    public static final int DEFAULT_FONT_SIZE = 9;

    private String fontName;

    private int fontSize;

    private int[] color;

    public ViewableModel() {
        fontName = null;
        fontSize = DEFAULT_FONT_SIZE;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(final int fontSize) {
        this.fontSize = fontSize;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(final String fontName) {
        this.fontName = fontName;
    }

    public void setColor(final int red, final int green, final int blue) {
        color = new int[3];
        color[0] = red;
        color[1] = green;
        color[2] = blue;
    }

    public int[] getColor() {
        return color;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewableModel clone() {
        final ViewableModel clone = (ViewableModel) super.clone();
        if (color != null) {
            clone.color = new int[] {color[0], color[1], color[2]};
        }

        return clone;
    }

    public void refreshFont() {
        if (isUpdateable()) {
            this.firePropertyChange("refreshFont", null, null);
        }
    }

}
