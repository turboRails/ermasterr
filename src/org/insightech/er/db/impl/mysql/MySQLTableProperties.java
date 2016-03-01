package org.insightech.er.db.impl.mysql;

import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;

public class MySQLTableProperties extends TableProperties {

    private static final long serialVersionUID = 3126556935094407067L;

    private String storageEngine;

    private String characterSet;

    private String collation;

    private Integer primaryKeyLengthOfText;

    public String getStorageEngine() {
        return storageEngine;
    }

    public void setStorageEngine(final String storageEngine) {
        this.storageEngine = storageEngine;
    }

    public String getCharacterSet() {
        return characterSet;
    }

    public void setCharacterSet(final String characterSet) {
        this.characterSet = characterSet;
    }

    public String getCollation() {
        return collation;
    }

    public void setCollation(final String collation) {
        this.collation = collation;
    }

    public Integer getPrimaryKeyLengthOfText() {
        return primaryKeyLengthOfText;
    }

    public void setPrimaryKeyLengthOfText(final Integer primaryKeyLengthOfText) {
        this.primaryKeyLengthOfText = primaryKeyLengthOfText;
    }

}
