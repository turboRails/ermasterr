package org.insightech.er.editor.model.diagram_contents.not_element.dictionary;

import java.util.Comparator;

import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ObjectModel;
import org.insightech.er.util.Format;

public class Word extends AbstractModel implements ObjectModel, Comparable<Word> {

    private static final long serialVersionUID = 4315217440968295922L;

    private static final Comparator<Word> WITHOUT_NAME_COMPARATOR = new WordWithoutNameComparator();

    public static final Comparator<Word> PHYSICAL_NAME_COMPARATOR = new WordPhysicalNameComparator();

    public static final Comparator<Word> LOGICAL_NAME_COMPARATOR = new WordLogicalNameComparator();

    private String physicalName;

    private String logicalName;

    private SqlType type;

    private TypeData typeData;

    private String description;

    public Word(final String physicalName, final String logicalName, final SqlType type, final TypeData typeData, final String description, final String database) {
        this.physicalName = physicalName;
        this.logicalName = logicalName;
        setType(type, typeData, database);
        this.description = description;
    }

    public Word(final Word word) {
        physicalName = word.physicalName;
        logicalName = word.logicalName;
        type = word.type;
        typeData = word.typeData.clone();
        description = word.description;
    }

    public String getLogicalName() {
        return logicalName;
    }

    public String getPhysicalName() {
        return physicalName;
    }

    public SqlType getType() {
        return type;
    }

    public void setLogicalName(final String logicalName) {
        this.logicalName = logicalName;
    }

    public void setPhysicalName(final String physicalName) {
        this.physicalName = physicalName;
    }

    public void setType(final SqlType type, final TypeData typeData, final String database) {
        this.type = type;
        this.typeData = typeData.clone();

        if (type != null && type.isNeedLength(database)) {
            if (this.typeData.getLength() == null) {
                this.typeData.setLength(0);
            }
        } else {
            this.typeData.setLength(null);
        }

        if (type != null && type.isNeedDecimal(database)) {
            if (this.typeData.getDecimal() == null) {
                this.typeData.setDecimal(0);
            }
        } else {
            this.typeData.setDecimal(null);
        }

    }

    public TypeData getTypeData() {
        return typeData;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void copyTo(final Word to) {
        to.physicalName = physicalName;
        to.logicalName = logicalName;
        to.description = description;
        to.type = type;
        to.typeData = typeData.clone();
    }

    @Override
    public int compareTo(final Word o) {
        return PHYSICAL_NAME_COMPARATOR.compare(this, o);
    }

    @Override
    public String getName() {
        return getLogicalName();
    }

    @Override
    public String getObjectType() {
        return "word";
    }

    private static class WordWithoutNameComparator implements Comparator<Word> {

        @Override
        public int compare(final Word o1, final Word o2) {
            if (o1 == o2) {
                return 0;
            }
            if (o2 == null) {
                return -1;
            }
            if (o1 == null) {
                return 1;
            }

            if (o1.type == null) {
                if (o2.type != null) {
                    return 1;
                }
            } else {
                if (o2.type == null) {
                    return -1;
                }
                final int value = o1.type.getId().compareTo(o2.type.getId());
                if (value != 0) {
                    return value;
                }
            }

            if (o1.typeData == null) {
                if (o2.typeData != null) {
                    return 1;
                }
            } else {
                if (o2.typeData == null) {
                    return -1;
                }
                final int value = o1.typeData.compareTo(o2.typeData);
                if (value != 0) {
                    return value;
                }
            }

            final int value = Format.null2blank(o1.description).compareTo(Format.null2blank(o2.description));
            if (value != 0) {
                return value;
            }

            return 0;
        }

    }

    private static class WordPhysicalNameComparator implements Comparator<Word> {

        @Override
        public int compare(final Word o1, final Word o2) {
            if (o1 == o2) {
                return 0;
            }
            if (o2 == null) {
                return -1;
            }
            if (o1 == null) {
                return 1;
            }

            int value = 0;

            value = Format.null2blank(o1.physicalName).toUpperCase().compareTo(Format.null2blank(o2.physicalName).toUpperCase());
            if (value != 0) {
                return value;
            }

            value = Format.null2blank(o1.logicalName).toUpperCase().compareTo(Format.null2blank(o2.logicalName).toUpperCase());
            if (value != 0) {
                return value;
            }

            return WITHOUT_NAME_COMPARATOR.compare(o1, o2);
        }

    }

    private static class WordLogicalNameComparator implements Comparator<Word> {

        @Override
        public int compare(final Word o1, final Word o2) {
            if (o1 == o2) {
                return 0;
            }
            if (o2 == null) {
                return -1;
            }
            if (o1 == null) {
                return 1;
            }

            int value = 0;

            value = Format.null2blank(o1.logicalName).toUpperCase().compareTo(Format.null2blank(o2.logicalName).toUpperCase());
            if (value != 0) {
                return value;
            }

            value = Format.null2blank(o1.physicalName).toUpperCase().compareTo(Format.null2blank(o2.physicalName).toUpperCase());
            if (value != 0) {
                return value;
            }

            return WITHOUT_NAME_COMPARATOR.compare(o1, o2);
        }

    }

}
