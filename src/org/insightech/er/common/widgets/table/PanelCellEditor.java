package org.insightech.er.common.widgets.table;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;

import javax.swing.AbstractCellEditor;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

public abstract class PanelCellEditor extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {

    private static final long serialVersionUID = -3646026286712349658L;

    private final JPanel editPanel;

    public PanelCellEditor() {
        editPanel = new JPanel();
        editPanel.setLayout(null);
    }

    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
        return editPanel;
    }

    @Override
    public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column) {
        return editPanel;
    }

    @Override
    public Object getCellEditorValue() {
        return editPanel;
    }

    protected static Font getAwtFont() {
        final FontData fontData = Display.getCurrent().getSystemFont().getFontData()[0];

        final Font font = new Font(fontData.getName(), Font.PLAIN, 12);

        return font;
    }

    protected void addComponent(final Component component, final int x, final int y, final int w, final int h) {
        addComponent(editPanel, component, x, y, w, h);
    }

    protected static void addComponent(final Container parent, final Component component, final int x, final int y, final int w, final int h) {
        component.setFont(getAwtFont());

        component.setBounds(x, y, w, h);

        parent.add(component);
    }
}
