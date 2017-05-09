package org.insightech.er.db.impl.informix;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;

public class InformixDDLCreator extends DDLCreator {

	public InformixDDLCreator(ERDiagram diagram, Category targetCategory,
			boolean semicolon) {
		super(diagram, targetCategory, semicolon);
	}

	@Override
	protected String getDDL(Tablespace tablespace) {
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDDL(Relation relation) {
		StringBuilder ddl = new StringBuilder();

		ddl.append("ALTER TABLE ");
		ddl.append(filterName(relation.getTargetTableView().getNameWithSchema(
				this.getDiagram().getDatabase())));
		ddl.append(LF());
		ddl.append("\tADD CONSTRAINT ");
		ddl.append("FOREIGN KEY (");

		boolean first = true;

		for (NormalColumn column : relation.getForeignKeyColumns()) {
			if (!first) {
				ddl.append(", ");

			}
			ddl.append(filterName(column.getPhysicalName()));
			first = false;
		}

		ddl.append(")" + LF());
		ddl.append("\tREFERENCES ");
		ddl.append(filterName(relation.getSourceTableView().getNameWithSchema(
				this.getDiagram().getDatabase())));
		ddl.append(" (");

		first = true;

		for (NormalColumn foreignKeyColumn : relation.getForeignKeyColumns()) {
			if (!first) {
				ddl.append(", ");

			}

			for (NormalColumn referencedColumn : foreignKeyColumn
					.getReferencedColumnList()) {
				if (referencedColumn.getColumnHolder() == relation
						.getSourceTableView()) {
					ddl.append(filterName(referencedColumn.getPhysicalName()));
					first = false;
					break;
				}
			}

		}

		ddl.append(")" + LF());
		
		if (relation.getName() != null && !relation.getName().trim().equals("")) {
			ddl.append("\tCONSTRAINT ");
			ddl.append(filterName(relation.getName()));
			ddl.append(LF());
		}
		
		if (!"RESTRICT".equalsIgnoreCase(relation.getOnDeleteAction())) {
			ddl.append("\tON DELETE ");
			ddl.append(filterName(relation.getOnDeleteAction()));
			ddl.append(LF());
		}

		if (this.semicolon) {
			ddl.append(";");
		}

		return ddl.toString();
	}
}
