package org.insightech.er.editor.view.contributor;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbenchPage;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.ERDiagramMultiPageEditor;
import org.insightech.er.editor.model.ViewableModel;

public abstract class ComboContributionItem extends ContributionItem {

    private Combo combo;

    private ToolItem toolitem;

    private final IWorkbenchPage workbenchPage;

    public ComboContributionItem(final String id, final IWorkbenchPage workbenchPage) {
        super(id);

        this.workbenchPage = workbenchPage;
    }

    @Override
    public final void fill(final Composite parent) {
        createControl(parent);
    }

    @Override
    public void fill(final ToolBar parent, final int index) {
        toolitem = new ToolItem(parent, SWT.SEPARATOR, index);
        final Control control = createControl(parent);
        toolitem.setControl(control);
    }

    protected Control createControl(final Composite parent) {
        combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
        // FontData fontData =
        // Display.getCurrent().getSystemFont().getFontData()[0];
        // Font font = new Font(Display.getCurrent(), fontData.getName(), 7,
        // SWT.NORMAL);
        // this.combo.setFont(font);
        setData(combo);

        combo.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final List selectedEditParts = ((IStructuredSelection) workbenchPage.getSelection()).toList();

                final CompoundCommand compoundCommand = new CompoundCommand();

                for (final Object editPart : selectedEditParts) {

                    final Object model = ((EditPart) editPart).getModel();

                    if (model instanceof ViewableModel) {
                        final ViewableModel viewableModel = (ViewableModel) model;

                        final Command command = createCommand(viewableModel);

                        if (command != null) {
                            compoundCommand.add(command);
                        }
                    }
                }

                if (!compoundCommand.getCommands().isEmpty()) {
                    executeCommand(compoundCommand);
                }
            }

            @Override
            public void widgetDefaultSelected(final SelectionEvent e) {

            }
        });

        combo.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(final FocusEvent e) {}

            @Override
            public void focusLost(final FocusEvent e) {}
        });

        toolitem.setWidth(computeWidth(combo));
        return combo;
    }

    abstract protected Command createCommand(ViewableModel viewableModel);

    private int computeWidth(final Control control) {
        return control.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x;
    }

    abstract protected void setData(Combo combo);

    private void executeCommand(final Command command) {
        final ERDiagramMultiPageEditor multiPageEditor = (ERDiagramMultiPageEditor) workbenchPage.getActiveEditor();
        final ERDiagramEditor editor = multiPageEditor.getActiveEditor();
        editor.getGraphicalViewer().getEditDomain().getCommandStack().execute(command);
    }

    public void setText(final String text) {
        if (combo != null && !combo.isDisposed() && text != null) {
            combo.setText(text);
        }
    }

    public String getText() {
        return combo.getText();
    }
}
