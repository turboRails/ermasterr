package org.insightech.er.db.impl.informix;

import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.db.sqltype.SqlTypeManagerBase;

public class InformixSqlTypeManager extends SqlTypeManagerBase {

	@Override
	public int getByteLength(SqlType type, Integer length, Integer decimal) {
		if (type == null) {
			return 0;
		}
		
		return 0;
	}
}
