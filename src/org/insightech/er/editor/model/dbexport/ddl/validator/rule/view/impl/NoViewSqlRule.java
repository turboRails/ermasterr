package org.insightech.er.editor.model.dbexport.ddl.validator.rule.view.impl;

import org.eclipse.core.resources.IMarker;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.dbexport.ddl.validator.ValidateResult;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.view.ViewRule;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;

public class NoViewSqlRule extends ViewRule {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validate(final View view) {
        if (view.getSql() == null || view.getSql().trim().equals("")) {
            final ValidateResult validateResult = new ValidateResult();
            validateResult.setMessage(ResourceString.getResourceString("error.validate.no.view.sql"));
            validateResult.setLocation(view.getLogicalName());
            validateResult.setSeverity(IMarker.SEVERITY_WARNING);
            validateResult.setObject(view);

            addError(validateResult);
        }

        return true;
    }
}
