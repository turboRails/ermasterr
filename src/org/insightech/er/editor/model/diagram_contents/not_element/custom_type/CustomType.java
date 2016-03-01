package org.insightech.er.editor.model.diagram_contents.not_element.custom_type;

import org.insightech.er.editor.model.ObjectModel;
import org.insightech.er.editor.model.WithSchemaModel;

public class CustomType extends WithSchemaModel implements ObjectModel {

    private static final long serialVersionUID = -4492787972500741281L;

    private String description;

    @Override
    public String getObjectType() {
        return "sequence";
    }

    /**
     * description ���擾���܂�.
     * 
     * @return description
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * description ��ݒ肵�܂�.
     * 
     * @param description
     *            description
     */
    public void setDescription(final String description) {
        this.description = description;
    }

}
