package org.insightech.er.editor.model.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Display;

public class PageSetting implements Serializable {

    private static final long serialVersionUID = 7520875865783223474L;

    private static final int DEFAULT_MARGIN = 30;

    private final boolean directionHorizontal;

    private final int scale;

    private final String paperSize;

    private final int topMargin;

    private final int leftMargin;

    private final int bottomMargin;

    private final int rightMargin;

    public PageSetting() {
        directionHorizontal = true;
        scale = 100;
        paperSize = "A4 210 x 297 mm";
        topMargin = DEFAULT_MARGIN;
        rightMargin = DEFAULT_MARGIN;
        bottomMargin = DEFAULT_MARGIN;
        leftMargin = DEFAULT_MARGIN;
    }

    public PageSetting(final boolean directionHorizontal, final int scale, final String paperSize, final int topMargin, final int rightMargin, final int bottomMargin, final int leftMargin) {
        this.directionHorizontal = directionHorizontal;
        this.scale = scale;
        this.paperSize = paperSize;
        this.topMargin = topMargin;
        this.rightMargin = rightMargin;
        this.bottomMargin = bottomMargin;
        this.leftMargin = leftMargin;
    }

    public static List<String> getAllPaperSize() {
        final List<String> allPaperSize = new ArrayList<String>();

        allPaperSize.add("A0 841 x 1189 mm");
        allPaperSize.add("A1 594 x 841 mm");
        allPaperSize.add("A2 420 x 594 mm");
        allPaperSize.add("A3 297 x 420 mm");
        allPaperSize.add("A4 210 x 297 mm");
        allPaperSize.add("B0 1000 x 1414 mm");
        allPaperSize.add("B1 707 x 1000 mm");
        allPaperSize.add("B2 500 x 707 mm");
        allPaperSize.add("B3 353 x 500 mm");
        allPaperSize.add("B4 250 x 353 mm");
        allPaperSize.add("B5 176 x 250 mm");

        return allPaperSize;
    }

    /**
     * directionHorizontal を取得します.
     * 
     * @return directionHorizontal
     */
    public boolean isDirectionHorizontal() {
        return directionHorizontal;
    }

    /**
     * scale を取得します.
     * 
     * @return scale
     */
    public int getScale() {
        return scale;
    }

    /**
     * paperSize を取得します.
     * 
     * @return paperSize
     */
    public String getPaperSize() {
        return paperSize;
    }

    /**
     * topMargin を取得します.
     * 
     * @return topMargin
     */
    public int getTopMargin() {
        return topMargin;
    }

    /**
     * leftMargin を取得します.
     * 
     * @return leftMargin
     */
    public int getLeftMargin() {
        return leftMargin;
    }

    /**
     * bottomMargin を取得します.
     * 
     * @return bottomMargin
     */
    public int getBottomMargin() {
        return bottomMargin;
    }

    /**
     * rightMargin を取得します.
     * 
     * @return rightMargin
     */
    public int getRightMargin() {
        return rightMargin;
    }

    public int getWidth() {
        return (int) ((getLength(directionHorizontal) - (leftMargin / 10) - (rightMargin / 10)) * Display.getCurrent().getDPI().x / 25.4 * 100 / scale);
    }

    public int getHeight() {
        return (int) ((getLength(!directionHorizontal) - (topMargin / 10) - (bottomMargin / 10)) * Display.getCurrent().getDPI().y / 25.4 * 100 / scale);
    }

    private int getLength(final boolean horizontal) {
        if (horizontal) {
            if (paperSize.startsWith("A0")) {
                return 1189;
            } else if (paperSize.startsWith("A1")) {
                return 841;
            } else if (paperSize.startsWith("A2")) {
                return 594;
            } else if (paperSize.startsWith("A3")) {
                return 420;
            } else if (paperSize.startsWith("A4")) {
                return 297;
            } else if (paperSize.startsWith("B0")) {
                return 1414;
            } else if (paperSize.startsWith("B1")) {
                return 1000;
            } else if (paperSize.startsWith("B2")) {
                return 707;
            } else if (paperSize.startsWith("B3")) {
                return 500;
            } else if (paperSize.startsWith("B4")) {
                return 353;
            } else if (paperSize.startsWith("B5")) {
                return 250;
            } else {
                // A4
                return 297;
            }

        } else {
            if (paperSize.startsWith("A0")) {
                return 841;
            } else if (paperSize.startsWith("A1")) {
                return 594;
            } else if (paperSize.startsWith("A2")) {
                return 420;
            } else if (paperSize.startsWith("A3")) {
                return 297;
            } else if (paperSize.startsWith("A4")) {
                return 210;
            } else if (paperSize.startsWith("B0")) {
                return 1000;
            } else if (paperSize.startsWith("B1")) {
                return 707;
            } else if (paperSize.startsWith("B2")) {
                return 500;
            } else if (paperSize.startsWith("B3")) {
                return 353;
            } else if (paperSize.startsWith("B4")) {
                return 250;
            } else if (paperSize.startsWith("B5")) {
                return 176;
            } else {
                // A4
                return 210;
            }
        }
    }
}
