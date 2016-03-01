package org.insightech.er.editor.view.figure.table.style.funny;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.OrderedLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.swt.graphics.Font;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ImageKey;
import org.insightech.er.Resources;
import org.insightech.er.editor.view.figure.table.TableFigure;
import org.insightech.er.editor.view.figure.table.column.GroupColumnFigure;
import org.insightech.er.editor.view.figure.table.column.NormalColumnFigure;
import org.insightech.er.editor.view.figure.table.style.AbstractStyleSupport;

public class FunnyStyleSupport extends AbstractStyleSupport {

    private Label nameLabel;

    public FunnyStyleSupport(final TableFigure tableFigure) {
        super(tableFigure);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(final TableFigure tableFigure) {
        tableFigure.setCornerDimensions(new Dimension(20, 20));
        tableFigure.setForegroundColor(ColorConstants.black);
        tableFigure.setBorder(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initTitleBar(final Figure top) {
        top.setLayoutManager(new BorderLayout());

        final Figure title = new Figure();
        top.add(title, BorderLayout.TOP);
        final FlowLayout titleLayout = new FlowLayout();
        titleLayout.setMinorAlignment(OrderedLayout.ALIGN_CENTER);
        title.setLayoutManager(titleLayout);

        final ImageFigure image = new ImageFigure();
        image.setBorder(new MarginBorder(new Insets(5, 10, 5, 2)));
        image.setImage(ERDiagramActivator.getImage(getTableFigure().getImageKey()));
        title.add(image);

        nameLabel = new Label();
        nameLabel.setBorder(new MarginBorder(new Insets(5, 0, 5, 20)));
        title.add(nameLabel);

        final Figure separater = new Figure();
        separater.setSize(-1, 1);
        separater.setBackgroundColor(ColorConstants.black);
        separater.setOpaque(true);

        top.add(separater, BorderLayout.BOTTOM);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createColumnArea(final IFigure columns) {
        initColumnArea(columns);

        columns.setBorder(new MarginBorder(0, 0, 0, 0));
        columns.setBackgroundColor(ColorConstants.white);
        columns.setOpaque(true);

        final Figure centerFigure = new Figure();
        centerFigure.setLayoutManager(new BorderLayout());
        centerFigure.setBorder(new MarginBorder(new Insets(0, 2, 0, 2)));

        centerFigure.add(columns, BorderLayout.CENTER);
        getTableFigure().add(centerFigure, BorderLayout.CENTER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createFooter() {
        final IFigure footer = new Figure();
        final BorderLayout footerLayout = new BorderLayout();
        footer.setLayoutManager(footerLayout);
        footer.setBorder(new MarginBorder(new Insets(0, 2, 0, 2)));

        final IFigure footer1 = new Figure();
        footer1.setSize(-1, 10);
        footer1.setBackgroundColor(Resources.VERY_LIGHT_GRAY);
        footer1.setOpaque(true);

        footer.add(footer1, BorderLayout.TOP);

        final IFigure footer2 = new Figure();
        footer2.setSize(-1, 7);

        footer.add(footer2, BorderLayout.BOTTOM);

        getTableFigure().add(footer, BorderLayout.BOTTOM);
    }

    @Override
    public void setName(final String name) {
        nameLabel.setForegroundColor(getTextColor());
        nameLabel.setText(name);
    }

    @Override
    public void setFont(final Font font, final Font titleFont) {
        nameLabel.setFont(titleFont);
    }

    @Override
    public void addColumn(final NormalColumnFigure columnFigure, final int viewMode, final String physicalName, final String logicalName, final String type, final boolean primaryKey, final boolean foreignKey, final boolean isNotNull, final boolean uniqueKey, final boolean displayKey, final boolean displayDetail, final boolean displayType, final boolean isSelectedReferenced, final boolean isSelectedForeignKey, final boolean isAdded, final boolean isUpdated, final boolean isRemoved) {

        final Label label = createColumnLabel();
        label.setForegroundColor(ColorConstants.black);

        final StringBuilder text = new StringBuilder();
        text.append(getColumnText(viewMode, physicalName, logicalName, type, isNotNull, uniqueKey, displayDetail, displayType));

        if (displayKey) {
            if (primaryKey) {
                final ImageFigure image = new ImageFigure();
                image.setBorder(new MarginBorder(new Insets(0, 0, 0, 0)));
                image.setImage(ERDiagramActivator.getImage(ImageKey.PRIMARY_KEY));
                columnFigure.add(image);

            } else {
                final Label filler = new Label();
                filler.setBorder(new MarginBorder(new Insets(0, 0, 0, 16)));
                columnFigure.add(filler);

            }

            if (foreignKey) {
                final ImageFigure image = new ImageFigure();
                image.setBorder(new MarginBorder(new Insets(0, 0, 0, 0)));
                image.setImage(ERDiagramActivator.getImage(ImageKey.FOREIGN_KEY));
                columnFigure.add(image);

            } else {
                final Label filler = new Label();
                filler.setBorder(new MarginBorder(new Insets(0, 0, 0, 16)));
                columnFigure.add(filler);

            }

            if (primaryKey && foreignKey) {
                label.setForegroundColor(ColorConstants.blue);

            } else if (primaryKey) {
                label.setForegroundColor(ColorConstants.red);

            } else if (foreignKey) {
                label.setForegroundColor(ColorConstants.darkGreen);

            }
        }

        label.setText(text.toString());

        setColumnFigureColor(columnFigure, isSelectedReferenced, isSelectedForeignKey, isAdded, isUpdated, isRemoved);

        columnFigure.add(label);
    }

    @Override
    public void addColumnGroup(final GroupColumnFigure columnFigure, final int viewMode, final String name, final boolean isAdded, final boolean isUpdated, final boolean isRemoved) {

        Label filler = new Label();
        filler.setBorder(new MarginBorder(new Insets(0, 0, 0, 16)));
        columnFigure.add(filler);

        filler = new Label();
        filler.setBorder(new MarginBorder(new Insets(0, 0, 0, 16)));
        columnFigure.add(filler);

        final StringBuilder text = new StringBuilder();
        text.append(name);
        text.append(" (GROUP)");

        setColumnFigureColor(columnFigure, false, false, isAdded, isUpdated, isRemoved);

        final Label label = createColumnLabel();

        label.setForegroundColor(ColorConstants.black);

        label.setText(text.toString());

        columnFigure.add(label);
    }
}
