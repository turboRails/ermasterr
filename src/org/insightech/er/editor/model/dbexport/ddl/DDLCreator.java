package org.insightech.er.editor.model.dbexport.ddl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

import org.insightech.er.ResourceString;
import org.insightech.er.db.DBManager;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableViewProperties;
import org.insightech.er.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.insightech.er.editor.model.settings.Environment;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.model.settings.export.ExportDDLSetting;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;

public abstract class DDLCreator {

    private final ERDiagram diagram;

    private final Category targetCategory;

    protected boolean semicolon;

    protected Environment environment;

    protected DDLTarget ddlTarget;

    private String lineFeedCode;

    public DDLCreator(final ERDiagram diagram, final Category targetCategory, final boolean semicolon) {
        this.diagram = diagram;
        this.semicolon = semicolon;
        this.targetCategory = targetCategory;
    }

    public void init(final Environment environment, final DDLTarget ddlTarget, final String lineFeedCode) {
        this.environment = environment;
        this.ddlTarget = ddlTarget;
        this.lineFeedCode = lineFeedCode;
    }

    public String getDropDDL(final ERDiagram diagram) {
        final StringBuilder ddl = new StringBuilder();

        diagram.getDiagramContents().sort();

        if (ddlTarget.dropIndex) {
            ddl.append(getDropIndexes(diagram));
        }
        if (ddlTarget.dropView) {
            ddl.append(getDropViews(diagram));
        }
        if (ddlTarget.dropTrigger) {
            ddl.append(getDropTriggers(diagram));
        }
        if (ddlTarget.dropTable) {
            ddl.append(getDropTables(diagram));
        }
        if (ddlTarget.dropSequence && DBManagerFactory.getDBManager(diagram).isSupported(DBManager.SUPPORT_SEQUENCE)) {
            ddl.append(getDropSequences(diagram));
        }
        if (ddlTarget.dropTablespace) {
            ddl.append(getDropTablespaces(diagram));
        }

        ddl.append(LF());

        return ddl.toString();
    }

    private String getDropTablespaces(final ERDiagram diagram) {
        final StringBuilder ddl = new StringBuilder();

        boolean first = true;

        if (getDBManager().createTablespaceProperties() != null) {
            for (final Tablespace tablespace : diagram.getDiagramContents().getTablespaceSet()) {
                if (first) {
                    ddl.append(LF() + "/* Drop Tablespaces */" + LF(2));
                    first = false;
                }

                ddl.append(this.getDropDDL(tablespace));
                ddl.append(LF(3));
            }
        }

        return ddl.toString();
    }

    private String getDropSequences(final ERDiagram diagram) {
        final StringBuilder ddl = new StringBuilder();

        boolean first = true;

        for (final Sequence sequence : diagram.getDiagramContents().getSequenceSet()) {
            if (first) {
                ddl.append(LF() + "/* Drop Sequences */" + LF(2));
                first = false;
            }
            ddl.append(this.getDropDDL(sequence));
            ddl.append(LF());
        }

        if (!first) {
            ddl.append(LF(2));
        }

        return ddl.toString();
    }

    private String getDropViews(final ERDiagram diagram) {
        final StringBuilder ddl = new StringBuilder();

        boolean first = true;

        for (final View view : diagram.getDiagramContents().getContents().getViewSet()) {
            if (first) {
                ddl.append(LF() + "/* Drop Views */" + LF(2));
                first = false;
            }
            ddl.append(this.getDropDDL(view));
            ddl.append(LF());
        }

        if (!first) {
            ddl.append(LF(2));
        }

        return ddl.toString();
    }

    private String getDropTriggers(final ERDiagram diagram) {
        final StringBuilder ddl = new StringBuilder();

        boolean first = true;

        for (final Trigger trigger : diagram.getDiagramContents().getTriggerSet()) {
            if (first) {
                ddl.append(LF() + "/* Drop Triggers */" + LF(2));
                first = false;
            }
            ddl.append(this.getDropDDL(trigger));
            ddl.append(LF());
        }

        if (!first) {
            ddl.append(LF(2));
        }

        return ddl.toString();
    }

