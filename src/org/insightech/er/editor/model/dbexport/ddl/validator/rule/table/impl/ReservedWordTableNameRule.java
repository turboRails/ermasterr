package org.insightech.er.editor.model.dbexport.ddl.validator.rule.table.impl;

import org.eclipse.core.resources.IMarker;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.dbexport.ddl.validator.ValidateResult;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.table.TableRule;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;

public class ReservedWordTableNameRule extends TableRule {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validate(final ERTable table) {
        if (table.getPhysicalName() != null) {
            if (getDBManager().isReservedWord(table.getPhysicalName())) {
                final ValidateResult validateResult = new ValidateResult();
                validateResult.setMessage(ResourceString.getResourceString("error.validate.reserved.table.name") + table.getPhysicalName());
                validateResult.setLocation(table.getLogicalName());
                validateResult.setSeverity(IMarker.SEVERITY_WARNING);
                validateResult.setObject(table);

                addError(validateResult);
            }
        }

        return true;
    }

}
