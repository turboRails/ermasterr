package org.insightech.er.editor.model.dbexport.ddl.validator.rule.all;

import org.eclipse.core.resources.IMarker;
import org.insightech.er.ResourceString;
import org.insightech.er.db.DBManager;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.validator.ValidateResult;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.BaseRule;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.Trigger;

public class ReservedNameRule extends BaseRule {

    @Override
    public boolean validate(final ERDiagram diagram) {
        final DBManager dbManager = DBManagerFactory.getDBManager(diagram);

        for (final ERTable table : diagram.getDiagramContents().getContents().getTableSet()) {

            for (final Index index : table.getIndexes()) {
                final String indexName = index.getName().toLowerCase();

                if (dbManager.isReservedWord(indexName)) {
                    final ValidateResult validateResult = new ValidateResult();
                    validateResult.setMessage(ResourceString.getResourceString("error.validate.reserved.name") + " [INDEX] " + indexName + " (" + table.getLogicalName() + ")");
                    validateResult.setLocation(indexName);
                    validateResult.setSeverity(IMarker.SEVERITY_WARNING);
                    validateResult.setObject(index);

                    addError(validateResult);
                }
            }
        }

        for (final Sequence sequence : diagram.getDiagramContents().getSequenceSet()) {
            final String name = sequence.getName().toLowerCase();

            if (dbManager.isReservedWord(name)) {
                final ValidateResult validateResult = new ValidateResult();
                validateResult.setMessage(ResourceString.getResourceString("error.validate.reserved.name") + " [SEQUENCE] " + name);
                validateResult.setLocation(name);
                validateResult.setSeverity(IMarker.SEVERITY_WARNING);
                validateResult.setObject(sequence);

                addError(validateResult);
            }
        }

        for (final View view : diagram.getDiagramContents().getContents().getViewSet()) {
            final String name = view.getName().toLowerCase();

            if (dbManager.isReservedWord(name)) {
                final ValidateResult validateResult = new ValidateResult();
                validateResult.setMessage(ResourceString.getResourceString("error.validate.reserved.name") + " [VIEW] " + name);
                validateResult.setLocation(name);
                validateResult.setSeverity(IMarker.SEVERITY_WARNING);
                validateResult.setObject(view);

                addError(validateResult);
            }
        }

        for (final Trigger trigger : diagram.getDiagramContents().getTriggerSet()) {
            final String name = trigger.getName().toLowerCase();

            if (dbManager.isReservedWord(name)) {
                final ValidateResult validateResult = new ValidateResult();
                validateResult.setMessage(ResourceString.getResourceString("error.validate.reserved.name") + " [TRIGGER] " + name);
                validateResult.setLocation(name);
                validateResult.setSeverity(IMarker.SEVERITY_WARNING);
                validateResult.setObject(trigger);

                addError(validateResult);
            }
        }

        return true;
    }

}
