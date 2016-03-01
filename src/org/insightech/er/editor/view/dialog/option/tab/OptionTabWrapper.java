package org.insightech.er.editor.view.dialog.option.tab;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.TabFolder;
import org.insightech.er.common.dialog.ValidatableTabWrapper;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.dialog.option.OptionSettingDialog;

public class OptionTabWrapper extends ValidatableTabWrapper {

    private Button autoImeChangeCheck;

    private Button validatePhysicalNameCheck;

    private Button useBezierCurveCheck;

    private Button suspendValidatorCheck;

    private final Settings settings;

    public OptionTabWrapper(final OptionSettingDialog dialog, final TabFolder parent, final Settings settings) {
        super(dialog, parent, "label.option");

        this.settings = settings;
    }

    @Override
    public void initComposite() {
        autoImeChangeCheck = CompositeFactory.createCheckbox(dialog, this, "label.auto.ime.change", false);
        validatePhysicalNameCheck = CompositeFactory.createCheckbox(dialog, this, "label.validate.physical.name", false);
        useBezierCurveCheck = CompositeFactory.createCheckbox(dialog, this, "label.use.bezier.curve", false);
        suspendValidatorCheck = CompositeFactory.createCheckbox(dialog, this, "label.suspend.validator", false);
    }

    @Override
    public void setData() {
        autoImeChangeCheck.setSelection(settings.isAutoImeChange());
        validatePhysicalNameCheck.setSelection(settings.isValidatePhysicalName());
        useBezierCurveCheck.setSelection(settings.isUseBezierCurve());
        suspendValidatorCheck.setSelection(settings.isSuspendValidator());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validatePage() throws InputException {
        settings.setAutoImeChange(autoImeChangeCheck.getSelection());
        settings.setValidatePhysicalName(validatePhysicalNameCheck.getSelection());
        settings.setUseBezierCurve(useBezierCurveCheck.getSelection());
        settings.setSuspendValidator(suspendValidatorCheck.getSelection());
    }

    @Override
    public void setInitFocus() {
        autoImeChangeCheck.setFocus();
    }

    @Override
    public void perfomeOK() {}
}
