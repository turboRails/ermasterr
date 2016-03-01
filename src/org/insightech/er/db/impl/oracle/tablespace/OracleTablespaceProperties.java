package org.insightech.er.db.impl.oracle.tablespace;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;
import org.insightech.er.util.Check;

public class OracleTablespaceProperties implements TablespaceProperties {

    private static final long serialVersionUID = -6976279893674797115L;

    private String dataFile;

    private String fileSize;

    private boolean autoExtend;

    private String autoExtendSize;

    private String autoExtendMaxSize;

    private String minimumExtentSize;

    private String initial;

    private String next;

    private String minExtents;

    private String maxExtents;

    private String pctIncrease;

    private boolean logging;

    private boolean offline;

    private boolean temporary;

    private boolean autoSegmentSpaceManagement;

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
     * fileSize を取得します.
     * 
     * @return fileSize
     */
    public String getFileSize() {
        return fileSize;
    }

    /**
     * fileSize を設定します.
     * 
     * @param fileSize
     *            fileSize
     */
    public void setFileSize(final String fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * autoExtend を取得します.
     * 
     * @return autoExtend
     */
    public boolean isAutoExtend() {
        return autoExtend;
    }

    /**
     * autoExtend を設定します.
     * 
     * @param autoExtend
     *            autoExtend
     */
    public void setAutoExtend(final boolean autoExtend) {
        this.autoExtend = autoExtend;
    }

    /**
     * autoExtendSize を取得します.
     * 
     * @return autoExtendSize
     */
    public String getAutoExtendSize() {
        return autoExtendSize;
    }

    /**
     * autoExtendSize を設定します.
     * 
     * @param autoExtendSize
     *            autoExtendSize
     */
    public void setAutoExtendSize(final String autoExtendSize) {
        this.autoExtendSize = autoExtendSize;
    }

    /**
     * autoExtendMaxSize を取得します.
     * 
     * @return autoExtendMaxSize
     */
    public String getAutoExtendMaxSize() {
        return autoExtendMaxSize;
    }

    /**
     * autoExtendMaxSize を設定します.
     * 
     * @param autoExtendMaxSize
     *            autoExtendMaxSize
     */
    public void setAutoExtendMaxSize(final String autoExtendMaxSize) {
        this.autoExtendMaxSize = autoExtendMaxSize;
    }

    /**
     * minimumExtentSize を取得します.
     * 
     * @return minimumExtentSize
     */
    public String getMinimumExtentSize() {
        return minimumExtentSize;
    }

    /**
     * minimumExtentSize を設定します.
     * 
     * @param minimumExtentSize
     *            minimumExtentSize
     */
    public void setMinimumExtentSize(final String minimumExtentSize) {
        this.minimumExtentSize = minimumExtentSize;
    }

    /**
     * logging を取得します.
     * 
     * @return logging
     */
    public boolean isLogging() {
        return logging;
    }

    /**
     * logging を設定します.
     * 
     * @param logging
     *            logging
     */
    public void setLogging(final boolean logging) {
        this.logging = logging;
    }

    /**
     * offline を取得します.
     * 
     * @return offline
     */
    public boolean isOffline() {
        return offline;
    }

    /**
     * offline を設定します.
     * 
     * @param offline
     *            offline
     */
    public void setOffline(final boolean offline) {
        this.offline = offline;
    }

    /**
     * temporary を取得します.
     * 
     * @return temporary
     */
    public boolean isTemporary() {
        return temporary;
    }

    /**
     * temporary を設定します.
     * 
     * @param temporary
     *            temporary
     */
    public void setTemporary(final boolean temporary) {
        this.temporary = temporary;
    }

    /**
     * autoSegmentSpaceManagement を取得します.
     * 
     * @return autoSegmentSpaceManagement
     */
    public boolean isAutoSegmentSpaceManagement() {
        return autoSegmentSpaceManagement;
    }

    /**
     * autoSegmentSpaceManagement を設定します.
     * 
     * @param autoSegmentSpaceManagement
     *            autoSegmentSpaceManagement
     */
    public void setAutoSegmentSpaceManagement(final boolean autoSegmentSpaceManagement) {
        this.autoSegmentSpaceManagement = autoSegmentSpaceManagement;
    }

    /**
     * initial を取得します.
     * 
     * @return initial
     */
    public String getInitial() {
        return initial;
    }

    /**
     * initial を設定します.
     * 
     * @param initial
     *            initial
     */
    public void setInitial(final String initial) {
        this.initial = initial;
    }

    /**
     * next を取得します.
     * 
     * @return next
     */
    public String getNext() {
        return next;
    }

    /**
     * next を設定します.
     * 
     * @param next
     *            next
     */
    public void setNext(final String next) {
        this.next = next;
    }

    /**
     * minExtents を取得します.
     * 
     * @return minExtents
     */
    public String getMinExtents() {
        return minExtents;
    }

    /**
     * minExtents を設定します.
     * 
     * @param minExtents
     *            minExtents
     */
    public void setMinExtents(final String minExtents) {
        this.minExtents = minExtents;
    }

    /**
     * maxExtents を取得します.
     * 
     * @return maxExtents
     */
    public String getMaxExtents() {
        return maxExtents;
    }

    /**
     * maxExtents を設定します.
     * 
     * @param maxExtents
     *            maxExtents
     */
    public void setMaxExtents(final String maxExtents) {
        this.maxExtents = maxExtents;
    }

    /**
     * pctIncrease を取得します.
     * 
     * @return pctIncrease
     */
    public String getPctIncrease() {
        return pctIncrease;
    }

    /**
     * pctIncrease を設定します.
     * 
     * @param pctIncrease
     *            pctIncrease
     */
    public void setPctIncrease(final String pctIncrease) {
        this.pctIncrease = pctIncrease;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TablespaceProperties clone() {
        final OracleTablespaceProperties properties = new OracleTablespaceProperties();

        properties.autoExtend = autoExtend;
        properties.autoExtendMaxSize = autoExtendMaxSize;
        properties.autoExtendSize = autoExtendSize;
        properties.autoSegmentSpaceManagement = autoSegmentSpaceManagement;
        properties.dataFile = dataFile;
        properties.fileSize = fileSize;
        properties.initial = initial;
        properties.logging = logging;
        properties.maxExtents = maxExtents;
        properties.minExtents = minExtents;
        properties.minimumExtentSize = minimumExtentSize;
        properties.next = next;
        properties.offline = offline;
        properties.pctIncrease = pctIncrease;
        properties.temporary = temporary;

        return properties;
    }

    @Override
    public LinkedHashMap<String, String> getPropertiesMap() {
        final LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

        map.put("label.tablespace.data.file", getDataFile());
        map.put("label.size", getFileSize());
        map.put("label.tablespace.auto.extend", String.valueOf(isAutoExtend()));
        map.put("label.size", getAutoExtendSize());
        map.put("label.max.size", getAutoExtendMaxSize());
        map.put("label.tablespace.minimum.extent.size", getMinimumExtentSize());
        map.put("label.tablespace.initial", getInitial());
        map.put("label.tablespace.next", getNext());
        map.put("label.tablespace.min.extents", getMinExtents());
        map.put("label.tablespace.pct.increase", getPctIncrease());
        map.put("label.tablespace.logging", String.valueOf(isLogging()));
        map.put("label.tablespace.offline", String.valueOf(isOffline()));
        map.put("label.tablespace.temporary", String.valueOf(isTemporary()));
        map.put("label.tablespace.auto.segment.space.management", String.valueOf(isAutoSegmentSpaceManagement()));

        return map;
    }

    @Override
    public List<String> validate() {
        final List<String> errorMessage = new ArrayList<String>();

        if (isAutoExtend() && Check.isEmptyTrim(getAutoExtendSize())) {
            errorMessage.add("error.tablespace.auto.extend.size.empty");
        }

        return errorMessage;
    }
}
