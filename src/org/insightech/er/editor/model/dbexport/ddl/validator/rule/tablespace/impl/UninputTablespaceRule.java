package org.insightech.er.editor.model.dbexport.ddl.validator.rule.tablespace.impl;

import org.eclipse.core.resources.IMarker;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.validator.ValidateResult;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.tablespace.TablespaceRule;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;
import org.insightech.er.editor.model.settings.Environment;

public class UninputTablespaceRule extends TablespaceRule {

    @Override
    public boolean validate(final ERDiagram diagram, final Tablespace tablespace, final Environment environment) {
        final TablespaceProperties tablespaceProperties = tablespace.getProperties(environment, diagram);

        for (final String errorMessage : tablespaceProperties.validate()) {
            final ValidateResult validateResult = new ValidateResult();
            validateResult.setMessage(ResourceString.getResourceString(errorMessage) + getMessageSuffix(tablespace, environment));
            validateResult.setLocation(tablespace.getName());
            validateResult.setSeverity(IMarker.SEVERITY_WARNING);
            validateResult.setObject(tablespace);

            addError(validateResult);
        }

        return true;
    }

    protected String getMessageSuffix(final Tablespace tablespace, final Environment environment) {
        final StringBuilder suffix = new StringBuilder();
        suffix.append(" ");
        suffix.append(ResourceString.getResourceString("error.tablespace.suffix.1"));
        suffix.append(tablespace.getName());
        suffix.append(ResourceString.getResourceString("error.tablespace.suffix.2"));
        suffix.append(environment.getName());

        return suffix.toString();
    }
}
