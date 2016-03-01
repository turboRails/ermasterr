package org.insightech.er.preference;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.db.DBManager;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.db.impl.standard_sql.StandardSQLDBManager;
import org.insightech.er.editor.model.settings.DBSetting;
import org.insightech.er.editor.model.settings.JDBCDriverSetting;
import org.insightech.er.preference.editor.FileListEditor;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

    public static final String TEMPLATE_FILE_LIST = "template_file_list";

    public static final String TRANSLATION_FILE_LIST = "translation_file_list";

    private static final String TEMPLATE_DIR = "template";

    private static final String TRANSLATION_DIR = "translation";

    private static final String JDBC_DRIVER_DB_NAME_PREFIX = "jdbc.driver.db.name.";

    private static final String JDBC_DRIVER_PATH_PREFIX = "jdbc.driver.path.";

    private static final String JDBC_DRIVER_CLASS_NAME_PREFIX = "jdbc.driver.class.name.";

    private static final String JDBC_DRIVER_CLASS_NAME_LIST_NUM = "jdbc.driver.class.name.list.num";

    private static final String DB_SETTING_LIST_NUM = "db.setting.list.num";

    private static final String DB_SETTING_DBSYSTEM = "db.setting.dbsystem.";

    private static final String DB_SETTING_SERVER = "db.setting.server.";

    private static final String DB_SETTING_PORT = "db.setting.port.";

    private static final String DB_SETTING_DATABASE = "db.setting.database.";

    private static final String DB_SETTING_USER = "db.setting.user.";

    private static final String DB_SETTING_USE_DEFAULT_DRIVER = "db.setting.use.default.driver.";

    private static final String DB_SETTING_URL = "db.setting.url.";

    private static final String DB_SETTING_DRIVER_CLASS_NAME = "db.setting.driver.class.name.";

    private static final String DB_SETTING_PASSWORD = "setting.password.";

    private static final String EXTENDED_CLASSPATH = "extended.classpath";

    public PreferenceInitializer() {}

    @Override
    public void initializeDefaultPreferences() {}

    public static String getTemplatePath(final String fileName) {
        final IPath dataLocation = ERDiagramActivator.getDefault().getStateLocation();
        final String path = dataLocation.append(PreferenceInitializer.TEMPLATE_DIR).append(fileName).toOSString();

        return path;
    }

    public static String getExtendedClasspath() {
        final IPreferenceStore store = ERDiagramActivator.getDefault().getPreferenceStore();

        final String path = store.getString(PreferenceInitializer.EXTENDED_CLASSPATH);

        return path;
    }

    public static void saveExtendedClasspath(final String path) {
        final IPreferenceStore store = ERDiagramActivator.getDefault().getPreferenceStore();

        store.setValue(PreferenceInitializer.EXTENDED_CLASSPATH, Format.null2blank(path));
    }

    public static String getTranslationPath(final String fileName) {
        final IPath dataLocation = ERDiagramActivator.getDefault().getStateLocation();
        final String path = dataLocation.append(PreferenceInitializer.TRANSLATION_DIR).append(fileName).toOSString();

        return path;
    }

    public static void saveJDBCDriverSettingList(final List<JDBCDriverSetting> driverSettingList) {
        clearJDBCDriverInfo();

        for (final JDBCDriverSetting driverSetting : driverSettingList) {
            addJDBCDriver(driverSetting.getDb(), driverSetting.getClassName(), driverSetting.getPath());
        }
    }

    public static void addJDBCDriver(final String db, final String className, final String path) {
        final IPreferenceStore store = ERDiagramActivator.getDefault().getPreferenceStore();

        final int listSize = store.getInt(PreferenceInitializer.JDBC_DRIVER_CLASS_NAME_LIST_NUM);

        store.setValue(PreferenceInitializer.JDBC_DRIVER_DB_NAME_PREFIX + listSize, Format.null2blank(db));
        store.setValue(PreferenceInitializer.JDBC_DRIVER_CLASS_NAME_PREFIX + listSize, Format.null2blank(className));
        store.setValue(PreferenceInitializer.JDBC_DRIVER_PATH_PREFIX + listSize, Format.null2blank(path));

        store.setValue(PreferenceInitializer.JDBC_DRIVER_CLASS_NAME_LIST_NUM, listSize + 1);
    }

    public static List<JDBCDriverSetting> getJDBCDriverSettingList() {
        final List<JDBCDriverSetting> list = new ArrayList<JDBCDriverSetting>();

        final List<JDBCDriverSetting> defaultDriverList = new ArrayList<JDBCDriverSetting>();

        for (final String db : DBManagerFactory.getAllDBList()) {
            if (!StandardSQLDBManager.ID.equals(db)) {
                final DBManager dbManager = DBManagerFactory.getDBManager(db);

                final JDBCDriverSetting driverSetting = new JDBCDriverSetting(db, dbManager.getDriverClassName(), null);
                defaultDriverList.add(driverSetting);
            }
        }

        final IPreferenceStore store = ERDiagramActivator.getDefault().getPreferenceStore();

        final int listSize = store.getInt(PreferenceInitializer.JDBC_DRIVER_CLASS_NAME_LIST_NUM);

        for (int i = 0; i < listSize; i++) {
            final String db = store.getString(PreferenceInitializer.JDBC_DRIVER_DB_NAME_PREFIX + i);
            final String className = store.getString(PreferenceInitializer.JDBC_DRIVER_CLASS_NAME_PREFIX + i);
            final String path = store.getString(PreferenceInitializer.JDBC_DRIVER_PATH_PREFIX + i);

            final JDBCDriverSetting setting = new JDBCDriverSetting(db, className, path);

            list.add(setting);

            defaultDriverList.remove(setting);
        }

        for (final JDBCDriverSetting defaultDriverSetting : defaultDriverList) {
            list.add(defaultDriverSetting);
        }

        Collections.sort(list);

        return list;
    }

    public static void clearJDBCDriverInfo() {
        final IPreferenceStore store = ERDiagramActivator.getDefault().getPreferenceStore();

        final int num = store.getInt(PreferenceInitializer.JDBC_DRIVER_CLASS_NAME_LIST_NUM);

        for (int i = 0; i < num; i++) {
            store.setValue(PreferenceInitializer.JDBC_DRIVER_CLASS_NAME_PREFIX + i, "");
            store.setValue(PreferenceInitializer.JDBC_DRIVER_PATH_PREFIX + i, "");
        }

        store.setValue(PreferenceInitializer.JDBC_DRIVER_CLASS_NAME_LIST_NUM, 0);
    }

    public static String getJDBCDriverPath(final String db, final String driverClassName) {
        String path = null;

        final IPreferenceStore store = ERDiagramActivator.getDefault().getPreferenceStore();

        final int listSize = store.getInt(PreferenceInitializer.JDBC_DRIVER_CLASS_NAME_LIST_NUM);

        for (int i = 0; i < listSize; i++) {
            if (db.equals(store.getString(PreferenceInitializer.JDBC_DRIVER_DB_NAME_PREFIX + i)) && driverClassName.equals(store.getString(PreferenceInitializer.JDBC_DRIVER_CLASS_NAME_PREFIX + i))) {
                path = store.getString(PreferenceInitializer.JDBC_DRIVER_PATH_PREFIX + i);
                break;
            }
        }

        return path;
    }

    public static DBSetting getDBSetting(final int no) {
        final IPreferenceStore store = ERDiagramActivator.getDefault().getPreferenceStore();

        final String dbsystem = store.getString(PreferenceInitializer.DB_SETTING_DBSYSTEM + no);
        final String server = store.getString(PreferenceInitializer.DB_SETTING_SERVER + no);
        final int portNo = store.getInt(PreferenceInitializer.DB_SETTING_PORT + no);

        final String database = store.getString(PreferenceInitializer.DB_SETTING_DATABASE + no);
        final String user = store.getString(PreferenceInitializer.DB_SETTING_USER + no);
        final String password = store.getString(PreferenceInitializer.DB_SETTING_PASSWORD + no);
        final String useDefaultDriverString = store.getString(PreferenceInitializer.DB_SETTING_USE_DEFAULT_DRIVER + no);
        final String url = store.getString(PreferenceInitializer.DB_SETTING_URL + no);
        final String driverClassName = store.getString(PreferenceInitializer.DB_SETTING_DRIVER_CLASS_NAME + no);

        boolean useDefaultDriver = true;

        if ("false".equals(useDefaultDriverString) || StandardSQLDBManager.ID.equals(dbsystem)) {
            useDefaultDriver = false;
        }

        return new DBSetting(dbsystem, server, portNo, database, user, password, useDefaultDriver, url, driverClassName);
    }

    public static void saveSetting(final int no, final DBSetting dbSetting) {
        final IPreferenceStore store = ERDiagramActivator.getDefault().getPreferenceStore();

        store.setValue(PreferenceInitializer.DB_SETTING_DBSYSTEM + no, Format.null2blank(dbSetting.getDbsystem()));
        store.setValue(PreferenceInitializer.DB_SETTING_SERVER + no, Format.null2blank(dbSetting.getServer()));
        store.setValue(PreferenceInitializer.DB_SETTING_PORT + no, dbSetting.getPort());
        store.setValue(PreferenceInitializer.DB_SETTING_DATABASE + no, Format.null2blank(dbSetting.getDatabase()));
        store.setValue(PreferenceInitializer.DB_SETTING_USER + no, Format.null2blank(dbSetting.getUser()));
        store.setValue(PreferenceInitializer.DB_SETTING_PASSWORD + no, Format.null2blank(dbSetting.getPassword()));
        store.setValue(PreferenceInitializer.DB_SETTING_USE_DEFAULT_DRIVER + no, String.valueOf(dbSetting.isUseDefaultDriver()));
        store.setValue(PreferenceInitializer.DB_SETTING_URL + no, Format.null2blank(dbSetting.getUrl()));
        store.setValue(PreferenceInitializer.DB_SETTING_DRIVER_CLASS_NAME + no, Format.null2blank(dbSetting.getDriverClassName()));
    }

    public static void saveSetting(final List<DBSetting> dbSettingList) {
        final IPreferenceStore store = ERDiagramActivator.getDefault().getPreferenceStore();

        store.setValue(PreferenceInitializer.DB_SETTING_LIST_NUM, dbSettingList.size());

        for (int i = 0; i < dbSettingList.size(); i++) {
            final DBSetting dbSetting = dbSettingList.get(i);
            PreferenceInitializer.saveSetting(i + 1, dbSetting);
        }
    }

    public static List<DBSetting> getDBSettingList(final String database) {
        final List<DBSetting> dbSettingList = new ArrayList<DBSetting>();

        final IPreferenceStore store = ERDiagramActivator.getDefault().getPreferenceStore();

        final int num = store.getInt(PreferenceInitializer.DB_SETTING_LIST_NUM);

        for (int i = 1; i <= num; i++) {
            final DBSetting dbSetting = PreferenceInitializer.getDBSetting(i);
            if (database != null && !dbSetting.getDbsystem().equals(database)) {
                continue;
            }
            dbSettingList.add(dbSetting);
        }

        Collections.sort(dbSettingList);

        return dbSettingList;
    }

    public static void addDBSetting(final DBSetting dbSetting) {
        final IPreferenceStore store = ERDiagramActivator.getDefault().getPreferenceStore();

        int num = store.getInt(PreferenceInitializer.DB_SETTING_LIST_NUM);
        num++;
        store.setValue(PreferenceInitializer.DB_SETTING_LIST_NUM, num);

        saveSetting(num, dbSetting);
    }

    public static List<String> getAllUserTranslations() {
        final String str = ERDiagramActivator.getDefault().getPreferenceStore().getString(PreferenceInitializer.TRANSLATION_FILE_LIST);

        final StringTokenizer st = new StringTokenizer(str, "/");
        final List<String> list = new ArrayList<String>();
        final Set<String> names = new HashSet<String>();

        while (st.hasMoreElements()) {
            final String fileName = st.nextToken();

            final File file = new File(PreferenceInitializer.getTranslationPath(fileName));
            if (file.exists() && !names.contains(fileName)) {
                list.add(fileName);
                names.add(fileName);
            }
        }

        return list;
    }

    public static List<String> getAllExcelTemplateFiles() {
        final String str = ERDiagramActivator.getDefault().getPreferenceStore().getString(PreferenceInitializer.TEMPLATE_FILE_LIST);

        return parseStringToList(str);
    }

    private static List<String> parseStringToList(final String stringList) {
        final StringTokenizer st = new StringTokenizer(stringList, "/");
        final List<String> list = new ArrayList<String>();

        while (st.hasMoreElements()) {
            list.add(st.nextToken());
        }

        return list;
    }

    public static void addPreferenceValue(final String value) {
        final IPreferenceStore preferenceStore = ERDiagramActivator.getDefault().getPreferenceStore();

        String values = preferenceStore.getString(PreferenceInitializer.TRANSLATION_FILE_LIST);

        if (Check.isEmpty(values)) {
            values = value;
        } else {
            values = values + FileListEditor.VALUE_SEPARATOR + value;
        }

        preferenceStore.setValue(PreferenceInitializer.TRANSLATION_FILE_LIST, values);
    }

}
