package org.insightech.er.editor.model.dbexport.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;

import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ResourceString;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.AbstractExportManager;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.progress_monitor.ProgressMonitor;
import org.insightech.er.editor.model.settings.export.ExportJavaSetting;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;
import org.insightech.er.util.io.FileUtils;
import org.insightech.er.util.io.IOUtils;

public class ExportToJavaManager extends AbstractExportManager {

    private static final String TEMPLATE_DIR = "javasource/";

    private static final String[] KEYWORDS = {"java.template.constructor", "java.template.getter.description", "java.template.set.adder.description", "java.template.set.getter.description", "java.template.set.property.description", "java.template.set.setter.description", "java.template.setter.description",};

    private static final String TEMPLATE;

    private static final String IMPLEMENTS;

    private static final String PROPERTIES;

    private static final String SET_PROPERTIES;

    private static final String SETTER_GETTER;

    private static final String SETTER_GETTER_ADDER;

    private static final String HASHCODE_EQUALS;

    private static final String HASHCODE_LOGIC;

    private static final String EQUALS_LOGIC;

    private static final String EXTENDS;

    private static final String HIBERNATE_TEMPLATE;

    private static final String HIBERNATE_PROPERTY;

    private static final String HIBERNATE_ID;

    private static final String HIBERNATE_COMPOSITE_ID;

    private static final String HIBERNATE_COMPOSITE_ID_KEY;

