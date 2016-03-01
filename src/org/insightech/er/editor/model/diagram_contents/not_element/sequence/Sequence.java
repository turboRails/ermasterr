package org.insightech.er.editor.model.diagram_contents.not_element.sequence;

import java.math.BigDecimal;

import org.insightech.er.editor.model.ObjectModel;
import org.insightech.er.editor.model.WithSchemaModel;

public class Sequence extends WithSchemaModel implements ObjectModel {

    private static final long serialVersionUID = -4492787972500741281L;

    private String description;

    private Integer increment;

    private Long minValue;

    private BigDecimal maxValue;

    private Long start;

    private Integer cache;

    private boolean nocache;

    private boolean cycle;

    private boolean order;

    private String dataType;

    private int decimalSize;

    @Override
    public String getObjectType() {
        return "sequence";
    }

    public Integer getCache() {
        return cache;
    }

    public void setCache(final Integer cache) {
        this.cache = cache;
    }

    public boolean isCycle() {
        return cycle;
    }

    public void setCycle(final boolean cycle) {
        this.cycle = cycle;
    }

    public Integer getIncrement() {
        return increment;
    }

    public void setIncrement(final Integer increment) {
        this.increment = increment;
    }

    public BigDecimal getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(final BigDecimal maxValue) {
        this.maxValue = maxValue;
    }

    public Long getMinValue() {
        return minValue;
    }

    public void setMinValue(final Long minValue) {
        this.minValue = minValue;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(final Long start) {
        this.start = start;
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

    public String getDataType() {
        return dataType;
    }

    public void setDataType(final String dataType) {
        this.dataType = dataType;
    }

    public int getDecimalSize() {
        return decimalSize;
    }

    public void setDecimalSize(final int decimalSize) {
        this.decimalSize = decimalSize;
    }

    public boolean isOrder() {
        return order;
    }

    public void setOrder(final boolean order) {
        this.order = order;
    }

    public boolean isNocache() {
        return nocache;
    }

    public void setNocache(final boolean nocache) {
        this.nocache = nocache;
    }

}
