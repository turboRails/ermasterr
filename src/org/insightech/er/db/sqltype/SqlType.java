package org.insightech.er.db.sqltype;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.db.impl.db2.DB2DBManager;
import org.insightech.er.db.impl.mysql.MySQLDBManager;
import org.insightech.er.db.impl.oracle.OracleDBManager;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.TypeData;
import org.insightech.er.util.Format;

public class SqlType implements Serializable {

    private static Logger logger = Logger.getLogger(SqlType.class.getName());

    private static final long serialVersionUID = -8273043043893517634L;

    public static final String SQL_TYPE_ID_SERIAL = "serial";

    public static final String SQL_TYPE_ID_BIG_SERIAL = "bigserial";

    public static final String SQL_TYPE_ID_INTEGER = "integer";

    public static final String SQL_TYPE_ID_BIG_INT = "bigint";

    public static final String SQL_TYPE_ID_CHAR = "character";

    public static final String SQL_TYPE_ID_VARCHAR = "varchar";

    private static final Pattern NEED_LENGTH_PATTERN = Pattern.compile(".+\\([a-zA-Z][,\\)].*");

    private static final Pattern NEED_DECIMAL_PATTERN1 = Pattern.compile(".+\\([a-zA-Z],[a-zA-Z]\\)");

    private static final Pattern NEED_DECIMAL_PATTERN2 = Pattern.compile(".+\\([a-zA-Z]\\).*\\([a-zA-Z]\\)");

    private static final List<SqlType> SQL_TYPE_LIST = new ArrayList<SqlType>();

    private final String name;

    private final Class javaClass;

    private final boolean needArgs;

    boolean fullTextIndexable;

    private static Map<String, Map<TypeKey, SqlType>> dbSqlTypeMap = new HashMap<String, Map<TypeKey, SqlType>>();

    private static Map<String, Map<SqlType, String>> dbSqlTypeToAliasMap = new HashMap<String, Map<SqlType, String>>();

    private static Map<String, Map<String, SqlType>> dbAliasToSqlTypeMap = new HashMap<String, Map<String, SqlType>>();

