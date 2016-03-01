package org.insightech.er.editor.model.testdata;

public class TableTestData implements Cloneable {

    private DirectTestData directTestData;

    private RepeatTestData repeatTestData;

    public TableTestData() {
        directTestData = new DirectTestData();
        repeatTestData = new RepeatTestData();
    }

    public DirectTestData getDirectTestData() {
        return directTestData;
    }

    public void setDirectTestData(final DirectTestData directTestData) {
        this.directTestData = directTestData;
    }

    public RepeatTestData getRepeatTestData() {
        return repeatTestData;
    }

    public void setRepeatTestData(final RepeatTestData repeatTestData) {
        this.repeatTestData = repeatTestData;
    }

    public int getTestDataNum() {
        return directTestData.getTestDataNum() + repeatTestData.getTestDataNum();
    }

    @Override
    public TableTestData clone() {
        final TableTestData clone = new TableTestData();

        clone.directTestData = directTestData.clone();
        clone.repeatTestData = repeatTestData.clone();

        return clone;
    }
}
