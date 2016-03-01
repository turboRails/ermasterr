package org.insightech.er.db.impl.db2.tablespace;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;

public class DB2TablespaceProperties implements TablespaceProperties {

    private static final long serialVersionUID = 3581869274788998047L;

    // (REGULAR/LARGI/SYSTEM TEMPORARY/USER TEMPORARY)
    private String type;

    private String pageSize;

    private String managedBy;

    private String container;

    // private String containerDirectoryPath;
    //
    // private String containerFilePath;
    //
    // private String containerPageNum;
    //
    // private String containerDevicePath;

    private String extentSize;

    private String prefetchSize;

    private String bufferPoolName;

    /**
     * type を取得します.
     * 
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * type を設定します.
     * 
     * @param type
     *            type
     */
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * pageSize を取得します.
     * 
     * @return pageSize
     */
    public String getPageSize() {
        return pageSize;
    }

    /**
     * pageSize を設定します.
     * 
     * @param pageSize
     *            pageSize
     */
    public void setPageSize(final String pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * managedBy を取得します.
     * 
     * @return managedBy
     */
    public String getManagedBy() {
        return managedBy;
    }

    /**
     * managedBy を設定します.
     * 
     * @param managedBy
     *            managedBy
     */
    public void setManagedBy(final String managedBy) {
        this.managedBy = managedBy;
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
     * prefetchSize を取得します.
     * 
     * @return prefetchSize
     */
    public String getPrefetchSize() {
        return prefetchSize;
    }

    /**
     * prefetchSize を設定します.
     * 
     * @param prefetchSize
     *            prefetchSize
     */
    public void setPrefetchSize(final String prefetchSize) {
        this.prefetchSize = prefetchSize;
    }

    /**
     * bufferPoolName を取得します.
     * 
     * @return bufferPoolName
     */
    public String getBufferPoolName() {
        return bufferPoolName;
    }

    /**
     * bufferPoolName を設定します.
     * 
     * @param bufferPoolName
     *            bufferPoolName
     */
    public void setBufferPoolName(final String bufferPoolName) {
        this.bufferPoolName = bufferPoolName;
    }

    /**
     * container を取得します.
     * 
     * @return container
     */
    public String getContainer() {
        return container;
    }

    /**
     * container を設定します.
     * 
     * @param container
     *            container
     */
    public void setContainer(final String container) {
        this.container = container;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TablespaceProperties clone() {
        final DB2TablespaceProperties properties = new DB2TablespaceProperties();

        properties.bufferPoolName = bufferPoolName;
        properties.container = container;
        // properties.containerDevicePath = this.containerDevicePath;
        // properties.containerDirectoryPath = this.containerDirectoryPath;
        // properties.containerFilePath = this.containerFilePath;
        // properties.containerPageNum = this.containerPageNum;
        properties.extentSize = extentSize;
        properties.managedBy = managedBy;
        properties.pageSize = pageSize;
        properties.prefetchSize = prefetchSize;
        properties.type = type;

        return properties;
    }

    @Override
    public LinkedHashMap<String, String> getPropertiesMap() {
        final LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

        map.put("label.tablespace.type", getType());
        map.put("label.tablespace.page.size", getPageSize());
        map.put("label.tablespace.managed.by", getManagedBy());
        map.put("label.tablespace.container", getContainer());
        map.put("label.tablespace.extent.size", getExtentSize());
        map.put("label.tablespace.prefetch.size", getPrefetchSize());
        map.put("label.tablespace.buffer.pool.name", getBufferPoolName());

        return map;
    }

    @Override
    public List<String> validate() {
        final List<String> errorMessage = new ArrayList<String>();
        return errorMessage;
    }
}