    private String getDropIndexes(final ERDiagram diagram) {
        final StringBuilder ddl = new StringBuilder();

        boolean first = true;

        for (final ERTable table : diagram.getDiagramContents().getContents().getTableSet()) {

            if (targetCategory != null && !targetCategory.contains(table)) {
                continue;
            }

            for (final Index index : table.getIndexes()) {
                if (first) {
                    ddl.append(LF() + "/* Drop Indexes */" + LF(2));
                    first = false;
                }
                ddl.append(this.getDropDDL(index, table));
                ddl.append(LF());
            }
        }

        if (!first) {
            ddl.append(LF(2));
        }

        return ddl.toString();
    }

    private String getDropTables(final ERDiagram diagram) {
        final StringBuilder ddl = new StringBuilder();

        final Set<TableView> doneTables = new HashSet<TableView>();

        boolean first = true;

        for (final ERTable table : diagram.getDiagramContents().getContents().getTableSet()) {

            if (targetCategory != null && !targetCategory.contains(table)) {
                continue;
            }

            if (first) {
                ddl.append(LF() + "/* Drop Tables */" + LF(2));
                first = false;
            }

            if (!doneTables.contains(table)) {
                ddl.append(this.getDropDDL(table, doneTables));
            }
        }

        if (!first) {
            ddl.append(LF(2));
        }

        return ddl.toString();
    }

    public String getCreateDDL(final ERDiagram diagram) {
        final StringBuilder ddl = new StringBuilder();

        diagram.getDiagramContents().sort();

        if (ddlTarget.createTablespace) {
            ddl.append(getCreateTablespaces(diagram));
        }
        if (ddlTarget.createSequence && DBManagerFactory.getDBManager(diagram).isSupported(DBManager.SUPPORT_SEQUENCE)) {
            ddl.append(getCreateSequences(diagram));
        }
        if (ddlTarget.createTable) {
            ddl.append(getCreateTables(diagram));
        }
        if (ddlTarget.createForeignKey) {
            ddl.append(getCreateForeignKeys(diagram));
        }
        if (ddlTarget.createTrigger) {
            ddl.append(getCreateTriggers(diagram));
        }
        if (ddlTarget.createView) {
            ddl.append(getCreateViews(diagram));
        }
        if (ddlTarget.createIndex) {
            ddl.append(getCreateIndexes(diagram));
        }
        if (ddlTarget.createComment) {
            ddl.append(getCreateComment(diagram));
        }

        ddl.append(LF());

        return ddl.toString();
    }

    private String getCreateTablespaces(final ERDiagram diagram) {
        final StringBuilder ddl = new StringBuilder();

        boolean first = true;

        if (getDBManager().createTablespaceProperties() != null) {
            for (final Tablespace tablespace : diagram.getDiagramContents().getTablespaceSet()) {
                if (first) {
                    ddl.append(LF() + "/* Create Tablespaces */" + LF(2));
                    first = false;
                }

                final String description = tablespace.getDescription();
                if (semicolon && !Check.isEmpty(description) && ddlTarget.inlineTableComment) {
                    ddl.append("-- ");
                    ddl.append(replaceLF(description, LF() + "-- "));
                    ddl.append(LF());
                }

                ddl.append(this.getDDL(tablespace));
                ddl.append(LF(3));
            }
        }

        return ddl.toString();
    }

    abstract protected String getDDL(Tablespace object);

    protected Iterable<ERTable> getTablesForCreateDDL() {
        return diagram.getDiagramContents().getContents().getTableSet();
    }

    private String getCreateTables(final ERDiagram diagram) {
        final StringBuilder ddl = new StringBuilder();

        boolean first = true;

        for (final ERTable table : getTablesForCreateDDL()) {

            if (targetCategory != null && !targetCategory.contains(table)) {
                continue;
            }

            if (first) {
                ddl.append(LF() + "/* Create Tables */" + LF(2));
                first = false;
            }

            ddl.append(this.getDDL(table));
            ddl.append(LF(3));
            ddl.append(getTableSettingDDL(table));
        }

        return ddl.toString();
    }

