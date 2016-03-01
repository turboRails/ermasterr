package org.insightech.er.db.impl.mysql;

import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.db.sqltype.SqlTypeManagerBase;

public class MySQLSqlTypeManager extends SqlTypeManagerBase {

    @Override
    public int getByteLength(final SqlType type, final Integer length, final Integer decimal) {
        return 0;
    }

}
