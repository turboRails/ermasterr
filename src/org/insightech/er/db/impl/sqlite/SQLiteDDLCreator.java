package org.insightech.er.db.impl.sqlite;

import java.util.LinkedHashSet;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;

public class SQLiteDDLCreator extends DDLCreator {

    public SQLiteDDLCreator(final ERDiagram diagram, final Category targetCategory, final boolean semicolon) {
        super(diagram, targetCategory, semicolon);
    }

    @Override
    protected String getDDL(final Tablespace tablespace) {
        return null;
    }

    @Override
    protected String getColulmnDDL(final NormalColumn normalColumn) {
        final StringBuilder ddl = new StringBuilder();

        ddl.append(super.getColulmnDDL(normalColumn));

        if (normalColumn.isAutoIncrement()) {
            ddl.append(" PRIMARY KEY AUTOINCREMENT");
        }

        return ddl.toString();
    }

    @Override
    protected String getPrimaryKeyDDL(final ERTable table) {
        boolean isAutoIncrement = false;

        for (final NormalColumn column : table.getNormalColumns()) {
            isAutoIncrement = column.isAutoIncrement();

            if (isAutoIncrement) {
                break;
            }
        }

        final StringBuilder ddl = new StringBuilder();

        if (!isAutoIncrement) {
            ddl.append(super.getPrimaryKeyDDL(table));
        }

        for (final Relation relation : table.getIncomingRelations()) {
            ddl.append("," + LF() + "\tFOREIGN KEY (");

            boolean first = true;

            for (final NormalColumn column : relation.getForeignKeyColumns()) {
                if (!first) {
                    ddl.append(", ");

                }
                ddl.append(filterName(column.getPhysicalName()));
                first = false;
            }

            ddl.append(")" + LF());
            ddl.append("\tREFERENCES ");
            ddl.append(filterName(relation.getSourceTableView().getNameWithSchema(getDiagram().getDatabase())));
            ddl.append(" (");

            first = true;

            for (final NormalColumn foreignKeyColumn : relation.getForeignKeyColumns()) {
                if (!first) {
                    ddl.append(", ");

                }

                ddl.append(filterName(foreignKeyColumn.getReferencedColumn(relation).getPhysicalName()));
                first = false;
            }

            ddl.append(")");
        }

        return ddl.toString();
    }

    @Override
    protected Iterable<ERTable> getTablesForCreateDDL() {
        final LinkedHashSet<ERTable> results = new LinkedHashSet<ERTable>();

        for (final ERTable table : getDiagram().getDiagramContents().getContents().getTableSet()) {
            if (!results.contains(table)) {
                getReferedTables(results, table);
                results.add(table);
            }
        }

        return results;
    }

    private void getReferedTables(final LinkedHashSet<ERTable> referedTables, final ERTable table) {
        for (final NodeElement nodeElement : table.getReferedElementList()) {
            if (nodeElement instanceof ERTable) {
                if (nodeElement != table) {
                    final ERTable referedTable = (ERTable) nodeElement;
                    if (!referedTables.contains(referedTable)) {
                        getReferedTables(referedTables, referedTable);
                        referedTables.add(referedTable);
                    }
                }
            }
        }
    }

    @Override
    protected String getCreateForeignKeys(final ERDiagram diagram) {
        return "";
    }

    @Override
    protected String filterName(final String name) {
        return "[" + super.filterName(name) + "]";
    }

}