    protected String getCreateForeignKeys(final ERDiagram diagram) {
        final StringBuilder ddl = new StringBuilder();

        boolean first = true;

        for (final ERTable table : diagram.getDiagramContents().getContents().getTableSet()) {

            if (targetCategory != null && !targetCategory.contains(table)) {
                continue;
            }

            for (final Relation relation : table.getOutgoingRelations()) {
                if (first) {
                    ddl.append(LF() + "/* Create Foreign Keys */" + LF(2));
                    first = false;
                }
                ddl.append(this.getDDL(relation));
                ddl.append(LF(3));
            }
        }

        return ddl.toString();
    }

    private String getCreateIndexes(final ERDiagram diagram) {
        final StringBuilder ddl = new StringBuilder();

        boolean first = true;

        for (final ERTable table : diagram.getDiagramContents().getContents().getTableSet()) {

            if (targetCategory != null && !targetCategory.contains(table)) {
                continue;
            }

            for (final Index index : table.getIndexes()) {
                if (first) {
                    ddl.append(LF() + "/* Create Indexes */" + LF(2));
                    first = false;
                }
                ddl.append(this.getDDL(index, table));
                ddl.append(LF());
            }
        }

        if (!first) {
            ddl.append(LF(2));
        }

        return ddl.toString();
    }

    private String getCreateViews(final ERDiagram diagram) {
        final StringBuilder ddl = new StringBuilder();

        boolean first = true;

        for (final View view : diagram.getDiagramContents().getContents().getViewSet()) {

            if (first) {
                ddl.append(LF() + "/* Create Views */" + LF(2));
                first = false;
            }
            ddl.append(this.getDDL(view));
            ddl.append(LF());
        }

        if (!first) {
            ddl.append(LF(2));
        }

        return ddl.toString();
    }

    private String getCreateTriggers(final ERDiagram diagram) {
        final StringBuilder ddl = new StringBuilder();

        boolean first = true;

        for (final Trigger trigger : diagram.getDiagramContents().getTriggerSet()) {

            if (first) {
                ddl.append(LF() + "/* Create Triggers */" + LF(2));
                first = false;
            }
            ddl.append(this.getDDL(trigger));
            ddl.append(LF());
        }

        if (!first) {
            ddl.append(LF(2));
        }

        return ddl.toString();
    }

    private String getCreateSequences(final ERDiagram diagram) {
        final StringBuilder ddl = new StringBuilder();

        boolean first = true;

        final List<String> autoSequenceNames = diagram.getDiagramContents().getContents().getTableSet().getAutoSequenceNames(diagram.getDatabase());

        for (final Sequence sequence : diagram.getDiagramContents().getSequenceSet()) {
            final String sequenceName = getNameWithSchema(sequence.getSchema(), sequence.getName()).toUpperCase();
            if (autoSequenceNames.contains(sequenceName)) {
                continue;
            }

            if (first) {
                ddl.append(LF() + "/* Create Sequences */" + LF(2));
                first = false;
            }
            ddl.append(this.getDDL(sequence));
            ddl.append(LF());
        }

        if (!first) {
            ddl.append(LF(2));
        }

        return ddl.toString();
    }

    private String getCreateComment(final ERDiagram diagram) {
        final StringBuilder ddl = new StringBuilder();

        boolean first = true;

        for (final ERTable table : diagram.getDiagramContents().getContents().getTableSet()) {

            if (targetCategory != null && !targetCategory.contains(table)) {
                continue;
            }
            final List<String> commentDDLList = getCommentDDL(table);

            if (!commentDDLList.isEmpty()) {
                if (first) {
                    ddl.append(LF() + "/* Comments */" + LF(2));
                    first = false;
                }

                for (final String commentDDL : commentDDLList) {
                    ddl.append(commentDDL);
                    ddl.append(LF());
                }
            }
        }

        if (!first) {
            ddl.append(LF(2));
        }

        return ddl.toString();
    }

