package org.insightech.er.editor.view.contributor;

import org.eclipse.gef.commands.Command;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.ui.IWorkbenchPage;
import org.insightech.er.editor.controller.command.display.ChangeFontCommand;
import org.insightech.er.editor.model.ViewableModel;

public class FontSizeContributionItem extends ComboContributionItem {

    public static final String ID = FontSizeContributionItem.class.getName();

    public FontSizeContributionItem(final IWorkbenchPage workbenchPage) {
        super(ID, workbenchPage);
    }

    @Override
    protected Command createCommand(final ViewableModel viewableModel) {
        final String text = getText();

        try {
            final int fontSize = Integer.parseInt(text);
            return new ChangeFontCommand(viewableModel, viewableModel.getFontName(), fontSize);

        } catch (final NumberFormatException e) {}

        return null;
    }

    @Override
    protected void setData(final Combo combo) {
        final int minimumSize = 5;
        for (int i = minimumSize; i < 17; i++) {
            combo.add(String.valueOf(i));
        }
    }

}
