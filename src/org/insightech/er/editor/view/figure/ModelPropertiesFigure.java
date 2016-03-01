package org.insightech.er.editor.view.figure;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.OrderedLayout;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.swt.graphics.Color;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.view.figure.layout.TableLayout;
import org.insightech.er.util.NameValue;

public class ModelPropertiesFigure extends RectangleFigure {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    private Color foregroundColor;

    public ModelPropertiesFigure() {
        final TableLayout layout = new TableLayout(2);

        setLayoutManager(layout);
    }

    private void addRow(final String name, final String value, final String tableStyle) {
        final Border border = new MarginBorder(5);

        final ToolbarLayout layout = new ToolbarLayout();
        layout.setMinorAlignment(OrderedLayout.ALIGN_TOPLEFT);
        layout.setStretchMinorAxis(true);

        final Label nameLabel = new Label();

        final Label valueLabel = new Label();

        nameLabel.setBorder(border);
        nameLabel.setText(name);
        nameLabel.setLabelAlignment(PositionConstants.LEFT);
        nameLabel.setForegroundColor(foregroundColor);

        this.add(nameLabel);

        if (!ResourceString.getResourceString("action.title.change.design.simple").equals(tableStyle) && !ResourceString.getResourceString("action.title.change.design.frame").equals(tableStyle)) {
            valueLabel.setBackgroundColor(ColorConstants.white);
            valueLabel.setOpaque(true);
            valueLabel.setForegroundColor(ColorConstants.black);

        } else {
            valueLabel.setOpaque(false);
            valueLabel.setForegroundColor(foregroundColor);
        }

        valueLabel.setBorder(border);
        valueLabel.setText(value);
        valueLabel.setLabelAlignment(PositionConstants.LEFT);

        this.add(valueLabel);
    }

    public void setData(final List<NameValue> properties, final Date creationDate, final Date updatedDate, final String tableStyle, final int[] color) {
        removeAll();

        decideColor(color);

        for (final NameValue property : properties) {
            addRow(property.getName(), property.getValue(), tableStyle);
        }

        addRow(ResourceString.getResourceString("label.creation.date"), DATE_FORMAT.format(creationDate), tableStyle);
        addRow(ResourceString.getResourceString("label.updated.date"), DATE_FORMAT.format(updatedDate), tableStyle);
    }

    private void decideColor(final int[] color) {
        if (color != null) {
            final int sum = color[0] + color[1] + color[2];

            if (sum > 255) {
                foregroundColor = ColorConstants.black;
            } else {
                foregroundColor = ColorConstants.white;
            }
        }
    }
}
