package org.insightech.er.editor.model.dbexport.ddl.validator.rule.column.impl;

import org.eclipse.core.resources.IMarker;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.dbexport.ddl.validator.ValidateResult;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.column.ColumnRule;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;

public class ReservedWordColumnNameRule extends ColumnRule {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validate(final ERTable table, final NormalColumn column) {
        if (column.getPhysicalName() != null) {
            if (getDBManager().isReservedWord(column.getPhysicalName())) {
                final ValidateResult validateResult = new ValidateResult();
                validateResult.setMessage(ResourceString.getResourceString("error.validate.reserved.column.name1") + table.getPhysicalName() + ResourceString.getResourceString("error.validate.reserved.column.name2") + column.getPhysicalName());
                validateResult.setLocation(table.getLogicalName());
                validateResult.setSeverity(IMarker.SEVERITY_WARNING);
                validateResult.setObject(table);

                addError(validateResult);
            }
        }

        return true;
    }
}
