package org.insightech.er.editor.persistent.impl;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.insightech.er.ResourceString;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.db.impl.db2.DB2DBManager;
import org.insightech.er.db.impl.db2.tablespace.DB2TablespaceProperties;
import org.insightech.er.db.impl.mysql.MySQLDBManager;
import org.insightech.er.db.impl.mysql.MySQLTableProperties;
import org.insightech.er.db.impl.mysql.tablespace.MySQLTablespaceProperties;
import org.insightech.er.db.impl.oracle.OracleDBManager;
import org.insightech.er.db.impl.oracle.tablespace.OracleTablespaceProperties;
import org.insightech.er.db.impl.postgres.PostgresDBManager;
import org.insightech.er.db.impl.postgres.PostgresTableProperties;
import org.insightech.er.db.impl.postgres.tablespace.PostgresTablespaceProperties;
import org.insightech.er.db.impl.standard_sql.StandardSQLDBManager;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ViewableModel;
import org.insightech.er.editor.model.dbexport.ddl.DDLTarget;
import org.insightech.er.editor.model.diagram_contents.DiagramContents;
import org.insightech.er.editor.model.diagram_contents.element.connection.Bendpoint;
import org.insightech.er.editor.model.diagram_contents.element.connection.CommentConnection;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeSet;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.image.InsertedImage;
import org.insightech.er.editor.model.diagram_contents.element.node.model_properties.ModelProperties;
import org.insightech.er.editor.model.diagram_contents.element.node.note.Note;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.insightech.er.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.model.diagram_contents.element.node.view.properties.ViewProperties;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.TypeData;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.UniqueWordDictionary;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GroupSet;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.SequenceSet;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceSet;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.TriggerSet;
import org.insightech.er.editor.model.settings.CategorySetting;
import org.insightech.er.editor.model.settings.DBSetting;
import org.insightech.er.editor.model.settings.Environment;
import org.insightech.er.editor.model.settings.EnvironmentSetting;
import org.insightech.er.editor.model.settings.ExportSetting;
import org.insightech.er.editor.model.settings.PageSetting;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.model.settings.TranslationSetting;
import org.insightech.er.editor.model.settings.export.ExportDDLSetting;
import org.insightech.er.editor.model.settings.export.ExportExcelSetting;
import org.insightech.er.editor.model.settings.export.ExportHtmlSetting;
import org.insightech.er.editor.model.settings.export.ExportImageSetting;
import org.insightech.er.editor.model.settings.export.ExportJavaSetting;
import org.insightech.er.editor.model.settings.export.ExportTestDataSetting;
import org.insightech.er.editor.model.testdata.DirectTestData;
import org.insightech.er.editor.model.testdata.RepeatTestData;
import org.insightech.er.editor.model.testdata.RepeatTestDataDef;
import org.insightech.er.editor.model.testdata.TableTestData;
import org.insightech.er.editor.model.testdata.TestData;
import org.insightech.er.editor.model.tracking.ChangeTracking;
import org.insightech.er.editor.model.tracking.ChangeTrackingList;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;
import org.insightech.er.util.NameValue;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLLoader {

    private ERDiagram diagram;

    private String database;

    private class LoadContext {
        private final File file;

        private final Map<String, NodeElement> nodeElementMap;

        private final Map<String, NormalColumn> columnMap;

        private final Map<String, ComplexUniqueKey> complexUniqueKeyMap;

        private final Map<NormalColumn, String[]> columnRelationMap;

        private final Map<NormalColumn, String[]> columnReferencedColumnMap;

        private final Map<String, ColumnGroup> columnGroupMap;

        private final Map<Relation, String> referencedColumnMap;

        private final Map<Relation, String> referencedComplexUniqueKeyMap;

        private final Map<ConnectionElement, String> connectionSourceMap;

        private final Map<ConnectionElement, String> connectionTargetMap;

        private final Map<String, ConnectionElement> connectionMap;

        private final Map<String, Word> wordMap;

        private final Map<String, Tablespace> tablespaceMap;

        private final Map<String, Environment> environmentMap;

        private final UniqueWordDictionary uniqueWordDictionary;

        private final Dictionary dictionary;

        private LoadContext(final File file, final Dictionary dictionary) {
            this.file = file;
            nodeElementMap = new HashMap<String, NodeElement>();
            columnMap = new HashMap<String, NormalColumn>();
            complexUniqueKeyMap = new HashMap<String, ComplexUniqueKey>();
            columnRelationMap = new HashMap<NormalColumn, String[]>();
            columnReferencedColumnMap = new HashMap<NormalColumn, String[]>();
            columnGroupMap = new HashMap<String, ColumnGroup>();
            referencedColumnMap = new HashMap<Relation, String>();
            referencedComplexUniqueKeyMap = new HashMap<Relation, String>();
            connectionMap = new HashMap<String, ConnectionElement>();
            connectionSourceMap = new HashMap<ConnectionElement, String>();
            connectionTargetMap = new HashMap<ConnectionElement, String>();
            wordMap = new HashMap<String, Word>();
            tablespaceMap = new HashMap<String, Tablespace>();
            environmentMap = new HashMap<String, Environment>();
            uniqueWordDictionary = new UniqueWordDictionary();

            this.dictionary = dictionary;
            this.dictionary.clear();
        }

        private Date getUpdatedDate() {
            return new Date(file.lastModified());
        }

        private void resolve() {
            for (final ConnectionElement connection : connectionSourceMap.keySet()) {
                final String id = connectionSourceMap.get(connection);

                final NodeElement nodeElement = nodeElementMap.get(id);
                connection.setSource(nodeElement);
            }

            for (final ConnectionElement connection : connectionTargetMap.keySet()) {
                final String id = connectionTargetMap.get(connection);

                final NodeElement nodeElement = nodeElementMap.get(id);
                connection.setTarget(nodeElement);
            }

            for (final Relation relation : referencedColumnMap.keySet()) {
                final String id = referencedColumnMap.get(relation);

                final NormalColumn column = columnMap.get(id);
                relation.setReferencedColumn(column);
            }

            for (final Relation relation : referencedComplexUniqueKeyMap.keySet()) {
                final String id = referencedComplexUniqueKeyMap.get(relation);

                final ComplexUniqueKey complexUniqueKey = complexUniqueKeyMap.get(id);
                relation.setReferencedComplexUniqueKey(complexUniqueKey);
            }

            final Set<NormalColumn> foreignKeyColumnSet = new HashSet<NormalColumn>(columnReferencedColumnMap.keySet());

            while (!foreignKeyColumnSet.isEmpty()) {
                final NormalColumn foreignKeyColumn = foreignKeyColumnSet.iterator().next();
                foreignKeyColumnSet.remove(foreignKeyColumn);
                reduce(foreignKeyColumnSet, foreignKeyColumn);
            }
        }

        private void reduce(final Set<NormalColumn> foreignKeyColumnSet, final NormalColumn foreignKeyColumn) {

            final String[] referencedColumnIds = columnReferencedColumnMap.get(foreignKeyColumn);

            final String[] relationIds = columnRelationMap.get(foreignKeyColumn);

            final List<NormalColumn> referencedColumnList = new ArrayList<NormalColumn>();

            if (referencedColumnIds != null) {
                for (final String referencedColumnId : referencedColumnIds) {
                    try {
                        // [ermasterr] Change id generation to model's hash
                        //Integer.parseInt(referencedColumnId);

                        final NormalColumn referencedColumn = columnMap.get(referencedColumnId);
                        referencedColumnList.add(referencedColumn);

                        if (foreignKeyColumnSet.contains(referencedColumn) && foreignKeyColumn != referencedColumn) {
                            reduce(foreignKeyColumnSet, referencedColumn);
                        }

                    } catch (final NumberFormatException e) {}
                }
            }

            if (relationIds != null) {
                for (final String relationId : relationIds) {
                    try {
                        // [ermasterr] Change id generation to model's hash
                        //Integer.parseInt(relationId);

                        final Relation relation = (Relation) connectionMap.get(relationId);
                        for (final NormalColumn referencedColumn : referencedColumnList) {
                            if (referencedColumn.getColumnHolder() == relation.getSourceTableView()) {
                                foreignKeyColumn.addReference(referencedColumn, relation);
                                break;
                            }
                        }

                    } catch (final NumberFormatException e) {}
                }
            }

        }
    }

    public ERDiagram load(final InputStream in, final File file) throws Exception {
        final DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        final Document document = parser.parse(in);

        Node root = document.getFirstChild();

        while (root.getNodeType() == Node.COMMENT_NODE) {
            document.removeChild(root);
            root = document.getFirstChild();
        }

        load((Element) root, file);

        return diagram;
    }

    private String getStringValue(final Element element, final String tagname) {
        final NodeList nodeList = element.getElementsByTagName(tagname);

        if (nodeList.getLength() == 0) {
            return null;
        }

        final Node node = nodeList.item(0);

        if (node.getFirstChild() == null) {
            return "";
        }

        return node.getFirstChild().getNodeValue();
    }

    private String[] getTagValues(final Element element, final String tagname) {
        final NodeList nodeList = element.getElementsByTagName(tagname);

        final String[] values = new String[nodeList.getLength()];

        for (int i = 0; i < nodeList.getLength(); i++) {
            final Node node = nodeList.item(i);
            if (node.getFirstChild() != null) {
                values[i] = node.getFirstChild().getNodeValue();
            }
        }

        return values;
    }

    private boolean getBooleanValue(final Element element, final String tagname) {
        return getBooleanValue(element, tagname, false);
    }

    private boolean getBooleanValue(final Element element, final String tagname, final boolean defaultValue) {
        final NodeList nodeList = element.getElementsByTagName(tagname);

        if (nodeList.getLength() == 0) {
            return defaultValue;
        }

        final Node node = nodeList.item(0);

        final String value = node.getFirstChild().getNodeValue();

        return Boolean.valueOf(value).booleanValue();
    }

    private int getIntValue(final Element element, final String tagname) {
        return getIntValue(element, tagname, 0);
    }

    private int getIntValue(final Element element, final String tagname, final int defaultValue) {
        final NodeList nodeList = element.getElementsByTagName(tagname);

        if (nodeList.getLength() == 0) {
            return defaultValue;
        }

        final Node node = nodeList.item(0);

        if (node.getFirstChild() == null) {
            return defaultValue;
        }

        final String value = node.getFirstChild().getNodeValue();

        return Integer.valueOf(value).intValue();
    }

    private Integer getIntegerValue(final Element element, final String tagname) {
        final NodeList nodeList = element.getElementsByTagName(tagname);

        if (nodeList.getLength() == 0) {
            return null;
        }

        final Node node = nodeList.item(0);

        if (node.getFirstChild() == null) {
            return null;
        }

        final String value = node.getFirstChild().getNodeValue();

        try {
            return Integer.valueOf(value);

        } catch (final NumberFormatException e) {
            return null;
        }
    }

    private Long getLongValue(final Element element, final String tagname) {
        final NodeList nodeList = element.getElementsByTagName(tagname);

        if (nodeList.getLength() == 0) {
            return null;
        }

        final Node node = nodeList.item(0);

        if (node.getFirstChild() == null) {
            return null;
        }

        final String value = node.getFirstChild().getNodeValue();

        try {
            return Long.valueOf(value);

        } catch (final NumberFormatException e) {
            return null;
        }
    }

    private BigDecimal getBigDecimalValue(final Element element, final String tagname) {
        final String value = getStringValue(element, tagname);

        try {
            return new BigDecimal(value);
        } catch (final Exception e) {}

        return null;
    }

    private double getDoubleValue(final Element element, final String tagname) {
        final NodeList nodeList = element.getElementsByTagName(tagname);

        if (nodeList.getLength() == 0) {
            return 0;
        }

        final Node node = nodeList.item(0);

        if (node.getFirstChild() == null) {
            return 0;
        }

        final String value = node.getFirstChild().getNodeValue();

        return Double.valueOf(value).doubleValue();
    }

    private Date getDateValue(final Element element, final String tagname) {
        final NodeList nodeList = element.getElementsByTagName(tagname);

        if (nodeList.getLength() == 0) {
            return null;
        }

        final Node node = nodeList.item(0);

        if (node.getFirstChild() == null) {
            return null;
        }

        final String value = node.getFirstChild().getNodeValue();

        try {
            return PersistentXmlImpl.DATE_FORMAT.parse(value);

        } catch (final ParseException e) {
            return null;
        }
    }

    private Element getElement(final Element element, final String tagname) {
        final NodeList nodeList = element.getChildNodes();

        if (nodeList.getLength() == 0) {
            return null;
        }

        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                final Element ele = (Element) nodeList.item(i);
                if (ele.getTagName().equals(tagname)) {
                    return ele;
                }
            }
        }

        return null;
    }

    private void load(final Element root, final File file) {
        final Element settings = getElement(root, "settings");
        database = loadDatabase(settings);

        diagram = new ERDiagram(database);

        loadDBSetting(diagram, root);
        loadPageSetting(diagram, root);

        loadColor(diagram, root);
        loadDefaultColor(diagram, root);
        loadFont(diagram, root);

        final DiagramContents diagramContents = diagram.getDiagramContents();
        LoadContext context = loadDiagramContents(diagramContents, root, file);

        final int categoryIndex = this.getIntValue(root, "category_index");
        diagram.setCurrentCategory(null, categoryIndex);

        final double zoom = getDoubleValue(root, "zoom");
        diagram.setZoom(zoom);

        final int x = this.getIntValue(root, "x");
        final int y = this.getIntValue(root, "y");
        diagram.setLocation(x, y);

        loadChangeTrackingList(diagram.getChangeTrackingList(), root, context);

        diagram.getDiagramContents().getSettings().getTranslationSetting().load();
    }

    private String loadDatabase(final Element settingsElement) {
        String database = getStringValue(settingsElement, "database");
        if (database == null) {
            database = DBManagerFactory.getAllDBList().get(0);
        }

        return database;
    }

    private LoadContext loadDiagramContents(final DiagramContents diagramContents, final Element parent, final File file) {
        final Dictionary dictionary = diagramContents.getDictionary();

        final LoadContext context = new LoadContext(file, dictionary);

        loadDictionary(dictionary, parent, context);

        final Settings settings = diagramContents.getSettings();
        loadEnvironmentSetting(settings.getEnvironmentSetting(), parent, context);

        loadTablespaceSet(diagramContents.getTablespaceSet(), parent, context);

        final GroupSet columnGroups = diagramContents.getGroups();
        columnGroups.clear();

        loadColumnGroups(columnGroups, parent, context);
        loadContents(diagramContents.getContents(), parent, context);
        loadTestDataList(diagramContents.getTestDataList(), parent, context);
        loadSequenceSet(diagramContents.getSequenceSet(), parent);
        loadTriggerSet(diagramContents.getTriggerSet(), parent);

        loadSettings(settings, parent, context);

        context.resolve();

        return context;
    }

    private void loadSequenceSet(final SequenceSet sequenceSet, final Element parent) {
        final Element element = getElement(parent, "sequence_set");

        if (element != null) {
            final NodeList nodeList = element.getElementsByTagName("sequence");

            for (int i = 0; i < nodeList.getLength(); i++) {
                final Element sequenceElemnt = (Element) nodeList.item(i);
                final Sequence sequence = loadSequence(sequenceElemnt);

                sequenceSet.addObject(sequence);
            }
        }
    }

    private Sequence loadSequence(final Element element) {
        final Sequence sequence = new Sequence();

        sequence.setName(getStringValue(element, "name"));
        sequence.setSchema(getStringValue(element, "schema"));
        sequence.setIncrement(getIntegerValue(element, "increment"));
        sequence.setMinValue(getLongValue(element, "min_value"));
        sequence.setMaxValue(getBigDecimalValue(element, "max_value"));
        sequence.setStart(getLongValue(element, "start"));
        sequence.setCache(getIntegerValue(element, "cache"));
        sequence.setNocache(this.getBooleanValue(element, "nocache"));
        sequence.setCycle(this.getBooleanValue(element, "cycle"));
        sequence.setOrder(this.getBooleanValue(element, "order"));
        sequence.setDescription(getStringValue(element, "description"));
        sequence.setDataType(getStringValue(element, "data_type"));
        sequence.setDecimalSize(this.getIntValue(element, "decimal_size"));

        return sequence;
    }

    private void loadTriggerSet(final TriggerSet triggerSet, final Element parent) {
        final Element element = getElement(parent, "trigger_set");

        if (element != null) {
            final NodeList nodeList = element.getElementsByTagName("trigger");

            for (int i = 0; i < nodeList.getLength(); i++) {
                final Element triggerElemnt = (Element) nodeList.item(i);
                final Trigger trigger = loadTrigger(triggerElemnt);

                triggerSet.addObject(trigger);
            }
        }
    }

    private Trigger loadTrigger(final Element element) {
        final Trigger trigger = new Trigger();

        trigger.setName(getStringValue(element, "name"));
        trigger.setSchema(getStringValue(element, "schema"));
        trigger.setSql(getStringValue(element, "sql"));
        trigger.setDescription(getStringValue(element, "description"));

        return trigger;
    }

    private void loadTablespaceSet(final TablespaceSet tablespaceSet, final Element parent, final LoadContext context) {
        final Element element = getElement(parent, "tablespace_set");

        if (element != null) {
            final NodeList nodeList = element.getElementsByTagName("tablespace");

            for (int i = 0; i < nodeList.getLength(); i++) {
                final Element tablespaceElemnt = (Element) nodeList.item(i);
                final Tablespace tablespace = loadTablespace(tablespaceElemnt, context);
                if (tablespace != null) {
                    tablespaceSet.addObject(tablespace);
                }
            }
        }
    }

    private Tablespace loadTablespace(final Element element, final LoadContext context) {
        final String id = getStringValue(element, "id");

        final Tablespace tablespace = new Tablespace();
        tablespace.setName(getStringValue(element, "name"));

        final NodeList nodeList = element.getElementsByTagName("properties");

        for (int i = 0; i < nodeList.getLength(); i++) {
            final Element propertiesElemnt = (Element) nodeList.item(i);

            final String environmentId = getStringValue(propertiesElemnt, "environment_id");
            final Environment environment = context.environmentMap.get(environmentId);

            TablespaceProperties tablespaceProperties = null;

            if (DB2DBManager.ID.equals(database)) {
                tablespaceProperties = loadTablespacePropertiesDB2(propertiesElemnt);

            } else if (MySQLDBManager.ID.equals(database)) {
                tablespaceProperties = loadTablespacePropertiesMySQL(propertiesElemnt);

            } else if (OracleDBManager.ID.equals(database)) {
                tablespaceProperties = loadTablespacePropertiesOracle(propertiesElemnt);

            } else if (PostgresDBManager.ID.equals(database)) {
                tablespaceProperties = loadTablespacePropertiesPostgres(propertiesElemnt);

            }

            tablespace.putProperties(environment, tablespaceProperties);
        }

        if (id != null) {
            context.tablespaceMap.put(id, tablespace);
        }

        return tablespace;
    }

    private TablespaceProperties loadTablespacePropertiesDB2(final Element element) {
        final DB2TablespaceProperties properties = new DB2TablespaceProperties();

        properties.setBufferPoolName(getStringValue(element, "buffer_pool_name"));
        properties.setContainer(getStringValue(element, "container"));
        // properties.setContainerDevicePath(this.getStringValue(element,
        // "container_device_path"));
        // properties.setContainerDirectoryPath(this.getStringValue(element,
        // "container_directory_path"));
        // properties.setContainerFilePath(this.getStringValue(element,
        // "container_file_path"));
        // properties.setContainerPageNum(this.getStringValue(element,
        // "container_page_num"));
        properties.setExtentSize(getStringValue(element, "extent_size"));
        properties.setManagedBy(getStringValue(element, "managed_by"));
        properties.setPageSize(getStringValue(element, "page_size"));
        properties.setPrefetchSize(getStringValue(element, "prefetch_size"));
        properties.setType(getStringValue(element, "type"));

        return properties;
    }

    private TablespaceProperties loadTablespacePropertiesMySQL(final Element element) {
        final MySQLTablespaceProperties properties = new MySQLTablespaceProperties();

        properties.setDataFile(getStringValue(element, "data_file"));
        properties.setEngine(getStringValue(element, "engine"));
        properties.setExtentSize(getStringValue(element, "extent_size"));
        properties.setInitialSize(getStringValue(element, "initial_size"));
        properties.setLogFileGroup(getStringValue(element, "log_file_group"));

        return properties;
    }

    private TablespaceProperties loadTablespacePropertiesOracle(final Element element) {
        final OracleTablespaceProperties properties = new OracleTablespaceProperties();

        properties.setAutoExtend(this.getBooleanValue(element, "auto_extend"));
        properties.setAutoExtendMaxSize(getStringValue(element, "auto_extend_max_size"));
        properties.setAutoExtendSize(getStringValue(element, "auto_extend_size"));
        properties.setAutoSegmentSpaceManagement(this.getBooleanValue(element, "auto_segment_space_management"));
        properties.setDataFile(getStringValue(element, "data_file"));
        properties.setFileSize(getStringValue(element, "file_size"));
        properties.setInitial(getStringValue(element, "initial"));
        properties.setLogging(this.getBooleanValue(element, "logging"));
        properties.setMaxExtents(getStringValue(element, "max_extents"));
        properties.setMinExtents(getStringValue(element, "min_extents"));
        properties.setMinimumExtentSize(getStringValue(element, "minimum_extent_size"));
        properties.setNext(getStringValue(element, "next"));
        properties.setOffline(this.getBooleanValue(element, "offline"));
        properties.setPctIncrease(getStringValue(element, "pct_increase"));
        properties.setTemporary(this.getBooleanValue(element, "temporary"));

        return properties;
    }

    private TablespaceProperties loadTablespacePropertiesPostgres(final Element element) {
        final PostgresTablespaceProperties properties = new PostgresTablespaceProperties();

        properties.setLocation(getStringValue(element, "location"));
        properties.setOwner(getStringValue(element, "owner"));

        return properties;
    }

    private void loadChangeTrackingList(final ChangeTrackingList changeTrackingList, final Element parent, final LoadContext context) {
        final Element element = getElement(parent, "change_tracking_list");

        if (element != null) {
            final NodeList nodeList = element.getElementsByTagName("change_tracking");

            for (int i = 0; i < nodeList.getLength(); i++) {
                final Element changeTrackingElemnt = (Element) nodeList.item(i);
                final ChangeTracking changeTracking = loadChangeTracking(changeTrackingElemnt, context);

                changeTrackingList.addChangeTracking(changeTracking);
            }
        }
    }

    private ChangeTracking loadChangeTracking(final Element element, final LoadContext context) {
        final DiagramContents diagramContents = new DiagramContents();

        loadDiagramContents(diagramContents, element, context.file);

        final ChangeTracking changeTracking = new ChangeTracking(diagramContents);

        changeTracking.setComment(getStringValue(element, "comment"));
        //changeTracking.setUpdatedDate(getDateValue(element, "updated_date"));
        changeTracking.setUpdatedDate(context.getUpdatedDate());

        return changeTracking;
    }

    private void loadColumnGroups(final GroupSet columnGroups, final Element parent, final LoadContext context) {

        final Element element = getElement(parent, "column_groups");

        final NodeList nodeList = element.getElementsByTagName("column_group");

        for (int i = 0; i < nodeList.getLength(); i++) {
            final Element columnGroupElement = (Element) nodeList.item(i);

            final ColumnGroup columnGroup = new ColumnGroup();

            columnGroup.setGroupName(getStringValue(columnGroupElement, "group_name"));

            final List<Column> columns = loadColumns(columnGroupElement, context);
            for (final Column column : columns) {
                columnGroup.addColumn((NormalColumn) column);
            }

            columnGroups.add(columnGroup);

            final String id = getStringValue(columnGroupElement, "id");
            context.columnGroupMap.put(id, columnGroup);
        }

    }

    private void loadTestDataList(final List<TestData> testDataList, final Element parent, final LoadContext context) {

        final Element element = getElement(parent, "test_data_list");

        if (element != null) {
            final NodeList nodeList = element.getElementsByTagName("test_data");

            for (int i = 0; i < nodeList.getLength(); i++) {
                final Element testDataElement = (Element) nodeList.item(i);

                final TestData testData = new TestData();
                loadTestData(testData, testDataElement, context);
                testDataList.add(testData);
            }
        }
    }

    private void loadTestData(final TestData testData, final Element element, final LoadContext context) {

        testData.setName(getStringValue(element, "name"));
        testData.setExportOrder(this.getIntValue(element, "export_order"));

        final NodeList nodeList = element.getElementsByTagName("table_test_data");

        for (int i = 0; i < nodeList.getLength(); i++) {
            final Element tableTestDataElement = (Element) nodeList.item(i);

            final TableTestData tableTestData = new TableTestData();

            final String tableId = getStringValue(tableTestDataElement, "table_id");
            final ERTable table = (ERTable) context.nodeElementMap.get(tableId);
            if (table != null) {
                loadDirectTestData(tableTestData.getDirectTestData(), tableTestDataElement, context);
                loadRepeatTestData(tableTestData.getRepeatTestData(), tableTestDataElement, context);

                testData.putTableTestData(table, tableTestData);
            }
        }

    }

    private void loadDirectTestData(final DirectTestData directTestData, final Element parent, final LoadContext context) {
        final Element element = getElement(parent, "direct_test_data");

        final NodeList nodeList = element.getElementsByTagName("data");

        final List<Map<NormalColumn, String>> dataList = directTestData.getDataList();
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Element dataElement = (Element) nodeList.item(i);

            final NodeList columnNodeList = dataElement.getElementsByTagName("column_data");

            final Map<NormalColumn, String> data = new HashMap<NormalColumn, String>();

            for (int j = 0; j < columnNodeList.getLength(); j++) {
                final Element columnDataElement = (Element) columnNodeList.item(j);

                final String columnId = getStringValue(columnDataElement, "column_id");
                final NormalColumn column = context.columnMap.get(columnId);

                final String value = getStringValue(columnDataElement, "value");

                data.put(column, value);
            }

            dataList.add(data);
        }
    }

    private void loadRepeatTestData(final RepeatTestData repeatTestData, final Element parent, final LoadContext context) {
        final Element element = getElement(parent, "repeat_test_data");

        final int testDataNum = getIntegerValue(element, "test_data_num");
        repeatTestData.setTestDataNum(testDataNum);

        final Element dataDefListElement = getElement(element, "data_def_list");

        final NodeList nodeList = dataDefListElement.getElementsByTagName("data_def");

        for (int i = 0; i < nodeList.getLength(); i++) {
            final Element dataDefElement = (Element) nodeList.item(i);

            final String columnId = getStringValue(dataDefElement, "column_id");
            final NormalColumn column = context.columnMap.get(columnId);

            final RepeatTestDataDef dataDef = new RepeatTestDataDef();

            dataDef.setType(getStringValue(dataDefElement, "type"));
            dataDef.setRepeatNum(this.getIntValue(dataDefElement, "repeat_num"));
            dataDef.setTemplate(getStringValue(dataDefElement, "template"));
            dataDef.setFrom(getStringValue(dataDefElement, "from"));
            dataDef.setTo(getStringValue(dataDefElement, "to"));
            dataDef.setIncrement(getStringValue(dataDefElement, "increment"));
            dataDef.setSelects(getTagValues(dataDefElement, "select"));

            final Element modifiedValuesElement = getElement(dataDefElement, "modified_values");
            if (modifiedValuesElement != null) {
                final NodeList modifiedValueNodeList = modifiedValuesElement.getElementsByTagName("modified_value");

                for (int j = 0; j < modifiedValueNodeList.getLength(); j++) {
                    final Element modifiedValueNode = (Element) modifiedValueNodeList.item(j);

                    final Integer row = this.getIntValue(modifiedValueNode, "row");
                    final String value = getStringValue(modifiedValueNode, "value");

                    dataDef.setModifiedValue(row, value);
                }
            }

            repeatTestData.setDataDef(column, dataDef);
        }
    }

    private void loadDictionary(final Dictionary dictionary, final Element parent, final LoadContext context) {

        final Element element = getElement(parent, "dictionary");

        if (element != null) {
            final NodeList nodeList = element.getElementsByTagName("word");

            for (int i = 0; i < nodeList.getLength(); i++) {
                final Element wordElement = (Element) nodeList.item(i);

                loadWord(wordElement, context);
            }
        }
    }

    private Word loadWord(final Element element, final LoadContext context) {

        final String id = getStringValue(element, "id");

        final String type = getStringValue(element, "type");

        final TypeData typeData = new TypeData(getIntegerValue(element, "length"), getIntegerValue(element, "decimal"), this.getBooleanValue(element, "array"), getIntegerValue(element, "array_dimension"), this.getBooleanValue(element, "unsigned"), this.getBooleanValue(element, "zerofill"), this.getBooleanValue(element, "binary"), getStringValue(element, "args"), this.getBooleanValue(element, "char_semantics"));

        final Word word = new Word(Format.null2blank(getStringValue(element, "physical_name")), Format.null2blank(getStringValue(element, "logical_name")), SqlType.valueOfId(type), typeData, Format.null2blank(getStringValue(element, "description")), database);

        context.wordMap.put(id, word);

        return word;
    }

    private List<Column> loadColumns(final Element parent, final LoadContext context) {
        final List<Column> columns = new ArrayList<Column>();

        final Element element = getElement(parent, "columns");

        final NodeList groupList = element.getChildNodes();

        for (int i = 0; i < groupList.getLength(); i++) {
            if (groupList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            final Element columnElement = (Element) groupList.item(i);

            if ("column_group".equals(columnElement.getTagName())) {
                final ColumnGroup column = loadColumnGroup(columnElement, context);
                columns.add(column);

            } else if ("normal_column".equals(columnElement.getTagName())) {
                final NormalColumn column = loadNormalColumn(columnElement, context);
                columns.add(column);
            }
        }

        return columns;
    }

    private ColumnGroup loadColumnGroup(final Element element, final LoadContext context) {
        final String key = element.getFirstChild().getNodeValue();

        return context.columnGroupMap.get(key);
    }

    private NormalColumn loadNormalColumn(final Element element, final LoadContext context) {

        final String id = getStringValue(element, "id");

        final String type = getStringValue(element, "type");

        final String wordId = getStringValue(element, "word_id");

        Word word = context.wordMap.get(wordId);

        NormalColumn normalColumn = null;

        if (word == null) {
            word = new Word(getStringValue(element, "physical_name"), getStringValue(element, "logical_name"), SqlType.valueOfId(type), new TypeData(null, null, false, null, false, false, false, null, false), getStringValue(element, "description"), database);

            word = context.uniqueWordDictionary.getUniqueWord(word);
        }

        String defaultValue = getStringValue(element, "default_value");
        defaultValue = ResourceString.normalize(ResourceString.KEY_DEFAULT_VALUE_EMPTY_STRING, defaultValue);
        defaultValue = ResourceString.normalize(ResourceString.KEY_DEFAULT_VALUE_CURRENT_DATE_TIME, defaultValue);

        normalColumn = new NormalColumn(word, this.getBooleanValue(element, "not_null"), this.getBooleanValue(element, "primary_key"), this.getBooleanValue(element, "unique_key"), this.getBooleanValue(element, "auto_increment"), defaultValue, getStringValue(element, "constraint"), getStringValue(element, "unique_key_name"), getStringValue(element, "character_set"), getStringValue(element, "collation"));

        final Element autoIncrementSettingElement = getElement(element, "sequence");
        if (autoIncrementSettingElement != null) {
            final Sequence autoIncrementSetting = loadSequence(autoIncrementSettingElement);
            normalColumn.setAutoIncrementSetting(autoIncrementSetting);
        }

        boolean isForeignKey = false;

        final String[] relationIds = getTagValues(element, "relation");
        if (relationIds != null) {
            context.columnRelationMap.put(normalColumn, relationIds);
        }

        String[] referencedColumnIds = getTagValues(element, "referenced_column");
        final List<String> temp = new ArrayList<String>();
        for (final String str : referencedColumnIds) {
            try {
                if (str != null) {
                    // [ermasterr] Change id generation to model's hash
                    //Integer.parseInt(str);
                    temp.add(str);
                }

            } catch (final NumberFormatException e) {}
        }

        referencedColumnIds = temp.toArray(new String[temp.size()]);

        if (referencedColumnIds.length != 0) {
            context.columnReferencedColumnMap.put(normalColumn, referencedColumnIds);
            isForeignKey = true;
        }

        if (!isForeignKey) {
            context.dictionary.add(normalColumn);
        }

        context.columnMap.put(id, normalColumn);

        return normalColumn;
    }

    private void loadSettings(final Settings settings, final Element parent, final LoadContext context) {
        final Element element = getElement(parent, "settings");

        if (element != null) {
            settings.setDatabase(loadDatabase(element));
            settings.setCapital(this.getBooleanValue(element, "capital"));
            settings.setTableStyle(Format.null2blank(getStringValue(element, "table_style")));

            settings.setNotation(getStringValue(element, "notation"));
            settings.setNotationLevel(this.getIntValue(element, "notation_level"));
            settings.setNotationExpandGroup(this.getBooleanValue(element, "notation_expand_group", false));

            settings.setViewMode(this.getIntValue(element, "view_mode"));
            settings.setOutlineViewMode(this.getIntValue(element, "outline_view_mode"));
            settings.setViewOrderBy(this.getIntValue(element, "view_order_by"));

            settings.setAutoImeChange(this.getBooleanValue(element, "auto_ime_change", false));
            settings.setValidatePhysicalName(this.getBooleanValue(element, "validate_physical_name", true));
            settings.setUseBezierCurve(this.getBooleanValue(element, "use_bezier_curve", false));
            settings.setSuspendValidator(this.getBooleanValue(element, "suspend_validator", false));

            final CategorySetting categorySetting = settings.getCategorySetting();
            loadCategorySetting(categorySetting, element, context);

            // must load categorySetting before exportSetting
            final ExportSetting exportSetting = settings.getExportSetting();
            loadExportSetting(exportSetting, element, context);

            final TranslationSetting translationSetting = settings.getTranslationSetting();
            loadTranslationSetting(translationSetting, element, context);

            final ModelProperties modelProperties = settings.getModelProperties();
            loadModelProperties(modelProperties, element, context);

            loadTableProperties((TableProperties) settings.getTableViewProperties(), element, context);

        }
    }

    private void loadExportSetting(final ExportSetting exportSetting, final Element parent, final LoadContext context) {
        final Element element = getElement(parent, "export_setting");

        if (element != null) {
            loadExportDDLSetting(exportSetting.getExportDDLSetting(), element, context);
            loadExportExcelSetting(exportSetting.getExportExcelSetting(), element, context);
            loadExportHtmlSetting(exportSetting.getExportHtmlSetting(), element, context);
            loadExportImageSetting(exportSetting.getExportImageSetting(), element, context);
            loadExportJavaSetting(exportSetting.getExportJavaSetting(), element, context);
            loadExportTestDataSetting(exportSetting.getExportTestDataSetting(), element, context);
        }
    }

    private void loadExportDDLSetting(final ExportDDLSetting exportDDLSetting, final Element parent, final LoadContext context) {
        final Element element = getElement(parent, "export_ddl_setting");

        if (element != null) {
            exportDDLSetting.setDdlOutput(Format.null2blank(getStringValue(element, "output_path")));
            exportDDLSetting.setSrcFileEncoding(Format.null2blank(getStringValue(element, "encoding")));
            exportDDLSetting.setLineFeed(Format.null2blank(getStringValue(element, "line_feed")));
            exportDDLSetting.setOpenAfterSaved(this.getBooleanValue(element, "is_open_after_saved", true));

            final String environmentId = getStringValue(element, "environment_id");
            final Environment environment = context.environmentMap.get(environmentId);
            exportDDLSetting.setEnvironment(environment);

            final String categoryId = getStringValue(element, "category_id");
            final Category category = (Category) context.nodeElementMap.get(categoryId);
            exportDDLSetting.setCategory(category);

            loadDDLTarget(exportDDLSetting.getDdlTarget(), element, context);
        }
    }

    private void loadDDLTarget(final DDLTarget ddlTarget, final Element parent, final LoadContext context) {
        final Element element = getElement(parent, "ddl_target");

        if (element != null) {
            ddlTarget.createComment = this.getBooleanValue(element, "create_comment", true);
            ddlTarget.createForeignKey = this.getBooleanValue(element, "create_foreignKey", true);
            ddlTarget.createIndex = this.getBooleanValue(element, "create_index", true);
            ddlTarget.createSequence = this.getBooleanValue(element, "create_sequence", true);
            ddlTarget.createTable = this.getBooleanValue(element, "create_table", true);
            ddlTarget.createTablespace = this.getBooleanValue(element, "create_tablespace", true);
            ddlTarget.createTrigger = this.getBooleanValue(element, "create_trigger", true);
            ddlTarget.createView = this.getBooleanValue(element, "create_view", true);

            ddlTarget.dropIndex = this.getBooleanValue(element, "drop_index", true);
            ddlTarget.dropSequence = this.getBooleanValue(element, "drop_sequence", true);
            ddlTarget.dropTable = this.getBooleanValue(element, "drop_table", true);
            ddlTarget.dropTablespace = this.getBooleanValue(element, "drop_tablespace", true);
            ddlTarget.dropTrigger = this.getBooleanValue(element, "drop_trigger", true);
            ddlTarget.dropView = this.getBooleanValue(element, "drop_view", true);

            ddlTarget.inlineColumnComment = this.getBooleanValue(element, "inline_column_comment", true);
            ddlTarget.inlineTableComment = this.getBooleanValue(element, "inline_table_comment", true);

            ddlTarget.commentValueDescription = this.getBooleanValue(element, "comment_value_description", true);
            ddlTarget.commentValueLogicalName = this.getBooleanValue(element, "comment_value_logical_name", false);
            ddlTarget.commentValueLogicalNameDescription = this.getBooleanValue(element, "comment_value_logical_name_description", false);
            ddlTarget.commentReplaceLineFeed = this.getBooleanValue(element, "comment_replace_line_feed");
            ddlTarget.commentReplaceString = getStringValue(element, "comment_replace_string");
        }
    }

    private void loadExportExcelSetting(final ExportExcelSetting exportExcelSetting, final Element parent, final LoadContext context) {
        final Element element = getElement(parent, "export_excel_setting");

        if (element != null) {
            final String categoryId = getStringValue(element, "category_id");
            final Category category = (Category) context.nodeElementMap.get(categoryId);
            exportExcelSetting.setCategory(category);

            exportExcelSetting.setExcelOutput(Format.null2blank(getStringValue(element, "output_path")));
            exportExcelSetting.setExcelTemplate(Format.null2blank(getStringValue(element, "template")));
            exportExcelSetting.setExcelTemplatePath(Format.null2blank(getStringValue(element, "template_path")));
            exportExcelSetting.setUsedDefaultTemplateLang(Format.null2blank(getStringValue(element, "used_default_template_lang")));
            exportExcelSetting.setImageOutput(Format.null2blank(getStringValue(element, "image_output")));
            exportExcelSetting.setOpenAfterSaved(this.getBooleanValue(element, "is_open_after_saved", true));
            exportExcelSetting.setPutERDiagramOnExcel(this.getBooleanValue(element, "is_put_diagram", true));
            exportExcelSetting.setUseLogicalNameAsSheet(this.getBooleanValue(element, "is_use_logical_name", true));
        }
    }

    private void loadExportHtmlSetting(final ExportHtmlSetting exportHtmlSetting, final Element parent, final LoadContext context) {
        final Element element = getElement(parent, "export_html_setting");

        if (element != null) {
            exportHtmlSetting.setOutputDir(getStringValue(element, "output_dir"));
            // exportHtmlSetting.setSrcFileEncoding(Format.null2blank(this
            // .getStringValue(element, "file_encoding")));
            exportHtmlSetting.setWithCategoryImage(this.getBooleanValue(element, "with_category_image", true));
            exportHtmlSetting.setWithImage(this.getBooleanValue(element, "with_image", true));
            exportHtmlSetting.setOpenAfterSaved(this.getBooleanValue(element, "is_open_after_saved", true));
        }
    }

    private void loadExportImageSetting(final ExportImageSetting exportImageSetting, final Element parent, final LoadContext context) {
        final Element element = getElement(parent, "export_image_setting");

        if (element != null) {
            exportImageSetting.setOutputFilePath(getStringValue(element, "output_file_path"));
            exportImageSetting.setCategoryDirPath(getStringValue(element, "category_dir_path"));
            exportImageSetting.setWithCategoryImage(this.getBooleanValue(element, "with_category_image", true));
            exportImageSetting.setOpenAfterSaved(this.getBooleanValue(element, "is_open_after_saved", true));
        }
    }

    private void loadExportJavaSetting(final ExportJavaSetting exportJavaSetting, final Element parent, final LoadContext context) {
        final Element element = getElement(parent, "export_java_setting");

        if (element != null) {
            exportJavaSetting.setJavaOutput(getStringValue(element, "java_output"));
            exportJavaSetting.setPackageName(Format.null2blank(getStringValue(element, "package_name")));
            exportJavaSetting.setClassNameSuffix(Format.null2blank(getStringValue(element, "class_name_suffix")));
            exportJavaSetting.setSrcFileEncoding(getStringValue(element, "src_file_encoding"));
            exportJavaSetting.setWithHibernate(this.getBooleanValue(element, "with_hibernate", false));
        }
    }

    private void loadExportTestDataSetting(final ExportTestDataSetting exportTestDataSetting, final Element parent, final LoadContext context) {
        final Element element = getElement(parent, "export_testdata_setting");

        if (element != null) {
            exportTestDataSetting.setExportFileEncoding(getStringValue(element, "file_encoding"));
            exportTestDataSetting.setExportFilePath(getStringValue(element, "file_path"));
            exportTestDataSetting.setExportFormat(this.getIntValue(element, "format"));
        }
    }

    private void loadCategorySetting(final CategorySetting categorySetting, final Element parent, final LoadContext context) {
        final Element element = getElement(parent, "category_settings");
        categorySetting.setFreeLayout(this.getBooleanValue(element, "free_layout"));
        categorySetting.setShowReferredTables(this.getBooleanValue(element, "show_referred_tables"));

        final Element categoriesElement = getElement(element, "categories");

        final NodeList nodeList = categoriesElement.getChildNodes();

        final List<Category> selectedCategories = new ArrayList<Category>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            final Element categoryElement = (Element) nodeList.item(i);

            final Category category = new Category();

            loadNodeElement(category, categoryElement, context);
            category.setName(getStringValue(categoryElement, "name"));
            final boolean isSelected = this.getBooleanValue(categoryElement, "selected");

            final String[] keys = getTagValues(categoryElement, "node_element");

            final List<NodeElement> nodeElementList = new ArrayList<NodeElement>();

            for (final String key : keys) {
                final NodeElement nodeElement = context.nodeElementMap.get(key);
                if (nodeElement != null) {
                    nodeElementList.add(nodeElement);
                }
            }

            category.setContents(nodeElementList);
            categorySetting.addCategory(category);

            if (isSelected) {
                selectedCategories.add(category);
            }
        }

        categorySetting.setSelectedCategories(selectedCategories);
    }

    private void loadTranslationSetting(final TranslationSetting translationSetting, final Element parent, final LoadContext context) {
        final Element element = getElement(parent, "translation_settings");
        if (element != null) {
            translationSetting.setUse(this.getBooleanValue(element, "use"));

            final Element translationsElement = getElement(element, "translations");

            final NodeList nodeList = translationsElement.getChildNodes();

            final List<String> selectedTranslations = new ArrayList<String>();

            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }

                final Element translationElement = (Element) nodeList.item(i);

                selectedTranslations.add(getStringValue(translationElement, "name"));
            }

            translationSetting.setSelectedTranslations(selectedTranslations);
        }
    }

    private void loadEnvironmentSetting(final EnvironmentSetting environmentSetting, final Element parent, final LoadContext context) {
        final Element settingElement = getElement(parent, "settings");
        final Element element = getElement(settingElement, "environment_setting");

        final List<Environment> environmentList = new ArrayList<Environment>();

        if (element != null) {
            final NodeList nodeList = element.getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }

                final Element environmentElement = (Element) nodeList.item(i);

                final String id = getStringValue(environmentElement, "id");
                final String name = getStringValue(environmentElement, "name");
                final Environment environment = new Environment(name);

                environmentList.add(environment);
                context.environmentMap.put(id, environment);
            }
        }

        if (environmentList.isEmpty()) {
            final Environment environment = new Environment(ResourceString.getResourceString("label.default"));
            environmentList.add(environment);
            context.environmentMap.put("", environment);
        }

        environmentSetting.setEnvironments(environmentList);
    }

    private void loadModelProperties(final ModelProperties modelProperties, final Element parent, final LoadContext context) {
        final Element element = getElement(parent, "model_properties");

        loadLocation(modelProperties, element);
        loadColor(modelProperties, element);
        loadFont(modelProperties, element);

        modelProperties.setDisplay(this.getBooleanValue(element, "display", false));
        modelProperties.setCreationDate(getDateValue(element, "creation_date"));
        //modelProperties.setUpdatedDate(getDateValue(element, "updated_date"));
        modelProperties.setUpdatedDate(context.getUpdatedDate());

        final NodeList nodeList = element.getElementsByTagName("model_property");

        for (int i = 0; i < nodeList.getLength(); i++) {
            final Element propertyElement = (Element) nodeList.item(i);

            final NameValue nameValue = new NameValue(getStringValue(propertyElement, "name"), getStringValue(propertyElement, "value"));

            modelProperties.addProperty(nameValue);
        }
    }

    private void loadLocation(final NodeElement nodeElement, final Element element) {

        final int x = this.getIntValue(element, "x");
        final int y = this.getIntValue(element, "y");
        final int width = this.getIntValue(element, "width");
        final int height = this.getIntValue(element, "height");

        nodeElement.setLocation(new Location(x, y, width, height));
    }

    private void loadFont(final ViewableModel viewableModel, final Element element) {
        final String fontName = getStringValue(element, "font_name");
        final int fontSize = this.getIntValue(element, "font_size");

        if (!Check.isEmptyTrim(fontName)) {
            viewableModel.setFontName(fontName);
        }
        viewableModel.setFontSize(fontSize);
    }

    private void loadContents(final NodeSet contents, final Element parent, final LoadContext context) {
        final Element element = getElement(parent, "contents");

        final NodeList nodeList = element.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            final Node node = nodeList.item(i);

            if ("table".equals(node.getNodeName())) {
                final ERTable table = loadTable((Element) node, context);
                contents.addNodeElement(table);

            } else if ("view".equals(node.getNodeName())) {
                final View view = loadView((Element) node, context);
                contents.addNodeElement(view);

            } else if ("note".equals(node.getNodeName())) {
                final Note note = loadNote((Element) node, context);
                contents.addNodeElement(note);

            } else if ("image".equals(node.getNodeName())) {
                final InsertedImage insertedImage = loadInsertedImage((Element) node, context);
                contents.addNodeElement(insertedImage);
            }
        }
    }

    private ERTable loadTable(final Element element, final LoadContext context) {
        final ERTable table = new ERTable();

        table.setDiagram(diagram);

        loadNodeElement(table, element, context);
        table.setPhysicalName(getStringValue(element, "physical_name"));
        table.setLogicalName(getStringValue(element, "logical_name"));
        table.setDescription(getStringValue(element, "description"));
        table.setConstraint(getStringValue(element, "constraint"));
        table.setPrimaryKeyName(getStringValue(element, "primary_key_name"));
        table.setOption(getStringValue(element, "option"));

        final List<Column> columns = loadColumns(element, context);
        table.setColumns(columns);

        final List<Index> indexes = loadIndexes(element, table, context);
        table.setIndexes(indexes);

        final List<ComplexUniqueKey> complexUniqueKeyList = loadComplexUniqueKeyList(element, table, context);
        table.setComplexUniqueKeyList(complexUniqueKeyList);

        loadTableProperties((TableProperties) table.getTableViewProperties(), element, context);

        return table;
    }

    private View loadView(final Element element, final LoadContext context) {
        final View view = new View();

        view.setDiagram(diagram);

        loadNodeElement(view, element, context);
        view.setPhysicalName(getStringValue(element, "physical_name"));
        view.setLogicalName(getStringValue(element, "logical_name"));
        view.setDescription(getStringValue(element, "description"));
        view.setSql(getStringValue(element, "sql"));

        final List<Column> columns = loadColumns(element, context);
        view.setColumns(columns);

        loadViewProperties((ViewProperties) view.getTableViewProperties(), element, context);

        return view;
    }

    private List<Index> loadIndexes(final Element parent, final ERTable table, final LoadContext context) {
        final List<Index> indexes = new ArrayList<Index>();

        final Element element = getElement(parent, "indexes");

        final NodeList nodeList = element.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            final Element indexElement = (Element) nodeList.item(i);

            String type = getStringValue(indexElement, "type");
            if ("null".equals(type)) {
                type = null;
            }

            final Index index = new Index(table, getStringValue(indexElement, "name"), this.getBooleanValue(indexElement, "non_unique", true), type, getStringValue(indexElement, "description"));

            index.setFullText(this.getBooleanValue(indexElement, "full_text", false));

            loadIndexColumns(index, indexElement, context);

            indexes.add(index);
        }

        return indexes;
    }

    private void loadIndexColumns(final Index index, final Element parent, final LoadContext context) {
        final Element element = getElement(parent, "columns");
        final NodeList nodeList = element.getChildNodes();

        final List<Boolean> descs = new ArrayList<Boolean>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            final Element columnElement = (Element) nodeList.item(i);

            final String id = getStringValue(columnElement, "id");
            final NormalColumn column = context.columnMap.get(id);

            final Boolean desc = new Boolean(this.getBooleanValue(columnElement, "desc", true));

            index.addColumn(column);
            descs.add(desc);
        }

        index.setDescs(descs);
    }

    private List<ComplexUniqueKey> loadComplexUniqueKeyList(final Element parent, final ERTable table, final LoadContext context) {
        final List<ComplexUniqueKey> complexUniqueKeyList = new ArrayList<ComplexUniqueKey>();

        final Element element = getElement(parent, "complex_unique_key_list");
        if (element == null) {
            return complexUniqueKeyList;
        }

        final NodeList nodeList = element.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            final Element complexUniqueKeyElement = (Element) nodeList.item(i);

            final String id = getStringValue(complexUniqueKeyElement, "id");
            final String name = getStringValue(complexUniqueKeyElement, "name");

            final ComplexUniqueKey complexUniqueKey = new ComplexUniqueKey(name);

            loadComplexUniqueKeyColumns(complexUniqueKey, complexUniqueKeyElement, context);

            complexUniqueKeyList.add(complexUniqueKey);

            context.complexUniqueKeyMap.put(id, complexUniqueKey);
        }

        return complexUniqueKeyList;
    }

    private void loadComplexUniqueKeyColumns(final ComplexUniqueKey complexUniqueKey, final Element parent, final LoadContext context) {
        final Element element = getElement(parent, "columns");
        final NodeList nodeList = element.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            final Element columnElement = (Element) nodeList.item(i);

            final String id = getStringValue(columnElement, "id");
            final NormalColumn column = context.columnMap.get(id);

            complexUniqueKey.addColumn(column);
        }
    }

    private void loadTableProperties(final TableProperties tableProperties, final Element parent, final LoadContext context) {
        final Element element = getElement(parent, "table_properties");

        final String tablespaceId = getStringValue(element, "tablespace_id");
        final Tablespace tablespace = context.tablespaceMap.get(tablespaceId);
        tableProperties.setTableSpace(tablespace);

        tableProperties.setSchema(getStringValue(element, "schema"));

        if (tableProperties instanceof MySQLTableProperties) {
            loadTablePropertiesMySQL((MySQLTableProperties) tableProperties, element);

        } else if (tableProperties instanceof PostgresTableProperties) {
            loadTablePropertiesPostgres((PostgresTableProperties) tableProperties, element);

        }
    }

    private void loadTablePropertiesMySQL(final MySQLTableProperties tableProperties, final Element element) {

        tableProperties.setCharacterSet(getStringValue(element, "character_set"));
        tableProperties.setCollation(getStringValue(element, "collation"));
        tableProperties.setStorageEngine(getStringValue(element, "storage_engine"));
        tableProperties.setPrimaryKeyLengthOfText(getIntegerValue(element, "primary_key_length_of_text"));
    }

    private void loadTablePropertiesPostgres(final PostgresTableProperties tableProperties, final Element element) {
        tableProperties.setWithoutOIDs(this.getBooleanValue(element, "without_oids"));
    }

    private void loadViewProperties(final ViewProperties viewProperties, final Element parent, final LoadContext context) {
        final Element element = getElement(parent, "view_properties");

        if (element != null) {
            final String tablespaceId = getStringValue(element, "tablespace_id");
            final Tablespace tablespace = context.tablespaceMap.get(tablespaceId);
            viewProperties.setTableSpace(tablespace);

            viewProperties.setSchema(getStringValue(element, "schema"));
        }
    }

    private Note loadNote(final Element element, final LoadContext context) {
        final Note note = new Note();

        note.setText(getStringValue(element, "text"));
        loadNodeElement(note, element, context);

        return note;
    }

    private InsertedImage loadInsertedImage(final Element element, final LoadContext context) {
        final InsertedImage insertedImage = new InsertedImage();

        insertedImage.setBase64EncodedData(getStringValue(element, "data"));
        insertedImage.setHue(this.getIntValue(element, "hue"));
        insertedImage.setSaturation(this.getIntValue(element, "saturation"));
        insertedImage.setBrightness(this.getIntValue(element, "brightness"));
        insertedImage.setAlpha(this.getIntValue(element, "alpha", 255));
        insertedImage.setFixAspectRatio(this.getBooleanValue(element, "fix_aspect_ratio", true));

        loadNodeElement(insertedImage, element, context);

        return insertedImage;
    }

    private void loadNodeElement(final NodeElement nodeElement, final Element element, final LoadContext context) {
        final String id = getStringValue(element, "id");

        loadLocation(nodeElement, element);
        loadColor(nodeElement, element);
        loadFont(nodeElement, element);

        context.nodeElementMap.put(id, nodeElement);

        loadConnections(element, context);
    }

    private void loadConnections(final Element parent, final LoadContext context) {
        final Element element = getElement(parent, "connections");

        if (element != null) {
            final NodeList nodeList = element.getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }

                final Element connectionElement = (Element) nodeList.item(i);

                if ("relation".equals(connectionElement.getTagName())) {
                    loadRelation(connectionElement, context);

                } else if ("comment_connection".equals(connectionElement.getTagName())) {
                    loadCommentConnection(connectionElement, context);
                }
            }
        }
    }

    private void loadRelation(final Element element, final LoadContext context) {
        final boolean referenceForPK = this.getBooleanValue(element, "reference_for_pk");
        final Relation connection = new Relation(referenceForPK, null, null, true, true);

        this.load(connection, element, context);

        connection.setChildCardinality(getStringValue(element, "child_cardinality"));
        connection.setParentCardinality(getStringValue(element, "parent_cardinality"));
        connection.setName(getStringValue(element, "name"));
        connection.setOnDeleteAction(getStringValue(element, "on_delete_action"));
        connection.setOnUpdateAction(getStringValue(element, "on_update_action"));

        final String referencedComplexUniqueKeyId = getStringValue(element, "referenced_complex_unique_key");
        if (!"null".equals(referencedComplexUniqueKeyId)) {
            context.referencedComplexUniqueKeyMap.put(connection, referencedComplexUniqueKeyId);
        }
        final String referencedColumnId = getStringValue(element, "referenced_column");
        if (!"null".equals(referencedColumnId)) {
            context.referencedColumnMap.put(connection, referencedColumnId);
        }

        loadConnectionColor(connection, element);
    }

    private void loadCommentConnection(final Element element, final LoadContext context) {
        final CommentConnection connection = new CommentConnection();

        this.load(connection, element, context);
    }

    private void load(final ConnectionElement connection, final Element element, final LoadContext context) {
        final String id = getStringValue(element, "id");

        context.connectionMap.put(id, connection);

        final String source = getStringValue(element, "source");
        final String target = getStringValue(element, "target");

        context.connectionSourceMap.put(connection, source);
        context.connectionTargetMap.put(connection, target);

        connection.setSourceLocationp(this.getIntValue(element, "source_xp"), this.getIntValue(element, "source_yp"));
        connection.setTargetLocationp(this.getIntValue(element, "target_xp"), this.getIntValue(element, "target_yp"));

        final NodeList nodeList = element.getElementsByTagName("bendpoint");

        for (int i = 0; i < nodeList.getLength(); i++) {
            final Element bendPointElement = (Element) nodeList.item(i);

            final Bendpoint bendpoint = new Bendpoint(this.getIntValue(bendPointElement, "x"), this.getIntValue(bendPointElement, "y"));

            bendpoint.setRelative(this.getBooleanValue(bendPointElement, "relative"));

            connection.addBendpoint(i, bendpoint);
        }

        loadConnectionColor(connection, element);
    }

    private void loadDBSetting(final ERDiagram diagram, final Element element) {
        final Element dbSettingElement = getElement(element, "dbsetting");

        if (dbSettingElement != null) {
            final String dbsystem = getStringValue(element, "dbsystem");
            final String server = getStringValue(element, "server");
            final int port = this.getIntValue(element, "port");
            final String database = getStringValue(element, "database");
            final String user = getStringValue(element, "user");
            final String password = getStringValue(element, "password");
            boolean useDefaultDriver = this.getBooleanValue(element, "use_default_driver", true);
            if (StandardSQLDBManager.ID.equals(dbsystem)) {
                useDefaultDriver = false;
            }

            final String url = getStringValue(element, "url");
            final String driverClassName = getStringValue(element, "driver_class_name");

            final DBSetting dbSetting = new DBSetting(dbsystem, server, port, database, user, password, useDefaultDriver, url, driverClassName);
            diagram.setDbSetting(dbSetting);
        }
    }

    private void loadPageSetting(final ERDiagram diagram, final Element element) {
        final Element dbSettingElement = getElement(element, "page_setting");

        if (dbSettingElement != null) {
            final boolean directionHorizontal = this.getBooleanValue(element, "direction_horizontal");
            final int scale = this.getIntValue(element, "scale");
            final String paperSize = getStringValue(element, "paper_size");
            final int topMargin = this.getIntValue(element, "top_margin");
            final int leftMargin = this.getIntValue(element, "left_margin");
            final int bottomMargin = this.getIntValue(element, "bottom_margin");
            final int rightMargin = this.getIntValue(element, "right_margin");

            final PageSetting pageSetting = new PageSetting(directionHorizontal, scale, paperSize, topMargin, rightMargin, bottomMargin, leftMargin);
            diagram.setPageSetting(pageSetting);
        }
    }

    private void loadConnectionColor(final ConnectionElement model, final Element element) {
        final int[] rgb = new int[] {0, 0, 0};
        final Element color = getElement(element, "color");

        if (color != null) {
            rgb[0] = this.getIntValue(color, "r");
            rgb[1] = this.getIntValue(color, "g");
            rgb[2] = this.getIntValue(color, "b");
        }

        model.setColor(rgb[0], rgb[1], rgb[2]);
    }

    private void loadColor(final ViewableModel model, final Element element) {
        final int[] rgb = new int[] {255, 255, 255};
        final Element color = getElement(element, "color");

        if (color != null) {
            rgb[0] = this.getIntValue(color, "r");
            rgb[1] = this.getIntValue(color, "g");
            rgb[2] = this.getIntValue(color, "b");
        }

        model.setColor(rgb[0], rgb[1], rgb[2]);
    }

    private void loadDefaultColor(final ERDiagram diagram, final Element element) {
        final int[] rgb = new int[] {255, 255, 255};
        final Element color = getElement(element, "default_color");

        if (color != null) {
            rgb[0] = this.getIntValue(color, "r");
            rgb[1] = this.getIntValue(color, "g");
            rgb[2] = this.getIntValue(color, "b");
        }

        diagram.setDefaultColor(rgb[0], rgb[1], rgb[2]);
    }
}