    protected String getDDL(final ERTable table) {
        final StringBuilder ddl = new StringBuilder();

        final String tableComment = filterComment(table.getLogicalName(), table.getDescription(), false);

        if (semicolon && !Check.isEmpty(tableComment) && ddlTarget.inlineTableComment) {
            ddl.append("-- ");
            ddl.append(replaceLF(tableComment, LF() + "-- "));
            ddl.append(LF());
        }
        ddl.append("CREATE TABLE ");
        ddl.append(filterName(table.getNameWithSchema(diagram.getDatabase())));
        ddl.append(LF() + "(" + LF());

        boolean first = true;

        for (final Column column : table.getColumns()) {
            if (column instanceof NormalColumn) {
                final NormalColumn normalColumn = (NormalColumn) column;

                if (!first) {
                    ddl.append("," + LF());
                }

                ddl.append(getColulmnDDL(normalColumn));

                first = false;

            } else {
                final ColumnGroup columnGroup = (ColumnGroup) column;

                for (final NormalColumn normalColumn : columnGroup.getColumns()) {
                    if (!first) {
                        ddl.append("," + LF());
                    }

                    ddl.append(getColulmnDDL(normalColumn));

                    first = false;
                }
            }
        }

        ddl.append(getPrimaryKeyDDL(table));
        ddl.append(getUniqueKeyDDL(table));

        String constraint = Format.null2blank(table.getConstraint()).trim();
        if (!"".equals(constraint)) {
            constraint = replaceLF(constraint, LF() + "\t");

            ddl.append("," + LF());
            ddl.append("\t");
            ddl.append(constraint);
        }

        ddl.append(LF());
        ddl.append(")");

        ddl.append(getPostDDL(table));

        final String option = Format.null2blank(table.getOption()).trim();
        if (!"".equals(option)) {
            ddl.append(LF());
            ddl.append(option);
        }

        if (semicolon) {
            ddl.append(";");
        }

        return ddl.toString();
    }

    protected String getPrimaryKeyDDL(final ERTable table) {
        final StringBuilder ddl = new StringBuilder();

        final List<NormalColumn> primaryKeys = table.getPrimaryKeys();

        if (primaryKeys.size() != 0) {
            ddl.append("," + LF());
            ddl.append("\t");
            if (!Check.isEmpty(table.getPrimaryKeyName())) {
                ddl.append("CONSTRAINT ");
                ddl.append(table.getPrimaryKeyName());
                ddl.append(" ");
            }
            ddl.append("PRIMARY KEY (");

            boolean first = true;
            for (final NormalColumn primaryKey : primaryKeys) {
                if (!first) {
                    ddl.append(", ");
                }
                ddl.append(filterName(primaryKey.getPhysicalName()));
                ddl.append(getPrimaryKeyLength(table, primaryKey));
                first = false;
            }

            ddl.append(")");
        }

        return ddl.toString();
    }

    protected String getUniqueKeyDDL(final ERTable table) {
        final StringBuilder ddl = new StringBuilder();

        final List<ComplexUniqueKey> complexUniqueKeyList = table.getComplexUniqueKeyList();
        for (final ComplexUniqueKey complexUniqueKey : complexUniqueKeyList) {
            ddl.append("," + LF());
            ddl.append("\t");
            if (!Check.isEmpty(complexUniqueKey.getUniqueKeyName())) {
                ddl.append("CONSTRAINT ");
                ddl.append(complexUniqueKey.getUniqueKeyName());
                ddl.append(" ");
            }

            ddl.append("UNIQUE (");

            boolean first = true;
            for (final NormalColumn column : complexUniqueKey.getColumnList()) {
                if (!first) {
                    ddl.append(", ");
                }
                ddl.append(filterName(column.getPhysicalName()));
                first = false;
            }

            ddl.append(")");
        }

        return ddl.toString();
    }

    protected String getPrimaryKeyLength(final ERTable table, final NormalColumn primaryKey) {
        return "";
    }

    protected String getTableSettingDDL(final ERTable table) {
        return "";
    }

