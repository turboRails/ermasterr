package org.insightech.er.editor.model.dbimport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.insightech.er.util.Format;

public class DBObjectSet implements Serializable {

    private static final long serialVersionUID = 5632573903492090359L;

    private final Map<String, List<DBObject>> schemaDbObjectListMap;

    private final List<DBObject> tablespaceList;

    private final List<DBObject> noteList;

    private final List<DBObject> groupList;

    public DBObjectSet() {
        schemaDbObjectListMap = new TreeMap<String, List<DBObject>>();
        tablespaceList = new ArrayList<DBObject>();
        noteList = new ArrayList<DBObject>();
        groupList = new ArrayList<DBObject>();
    }

    public Map<String, List<DBObject>> getSchemaDbObjectListMap() {
        return schemaDbObjectListMap;
    }

    public List<DBObject> getTablespaceList() {
        return tablespaceList;
    }

    public List<DBObject> getNoteList() {
        return noteList;
    }

    public List<DBObject> getGroupList() {
        return groupList;
    }

    public void addAll(final List<DBObject> dbObjectList) {
        for (final DBObject dbObject : dbObjectList) {
            add(dbObject);
        }
    }

    public void add(final DBObject dbObject) {
        if (DBObject.TYPE_TABLESPACE.equals(dbObject.getType())) {
            tablespaceList.add(dbObject);

        } else if (DBObject.TYPE_NOTE.equals(dbObject.getType())) {
            noteList.add(dbObject);

        } else if (DBObject.TYPE_GROUP.equals(dbObject.getType())) {
            groupList.add(dbObject);

        } else {
            final String schema = Format.null2blank(dbObject.getSchema());
            List<DBObject> dbObjectList = schemaDbObjectListMap.get(schema);
            if (dbObjectList == null) {
                dbObjectList = new ArrayList<DBObject>();
                schemaDbObjectListMap.put(schema, dbObjectList);
            }

            dbObjectList.add(dbObject);
        }
    }

}
