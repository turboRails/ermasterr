package org.insightech.er.editor.model.dbexport.ddl.validator.rule.table;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.db.DBManager;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.validator.ValidateResult;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.BaseRule;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;

public abstract class TableRule extends BaseRule {

    private final List<ValidateResult> errorList;

    private String database;

    public TableRule() {
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

    @Override
    public boolean validate(final ERDiagram diagram) {
        database = diagram.getDatabase();

        for (final ERTable table : diagram.getDiagramContents().getContents().getTableSet()) {
            if (!this.validate(table)) {
                return false;
            }
        }

        return true;
    }

    protected DBManager getDBManager() {
        return DBManagerFactory.getDBManager(database);
    }

    abstract public boolean validate(ERTable table);
}
