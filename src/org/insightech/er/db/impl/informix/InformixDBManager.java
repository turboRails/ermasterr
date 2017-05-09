package org.insightech.er.db.impl.informix;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.insightech.er.db.DBManagerBase;
import org.insightech.er.db.sqltype.SqlTypeManager;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.db.PreTableExportManager;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.dbimport.ImportFromDBManager;
import org.insightech.er.editor.model.dbimport.PreImportFromDBManager;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;

public class InformixDBManager extends DBManagerBase {

	public static final String ID = "Informix";

	@Override
	public String getId() {
		return ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDriverClassName() {
		return "com.informix.jdbc.IfxDriver";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getURL() {
		return "jdbc:informix-sqli://<HOST NAME>:<PORT>/<DB NAME>:INFORMIXSERVER=<SERVER NAME>";
	}

	@Override
	public int getDefaultPort() {
		return 1525;
	}

	@Override
	public SqlTypeManager getSqlTypeManager() {
		return new InformixSqlTypeManager();
	}

	@Override
	public TableProperties createTableProperties(TableProperties tableProperties) {
		if (tableProperties != null
				&& tableProperties instanceof InformixTableProperties) {
			return tableProperties;
		}

		return new InformixTableProperties();
	}

	@Override
	public DDLCreator getDDLCreator(ERDiagram diagram, Category targetCategory,
			boolean semicolon) {
		return new InformixDDLCreator(diagram, targetCategory, semicolon);
	}

	@Override
	public List<String> getIndexTypeList(ERTable table) {
		List<String> list = new ArrayList<String>();

		list.add("BTREE");

		return list;
	}

	@Override
	protected int[] getSupportItems() {
		return new int[] { SUPPORT_AUTO_INCREMENT, SUPPORT_SCHEMA,
				SUPPORT_SEQUENCE, SUPPORT_SEQUENCE_NOCACHE };
	}

	@Override
	public ImportFromDBManager getTableImportManager() {
		return new InformixTableImportManager();
	}

	@Override
	public PreImportFromDBManager getPreTableImportManager() {
		return new InformixPreTableImportManager();
	}

	@Override
	public PreTableExportManager getPreTableExportManager() {
		return new InformixPreTableExportManager();
	}

	@Override
	public TablespaceProperties createTablespaceProperties() {
		return null;
	}

	@Override
	public TablespaceProperties checkTablespaceProperties(
			TablespaceProperties tablespaceProperties) {
		return null;
	}

	@Override
	public String[] getCurrentTimeValue() {
		return new String[] { "CURRENT", "SYSDATE" };
	}

	@Override
	public List<String> getSystemSchemaList() {
		List<String> list = new ArrayList<String>();

		list.add("sysadmin");
		list.add("sysmaster");
		list.add("sysuser");
		list.add("sysutils");

		return list;
	}

	@Override
	public BigDecimal getSequenceMaxValue() {
		return null;
	}
}
