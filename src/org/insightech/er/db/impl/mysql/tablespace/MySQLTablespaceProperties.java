package org.insightech.er.db.impl.mysql.tablespace;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;
import org.insightech.er.util.Check;

public class MySQLTablespaceProperties implements TablespaceProperties {

    private static final long serialVersionUID = 7900101196638704362L;

    private String dataFile;

    private String logFileGroup;

    private String extentSize;

    private String initialSize;

    private String engine;

    /**
     * dataFile を取得します.
     * 
     * @return dataFile
     */
    public String getDataFile() {
        return dataFile;
    }

    /**
     * dataFile を設定します.
     * 
     * @param dataFile
     *            dataFile
     */
    public void setDataFile(final String dataFile) {
        this.dataFile = dataFile;
    }

    /**
     * logFileGroup を取得します.
     * 
     * @return logFileGroup
     */
    public String getLogFileGroup() {
        return logFileGroup;
    }

    /**
     * logFileGroup を設定します.
     * 
     * @param logFileGroup
     *            logFileGroup
     */
    public void setLogFileGroup(final String logFileGroup) {
        this.logFileGroup = logFileGroup;
    }

    /**
     * extentSize を取得します.
     * 
     * @return extentSize
     */
    public String getExtentSize() {
        return extentSize;
    }

    /**
     * extentSize を設定します.
     * 
     * @param extentSize
     *            extentSize
     */
    public void setExtentSize(final String extentSize) {
        this.extentSize = extentSize;
    }

    /**
     * initialSize を取得します.
     * 
     * @return initialSize
     */
    public String getInitialSize() {
        return initialSize;
    }

    /**
     * initialSize を設定します.
     * 
     * @param initialSize
     *            initialSize
     */
    public void setInitialSize(final String initialSize) {
        this.initialSize = initialSize;
    }

    /**
     * engine を取得します.
     * 
     * @return engine
     */
    public String getEngine() {
        return engine;
    }

    /**
     * engine を設定します.
     * 
     * @param engine
     *            engine
     */
    public void setEngine(final String engine) {
        this.engine = engine;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TablespaceProperties clone() {
        final MySQLTablespaceProperties properties = new MySQLTablespaceProperties();

        properties.dataFile = dataFile;
        properties.engine = engine;
        properties.extentSize = extentSize;
        properties.initialSize = initialSize;
        properties.logFileGroup = logFileGroup;

        return properties;
    }

    @Override
    public LinkedHashMap<String, String> getPropertiesMap() {
        final LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

        map.put("label.tablespace.data.file", getDataFile());
        map.put("label.tablespace.log.file.group", getLogFileGroup());
        map.put("label.tablespace.extent.size", getExtentSize());
        map.put("label.tablespace.initial.size", getInitialSize());
        map.put("label.storage.engine", getEngine());

        return map;
    }

    @Override
    public List<String> validate() {
        final List<String> errorMessage = new ArrayList<String>();

        if (Check.isEmptyTrim(getDataFile())) {
            errorMessage.add("error.tablespace.data.file.empty");
        }
        if (Check.isEmptyTrim(getLogFileGroup())) {
            errorMessage.add("error.tablespace.log.file.group.empty");
        }
        if (Check.isEmptyTrim(getEngine())) {
            errorMessage.add("error.tablespace.storage.engine.empty");
        }

        return errorMessage;
    }

}
