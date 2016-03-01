package org.insightech.er.db.impl.standard_sql;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;

public class StandardSQLDDLCreator extends DDLCreator {

    public StandardSQLDDLCreator(final ERDiagram diagram, final Category targetCategory, final boolean semicolon) {
        super(diagram, targetCategory, semicolon);
    }

    @Override
    protected String getDDL(final Tablespace tablespace) {
        return null;
    }
}