    protected String getColulmnDDL(final NormalColumn normalColumn) {
        final StringBuilder ddl = new StringBuilder();

        final String columnComment = filterComment(normalColumn.getLogicalName(), normalColumn.getDescription(), true);

        if (semicolon && !Check.isEmpty(columnComment) && ddlTarget.inlineColumnComment) {
            ddl.append("\t-- ");
            ddl.append(replaceLF(columnComment, LF() + "\t-- "));
            ddl.append(LF());
        }

        ddl.append("\t");
        ddl.append(filterName(normalColumn.getPhysicalName()));
        ddl.append(" ");

        ddl.append(filter(Format.formatType(normalColumn.getType(), normalColumn.getTypeData(), diagram.getDatabase(), true)));

        if (!Check.isEmpty(normalColumn.getDefaultValue())) {
            String defaultValue = normalColumn.getDefaultValue();
            if (ResourceString.getResourceString("label.current.date.time").equals(defaultValue)) {
                defaultValue = getDBManager().getCurrentTimeValue()[0];

            } else if (ResourceString.getResourceString("label.empty.string").equals(defaultValue)) {
                defaultValue = "";
            }

            ddl.append(" DEFAULT ");
            if (doesNeedQuoteDefaultValue(normalColumn)) {
                ddl.append("'");
                ddl.append(Format.escapeSQL(defaultValue));
                ddl.append("'");

            } else {
                ddl.append(defaultValue);
            }
        }

        if (normalColumn.isNotNull()) {
            ddl.append(" NOT NULL");
        }

        if (normalColumn.isUniqueKey()) {
            if (!Check.isEmpty(normalColumn.getUniqueKeyName())) {
                ddl.append(" CONSTRAINT ");
                ddl.append(normalColumn.getUniqueKeyName());
            }
            ddl.append(" UNIQUE");
        }

        final String constraint = Format.null2blank(normalColumn.getConstraint());
        if (!"".equals(constraint)) {
            ddl.append(" ");
            ddl.append(constraint);
        }

        return ddl.toString();
    }

    protected boolean doesNeedQuoteDefaultValue(final NormalColumn normalColumn) {
        if (normalColumn.getType() == null) {
            return false;
        }

        if (normalColumn.getType().isNumber()) {
            return false;
        }

        if (normalColumn.getType().isTimestamp()) {
            if (!Character.isDigit(normalColumn.getDefaultValue().toCharArray()[0])) {
                return false;

            } else if (Check.isNumber(normalColumn.getDefaultValue())) {
                return false;
            }
        }

        return true;
    }

    protected List<String> getCommentDDL(final ERTable table) {
        return new ArrayList<String>();
    }

    /**
     * {@inheritDoc}
     */
    protected String getPostDDL(final ERTable table) {
        final TableViewProperties commonTableProperties = getDiagram().getDiagramContents().getSettings().getTableViewProperties();

        final TableProperties tableProperties = (TableProperties) table.getTableViewProperties();

        Tablespace tableSpace = tableProperties.getTableSpace();
        if (tableSpace == null) {
            tableSpace = commonTableProperties.getTableSpace();
        }

        final StringBuilder postDDL = new StringBuilder();

        if (tableSpace != null) {
            postDDL.append(" TABLESPACE ");
            postDDL.append(tableSpace.getName());
        }

        return postDDL.toString();
    }

    protected String getDDL(final Index index, final ERTable table) {
        final StringBuilder ddl = new StringBuilder();

        final String description = index.getDescription();
        if (semicolon && !Check.isEmpty(description) && ddlTarget.inlineTableComment) {
            ddl.append("-- ");
            ddl.append(replaceLF(description, LF() + "-- "));
            ddl.append(LF());
        }

        ddl.append("CREATE ");
        if (!index.isNonUnique()) {
            ddl.append("UNIQUE ");
        }
        ddl.append("INDEX ");
        ddl.append(filterName(index.getName()));
        ddl.append(" ON ");
        ddl.append(filterName(table.getNameWithSchema(diagram.getDatabase())));

        if (index.getType() != null && !index.getType().trim().equals("")) {
            ddl.append(" USING ");
            ddl.append(index.getType().trim());
        }

        ddl.append(" (");
        boolean first = true;

        int i = 0;
        final List<Boolean> descs = index.getDescs();

        for (final NormalColumn column : index.getColumns()) {
            if (!first) {
                ddl.append(", ");

            }

            ddl.append(filterName(column.getPhysicalName()));

            if (getDBManager().isSupported(DBManager.SUPPORT_DESC_INDEX)) {
                if (descs.size() > i) {
                    final Boolean desc = descs.get(i);
                    if (Boolean.TRUE.equals(desc)) {
                        ddl.append(" DESC");
                    } else {
                        ddl.append(" ASC");
                    }
                }
            }

            first = false;
            i++;
        }

        ddl.append(")");

        if (semicolon) {
            ddl.append(";");
        }

        return ddl.toString();
    }

