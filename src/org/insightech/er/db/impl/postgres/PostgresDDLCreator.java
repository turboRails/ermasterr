package org.insightech.er.db.impl.postgres;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.insightech.er.db.impl.postgres.tablespace.PostgresTablespaceProperties;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.insightech.er.util.Check;

public class PostgresDDLCreator extends DDLCreator {

    private static final Pattern DROP_TRIGGER_TABLE_PATTERN = Pattern.compile(".*\\s[oO][nN]\\s+(.+)\\s.*");

    public PostgresDDLCreator(final ERDiagram diagram, final Category targetCategory, final boolean semicolon) {
        super(diagram, targetCategory, semicolon);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPostDDL(final ERTable table) {
        final PostgresTableProperties commonTableProperties = (PostgresTableProperties) getDiagram().getDiagramContents().getSettings().getTableViewProperties();

        final PostgresTableProperties tableProperties = (PostgresTableProperties) table.getTableViewProperties();

        boolean isWithoutOIDs = tableProperties.isWithoutOIDs();
        if (!isWithoutOIDs) {
            isWithoutOIDs = commonTableProperties.isWithoutOIDs();
        }

        final StringBuilder postDDL = new StringBuilder();

        if (isWithoutOIDs) {
            postDDL.append(" WITHOUT OIDS");
        }

        postDDL.append(super.getPostDDL(table));

        return postDDL.toString();
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

    @Override
    protected String getDDL(final Tablespace tablespace) {
        final PostgresTablespaceProperties tablespaceProperties = (PostgresTablespaceProperties) tablespace.getProperties(environment, getDiagram());

        final StringBuilder ddl = new StringBuilder();

        ddl.append("CREATE TABLESPACE ");
        ddl.append(filterName(tablespace.getName()));
        ddl.append(LF());

        if (!Check.isEmpty(tablespaceProperties.getOwner())) {
            ddl.append(" OWNER ");
            ddl.append(tablespaceProperties.getOwner());
            ddl.append(LF());
        }

        ddl.append(" LOCATION '");
        ddl.append(tablespaceProperties.getLocation());
        ddl.append("'" + LF());

        if (semicolon) {
            ddl.append(";");
        }

        return ddl.toString();
    }

    private String getAutoIncrementSettingDDL(final ERTable table, final NormalColumn column) {
        final StringBuilder ddl = new StringBuilder();

        final Sequence sequence = column.getAutoIncrementSetting();

        if (sequence.getIncrement() != null || sequence.getMinValue() != null || sequence.getMaxValue() != null || sequence.getStart() != null || sequence.getCache() != null || sequence.isCycle()) {

            ddl.append("ALTER SEQUENCE ");
            ddl.append(filterName(table.getNameWithSchema(getDiagram().getDatabase()) + "_" + column.getPhysicalName() + "_SEQ"));

            if (sequence.getIncrement() != null) {
                ddl.append(" INCREMENT ");
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
                ddl.append(" RESTART ");
                ddl.append(sequence.getStart());
            }
            if (sequence.getCache() != null) {
                ddl.append(" CACHE ");
                ddl.append(sequence.getCache());
            }
            if (sequence.isCycle()) {
                ddl.append(" CYCLE");
            }
            if (semicolon) {
                ddl.append(";");
            }
        }

        return ddl.toString();
    }

    @Override
    protected String getTableSettingDDL(final ERTable table) {
        final StringBuilder ddl = new StringBuilder();

        boolean first = true;

        for (final NormalColumn column : table.getNormalColumns()) {
            if (column.getType() != null) {
                if (SqlType.SQL_TYPE_ID_SERIAL.equals(column.getType().getId()) || SqlType.SQL_TYPE_ID_BIG_SERIAL.equals(column.getType().getId())) {
                    final String autoIncrementSettingDDL = getAutoIncrementSettingDDL(table, column);
                    if (!Check.isEmpty(autoIncrementSettingDDL)) {
                        ddl.append(autoIncrementSettingDDL);
                        ddl.append(LF());
                        first = false;
                    }
                }
            }
        }

        if (!first) {
            ddl.append(LF(2));
        }

        return ddl.toString();
    }

    @Override
    public String getDropDDL(final Trigger trigger) {
        final StringBuilder ddl = new StringBuilder();

        ddl.append("DROP TRIGGER ");
        ddl.append(getIfExistsOption());
        ddl.append(filterName(trigger.getName()));
        ddl.append(" ON ");

        final Matcher matcher = DROP_TRIGGER_TABLE_PATTERN.matcher(trigger.getSql());
        if (matcher.find()) {
            ddl.append(matcher.group(1));
        }

        if (semicolon) {
            ddl.append(";");
        }

        return ddl.toString();
    }

    @Override
    public String getIfExistsOption() {
        return "IF EXISTS ";
    }
}