    static {
        try {
            TEMPLATE = loadResource("template");
            IMPLEMENTS = loadResource("@implements");
            PROPERTIES = loadResource("@properties");
            SET_PROPERTIES = loadResource("@set_properties");
            SETTER_GETTER = loadResource("@setter_getter");
            SETTER_GETTER_ADDER = loadResource("@setter_getter_adder");
            HASHCODE_EQUALS = loadResource("@hashCode_equals");
            HASHCODE_LOGIC = loadResource("@hashCode logic");
            EQUALS_LOGIC = loadResource("@equals logic");
            EXTENDS = loadResource("@extends");

            HIBERNATE_TEMPLATE = loadResource("hibernate/hbm");
            HIBERNATE_PROPERTY = loadResource("hibernate/@property");
            HIBERNATE_ID = loadResource("hibernate/@id");
            HIBERNATE_COMPOSITE_ID = loadResource("hibernate/@composite_id");
            HIBERNATE_COMPOSITE_ID_KEY = loadResource("hibernate/@composite_id_key");

        } catch (final IOException e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }

    private final ExportJavaSetting exportJavaSetting;

    private final String packageDir;

    private final Set<String> importClasseNames;

    private final Set<String> sets;

    public ExportToJavaManager(final ExportJavaSetting exportJavaSetting) {
        super("dialog.message.export.java");

        packageDir = exportJavaSetting.getPackageName().replaceAll("\\.", "\\/");

        this.exportJavaSetting = exportJavaSetting;

        importClasseNames = new TreeSet<String>();
        sets = new TreeSet<String>();
    }

    @Override
    protected int getTotalTaskCount() {
        return diagram.getDiagramContents().getContents().getTableSet().getList().size();
    }

    @Override
    protected void doProcess(final ProgressMonitor monitor) throws Exception {
        for (final ERTable table : diagram.getDiagramContents().getContents().getTableSet().getList()) {

            monitor.subTaskWithCounter("writing : " + this.getClassName(table));

            final String className = this.getClassName(table);
            String compositeIdClassName = null;

            if (exportJavaSetting.isWithHibernate()) {
                if (table.getPrimaryKeySize() > 1) {
                    compositeIdClassName = this.getCamelCaseName(table) + "Id";

                    final String compositeIdContent = generateCompositeIdContent(diagram, table, compositeIdClassName);

                    writeOut(File.separator + packageDir + File.separator + compositeIdClassName + ".java", compositeIdContent);
                }

                final String hbmContent = generateHbmContent(diagram, table, compositeIdClassName);

                writeOut(File.separator + packageDir + File.separator + className + ".hbm.xml", hbmContent);
            }

            final String content = generateContent(diagram, table, compositeIdClassName);

            writeOut(File.separator + packageDir + File.separator + className + ".java", content);

            monitor.worked(1);
        }
    }

    protected String getClassName(final ERTable table) {
        return this.getCamelCaseName(table) + this.getCamelCaseName(exportJavaSetting.getClassNameSuffix(), true);
    }

    protected String getCamelCaseName(final ERTable table) {
        return this.getCamelCaseName(table.getPhysicalName(), true);
    }

    protected String getCamelCaseName(final String name, final boolean capital) {
        String className = name.toLowerCase();

        if (capital && className.length() > 0) {
            final String first = className.substring(0, 1);
            final String other = className.substring(1);

            className = first.toUpperCase() + other;
        }

        while (className.indexOf("_") == 0) {
            className = className.substring(1);
        }

        int index = className.indexOf("_");

        while (index != -1) {
            final String before = className.substring(0, index);
            if (className.length() == index + 1) {
                className = before;
                break;
            }

            final String target = className.substring(index + 1, index + 2);

            String after = null;

            if (className.length() == index + 1) {
                after = "";

            } else {
                after = className.substring(index + 2);
            }

            className = before + target.toUpperCase() + after;

            index = className.indexOf("_");
        }

        return className;
    }

    private static String loadResource(final String templateName) throws IOException {
        final String resourceName = TEMPLATE_DIR + templateName + ".txt";

        final InputStream in = ERDiagramActivator.getClassLoader().getResourceAsStream(resourceName);

        if (in == null) {
            throw new FileNotFoundException(resourceName);
        }

        try {
            String content = IOUtils.toString(in);

            for (final String keyword : KEYWORDS) {
                content = content.replaceAll(keyword, Matcher.quoteReplacement(ResourceString.getResourceString(keyword)));
            }

            return content;

        } finally {
            in.close();
        }
    }

    private String generateContent(final ERDiagram diagram, final ERTable table, final String compositeIdClassName) throws IOException {
        importClasseNames.clear();
        importClasseNames.add("java.io.Serializable");
        sets.clear();

        String content = TEMPLATE;
        content = content.replace("@implements", IMPLEMENTS);

        content = this.replacePropertiesInfo(content, table, compositeIdClassName);
        content = replaceHashCodeEqualsInfo(content, table, compositeIdClassName);

        final String classDescription = ResourceString.getResourceString("java.template.class.description").replaceAll("@LogicalTableName", Matcher.quoteReplacement(table.getLogicalName()));

        content = replaceClassInfo(content, classDescription, this.getCamelCaseName(table), exportJavaSetting.getClassNameSuffix());
        content = replaceExtendInfo(content);
        content = replaceImportInfo(content);
        content = replaceConstructorInfo(content);

        return content;
    }

    private String generateCompositeIdContent(final ERDiagram diagram, final ERTable table, final String compositeIdClassName) throws IOException {
        importClasseNames.clear();
        importClasseNames.add("java.io.Serializable");
        sets.clear();

        String content = TEMPLATE;
        content = content.replace("@implements", IMPLEMENTS);

        content = this.replacePropertiesInfo(content, null, table.getPrimaryKeys(), null, null);
        content = replaceHashCodeEqualsInfo(content, table, null);

        final String classDescription = ResourceString.getResourceString("java.template.composite.id.class.description").replaceAll("@LogicalTableName", Matcher.quoteReplacement(table.getLogicalName()));

        content = replaceClassInfo(content, classDescription, compositeIdClassName, "");
        content = replaceExtendInfo(content);
        content = replaceImportInfo(content);
        content = replaceConstructorInfo(content);

        return content;
    }

    private String replacePropertiesInfo(final String content, final ERTable table, final String compositeIdClassName) throws IOException {
        return replacePropertiesInfo(content, table, table.getExpandedColumns(), table.getReferringElementList(), compositeIdClassName);
    }

    private String replacePropertiesInfo(String content, final ERTable table, final List<NormalColumn> columns, final List<NodeElement> referringElementList, final String compositeIdClassName) throws IOException {
        final StringBuilder properties = new StringBuilder();
        final StringBuilder setterGetters = new StringBuilder();

        if (compositeIdClassName != null) {
            addCompositeIdContent(properties, PROPERTIES, compositeIdClassName, table);
            addCompositeIdContent(setterGetters, SETTER_GETTER, compositeIdClassName, table);
        }

        for (final NormalColumn normalColumn : columns) {
            if (compositeIdClassName == null || !normalColumn.isPrimaryKey() || normalColumn.isForeignKey()) {
                this.addContent(properties, PROPERTIES, normalColumn);
                this.addContent(setterGetters, SETTER_GETTER, normalColumn);
            }
        }

        if (referringElementList != null) {
            for (final NodeElement referringElement : referringElementList) {
                if (referringElement instanceof TableView) {
                    final TableView tableView = (TableView) referringElement;

                    this.addContent(properties, SET_PROPERTIES, tableView);
                    this.addContent(setterGetters, SETTER_GETTER_ADDER, tableView);

                    sets.add(tableView.getPhysicalName());
                }
            }
        }

        content = content.replaceAll("@properties\r\n", Matcher.quoteReplacement(properties.toString()));
        content = content.replaceAll("@setter_getter\r\n", Matcher.quoteReplacement(setterGetters.toString()));

        return content;
    }

    private String replaceHashCodeEqualsInfo(String content, final ERTable table, final String compositeIdClassName) throws IOException {
        if (compositeIdClassName != null) {
            final StringBuilder hashCodes = new StringBuilder();
            final StringBuilder equals = new StringBuilder();

            addCompositeIdContent(hashCodes, HASHCODE_LOGIC, compositeIdClassName, table);
            addCompositeIdContent(equals, EQUALS_LOGIC, compositeIdClassName, table);

            String hashCodeEquals = HASHCODE_EQUALS;
            hashCodeEquals = hashCodeEquals.replaceAll("@hashCode logic\r\n", Matcher.quoteReplacement(hashCodes.toString()));
            hashCodeEquals = hashCodeEquals.replaceAll("@equals logic\r\n", equals.toString());

            content = content.replaceAll("@hashCode_equals\r\n", hashCodeEquals.toString());

        } else if (table.getPrimaryKeySize() > 0) {
            final StringBuilder hashCodes = new StringBuilder();
            final StringBuilder equals = new StringBuilder();

            for (final NormalColumn primaryKey : table.getPrimaryKeys()) {
                this.addContent(hashCodes, HASHCODE_LOGIC, primaryKey);
                this.addContent(equals, EQUALS_LOGIC, primaryKey);
            }

            String hashCodeEquals = HASHCODE_EQUALS;
            hashCodeEquals = hashCodeEquals.replaceAll("@hashCode logic\r\n", hashCodes.toString());
            hashCodeEquals = hashCodeEquals.replaceAll("@equals logic\r\n", equals.toString());

            content = content.replaceAll("@hashCode_equals\r\n", hashCodeEquals.toString());

        } else {
            content = content.replaceAll("@hashCode_equals\r\n", "");
        }

        return content;
    }

    private String replaceClassInfo(String content, final String classDescription, final String className, final String classNameSufix) {
        if (Check.isEmptyTrim(exportJavaSetting.getPackageName())) {
            content = content.replaceAll("package @package;\r\n\r\n", "");

        } else {
            content = content.replaceAll("@package", exportJavaSetting.getPackageName());
        }

        content = content.replaceAll("@classDescription", classDescription);
        content = content.replaceAll("@PhysicalTableName", className);
        content = content.replaceAll("@suffix", this.getCamelCaseName(classNameSufix, true));
        content = content.replaceAll("@version", "@version \\$Id\\$");

        return content;
    }

    private String replaceExtendInfo(String content) throws IOException {
        if (Check.isEmpty(exportJavaSetting.getExtendsClass())) {
            content = content.replaceAll("@import extends\r\n", "");
            content = content.replaceAll("@extends ", "");

        } else {
            importClasseNames.add(exportJavaSetting.getExtendsClass());

            content = content.replaceAll("@extends", Matcher.quoteReplacement(EXTENDS));

            final int index = exportJavaSetting.getExtendsClass().lastIndexOf(".");

            String extendsClassWithoutPackage = null;

            if (index == -1) {
                extendsClassWithoutPackage = exportJavaSetting.getExtendsClass();

            } else {
                extendsClassWithoutPackage = exportJavaSetting.getExtendsClass().substring(index + 1);
            }

            content = content.replaceAll("@extendsClassWithoutPackage", extendsClassWithoutPackage);
            content = content.replaceAll("@extendsClass", exportJavaSetting.getExtendsClass());
        }

        return content;
    }

    private String replaceImportInfo(String content) {
        final StringBuilder imports = new StringBuilder();
        for (final String importClasseName : importClasseNames) {
            imports.append("import ");
            imports.append(importClasseName);
            imports.append(";\r\n");
        }

        content = content.replaceAll("@import\r\n", Matcher.quoteReplacement(imports.toString()));

        return content;
    }

    private String replaceConstructorInfo(String content) {
        final StringBuilder constructor = new StringBuilder();
        for (final String tableName : sets) {
            constructor.append("\t\tthis.");
            constructor.append(this.getCamelCaseName(tableName, false));
            constructor.append("Set = new HashSet<");
            constructor.append(this.getCamelCaseName(tableName, true) + this.getCamelCaseName(exportJavaSetting.getClassNameSuffix(), true));
            constructor.append(">();\r\n");
        }

        content = content.replaceAll("@constructor\r\n", Matcher.quoteReplacement(constructor.toString()));

        return content;
    }

    private void addContent(final StringBuilder contents, final String template, final NormalColumn normalColumn) {

        String value = null;

        if (normalColumn.isForeignKey()) {
            final NormalColumn referencedColumn = normalColumn.getRootReferencedColumn();

            final ERTable referencedTable = (ERTable) referencedColumn.getColumnHolder();
            final String className = this.getClassName(referencedTable);

            value = template.replaceAll("@type", Matcher.quoteReplacement(className));
            value = value.replaceAll("@logicalColumnName", referencedTable.getName());

            String physicalName = normalColumn.getPhysicalName().toLowerCase();
            physicalName = physicalName.replaceAll(referencedColumn.getPhysicalName().toLowerCase(), "");
            if (physicalName.indexOf(referencedTable.getPhysicalName().toLowerCase()) == -1) {
                physicalName = physicalName + referencedTable.getPhysicalName();
            }

            value = value.replaceAll("@physicalColumnName", this.getCamelCaseName(physicalName, false));
            value = value.replaceAll("@PhysicalColumnName", this.getCamelCaseName(physicalName, true));

        } else {
            value = template.replaceAll("@type", this.getClassName(normalColumn.getType()));
            value = value.replaceAll("@logicalColumnName", normalColumn.getLogicalName());
            value = value.replaceAll("@physicalColumnName", this.getCamelCaseName(normalColumn.getPhysicalName(), false));
            value = value.replaceAll("@PhysicalColumnName", this.getCamelCaseName(normalColumn.getPhysicalName(), true));

        }

        contents.append(value);
        contents.append("\r\n");
    }

    private void addContent(final StringBuilder contents, final String template, final TableView tableView) {

        String value = template;

        importClasseNames.add("java.util.Set");
        importClasseNames.add("java.util.HashSet");

        value = value.replaceAll("@setType", Matcher.quoteReplacement("Set<" + this.getCamelCaseName(tableView.getPhysicalName(), true) + this.getCamelCaseName(exportJavaSetting.getClassNameSuffix(), true) + ">"));
        value = value.replaceAll("@type", Matcher.quoteReplacement(this.getCamelCaseName(tableView.getPhysicalName(), true) + this.getCamelCaseName(exportJavaSetting.getClassNameSuffix(), true)));
        value = value.replaceAll("@logicalColumnName", Matcher.quoteReplacement(tableView.getName()));

        value = value.replaceAll("@physicalColumnName", Matcher.quoteReplacement(this.getCamelCaseName(tableView.getPhysicalName(), false)));
        value = value.replaceAll("@PhysicalColumnName", Matcher.quoteReplacement(this.getCamelCaseName(tableView.getPhysicalName(), true)));

        contents.append(value);
        contents.append("\r\n");
    }

    private void addCompositeIdContent(final StringBuilder contents, final String template, final String compositeIdClassName, final ERTable table) {

        final String compositeIdPropertyName = compositeIdClassName.substring(0, 1).toLowerCase() + compositeIdClassName.substring(1);

        final String propertyDescription = ResourceString.getResourceString("java.template.composite.id.property.description").replaceAll("@LogicalTableName", Matcher.quoteReplacement(table.getLogicalName()));

        String value = template;

        value = value.replaceAll("@type", compositeIdClassName);
        value = value.replaceAll("@logicalColumnName", propertyDescription);

        value = value.replaceAll("@physicalColumnName", compositeIdPropertyName);
        value = value.replaceAll("@PhysicalColumnName", compositeIdClassName);

        contents.append(value);
        contents.append("\r\n");
    }

    private String getClassName(final SqlType type) {
        if (type == null) {
            return "";
        }
        final Class clazz = type.getJavaClass();

        final String name = clazz.getCanonicalName();
        if (!name.startsWith("java.lang")) {
            importClasseNames.add(name);
        }

        return clazz.getSimpleName();
    }

    private String getFullClassName(final SqlType type) {
        if (type == null) {
            return "";
        }
        final Class clazz = type.getJavaClass();

        final String name = clazz.getCanonicalName();

        return name;
    }

    private void writeOut(final String dstPath, final String content) throws IOException {
        final File file = new File(FileUtils.getFile(projectDir, exportJavaSetting.getJavaOutput()), dstPath);

        file.getParentFile().mkdirs();

        FileUtils.writeStringToFile(file, content, exportJavaSetting.getSrcFileEncoding());
    }

    private String generateHbmContent(final ERDiagram diagram, final ERTable table, final String compositeIdClassName) throws IOException {
        String content = HIBERNATE_TEMPLATE;

        content = content.replaceAll("@package", Matcher.quoteReplacement(exportJavaSetting.getPackageName()));
        content = content.replaceAll("@PhysicalTableName", Matcher.quoteReplacement(this.getCamelCaseName(table)));
        content = content.replaceAll("@suffix", Matcher.quoteReplacement(Format.null2blank(exportJavaSetting.getClassNameSuffix())));
        content = content.replaceAll("@realTableName", Matcher.quoteReplacement(table.getPhysicalName()));

        final StringBuilder properties = new StringBuilder();

        if (table.getPrimaryKeySize() == 1) {
            for (final NormalColumn column : table.getPrimaryKeys()) {
                String property = HIBERNATE_ID;
                property = property.replaceAll("@physicalColumnName", this.getCamelCaseName(column.getPhysicalName(), false));
                property = property.replaceAll("@realColumnName", column.getPhysicalName());
                property = property.replaceAll("@type", getFullClassName(column.getType()));
                property = property.replaceAll("@generator", "assigned");

                properties.append(property);
            }

        } else if (table.getPrimaryKeySize() > 1) {
            String property = HIBERNATE_COMPOSITE_ID;

            final StringBuilder keys = new StringBuilder();

            for (final NormalColumn column : table.getPrimaryKeys()) {
                String key = HIBERNATE_COMPOSITE_ID_KEY;
                key = key.replaceAll("@physicalColumnName", this.getCamelCaseName(column.getPhysicalName(), false));
                key = key.replaceAll("@realColumnName", column.getPhysicalName());
                key = key.replaceAll("@type", getFullClassName(column.getType()));

                keys.append(key);
            }

            final String compositeIdPropertyName = compositeIdClassName.substring(0, 1).toLowerCase() + compositeIdClassName.substring(1);

            property = property.replaceAll("@compositeIdPropertyName", compositeIdPropertyName);
            property = property.replaceAll("@compositeIdClassName", compositeIdClassName);
            property = property.replaceAll("@key_properties", keys.toString());

            properties.append(property);
        }

        for (final NormalColumn column : table.getExpandedColumns()) {
            if (!column.isPrimaryKey()) {
                String property = HIBERNATE_PROPERTY;
                property = property.replaceAll("@physicalColumnName", this.getCamelCaseName(column.getPhysicalName(), false));
                property = property.replaceAll("@realColumnName", column.getPhysicalName());
                property = property.replaceAll("@type", getFullClassName(column.getType()));
                property = property.replaceAll("@not-null", String.valueOf(column.isNotNull()));

                properties.append(property);
            }
        }

        content = content.replaceAll("@properties\r\n", properties.toString());

        return content;
    }

    @Override
    public File getOutputFileOrDir() {
        return new File(exportJavaSetting.getJavaOutput());
    }

}
