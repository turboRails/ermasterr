package org.insightech.er.db.impl.informix;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.insightech.er.editor.model.dbimport.ImportFromDBManagerEclipseBase;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;

public class InformixTableImportManager extends ImportFromDBManagerEclipseBase {


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getViewDefinitionSQL(String schema) {
		if (schema != null) {
			return "SELECT viewtext "
					+ "FROM sysviews v, systables t "
					+ "WHERE v.tabid=t.tabid AND t.owner = ? AND t.tabname = ?";

		} else {
			return "SELECT viewtext "
					+ "FROM sysviews v, systables t "
					+ "WHERE v.tabid=t.tabid AND t.tabname = ? ";
		}
	}

	@Override
	protected ColumnData createColumnData(ResultSet columnSet)
			throws SQLException {
		ColumnData columnData = super.createColumnData(columnSet);
		String type = columnData.type.toLowerCase();

		if ("bigint".equals(type)) {
		} 
		
		else if (type.startsWith("timestamp")) {
			columnData.size = columnData.decimalDegits;
		}
		
		else if(type.startsWith("datetime")) {
			String tableName = columnSet.getString("TABLE_NAME");
			
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("SELECT FIRST 1 * FROM " + tableName);
			ResultSetMetaData metaData = rs.getMetaData();

			for (int i = 0; i < metaData.getColumnCount(); i++)
			{
		            int col_no = i + 1;
		            String columnName = metaData.getColumnName(col_no);
		            String columnTypeName = metaData.getColumnTypeName(col_no);
		            
		            if(columnData.columnName.equals(columnName))
		            	columnData.type = columnTypeName;
			}
		}

		return columnData;
	}

	// TODO for identity column
	// private String getRestrictType(String tableName, String schema, ColumnData columnData) throws SQLException
	// {
	// String type = null;
	//
	// PreparedStatement ps = null;
	// ResultSet rs = null;
	//
	// try
	// {
	// ps = con.prepareStatement("select sequence_name from INFORMATION_SCHEMA.COLUMNS "
	// + " where table_name = ? "
	// + " and table_schema = ? "
	// + " and column_name = ?");
	//
	// ps.setString(1, tableName);
	// ps.setString(2, schema);
	// ps.setString(3, columnData.columnName);
	//
	// rs = ps.executeQuery();
	//
	// if (rs.next())
	// {
	// if (!Check.isEmpty(rs.getString("sequence_name")))
	// {
	// type = "identity";
	// }
	// }
	//
	// }
	// finally
	// {
	// if (rs != null)
	// {
	// rs.close();
	// }
	// if (ps != null)
	// {
	// ps.close();
	// }
	// }
	//
	// return type;
	// }

	@Override
	protected Sequence importSequence(String schema, String sequenceName) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = con.prepareStatement("SELECT s.start_val, s.min_val, s.max_val, s.inc_val, s.cache "
					+ "FROM syssequences s, systables t "
					+ "WHERE s.tabid=t.tabid AND t.owner = ? AND t.tabname = ?");
			
			stmt.setString(1, schema);
			stmt.setString(2, sequenceName);

			rs = stmt.executeQuery();

			if (rs.next()) {
				Sequence sequence = new Sequence();

				sequence.setName(sequenceName);
				sequence.setSchema(schema);
				sequence.setStart(rs.getLong("start_val"));
				sequence.setMinValue(rs.getLong("min_val"));
				sequence.setMinValue(rs.getLong("max_val"));
				sequence.setIncrement(rs.getInt("inc_val"));
				sequence.setCache(rs.getInt("cache"));

				return sequence;
			}

			return null;

		} finally {
			this.close(rs);
			this.close(stmt);
		}
	}
	
	@Override
	protected ERTable importTable(String tableNameWithSchema, String tableName, String schema) throws SQLException, InterruptedException
	{
		if(tableName.startsWith("sys") || tableName.endsWith("_aud"))
			return null;
		
		return super.importTable(tableNameWithSchema, tableName, schema);
	}

	@Override
	protected void cacheForeignKeyData() throws SQLException {
		this.tableForeignKeyDataMap = null;
	}
}
