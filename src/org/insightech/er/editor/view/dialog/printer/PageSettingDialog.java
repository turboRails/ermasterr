package org.insightech.er.editor.view.dialog.printer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.PageSetting;

public class PageSettingDialog extends AbstractDialog {

    private PageSetting pageSetting;

    private Button vButton;

    private Button hButton;

    private Spinner scaleSpinner;

    private Combo sizeCombo;

    private Spinner topMarginSpinner;

    private Spinner rightMarginSpinner;

    private Spinner bottomMarginSpinner;

    private Spinner leftMarginSpinner;

    private final ERDiagram diagram;

    public PageSettingDialog(final Shell parentShell, final ERDiagram diagram) {
        super(parentShell);

        pageSetting = diagram.getPageSetting();
        this.diagram = diagram;
    }

    @Override
    protected void initLayout(final GridLayout layout) {
        super.initLayout(layout);

        layout.numColumns = 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createErrorComposite(final Composite parent) {}

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize(final Composite parent) {
        initDirectionGroup(parent);
        initScaleGroup(parent);
        initSizeGroup(parent);
    }

    private void initDirectionGroup(final Composite parent) {
        final GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;

        final Group directionGroup = new Group(parent, SWT.NONE);
        directionGroup.setLayoutData(gridData);
        directionGroup.setText(ResourceString.getResourceString("label.page.direction"));

        final GridLayout directionGroupLayout = new GridLayout();
        directionGroupLayout.marginWidth = 20;
        directionGroupLayout.horizontalSpacing = 20;
        directionGroupLayout.numColumns = 4;

        directionGroup.setLayout(directionGroupLayout);

        final Label vImage = new Label(directionGroup, SWT.NONE);
        vImage.setImage(ERDiagramActivator.getImage(ImageKey.PAGE_SETTING_V));

        vButton = new Button(directionGroup, SWT.RADIO);
        vButton.setText(ResourceString.getResourceString("label.page.direction.v"));

        final Label hImage = new Label(directionGroup, SWT.NONE);
        hImage.setImage(ERDiagramActivator.getImage(ImageKey.PAGE_SETTING_H));

        hButton = new Button(directionGroup, SWT.RADIO);
        hButton.setText(ResourceString.getResourceString("label.page.direction.h"));
    }

    private void initScaleGroup(final Composite parent) {
        final GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;

        final Group scaleGroup = new Group(parent, SWT.NONE);
        scaleGroup.setLayoutData(gridData);
        scaleGroup.setText(ResourceString.getResourceString("label.page.scale.printing"));

        final GridLayout scaleGroupLayout = new GridLayout();
        scaleGroupLayout.marginWidth = 20;
        scaleGroupLayout.horizontalSpacing = 20;
        scaleGroupLayout.numColumns = 3;
        scaleGroup.setLayout(scaleGroupLayout);

        Label label = new Label(scaleGroup, SWT.NONE);
        label.setText(ResourceString.getResourceString("label.page.scale"));

        scaleSpinner = new Spinner(scaleGroup, SWT.BORDER);
        scaleSpinner.setIncrement(5);
        scaleSpinner.setMinimum(10);
        scaleSpinner.setMaximum(400);
        scaleSpinner.setSelection(100);

        label = new Label(scaleGroup, SWT.NONE);
        label.setText("%");

    }

    private void initSizeGroup(final Composite parent) {
        final GridData sizeGroupGridData = new GridData();
        sizeGroupGridData.grabExcessHorizontalSpace = true;
        sizeGroupGridData.horizontalAlignment = GridData.FILL;

        final Group sizeGroup = new Group(parent, SWT.NONE);
        sizeGroup.setLayoutData(sizeGroupGridData);

        final GridLayout sizeGroupLayout = new GridLayout();
        sizeGroupLayout.marginWidth = 20;
        sizeGroupLayout.horizontalSpacing = 20;
        sizeGroupLayout.numColumns = 2;
        sizeGroup.setLayout(sizeGroupLayout);

        Label label = new Label(sizeGroup, SWT.NONE);
        label.setText(ResourceString.getResourceString("label.page.size"));

        sizeCombo = new Combo(sizeGroup, SWT.READ_ONLY | SWT.BORDER);
        setPaperSize(sizeCombo);

        label = new Label(sizeGroup, SWT.NONE);
        label.setText(ResourceString.getResourceString("label.page.margin"));

        final Composite marginComposite = new Composite(sizeGroup, SWT.NONE);

        final GridLayout marginCompositeLayout = new GridLayout();
        marginCompositeLayout.marginWidth = 10;
        marginCompositeLayout.horizontalSpacing = 10;
        marginCompositeLayout.numColumns = 6;
        marginComposite.setLayout(marginCompositeLayout);

        label = new Label(marginComposite, SWT.NONE);
        label = new Label(marginComposite, SWT.NONE);

        label = new Label(marginComposite, SWT.NONE);
        label.setText(ResourceString.getResourceString("label.page.margin.top"));

        topMarginSpinner = new Spinner(marginComposite, SWT.BORDER);
        setMarginSpinner(topMarginSpinner);

        label = new Label(marginComposite, SWT.NONE);
        label = new Label(marginComposite, SWT.NONE);

        label = new Label(marginComposite, SWT.NONE);
        label.setText(ResourceString.getResourceString("label.page.margin.left"));

        leftMarginSpinner = new Spinner(marginComposite, SWT.BORDER);
        setMarginSpinner(leftMarginSpinner);

        label = new Label(marginComposite, SWT.NONE);
        label = new Label(marginComposite, SWT.NONE);

        label = new Label(marginComposite, SWT.NONE);
        label.setText(ResourceString.getResourceString("label.page.margin.right"));

        rightMarginSpinner = new Spinner(marginComposite, SWT.BORDER);
        setMarginSpinner(rightMarginSpinner);

        label = new Label(marginComposite, SWT.NONE);
        label = new Label(marginComposite, SWT.NONE);

        label = new Label(marginComposite, SWT.NONE);
        label.setText(ResourceString.getResourceString("label.page.margin.bottom"));

        bottomMarginSpinner = new Spinner(marginComposite, SWT.BORDER);
        setMarginSpinner(bottomMarginSpinner);

        label = new Label(marginComposite, SWT.NONE);
        label = new Label(marginComposite, SWT.NONE);
    }

    private void setMarginSpinner(final Spinner spinner) {
        spinner.setDigits(1);
        spinner.setIncrement(5);
        spinner.setMinimum(0);
        spinner.setMaximum(1000);
        spinner.setSelection(20);
    }

    private void setPaperSize(final Combo combo) {
        for (final String paperSize : PageSetting.getAllPaperSize()) {
            combo.add(paperSize);
        }

        combo.select(0);
    }

    @Override
    protected String getTitle() {
        return "dialog.title.page.setting";
    }

    @Override
    protected void perfomeOK() throws InputException {
        pageSetting = new PageSetting(hButton.getSelection(), scaleSpinner.getSelection(), sizeCombo.getText(), topMarginSpinner.getSelection(), rightMarginSpinner.getSelection(), bottomMarginSpinner.getSelection(), leftMarginSpinner.getSelection());
        diagram.setPageSetting(pageSetting);
    }

    @Override
    protected void setData() {
        if (pageSetting.isDirectionHorizontal()) {
            hButton.setSelection(true);
        } else {
            vButton.setSelection(true);
        }

        scaleSpinner.setSelection(pageSetting.getScale());
        sizeCombo.setText(pageSetting.getPaperSize());
        topMarginSpinner.setSelection(pageSetting.getTopMargin());
        rightMarginSpinner.setSelection(pageSetting.getRightMargin());
        bottomMarginSpinner.setSelection(pageSetting.getBottomMargin());

        leftMarginSpinner.setSelection(pageSetting.getLeftMargin());
    }

    @Override
    protected String getErrorMessage() {
        return null;
    }

}
