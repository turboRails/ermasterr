package org.insightech.er.db.impl.sqlserver2008;

import org.insightech.er.db.impl.sqlserver.SqlServerDDLCreator;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;

public class SqlServer2008DDLCreator extends SqlServerDDLCreator {

    public SqlServer2008DDLCreator(final ERDiagram diagram, final Category targetCategory, final boolean semicolon) {
        super(diagram, targetCategory, semicolon);
    }

    @Override
    public String getDropDDL(final Index index, final ERTable table) {
        final StringBuilder ddl = new StringBuilder();

        ddl.append("DROP INDEX ");
        ddl.append(getIfExistsOption());
        ddl.append(filterName(index.getName()));
        ddl.append(" ON ");
        ddl.append(filterName(table.getNameWithSchema(getDiagram().getDatabase())));

        if (semicolon) {
            ddl.append(";");
        }

        return ddl.toString();
    }
}
