package org.insightech.er.editor.model.dbimport;

import org.insightech.er.editor.model.AbstractModel;

public class DBObject {

    public static final String TYPE_TABLE = "table";

    public static final String TYPE_SEQUENCE = "sequence";

    public static final String TYPE_VIEW = "view";

    public static final String TYPE_TRIGGER = "trigger";

    public static final String TYPE_TABLESPACE = "tablespace";

    public static final String TYPE_NOTE = "note";

    public static final String TYPE_GROUP = "group";

    public static final String[] ALL_TYPES = {TYPE_TABLE, TYPE_VIEW, TYPE_SEQUENCE, TYPE_TRIGGER};

    private String schema;
    private String name;
    private String type;

    private AbstractModel model;

    public DBObject(final String schema, final String name, final String type) {
        this.schema = schema;
        this.name = name;
        this.type = type;
    }

    public void setModel(final AbstractModel model) {
        this.model = model;
    }

    public AbstractModel getModel() {
        return model;
    }

    /**
     * schema ���擾���܂�.
     * 
     * @return schema
     */
    public String getSchema() {
        return schema;
    }

    /**
     * schema ��ݒ肵�܂�.
     * 
     * @param schema
     *            schema
     */
    public void setSchema(final String schema) {
        this.schema = schema;
    }

    /**
     * name ���擾���܂�.
     * 
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * name ��ݒ肵�܂�.
     * 
     * @param name
     *            name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * type ���擾���܂�.
     * 
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * type ��ݒ肵�܂�.
     * 
     * @param type
     *            type
     */
    public void setType(final String type) {
        this.type = type;
    }
}