    protected String getDDL(final Relation relation) {
        final StringBuilder ddl = new StringBuilder();

        ddl.append("ALTER TABLE ");
        ddl.append(filterName(relation.getTargetTableView().getNameWithSchema(diagram.getDatabase())));
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
        ddl.append(filterName(relation.getSourceTableView().getNameWithSchema(diagram.getDatabase())));
        ddl.append(" (");

        first = true;

        for (final NormalColumn foreignKeyColumn : relation.getForeignKeyColumns()) {
            if (!first) {
                ddl.append(", ");

            }

            ddl.append(filterName(foreignKeyColumn.getReferencedColumn(relation).getPhysicalName()));
            first = false;
        }

        ddl.append(")" + LF());
        ddl.append("\tON UPDATE ");
        ddl.append(relation.getOnUpdateAction());
        ddl.append(LF());
        ddl.append("\tON DELETE ");
        ddl.append(relation.getOnDeleteAction());
        ddl.append(LF());

        if (semicolon) {
            ddl.append(";");
        }

        return ddl.toString();
    }

    protected String getDDL(final View view) {
        final StringBuilder ddl = new StringBuilder();

        final String description = view.getDescription();
        if (semicolon && !Check.isEmpty(description) && ddlTarget.inlineTableComment) {
            ddl.append("-- ");
            ddl.append(replaceLF(description, LF() + "-- "));
            ddl.append(LF());
        }

        ddl.append(getCreateOrReplacePrefix() + " VIEW ");
        ddl.append(filterName(getNameWithSchema(view.getTableViewProperties().getSchema(), view.getPhysicalName())));
        ddl.append(" AS ");
        String sql = filterName(view.getSql());
        if (sql.endsWith(";")) {
            sql = sql.substring(0, sql.length() - 1);
        }
        ddl.append(sql);

        if (semicolon) {
            ddl.append(";");
        }

        return ddl.toString();
    }

    protected String getDDL(final Trigger trigger) {
        final StringBuilder ddl = new StringBuilder();

        final String description = trigger.getDescription();
        if (semicolon && !Check.isEmpty(description) && ddlTarget.inlineTableComment) {
            ddl.append("-- ");
            ddl.append(replaceLF(description, LF() + "-- "));
            ddl.append(LF());
        }

        ddl.append(getCreateOrReplacePrefix() + " TRIGGER ");
        ddl.append(filterName(getNameWithSchema(trigger.getSchema(), trigger.getName())));
        ddl.append(" ");
        ddl.append(filterName(trigger.getSql()));

        if (semicolon) {
            ddl.append(";");
        }

        return ddl.toString();
    }

    protected String getDDL(final Sequence sequence) {
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
            ddl.append(" START ");
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

        return ddl.toString();
    }

    protected String getDropDDL(final Index index, final ERTable table) {
        final StringBuilder ddl = new StringBuilder();

        ddl.append("DROP INDEX ");
        ddl.append(getIfExistsOption());
        ddl.append(filterName(index.getName()));
        if (semicolon) {
            ddl.append(";");
        }

        return ddl.toString();
    }

    protected String getDropDDL(final ERTable table, final Set<TableView> doneTables) {
        final StringBuilder ddl = new StringBuilder();

        doneTables.add(table);

        for (final Relation relation : table.getOutgoingRelations()) {
            final TableView targetTableView = relation.getTargetTableView();

            if (!doneTables.contains(targetTableView)) {
                doneTables.add(targetTableView);

                if (targetTableView instanceof ERTable) {
                    final String targetTableDDL = this.getDropDDL((ERTable) targetTableView, doneTables);
                    ddl.append(targetTableDDL);
                }
            }
        }

        ddl.append(getDropTableDDL(filterName(table.getNameWithSchema(diagram.getDatabase()))));
        ddl.append(getPostDropDDL(table));

        if (semicolon) {
            ddl.append(";");
        }

        ddl.append(LF());

        return ddl.toString();
    }

