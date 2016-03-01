package org.insightech.er.editor.model.diagram_contents.not_element.group;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.TypeData;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.UniqueWordDictionary;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;

public class GlobalGroupSet {

    private static final String COLUMN_GOURP_SETTINGS_FILENAME = "column_group.xml"; //$NON-NLS-1$

    public static GroupSet load() {
        final GroupSet columnGroups = new GroupSet();

        try {
            final IDialogSettings settings = new DialogSettings("column_group_list");
            String database = settings.get("database");
            if (database == null) {
                database = DBManagerFactory.getAllDBList().get(0);
            }
            columnGroups.setDatabase(database);

            final String path = getPath();
            final File columnGroupListFile = new File(path);

            if (columnGroupListFile.exists()) {
                settings.load(path);

                final UniqueWordDictionary dictionary = new UniqueWordDictionary();

                for (final IDialogSettings columnGroupSection : settings.getSections()) {
                    final ColumnGroup columnGroup = new ColumnGroup();

                    columnGroup.setGroupName(columnGroupSection.get("group_name"));

                    for (final IDialogSettings columnSection : columnGroupSection.getSections()) {
                        final String physicalName = columnSection.get("physical_name");
                        final String logicalName = columnSection.get("logical_name");
                        final SqlType sqlType = SqlType.valueOfId(columnSection.get("type"));
                        final String defaultValue = columnSection.get("default_value");
                        final String description = columnSection.get("description");
                        final String constraint = columnSection.get("constraint");
                        final boolean notNull = Boolean.valueOf(columnSection.get("not_null")).booleanValue();
                        final boolean unique = Boolean.valueOf(columnSection.get("unique")).booleanValue();
                        final Integer length = toInteger(columnSection.get("length"));
                        final Integer decimal = toInteger(columnSection.get("decimal"));
                        final boolean array = Boolean.valueOf(columnSection.get("array")).booleanValue();
                        final Integer arrayDimension = toInteger(columnSection.get("array_dimension"));
                        final boolean unsigned = Boolean.valueOf(columnSection.get("unsigned")).booleanValue();
                        final boolean zerofill = Boolean.valueOf(columnSection.get("zerofill")).booleanValue();
                        final boolean binary = Boolean.valueOf(columnSection.get("binary")).booleanValue();
                        final String args = columnSection.get("args");
                        final boolean charSemantics = Boolean.valueOf(columnSection.get("char_semantics")).booleanValue();

                        final TypeData typeData = new TypeData(length, decimal, array, arrayDimension, unsigned, zerofill, binary, args, charSemantics);

                        Word word = new Word(physicalName, logicalName, sqlType, typeData, description, database);
                        word = dictionary.getUniqueWord(word, true);

                        final NormalColumn column = new NormalColumn(word, notNull, false, unique, false, defaultValue, constraint, null, null, null);

                        columnGroup.addColumn(column);
                    }

                    columnGroups.add(columnGroup);
                }
            }
        } catch (final IOException e) {
            ERDiagramActivator.showExceptionDialog(e);
        }

        return columnGroups;
    }

    public static void save(final GroupSet columnGroups) {
        try {
            final IDialogSettings settings = new DialogSettings("column_group_list");

            settings.put("database", columnGroups.getDatabase());

            int index = 0;

            for (final ColumnGroup columnGroup : columnGroups) {
                final IDialogSettings columnGroupSection = new DialogSettings("column_group_" + index);
                index++;

                columnGroupSection.put("group_name", columnGroup.getGroupName());

                int columnIndex = 0;

                for (final NormalColumn normalColumn : columnGroup.getColumns()) {
                    final IDialogSettings columnSection = new DialogSettings("column_" + columnIndex);
                    columnIndex++;

                    columnSection.put("physical_name", null2Blank(normalColumn.getPhysicalName()));
                    columnSection.put("logical_name", null2Blank(normalColumn.getLogicalName()));
                    columnSection.put("type", null2Blank(normalColumn.getType()));
                    columnSection.put("length", null2Blank(normalColumn.getTypeData().getLength()));
                    columnSection.put("decimal", null2Blank(normalColumn.getTypeData().getDecimal()));
                    columnSection.put("array", normalColumn.getTypeData().isArray());
                    columnSection.put("array_dimension", null2Blank(normalColumn.getTypeData().getArrayDimension()));
                    columnSection.put("unsigned", normalColumn.getTypeData().isUnsigned());
                    columnSection.put("zerofill", normalColumn.getTypeData().isZerofill());
                    columnSection.put("binary", normalColumn.getTypeData().isBinary());

                    columnSection.put("not_null", normalColumn.isNotNull());
                    columnSection.put("unique", normalColumn.isUniqueKey());
                    columnSection.put("default_value", null2Blank(normalColumn.getDefaultValue()));
                    columnSection.put("constraint", null2Blank(normalColumn.getConstraint()));
                    columnSection.put("description", null2Blank(normalColumn.getDescription()));
                    columnSection.put("char_semantics", normalColumn.getTypeData().isCharSemantics());

                    columnGroupSection.addSection(columnSection);
                }

                settings.addSection(columnGroupSection);
            }

            settings.save(getPath());

        } catch (final IOException e) {
            ERDiagramActivator.showExceptionDialog(e);
        }
    }

    private static String getPath() {
        final IPath dataLocation = ERDiagramActivator.getDefault().getStateLocation();
        final String path = dataLocation.append(COLUMN_GOURP_SETTINGS_FILENAME).toOSString();
        return path;
    }

    private static String null2Blank(final String str) {
        if (str == null) {
            return "";
        }

        return str;
    }

    private static String null2Blank(final Object object) {
        if (object == null) {
            return "";
        }

        return object.toString();
    }

    private static String null2Blank(final SqlType sqlType) {
        if (sqlType == null) {
            return "";
        }

        return sqlType.getId();
    }

    private static Integer toInteger(final String str) {
        if (str == null || str.equals("")) {
            return null;
        }

        try {
            return Integer.valueOf(str);
        } catch (final NumberFormatException e) {}

        return null;
    }
}
