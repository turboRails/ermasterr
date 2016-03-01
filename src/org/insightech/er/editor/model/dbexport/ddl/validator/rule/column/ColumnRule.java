package org.insightech.er.editor.model.dbexport.ddl.validator.rule.column;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.model.dbexport.ddl.validator.ValidateResult;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.table.TableRule;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;

public abstract class ColumnRule extends TableRule {

    private final List<ValidateResult> errorList;

    public ColumnRule() {
        errorList = new ArrayList<ValidateResult>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addError(final ValidateResult errorMessage) {
        errorList.add(errorMessage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValidateResult> getErrorList() {
        return errorList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        errorList.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validate(final ERTable table) {
        for (final Column column : table.getColumns()) {
            if (column instanceof NormalColumn) {
                final NormalColumn normalColumn = (NormalColumn) column;

                if (!this.validate(table, normalColumn)) {
                    return false;
                }

            } else {
                final ColumnGroup columnGroup = (ColumnGroup) column;

                for (final NormalColumn normalColumn : columnGroup.getColumns()) {
                    if (!this.validate(table, normalColumn)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    abstract public boolean validate(ERTable table, NormalColumn column);
}