    protected String getDropTableDDL(final String name) {
        final String ddl = "DROP TABLE " + getIfExistsOption() + name;

        return ddl;
    }

    protected String getPostDropDDL(final TableView table) {
        return "";
    }

    protected String getDropDDL(final View view) {
        final StringBuilder ddl = new StringBuilder();

        ddl.append("DROP VIEW ");
        ddl.append(getIfExistsOption());
        ddl.append(filterName(getNameWithSchema(view.getTableViewProperties().getSchema(), view.getPhysicalName())));
        if (semicolon) {
            ddl.append(";");
        }

        return ddl.toString();
    }

    protected String getDropDDL(final Trigger trigger) {
        final StringBuilder ddl = new StringBuilder();

        ddl.append("DROP TRIGGER ");
        ddl.append(getIfExistsOption());
        ddl.append(filterName(trigger.getName()));
        if (semicolon) {
            ddl.append(";");
        }

        return ddl.toString();
    }

    protected String getDropDDL(final Tablespace tablespace) {
        final StringBuilder ddl = new StringBuilder();

        ddl.append("DROP ");
        ddl.append("TABLESPACE ");
        ddl.append(getIfExistsOption());
        ddl.append(filterName(tablespace.getName()));
        if (semicolon) {
            ddl.append(";");
        }

        return ddl.toString();
    }

    protected String getDropDDL(final Sequence sequence) {
        final StringBuilder ddl = new StringBuilder();

        ddl.append("DROP ");
        ddl.append("SEQUENCE ");
        ddl.append(getIfExistsOption());
        ddl.append(filterName(getNameWithSchema(sequence.getSchema(), sequence.getName())));
        if (semicolon) {
            ddl.append(";");
        }

        return ddl.toString();
    }

    protected String filterName(final String str) {
        return filter(str);
    }

    protected String filter(final String str) {
        if (str == null) {
            return "";
        }

        final Settings settings = diagram.getDiagramContents().getSettings();

        if (settings.isCapital()) {
            return str.toUpperCase();
        }

        return str;
    }

    protected DBManager getDBManager() {
        return DBManagerFactory.getDBManager(diagram);
    }

    protected ERDiagram getDiagram() {
        return diagram;
    }

    protected String getNameWithSchema(String schema, final String name) {
        final StringBuilder sb = new StringBuilder();

        if (Check.isEmpty(schema)) {
            schema = getDiagram().getDiagramContents().getSettings().getTableViewProperties().getSchema();
        }

        if (!Check.isEmpty(schema)) {
            sb.append(schema);
            sb.append(".");
        }

        sb.append(name);

        return sb.toString();
    }

    public String getIfExistsOption() {
        return "";
    }

    protected String filterComment(final String logicalName, final String description, final boolean column) {
        String comment = null;

        if (ddlTarget.commentValueLogicalNameDescription) {
            comment = Format.null2blank(logicalName);

            if (!Check.isEmpty(description)) {
                comment = comment + " : " + Format.null2blank(description);
            }

        } else if (ddlTarget.commentValueLogicalName) {
            comment = Format.null2blank(logicalName);

        } else {
            comment = Format.null2blank(description);

        }

        if (ddlTarget.commentReplaceLineFeed) {
            comment = replaceLF(comment, ddlTarget.commentReplaceString);
        }

        return comment;
    }

    protected String getCreateOrReplacePrefix() {
        return "CREATE";
    }

    protected String replaceLF(String str, final String replaceString) {
        str = str.replaceAll("\r\n", "\n");
        str = str.replaceAll("\r", "\n");
        str = str.replaceAll("\n", Matcher.quoteReplacement(Format.null2blank(replaceString)));

        return str;
    }

    protected String LF() {
        return LF(1);
    }

    protected String LF(final int num) {
        String lf = System.getProperty("line.separator");

        if (ExportDDLSetting.LF.equals(lineFeedCode)) {
            lf = "\n";

        } else if (ExportDDLSetting.CRLF.equals(lineFeedCode)) {
            lf = "\r\n";
        }

        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < num; i++) {
            sb.append(lf);
        }

        return sb.toString();
    }
}
