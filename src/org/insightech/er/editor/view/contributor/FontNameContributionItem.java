package org.insightech.er.editor.view.contributor;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.gef.commands.Command;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.insightech.er.editor.controller.command.display.ChangeFontCommand;
import org.insightech.er.editor.model.ViewableModel;

public class FontNameContributionItem extends ComboContributionItem {

    public static final String ID = FontNameContributionItem.class.getName();

    public FontNameContributionItem(final IWorkbenchPage workbenchPage) {
        super(ID, workbenchPage);
    }

    @Override
    protected Command createCommand(final ViewableModel viewableModel) {
        return new ChangeFontCommand(viewableModel, getText(), viewableModel.getFontSize());
    }

    @Override
    protected void setData(final Combo combo) {
        final FontData[] fontDatas = Display.getCurrent().getFontList(null, true);
        final Set<String> nameSet = new LinkedHashSet<String>();
        for (int i = 0; i < fontDatas.length; i++) {
            if (!fontDatas[i].getName().startsWith("@")) {
                nameSet.add(fontDatas[i].getName());
            }
        }

        for (final String name : nameSet) {
            combo.add(name);
        }
    }

}
