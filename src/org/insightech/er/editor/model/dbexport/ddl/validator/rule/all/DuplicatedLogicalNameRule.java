package org.insightech.er.editor.model.dbexport.ddl.validator.rule.all;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.validator.ValidateResult;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.BaseRule;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.Trigger;

public class DuplicatedLogicalNameRule extends BaseRule {

    @Override
    public boolean validate(final ERDiagram diagram) {
        final Set<String> nameSet = new HashSet<String>();

        for (final ERTable table : diagram.getDiagramContents().getContents().getTableSet()) {
            final String name = table.getLogicalName().toLowerCase();

            if (nameSet.contains(name)) {
                final ValidateResult validateResult = new ValidateResult();
                validateResult.setMessage(ResourceString.getResourceString("error.validate.duplicated.name") + " [" + table.getObjectType().toUpperCase() + "] " + name);
                validateResult.setLocation(table.getLogicalName());
                validateResult.setSeverity(IMarker.SEVERITY_WARNING);
                validateResult.setObject(table);

                addError(validateResult);

            }
            nameSet.add(name);

            for (final Index index : table.getIndexes()) {
                final String indexName = index.getName().toLowerCase();

                if (nameSet.contains(indexName)) {
                    final ValidateResult validateResult = new ValidateResult();
                    validateResult.setMessage(ResourceString.getResourceString("error.validate.duplicated.name") + " [INDEX] " + indexName + " (" + table.getLogicalName() + ")");
                    validateResult.setLocation(indexName);
                    validateResult.setSeverity(IMarker.SEVERITY_WARNING);
                    validateResult.setObject(index);

                    addError(validateResult);
                }
                nameSet.add(indexName);
            }
        }

        for (final Sequence sequence : diagram.getDiagramContents().getSequenceSet()) {
            final String name = sequence.getName().toLowerCase();

            if (nameSet.contains(name)) {
                final ValidateResult validateResult = new ValidateResult();
                validateResult.setMessage(ResourceString.getResourceString("error.validate.duplicated.name") + " [SEQUENCE] " + name);
                validateResult.setLocation(name);
                validateResult.setSeverity(IMarker.SEVERITY_WARNING);
                validateResult.setObject(sequence);

                addError(validateResult);
            }
            nameSet.add(name);
        }

        for (final View view : diagram.getDiagramContents().getContents().getViewSet()) {
            final String name = view.getName().toLowerCase();

            if (nameSet.contains(name)) {
                final ValidateResult validateResult = new ValidateResult();
                validateResult.setMessage(ResourceString.getResourceString("error.validate.duplicated.name") + " [VIEW] " + name);
                validateResult.setLocation(name);
                validateResult.setSeverity(IMarker.SEVERITY_WARNING);
                validateResult.setObject(view);

                addError(validateResult);
            }
            nameSet.add(name);
        }

        for (final Trigger trigger : diagram.getDiagramContents().getTriggerSet()) {
            final String name = trigger.getName().toLowerCase();

            if (nameSet.contains(name)) {
                final ValidateResult validateResult = new ValidateResult();
                validateResult.setMessage(ResourceString.getResourceString("error.validate.duplicated.name") + " [TRIGGER] " + name);
                validateResult.setLocation(name);
                validateResult.setSeverity(IMarker.SEVERITY_WARNING);
                validateResult.setObject(trigger);

                addError(validateResult);
            }
            nameSet.add(name);
        }

        return true;
    }
}
