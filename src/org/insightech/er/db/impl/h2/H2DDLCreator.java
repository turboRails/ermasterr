package org.insightech.er.db.impl.h2;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.util.Check;

public class H2DDLCreator extends DDLCreator {

    public H2DDLCreator(final ERDiagram diagram, final Category targetCategory, final boolean semicolon) {
        super(diagram, targetCategory, semicolon);
    }

    @Override
    protected String getDDL(final Tablespace tablespace) {
        return null;
    }

    @Override
    public String getDDL(final Sequence sequence) {
        final StringBuilder ddl = new StringBuilder();

        final String description = sequence.getDescription();
        if (semicolon && !Check.isEmpty(description) && ddlTarget.inlineTableComment) {
            ddl.append("-- ");
            ddl.append(replaceLF(description, LF() + "-- "));
            ddl.append(LF());
        }

        ddl.append("CREATE ");
        ddl.append("SEQUENCE IF NOT EXISTS ");
        ddl.append(filterName(getNameWithSchema(sequence.getSchema(), sequence.getName())));
        if (sequence.getStart() != null) {
            ddl.append(" START WITH ");
            ddl.append(sequence.getStart());
        }
        if (sequence.getIncrement() != null) {
            ddl.append(" INCREMENT BY ");
            ddl.append(sequence.getIncrement());
        }
        if (sequence.getCache() != null) {
            ddl.append(" CACHE ");
            ddl.append(sequence.getCache());
        }
        if (semicolon) {
            ddl.append(";");
        }

        return ddl.toString();

    }

}