    static {
        try {
            SqlTypeFactory.load();

        } catch (final Exception e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }

    public static class TypeKey {
        private final String alias;

        private int size;

        private final int decimal;

        public TypeKey(String alias, final int size, final int decimal) {
            if (alias != null) {
                alias = alias.toUpperCase();
            }

            this.alias = alias;

            if (size == Integer.MAX_VALUE) {
                this.size = 0;
            } else {
                this.size = size;
            }

            this.decimal = decimal;
        }

        @Override
        public boolean equals(final Object obj) {
            final TypeKey other = (TypeKey) obj;

            if (alias == null) {
                if (other.alias == null) {
                    if (size == other.size && decimal == other.decimal) {
                        return true;
                    }
                    return false;

                } else {
                    return false;
                }

            } else {
                if (alias.equals(other.alias) && size == other.size && decimal == other.decimal) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public int hashCode() {
            if (alias == null) {
                return (size * 10) + decimal;
            }
            return (alias.hashCode() * 100) + (size * 10) + decimal;
        }

        @Override
        public String toString() {
            return "TypeKey [alias=" + alias + ", size=" + size + ", decimal=" + decimal + "]";
        }

    }

    public SqlType(final String name, final Class javaClass, final boolean needArgs, final boolean fullTextIndexable) {
        this.name = name;
        this.javaClass = javaClass;
        this.needArgs = needArgs;
        this.fullTextIndexable = fullTextIndexable;

        SQL_TYPE_LIST.add(this);
    }

    public static void setDBAliasMap(final Map<String, Map<SqlType, String>> dbSqlTypeToAliasMap, final Map<String, Map<String, SqlType>> dbAliasToSqlTypeMap, final Map<String, Map<TypeKey, SqlType>> dbSqlTypeMap) {
        SqlType.dbSqlTypeMap = dbSqlTypeMap;
        SqlType.dbSqlTypeToAliasMap = dbSqlTypeToAliasMap;
        SqlType.dbAliasToSqlTypeMap = dbAliasToSqlTypeMap;
    }

    public void addToSqlTypeMap(final TypeKey typeKey, final String database) {
        final Map<TypeKey, SqlType> sqlTypeMap = dbSqlTypeMap.get(database);
        sqlTypeMap.put(typeKey, this);
    }

    public String getId() {
        return name;
    }

    public Class getJavaClass() {
        return javaClass;
    }

    public boolean doesNeedArgs() {
        return needArgs;
    }

    public boolean isFullTextIndexable() {
        return fullTextIndexable;
    }

    protected static List<SqlType> getAllSqlType() {
        return SQL_TYPE_LIST;
    }

    public static SqlType valueOf(final String database, final String alias) {
        return dbAliasToSqlTypeMap.get(database).get(alias);
    }

    public static SqlType valueOf(final String database, final String alias, int size, int decimal) {
        if (alias == null) {
            return null;
        }

        final Map<TypeKey, SqlType> sqlTypeMap = dbSqlTypeMap.get(database);

        TypeKey typeKey = new TypeKey(alias, size, decimal);
        SqlType sqlType = sqlTypeMap.get(typeKey);

        if (sqlType != null) {
            return sqlType;
        }

        if (decimal > 0) {
            decimal = -1;

            typeKey = new TypeKey(alias, size, decimal);
            sqlType = sqlTypeMap.get(typeKey);

            if (sqlType != null) {
                return sqlType;
            }
        }

        if (size > 0) {
            size = -1;

            typeKey = new TypeKey(alias, size, decimal);
            sqlType = sqlTypeMap.get(typeKey);

            if (sqlType != null) {
                return sqlType;
            }
        }

        typeKey = new TypeKey(alias, 0, 0);
        sqlType = sqlTypeMap.get(typeKey);

        return sqlType;
    }

    public static SqlType valueOfId(final String id) {
        SqlType sqlType = null;

        if (id == null) {
            return null;
        }

        for (final SqlType type : SQL_TYPE_LIST) {
            if (id.equals(type.getId())) {
                sqlType = type;
            }
        }
        return sqlType;
    }

    public boolean isNeedLength(final String database) {
        final String alias = getAlias(database);
        if (alias == null) {
            return false;
        }

        final Matcher matcher = NEED_LENGTH_PATTERN.matcher(alias);

        if (matcher.matches()) {
            return true;
        }

        return false;
    }

    public boolean isNeedDecimal(final String database) {
        final String alias = getAlias(database);
        if (alias == null) {
            return false;
        }

        Matcher matcher = NEED_DECIMAL_PATTERN1.matcher(alias);

        if (matcher.matches()) {
            return true;
        }

        matcher = NEED_DECIMAL_PATTERN2.matcher(alias);

        if (matcher.matches()) {
            return true;
        }

        return false;
    }

    public boolean isNeedCharSemantics(final String database) {
        if (!OracleDBManager.ID.equals(database)) {
            return false;
        }

        if (name.startsWith(SQL_TYPE_ID_CHAR) || name.startsWith(SQL_TYPE_ID_VARCHAR)) {
            return true;
        }

        return false;
    }

    public boolean isTimestamp() {
        if (javaClass == Date.class) {
            return true;
        }

        return false;
    }

    public boolean isNumber() {
        if (Number.class.isAssignableFrom(javaClass)) {
            return true;
        }

        return false;
    }

    public static List<String> getAliasList(final String database) {
        final Map<SqlType, String> aliasMap = dbSqlTypeToAliasMap.get(database);

        final Set<String> aliases = new LinkedHashSet<String>();

        for (final Entry<SqlType, String> entry : aliasMap.entrySet()) {
            final String alias = entry.getValue();
            aliases.add(alias);
        }

        final List<String> list = new ArrayList<String>(aliases);

        Collections.sort(list);

        return list;
    }

    public String getAlias(final String database) {
        final Map<SqlType, String> aliasMap = dbSqlTypeToAliasMap.get(database);

        return aliasMap.get(this);
    }

    public boolean isUnsupported(final String database) {
        final String alias = getAlias(database);

        if (alias == null) {
            return true;
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof SqlType)) {
            return false;
        }

        final SqlType type = (SqlType) obj;

        return name.equals(type.name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getId();
    }

    public static void main2(final String[] args) {
        for (final Entry<TypeKey, SqlType> entry : dbSqlTypeMap.get(MySQLDBManager.ID).entrySet()) {
            logger.info(entry.getKey().toString() + ":" + entry.getValue().getAlias(MySQLDBManager.ID));
        }
    }

    public static void main(final String[] args) {
        String targetDb = null;
        targetDb = DB2DBManager.ID;

        final boolean zerofill = false;
        final int testIntValue = 5;

        final int maxIdLength = 37;

        final StringBuilder msg = new StringBuilder();

        msg.append("\n");

        final List<SqlType> list = getAllSqlType();

        final List<String> dbList = DBManagerFactory.getAllDBList();
        int errorCount = 0;

        String str = "ID";

        if (targetDb == null) {
            msg.append(str);

            for (final String db : dbList) {
                int spaceLength = maxIdLength - str.length();
                if (spaceLength < 4) {
                    spaceLength = 4;
                }

                for (int i = 0; i < spaceLength; i++) {
                    msg.append(" ");
                }

                str = db;
                msg.append(db);
            }

            msg.append("\n");
            msg.append("\n");

            final StringBuilder builder = new StringBuilder();

            for (final SqlType type : list) {
                builder.append(type.name);
                int spaceLength = maxIdLength - type.name.length();
                if (spaceLength < 4) {
                    spaceLength = 4;
                }

                for (final String db : dbList) {
                    for (int i = 0; i < spaceLength; i++) {
                        builder.append(" ");
                    }

                    final String alias = type.getAlias(db);

                    if (alias != null) {
                        builder.append(type.getAlias(db));
                        spaceLength = maxIdLength - type.getAlias(db).length();
                        if (spaceLength < 4) {
                            spaceLength = 4;
                        }

                    } else {
                        if (type.isUnsupported(db)) {
                            builder.append("□□□□□□");
                        } else {
                            builder.append("■■■■■■");
                            errorCount++;
                        }

                        spaceLength = maxIdLength - "□□□□□□".length();
                        if (spaceLength < 4) {
                            spaceLength = 4;
                        }
                    }
                }

                builder.append("\r\n");
            }

            final String allColumn = builder.toString();
            msg.append(allColumn + "\n");
        }

        int errorCount2 = 0;
        int errorCount3 = 0;

        for (final String db : dbList) {
            if (targetDb == null || db.equals(targetDb)) {
                if (targetDb == null) {
                    msg.append("-- for " + db + "\n");
                }
                msg.append("CREATE TABLE TYPE_TEST (\n");

                int count = 0;

                for (final SqlType type : list) {
                    final String alias = type.getAlias(db);
                    if (alias == null) {
                        continue;
                    }

                    if (count != 0) {
                        msg.append(",\n");
                    }
                    msg.append("\tCOL_" + count + " ");

                    if (type.isNeedLength(db) && type.isNeedDecimal(db)) {
                        final TypeData typeData = new TypeData(Integer.valueOf(testIntValue), Integer.valueOf(testIntValue), false, null, false, false, false, null, false);

                        str = Format.formatType(type, typeData, db, true);

                        if (zerofill && db.equals(MySQLDBManager.ID)) {
                            if (type.isNumber()) {
                                str = str + " unsigned zerofill";
                            }
                        }

                        if (str.equals(alias)) {
                            errorCount3++;
                            msg.append("×3");
                        }

                    } else if (type.isNeedLength(db)) {
                        final TypeData typeData = new TypeData(Integer.valueOf(testIntValue), null, false, null, false, false, false, null, false);

                        str = Format.formatType(type, typeData, db, true);

                        if (zerofill && db.equals(MySQLDBManager.ID)) {
                            if (type.isNumber()) {
                                str = str + " unsigned zerofill";
                            }
                        }

                        if (str.equals(alias)) {
                            errorCount3++;
                            msg.append("×3");
                        }

                    } else if (type.isNeedDecimal(db)) {
                        final TypeData typeData = new TypeData(null, Integer.valueOf(testIntValue), false, null, false, false, false, null, false);

                        str = Format.formatType(type, typeData, db, true);

                        if (zerofill && db.equals(MySQLDBManager.ID)) {
                            if (type.isNumber()) {
                                str = str + " unsigned zerofill";
                            }
                        }

                        if (str.equals(alias)) {
                            errorCount3++;
                            msg.append("×3");
                        }

                    } else if (type.doesNeedArgs()) {
                        str = alias + "('1')";

                    } else {
                        str = alias;

                        if (zerofill && db.equals(MySQLDBManager.ID)) {
                            if (type.isNumber()) {
                                str = str + " unsigned zerofill";
                            }
                        }

                        if (str.equals("uniqueidentifier rowguidcol")) {
                            str += " not null unique";
                        }
                    }

                    if (str != null) {

                        final Matcher m1 = NEED_LENGTH_PATTERN.matcher(str);
                        final Matcher m2 = NEED_DECIMAL_PATTERN1.matcher(str);
                        final Matcher m3 = NEED_DECIMAL_PATTERN2.matcher(str);

                        if (m1.matches() || m2.matches() || m3.matches()) {
                            errorCount2++;
                            msg.append("×2");
                        }
                    }

                    msg.append(str);

                    count++;
                }
                msg.append("\n");
                msg.append(");\n");
                msg.append("\n");
            }
        }

        msg.append("\n");

        if (targetDb == null) {
            msg.append(errorCount + " 個の型が変換できませんでした。\n");
            msg.append(errorCount2 + " 個の数字型の指定が不足しています。\n");
            msg.append(errorCount3 + " 個の数字型の指定が余分です。\n");
        }

        System.out.println(msg.toString());
    }
}
