package org.insightech.er.editor.view.dialog.tracking;

import java.text.DateFormat;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ResourceString;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.controller.command.tracking.AddChangeTrackingCommand;
import org.insightech.er.editor.controller.command.tracking.CalculateChangeTrackingCommand;
import org.insightech.er.editor.controller.command.tracking.DeleteChangeTrackingCommand;
import org.insightech.er.editor.controller.command.tracking.DisplaySelectedChangeTrackingCommand;
import org.insightech.er.editor.controller.command.tracking.ResetChangeTrackingCommand;
import org.insightech.er.editor.controller.command.tracking.UpdateChangeTrackingCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeSet;
import org.insightech.er.editor.model.tracking.ChangeTracking;
import org.insightech.er.util.Check;

public class ChangeTrackingDialog extends Dialog {

    private Table changeTrackingTable;

    private Text textArea = null;

    private Button registerButton;

    private Button updateButton;

    private Button deleteButton;

    private Button replaceButton;

    private Button comparisonDisplayButton;

    private Button comparisonResetButton;

    private final GraphicalViewer viewer;

    private final ERDiagram diagram;

    public ChangeTrackingDialog(final Shell parentShell, final GraphicalViewer viewer, final ERDiagram diagram) {
        super(parentShell);

        this.viewer = viewer;
        this.diagram = diagram;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Control createDialogArea(final Composite parent) {
        getShell().setText(ResourceString.getResourceString("dialog.title.change.tracking"));

        final Composite composite = (Composite) super.createDialogArea(parent);

        final GridLayout layout = new GridLayout();
        initLayout(layout);

        composite.setLayout(layout);

        initialize(composite);

        setData();

        return composite;
    }

    protected void initLayout(final GridLayout layout) {
        layout.numColumns = 6;

        layout.marginLeft = 20;
        layout.marginRight = 20;
        layout.marginBottom = 15;
        layout.marginTop = 15;
    }

    private void initialize(final Composite composite) {
        changeTrackingTable = CompositeFactory.createTable(composite, 150, 6);

        CompositeFactory.createLeftLabel(composite, "label.contents.of.change", 6);

        textArea = CompositeFactory.createTextArea(null, composite, null, -1, 100, 6, true);

        registerButton = CompositeFactory.createSmallButton(composite, "label.button.add");

        updateButton = CompositeFactory.createSmallButton(composite, "label.button.update");

        deleteButton = CompositeFactory.createSmallButton(composite, "label.button.delete");

        replaceButton = CompositeFactory.createButton(composite, "label.button.change.tracking", 1, -1);
        comparisonDisplayButton = CompositeFactory.createButton(composite, "label.button.comparison.display", 1, -1);
        comparisonResetButton = CompositeFactory.createButton(composite, "label.button.comparison.reset", 1, -1);

        CompositeFactory.createTableColumn(changeTrackingTable, "label.date", 200);
        CompositeFactory.createTableColumn(changeTrackingTable, "label.contents.of.change", 500);

        changeTrackingTable.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final int index = changeTrackingTable.getSelectionIndex();
                if (index == -1) {
                    return;
                }

                selectChangeTracking(index);
            }
        });

        registerButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final ChangeTracking changeTracking = new ChangeTracking(diagram.getDiagramContents());
                changeTracking.setComment(textArea.getText());

                final Command command = new AddChangeTrackingCommand(diagram, changeTracking);

                viewer.getEditDomain().getCommandStack().execute(command);

                final int index = changeTrackingTable.getItemCount();

                setData();

                selectChangeTracking(index);
            }
        });

        updateButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final int index = changeTrackingTable.getSelectionIndex();
                if (index == -1) {
                    return;
                }

                final ChangeTracking changeTracking = diagram.getChangeTrackingList().get(index);

                final Command command = new UpdateChangeTrackingCommand(changeTracking, textArea.getText());

                viewer.getEditDomain().getCommandStack().execute(command);

                setData();

                selectChangeTracking(index);
            }
        });

        deleteButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                int index = changeTrackingTable.getSelectionIndex();
                if (index == -1) {
                    return;
                }

                final Command command = new DeleteChangeTrackingCommand(diagram, index);

                viewer.getEditDomain().getCommandStack().execute(command);

                setData();

                if (changeTrackingTable.getItemCount() > 0) {
                    if (index >= changeTrackingTable.getItemCount()) {
                        index = changeTrackingTable.getItemCount() - 1;
                    }

                    selectChangeTracking(index);
                }
            }
        });

        replaceButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final int index = changeTrackingTable.getSelectionIndex();
                if (index == -1) {
                    return;
                }
                final MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
                messageBox.setText(ResourceString.getResourceString("dialog.title.change.tracking"));
                messageBox.setMessage(ResourceString.getResourceString("dialog.message.change.tracking"));

                if (messageBox.open() == SWT.YES) {
                    final ChangeTracking changeTracking = new ChangeTracking(diagram.getDiagramContents());
                    changeTracking.setComment("");

                    diagram.getChangeTrackingList().addChangeTracking(changeTracking);

                    setData();

                    changeTrackingTable.select(index);
                }

                final ChangeTracking changeTracking = diagram.getChangeTrackingList().get(index);

                final ChangeTracking copy = new ChangeTracking(changeTracking.getDiagramContents());

                final Command command = new DisplaySelectedChangeTrackingCommand(diagram, copy.getDiagramContents());

                viewer.getEditDomain().getCommandStack().execute(command);
            }
        });

        comparisonDisplayButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final int index = changeTrackingTable.getSelectionIndex();
                if (index == -1) {
                    return;
                }

                final ChangeTracking changeTracking = diagram.getChangeTrackingList().get(index);

                final NodeSet nodeElementList = changeTracking.getDiagramContents().getContents();

                final Command command = new CalculateChangeTrackingCommand(diagram, nodeElementList);

                viewer.getEditDomain().getCommandStack().execute(command);

                comparisonResetButton.setEnabled(true);
            }
        });

        comparisonResetButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final Command command = new ResetChangeTrackingCommand(diagram);
                viewer.getEditDomain().getCommandStack().execute(command);

                comparisonResetButton.setEnabled(false);
            }
        });

        textArea.setFocus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createButtonsForButtonBar(final Composite parent) {
        createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void buttonPressed(final int buttonId) {
        if (buttonId == IDialogConstants.CLOSE_ID) {
            setReturnCode(buttonId);
            close();
        }

        super.buttonPressed(buttonId);
    }

    private void setData() {
        changeTrackingTable.removeAll();

        setButtonEnabled(false);
        comparisonDisplayButton.setEnabled(false);

        for (final ChangeTracking changeTracking : diagram.getChangeTrackingList().getList()) {
            final TableItem tableItem = new TableItem(changeTrackingTable, SWT.NONE);

            final String date = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(changeTracking.getUpdatedDate());
            tableItem.setText(0, date);

            if (!Check.isEmpty(changeTracking.getComment())) {
                tableItem.setText(1, changeTracking.getComment());
            } else {
                tableItem.setText(1, "*** empty log message ***");
            }
        }

        comparisonResetButton.setEnabled(diagram.getChangeTrackingList().isCalculated());
    }

    private void setButtonEnabled(final boolean enabled) {
        updateButton.setEnabled(enabled);
        deleteButton.setEnabled(enabled);
        replaceButton.setEnabled(enabled);
        comparisonDisplayButton.setEnabled(enabled);
    }

    private void selectChangeTracking(final int index) {
        changeTrackingTable.select(index);

        final ChangeTracking changeTracking = diagram.getChangeTrackingList().get(index);

        if (changeTracking.getComment() != null) {
            textArea.setText(changeTracking.getComment());
        } else {
            textArea.setText("");
        }

        if (index >= 0) {
            setButtonEnabled(true);
        } else {
            setButtonEnabled(false);
        }
    }
}
