package org.insightech.er.editor.persistent.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.image.InsertedImage;
import org.insightech.er.editor.model.diagram_contents.element.node.note.Note;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.editor.model.settings.Environment;

// [ermasterr] Add unique id generator for models
final class IdGenerator {
    private static final String DELIM = "_";

    static String columnGroupId(final ColumnGroup columnGroup) {
        return sha1(makeSeed(IdSeedPrefix.COLUMN_GROUP, columnGroup.getGroupName()));
    }

    static String columnGroupColumnId(final ColumnGroup columnGroup, final NormalColumn normalColumn) {
        return sha1(makeSeed(IdSeedPrefix.COLUMN, columnGroup.getName(), normalColumn.getPhysicalName()));
    }

    static String nodeElementId(final NodeElement content) {
        String s = "";
        if (content instanceof ERTable) {
            ERTable table = (ERTable) content;
            if (table.getPhysicalName() != null) {
                s = table.getPhysicalName();
            }
        } else
        if (content instanceof Note) {
            Note note = (Note) content;
            if (note.getText() != null) {
                s = note.getText();
            }
        } else
        if (content instanceof InsertedImage) {
            InsertedImage image = (InsertedImage) content;
            if (image.getBase64EncodedData() != null) {
                s = image.getBase64EncodedData();
            }
        } else {
            throw new IllegalArgumentException(content.toString());
        }

        return sha1(makeSeed(IdSeedPrefix.NODE_ELEMENT,
                s,
                String.valueOf(content.getLocation().hashCode())));
    }

    static String connectionId(final NodeElement content, final ConnectionElement connectionElement) {
        String name = "";
        if (connectionElement instanceof Relation) {
            Relation relation = (Relation) connectionElement;
            if (relation.getName() != null) {
                name = relation.getName();
            }
        }
        int position = connectionElement.getSourceXp() + connectionElement.getSourceYp() +
                connectionElement.getTargetXp() + connectionElement.getTargetYp() +
                connectionElement.getSource().getLocation().hashCode() +
                connectionElement.getTarget().getLocation().hashCode() +
                connectionElement.getSource().getActualLocation().hashCode() +
                connectionElement.getTarget().getActualLocation().hashCode();
        return sha1(makeSeed(IdSeedPrefix.CONNECTION,
                nodeElementId(content), name, String.valueOf(position)));
    }

    static String tableColumnId(final ERTable table, final NormalColumn normalColumn) {
        return sha1(makeSeed(IdSeedPrefix.COLUMN, table.getPhysicalName(), normalColumn.getPhysicalName()));
    }

    static String complexUniqueKeyId(final ERTable table, final ComplexUniqueKey complexUniqueKey) {
        return sha1(makeSeed(IdSeedPrefix.COMPLEX_UNIQUE_KEY, table.getPhysicalName(), complexUniqueKey.getUniqueKeyName()));
    }

    static String categoryId(final Category category) {
        return sha1(makeSeed(IdSeedPrefix.NODE_ELEMENT, category.getName()));
    }

    static String wordId(final Word word) {
        String physicalName = word.getPhysicalName();
        String logicalName = word.getLogicalName();
        String type = word.getType() == null ? "" : word.getType().toString();
        String description = word.getDescription();
        String typeData = String.valueOf(word.getTypeData().hashCode());
        return sha1(makeSeed(IdSeedPrefix.WORD, physicalName, logicalName, type, description, typeData));
    }

    static String tablespaceId(final Tablespace tablespace) {
        return sha1(makeSeed(IdSeedPrefix.TABLESPACE, tablespace.getName()));
    }

    static String environmentId(final Environment environment) {
        return sha1(makeSeed(IdSeedPrefix.ENVIRONMENT, environment.getName()));
    }

    private static enum IdSeedPrefix {
        COLUMN_GROUP,
        COLUMN,
        NODE_ELEMENT,
        CONNECTION,
        COMPLEX_UNIQUE_KEY,
        WORD,
        TABLESPACE,
        ENVIRONMENT,
        ;
    }

    private static String makeSeed(IdSeedPrefix prefix, final String... s) {
        String[] name = new String[1 + s.length];
        name[0] = prefix.toString();
        System.arraycopy(s, 0, name, 1, s.length);
        return String.join(DELIM, name);
    }

    private static String sha1(String seed) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            return generateHash(md, seed);
        } catch (NoSuchAlgorithmException ignore) {
            throw new IllegalStateException();
        }
    }

    private static String generateHash(MessageDigest md, String seed) {
        md.update(seed.getBytes());
        byte[] bytes = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String hex = String.format("%02x", b);
            sb.append(hex);
        }
        return sb.toString();
    }
    
}
