package org.insightech.er.db.impl.standard_sql;

import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.db.sqltype.SqlTypeManagerBase;

public class StandardSQLSqlTypeManager extends SqlTypeManagerBase {

    @Override
    public int getByteLength(final SqlType type, final Integer length, final Integer decimal) {
        return 0;
    }

}
