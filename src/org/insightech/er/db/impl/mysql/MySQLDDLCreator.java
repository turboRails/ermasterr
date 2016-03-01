package org.insightech.er.db.impl.mysql;

import java.util.List;

import org.insightech.er.ResourceString;
import org.insightech.er.db.DBManager;
import org.insightech.er.db.impl.mysql.tablespace.MySQLTablespaceProperties;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;

public class MySQLDDLCreator extends DDLCreator {

    public MySQLDDLCreator(final ERDiagram diagram, final Category targetCategory, final boolean semicolon) {
        super(diagram, targetCategory, semicolon);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPostDDL(final ERTable table) {
        final MySQLTableProperties commonTableProperties = (MySQLTableProperties) getDiagram().getDiagramContents().getSettings().getTableViewProperties();

        final MySQLTableProperties tableProperties = (MySQLTableProperties) table.getTableViewProperties();

        String engine = tableProperties.getStorageEngine();
        if (Check.isEmpty(engine)) {
            engine = commonTableProperties.getStorageEngine();
        }
        String characterSet = tableProperties.getCharacterSet();
        if (Check.isEmpty(characterSet)) {
            characterSet = commonTableProperties.getCharacterSet();
        }

        final String collation = tableProperties.getCollation();
        if (Check.isEmpty(collation)) {
            characterSet = commonTableProperties.getCharacterSet();
        }

        final StringBuilder postDDL = new StringBuilder();
        if (!Check.isEmpty(engine)) {
            postDDL.append(" ENGINE = ");
            postDDL.append(engine);
        }

        if (ddlTarget.createComment) {
            String comment = filterComment(table.getLogicalName(), table.getDescription(), false);
            comment = replaceLF(comment, LF());

            if (!Check.isEmpty(comment)) {
                postDDL.append(" COMMENT = '");
                postDDL.append(comment.replaceAll("'", "''"));
                postDDL.append("'");
            }
        }

        if (!Check.isEmpty(characterSet)) {
            postDDL.append(" DEFAULT CHARACTER SET ");
            postDDL.append(characterSet);

            if (!Check.isEmpty(collation)) {
                postDDL.append(" COLLATE ");
                postDDL.append(collation);
            }
        }

        postDDL.append(super.getPostDDL(table));

        return postDDL.toString();
    }

    @Override
    protected String getUniqueKeyDDL(final ERTable table) {
        final StringBuilder ddl = new StringBuilder();

        for (final NormalColumn column : table.getExpandedColumns()) {
            if (column.isUniqueKey()) {
                ddl.append("," + LF());
                ddl.append("\t");
                if (!Check.isEmpty(column.getUniqueKeyName())) {
                    ddl.append("CONSTRAINT ");
                    ddl.append(column.getUniqueKeyName());
                    ddl.append(" ");
                }

                ddl.append("UNIQUE (");
                ddl.append(filterName(column.getPhysicalName()));
                ddl.append(")");
            }
        }

        ddl.append(super.getUniqueKeyDDL(table));

        return ddl.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getColulmnDDL(final NormalColumn normalColumn) {
        final StringBuilder ddl = new StringBuilder();

        final String description = normalColumn.getDescription();
        if (semicolon && !Check.isEmpty(description) && ddlTarget.inlineColumnComment) {
            ddl.append("\t-- ");
            ddl.append(replaceLF(description, LF() + "\t-- "));
            ddl.append(LF());
        }

        ddl.append("\t");
        ddl.append(filterName(normalColumn.getPhysicalName()));
        ddl.append(" ");

        ddl.append(filter(Format.formatType(normalColumn.getType(), normalColumn.getTypeData(), getDiagram().getDatabase(), true)));

        if (normalColumn.getTypeData().isBinary()) {
            ddl.append(" BINARY");
        }

        if (!Check.isEmpty(normalColumn.getCharacterSet())) {
            ddl.append(" CHARACTER SET ");
            ddl.append(normalColumn.getCharacterSet());

            if (!Check.isEmpty(normalColumn.getCollation())) {
                ddl.append(" COLLATE ");
                ddl.append(normalColumn.getCollation());
            }
        }

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

        final String constraint = Format.null2blank(normalColumn.getConstraint());
        if (!"".equals(constraint)) {
            ddl.append(" ");
            ddl.append(constraint);
        }

        if (normalColumn.isAutoIncrement()) {
            ddl.append(" AUTO_INCREMENT");
        }

        if (ddlTarget.createComment) {
            String comment = filterComment(normalColumn.getLogicalName(), normalColumn.getDescription(), true);
            comment = replaceLF(comment, LF());

            if (!Check.isEmpty(comment)) {
                ddl.append(" COMMENT '");
                ddl.append(comment.replaceAll("'", "''"));
                ddl.append("'");
            }
        }

        return ddl.toString();
    }

    @Override
    protected boolean doesNeedQuoteDefaultValue(final NormalColumn normalColumn) {
        if (!super.doesNeedQuoteDefaultValue(normalColumn)) {
            return false;
        }

        if ("CURRENT_TIMESTAMP".equalsIgnoreCase(normalColumn.getDefaultValue().trim())) {
            return false;
        }

        return true;
    }

    @Override
    protected String getDDL(final Tablespace tablespace) {
        final MySQLTablespaceProperties tablespaceProperties = (MySQLTablespaceProperties) tablespace.getProperties(environment, getDiagram());

        final StringBuilder ddl = new StringBuilder();

        ddl.append("CREATE TABLESPACE ");
        ddl.append(filterName(tablespace.getName()));
        ddl.append(LF());
        ddl.append(" ADD DATAFILE '");
        ddl.append(tablespaceProperties.getDataFile());
        ddl.append("'" + LF());
        ddl.append(" USE LOGFILE GROUP ");
        ddl.append(tablespaceProperties.getLogFileGroup());
        ddl.append(LF());

        if (!Check.isEmpty(tablespaceProperties.getExtentSize())) {
            ddl.append(" EXTENT_SIZE ");
            ddl.append(tablespaceProperties.getExtentSize());
            ddl.append(LF());
        }

        ddl.append(" INITIAL_SIZE ");
        ddl.append(tablespaceProperties.getInitialSize());
        ddl.append(LF());
        ddl.append(" ENGINE ");
        ddl.append(tablespaceProperties.getEngine());
        ddl.append(LF());

        if (semicolon) {
            ddl.append(";");
        }

        return ddl.toString();
    }

    @Override
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

        int maxLength = 60;

        if (column) {
            maxLength = 255;
        }

        if (comment.length() > maxLength) {
            comment = comment.substring(0, maxLength);
        }

        return comment;
    }

    @Override
    public String getDDL(final Index index, final ERTable table) {
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

        if (index.getType() != null && !index.getType().trim().equals("")) {
            ddl.append(" USING ");
            ddl.append(index.getType().trim());
        }

        ddl.append(" ON ");
        ddl.append(filterName(table.getNameWithSchema(getDiagram().getDatabase())));

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

    @Override
    public String getDropDDL(final ERDiagram diagram) {
        final StringBuilder ddl = new StringBuilder();
        ddl.append("SET SESSION FOREIGN_KEY_CHECKS=0");
        if (semicolon) {
            ddl.append(";");
        }
        ddl.append(LF());

        ddl.append(super.getDropDDL(diagram));

        return ddl.toString();
    }

    @Override
    public String getDropDDL(final Index index, final ERTable table) {
        final StringBuilder ddl = new StringBuilder();

        ddl.append("DROP INDEX ");
        ddl.append(filterName(index.getName()));
        ddl.append(" ON ");
        ddl.append(filterName(table.getNameWithSchema(getDiagram().getDatabase())));

        if (semicolon) {
            ddl.append(";");
        }

        return ddl.toString();
    }

    @Override
    protected String getPrimaryKeyLength(final ERTable table, final NormalColumn primaryKey) {
        final SqlType type = primaryKey.getType();

        if (type != null && type.isFullTextIndexable() && !type.isNeedLength(getDiagram().getDatabase())) {
            Integer length = null;

            MySQLTableProperties tableProperties = (MySQLTableProperties) table.getTableViewProperties();

            length = tableProperties.getPrimaryKeyLengthOfText();

            if (length == null) {
                tableProperties = (MySQLTableProperties) getDiagram().getDiagramContents().getSettings().getTableViewProperties();

                length = tableProperties.getPrimaryKeyLengthOfText();
            }

            return "(" + length + ")";
        }

        return "";
    }

    @Override
    public String getIfExistsOption() {
        return "IF EXISTS ";
    }

}
