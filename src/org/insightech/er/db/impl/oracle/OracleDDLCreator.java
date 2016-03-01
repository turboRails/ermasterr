package org.insightech.er.db.impl.oracle;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.db.impl.oracle.tablespace.OracleTablespaceProperties;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.insightech.er.util.Check;

public class OracleDDLCreator extends DDLCreator {

    public OracleDDLCreator(final ERDiagram diagram, final Category targetCategory, final boolean semicolon) {
        super(diagram, targetCategory, semicolon);
    }

    @Override
    public String getPostDropDDL(final TableView table) {
        return " CASCADE CONSTRAINTS";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getCommentDDL(final ERTable table) {
        final List<String> ddlList = new ArrayList<String>();

        String tableComment = filterComment(table.getLogicalName(), table.getDescription(), false);
        tableComment = replaceLF(tableComment, LF());

        if (!Check.isEmpty(tableComment)) {
            final StringBuilder ddl = new StringBuilder();

            ddl.append("COMMENT ON TABLE ");
            ddl.append(filterName(table.getNameWithSchema(getDiagram().getDatabase())));
            ddl.append(" IS '");
            ddl.append(tableComment.replaceAll("'", "''"));
            ddl.append("'");
            if (semicolon) {
                ddl.append(";");
            }

            ddlList.add(ddl.toString());
        }

        for (final Column column : table.getColumns()) {
            if (column instanceof NormalColumn) {
                final NormalColumn normalColumn = (NormalColumn) column;

                String comment = filterComment(normalColumn.getLogicalName(), normalColumn.getDescription(), true);
                comment = replaceLF(comment, LF());

                if (!Check.isEmpty(comment)) {
                    final StringBuilder ddl = new StringBuilder();

                    ddl.append("COMMENT ON COLUMN ");
                    ddl.append(filterName(table.getNameWithSchema(getDiagram().getDatabase())));
                    ddl.append(".");
                    ddl.append(filterName(normalColumn.getPhysicalName()));
                    ddl.append(" IS '");
                    ddl.append(comment.replaceAll("'", "''"));
                    ddl.append("'");
                    if (semicolon) {
                        ddl.append(";");
                    }

                    ddlList.add(ddl.toString());
                }

            } else {
                final ColumnGroup columnGroup = (ColumnGroup) column;

                for (final NormalColumn normalColumn : columnGroup.getColumns()) {
                    final String comment = filterComment(normalColumn.getLogicalName(), normalColumn.getDescription(), true);

                    if (!Check.isEmpty(comment)) {
                        final StringBuilder ddl = new StringBuilder();

                        ddl.append("COMMENT ON COLUMN ");
                        ddl.append(filterName(table.getNameWithSchema(getDiagram().getDatabase())));
                        ddl.append(".");
                        ddl.append(filterName(normalColumn.getPhysicalName()));
                        ddl.append(" IS '");
                        ddl.append(comment.replaceAll("'", "''"));
                        ddl.append("'");
                        if (semicolon) {
                            ddl.append(";");
                        }

                        ddlList.add(ddl.toString());
                    }
                }
            }
        }

        return ddlList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDDL(final Relation relation) {
        final StringBuilder ddl = new StringBuilder();

        ddl.append("ALTER TABLE ");
        ddl.append(filterName(relation.getTargetTableView().getNameWithSchema(getDiagram().getDatabase())));
        ddl.append(LF());
        ddl.append("\tADD ");
        if (relation.getName() != null && !relation.getName().trim().equals("")) {
            ddl.append("CONSTRAINT ");
            ddl.append(filterName(relation.getName()));
            ddl.append(" ");
        }
        ddl.append("FOREIGN KEY (");

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

            for (final NormalColumn referencedColumn : foreignKeyColumn.getReferencedColumnList()) {
                if (referencedColumn.getColumnHolder() == relation.getSourceTableView()) {
                    ddl.append(filterName(referencedColumn.getPhysicalName()));
                    first = false;
                    break;
                }
            }

        }

        ddl.append(")" + LF());
        if (!"RESTRICT".equalsIgnoreCase(relation.getOnDeleteAction())) {
            ddl.append("\tON DELETE ");
            ddl.append(filterName(relation.getOnDeleteAction()));
            ddl.append(LF());
        }

        if (semicolon) {
            ddl.append(";");
        }

        return ddl.toString();
    }

    @Override
    protected String getDDL(final Tablespace tablespace) {
        final OracleTablespaceProperties tablespaceProperties = (OracleTablespaceProperties) tablespace.getProperties(environment, getDiagram());

        final StringBuilder ddl = new StringBuilder();

        ddl.append("CREATE TABLESPACE ");
        ddl.append(filterName(tablespace.getName()));
        ddl.append(LF());

        if (!Check.isEmpty(tablespaceProperties.getDataFile())) {
            ddl.append(" DATAFILE ");
            ddl.append(tablespaceProperties.getDataFile());

            if (!Check.isEmpty(tablespaceProperties.getFileSize())) {
                ddl.append(" SIZE ");
                ddl.append(tablespaceProperties.getFileSize());
            }

            ddl.append(LF());
        }

        if (tablespaceProperties.isAutoExtend()) {
            ddl.append(" AUTOEXTEND ON NEXT ");
            ddl.append(tablespaceProperties.getAutoExtendSize());

            if (!Check.isEmpty(tablespaceProperties.getAutoExtendMaxSize())) {
                ddl.append(" MAXSIZE ");
                ddl.append(tablespaceProperties.getAutoExtendMaxSize());
            }

            ddl.append(LF());
        }

        if (!Check.isEmpty(tablespaceProperties.getMinimumExtentSize())) {
            ddl.append(" MINIMUM EXTENT ");
            ddl.append(tablespaceProperties.getMinimumExtentSize());
            ddl.append(LF());
        }

        ddl.append(" DEFAULT STORAGE(" + LF());
        if (!Check.isEmpty(tablespaceProperties.getInitial())) {
            ddl.append("  INITIAL ");
            ddl.append(tablespaceProperties.getInitial());
            ddl.append(LF());
        }
        if (!Check.isEmpty(tablespaceProperties.getNext())) {
            ddl.append("  NEXT ");
            ddl.append(tablespaceProperties.getNext());
            ddl.append(LF());
        }
        if (!Check.isEmpty(tablespaceProperties.getMinExtents())) {
            ddl.append("  MINEXTENTS ");
            ddl.append(tablespaceProperties.getMinExtents());
            ddl.append(LF());
        }
        if (!Check.isEmpty(tablespaceProperties.getMaxExtents())) {
            ddl.append("  MAXEXTEMTS ");
            ddl.append(tablespaceProperties.getMaxExtents());
            ddl.append(LF());
        }
        if (!Check.isEmpty(tablespaceProperties.getPctIncrease())) {
            ddl.append("  PCTINCREASE ");
            ddl.append(tablespaceProperties.getPctIncrease());
            ddl.append(LF());
        }
        ddl.append(" )" + LF());

        if (tablespaceProperties.isLogging()) {
            ddl.append(" LOGGING ");
        } else {
            ddl.append(" NOLOGGING ");
        }
        ddl.append(LF());

        if (tablespaceProperties.isOffline()) {
            ddl.append(" OFFLINE ");
        } else {
            ddl.append(" ONLINE ");
        }
        ddl.append(LF());

        if (tablespaceProperties.isTemporary()) {
            ddl.append(" TEMPORARY");
        } else {
            ddl.append(" PERMANENT ");
        }
        ddl.append(LF());

        if (tablespaceProperties.isAutoSegmentSpaceManagement()) {
            ddl.append(" SEGMENT SPACE MANAGEMENT AUTO ");
        } else {
            ddl.append(" SEGMENT SPACE MANAGEMENT MANUAL ");
        }
        ddl.append(LF());

        if (semicolon) {
            ddl.append(";");
        }

        return ddl.toString();
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
        ddl.append("SEQUENCE ");
        ddl.append(filterName(getNameWithSchema(sequence.getSchema(), sequence.getName())));
        if (sequence.getIncrement() != null) {
            ddl.append(" INCREMENT BY ");
            ddl.append(sequence.getIncrement());
        }
        if (sequence.getMinValue() != null) {
            ddl.append(" MINVALUE ");
            ddl.append(sequence.getMinValue());
        }
        if (sequence.getMaxValue() != null) {
            ddl.append(" MAXVALUE ");
            ddl.append(sequence.getMaxValue());
        }
        if (sequence.getStart() != null) {
            ddl.append(" START WITH ");
            ddl.append(sequence.getStart());
        }
        if (!sequence.isNocache() && sequence.getCache() != null) {
            ddl.append(" CACHE ");
            ddl.append(sequence.getCache());
        }
        if (sequence.isCycle()) {
            ddl.append(" CYCLE");
        }
        if (sequence.isNocache()) {
            ddl.append(" NOCACHE");
        }
        if (sequence.isOrder()) {
            ddl.append(" ORDER");
        }

        if (semicolon) {
            ddl.append(";");
        }

        return ddl.toString();
    }

    @Override
    public String getDDL(final Trigger trigger) {
        return super.getDDL(trigger) + LF(2) + "/" + LF();
    }

    @Override
    public String getCreateOrReplacePrefix() {
        return "CREATE OR REPLACE";
    }

}
