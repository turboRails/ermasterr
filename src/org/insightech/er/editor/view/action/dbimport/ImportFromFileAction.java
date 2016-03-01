package org.insightech.er.editor.view.action.dbimport;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbimport.DBObject;
import org.insightech.er.editor.model.dbimport.DBObjectSet;
import org.insightech.er.editor.model.diagram_contents.DiagramContents;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeSet;
import org.insightech.er.editor.model.diagram_contents.element.node.note.Note;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.UniqueWordDictionary;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GroupSet;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.SequenceSet;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceSet;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.TriggerSet;
import org.insightech.er.editor.model.edit.CopyManager;
import org.insightech.er.editor.persistent.Persistent;
import org.insightech.er.editor.view.dialog.dbimport.AbstractSelectImportedObjectDialog;
import org.insightech.er.editor.view.dialog.dbimport.SelectImportedObjectFromFileDialog;

public class ImportFromFileAction extends AbstractImportAction {

    public static final String ID = ImportFromFileAction.class.getName();

    private ERDiagram loadedDiagram;

    public ImportFromFileAction(final ERDiagramEditor editor) {
        super(ID, ResourceString.getResourceString("action.title.import.file"), editor);
        setImageDescriptor(ERDiagramActivator.getImageDescriptor(ImageKey.TABLE));
    }

    protected DBObjectSet preImport() throws Exception {
        final String fileName = getLoadFilePath(getEditorPart());
        if (fileName == null) {
            return null;
        }

        final Persistent persistent = Persistent.getInstance();

        final Path path = new Path(fileName);

        InputStream in = null;

        try {
            final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);

            if (file == null || !file.exists()) {
                final File realFile = path.toFile();
                if (realFile == null || !realFile.exists()) {
                    ERDiagramActivator.showErrorDialog("error.import.file");
                    return null;
                }

                in = new FileInputStream(realFile);

            } else {
                if (!file.isSynchronized(IResource.DEPTH_ONE)) {
                    file.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
                }

                in = file.getContents();
            }

            loadedDiagram = persistent.load(in);

        } finally {
            in.close();
        }

        return getAllObjects(loadedDiagram);
    }

    protected AbstractSelectImportedObjectDialog createSelectImportedObjectDialog(final DBObjectSet dbObjectSet) {
        final ERDiagram diagram = getDiagram();

        return new SelectImportedObjectFromFileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), diagram, dbObjectSet);
    }

    protected String getLoadFilePath(final IEditorPart editorPart) {

        final FileDialog fileDialog = new FileDialog(editorPart.getEditorSite().getShell(), SWT.OPEN);

        fileDialog.setFilterPath(getBasePath());

        final String[] filterExtensions = getFilterExtensions();
        fileDialog.setFilterExtensions(filterExtensions);

        return fileDialog.open();
    }

    protected String[] getFilterExtensions() {
        return new String[] {"*.erm"};
    }

    private DBObjectSet getAllObjects(final ERDiagram loadedDiagram) {
        final DBObjectSet dbObjects = new DBObjectSet();

        for (final ERTable table : loadedDiagram.getDiagramContents().getContents().getTableSet()) {
            final DBObject dbObject = new DBObject(table.getTableViewProperties().getSchema(), table.getName(), DBObject.TYPE_TABLE);
            dbObject.setModel(table);
            dbObjects.add(dbObject);
        }

        for (final View view : loadedDiagram.getDiagramContents().getContents().getViewSet()) {
            final DBObject dbObject = new DBObject(view.getTableViewProperties().getSchema(), view.getName(), DBObject.TYPE_VIEW);
            dbObject.setModel(view);
            dbObjects.add(dbObject);
        }

        for (final Note note : loadedDiagram.getDiagramContents().getContents().getNoteSet()) {
            final DBObject dbObject = new DBObject(null, note.getName(), DBObject.TYPE_NOTE);
            dbObject.setModel(note);
            dbObjects.add(dbObject);
        }

        for (final Sequence sequence : loadedDiagram.getDiagramContents().getSequenceSet()) {
            final DBObject dbObject = new DBObject(sequence.getSchema(), sequence.getName(), DBObject.TYPE_SEQUENCE);
            dbObject.setModel(sequence);
            dbObjects.add(dbObject);
        }

        for (final Trigger trigger : loadedDiagram.getDiagramContents().getTriggerSet()) {
            final DBObject dbObject = new DBObject(trigger.getSchema(), trigger.getName(), DBObject.TYPE_TRIGGER);
            dbObject.setModel(trigger);
            dbObjects.add(dbObject);
        }

        for (final Tablespace tablespace : loadedDiagram.getDiagramContents().getTablespaceSet()) {
            final DBObject dbObject = new DBObject(null, tablespace.getName(), DBObject.TYPE_TABLESPACE);
            dbObject.setModel(tablespace);
            dbObjects.add(dbObject);
        }

        for (final ColumnGroup columnGroup : loadedDiagram.getDiagramContents().getGroups()) {
            final DBObject dbObject = new DBObject(null, columnGroup.getName(), DBObject.TYPE_GROUP);
            dbObject.setModel(columnGroup);
            dbObjects.add(dbObject);
        }

        return dbObjects;
    }

    protected void loadData(final List<DBObject> selectedObjectList, final boolean useCommentAsLogicalName, final boolean mergeWord, final boolean mergeGroup) {

        final Set<AbstractModel> selectedSets = new HashSet<AbstractModel>();
        for (final DBObject dbObject : selectedObjectList) {
            selectedSets.add(dbObject.getModel());
        }

        final DiagramContents contents = loadedDiagram.getDiagramContents();

        final GroupSet columnGroupSet = contents.getGroups();

        for (final Iterator<ColumnGroup> iter = columnGroupSet.iterator(); iter.hasNext();) {
            final ColumnGroup columnGroup = iter.next();

            if (!selectedSets.contains(columnGroup)) {
                iter.remove();
            }
        }

        importedColumnGroups = columnGroupSet.getGroupList();

        final SequenceSet sequenceSet = contents.getSequenceSet();

        for (final Iterator<Sequence> iter = sequenceSet.iterator(); iter.hasNext();) {
            final Sequence sequence = iter.next();

            if (!selectedSets.contains(sequence)) {
                iter.remove();
            }
        }

        importedSequences = sequenceSet.getObjectList();

        final TriggerSet triggerSet = contents.getTriggerSet();

        for (final Iterator<Trigger> iter = triggerSet.iterator(); iter.hasNext();) {
            final Trigger trigger = iter.next();

            if (!selectedSets.contains(trigger)) {
                iter.remove();
            }
        }

        importedTriggers = triggerSet.getObjectList();

        final TablespaceSet tablespaceSet = contents.getTablespaceSet();

        for (final Iterator<Tablespace> iter = tablespaceSet.iterator(); iter.hasNext();) {
            final Tablespace tablespace = iter.next();

            if (!selectedSets.contains(tablespace)) {
                iter.remove();
            }
        }

        importedTablespaces = tablespaceSet.getObjectList();

        final NodeSet nodeSet = contents.getContents();
        final List<NodeElement> nodeElementList = nodeSet.getNodeElementList();

        for (final Iterator<NodeElement> iter = nodeElementList.iterator(); iter.hasNext();) {
            final NodeElement nodeElement = iter.next();

            if (!selectedSets.contains(nodeElement)) {
                iter.remove();
            }
        }

        final NodeSet selectedNodeSet = new NodeSet();

        final UniqueWordDictionary dictionary = new UniqueWordDictionary();

        if (mergeWord) {
            dictionary.init(getDiagram());
        }

        for (final NodeElement nodeElement : nodeElementList) {
            if (mergeWord) {
                if (nodeElement instanceof TableView) {
                    final TableView tableView = (TableView) nodeElement;

                    for (final NormalColumn normalColumn : tableView.getNormalColumns()) {
                        final Word word = normalColumn.getWord();
                        if (word != null) {
                            final Word replaceWord = dictionary.getUniqueWord(word, false);

                            if (replaceWord != null) {
                                normalColumn.setWord(replaceWord);
                            }
                        }
                    }
                }
            }

            selectedNodeSet.addNodeElement(nodeElement);
        }

        for (final NodeElement nodeElement : selectedNodeSet) {
            if (nodeElement instanceof TableView) {
                final TableView tableView = (TableView) nodeElement;

                for (final Iterator<Column> iter = tableView.getColumns().iterator(); iter.hasNext();) {
                    final Column column = iter.next();

                    if (column instanceof ColumnGroup) {
                        if (!importedColumnGroups.contains(column)) {
                            iter.remove();
                        }
                    }
                }
            }
        }

        if (mergeGroup) {
            mergeGroup(selectedNodeSet);
        }

        final CopyManager copyManager = new CopyManager(null);
        final NodeSet copyList = copyManager.copyNodeElementList(selectedNodeSet);

        importedNodeElements = copyList.getNodeElementList();
    }

    private void mergeGroup(final NodeSet selectedNodeSet) {
        final GroupSet currentGroupSet = getDiagram().getDiagramContents().getGroups();

        for (final Iterator<ColumnGroup> iter = importedColumnGroups.iterator(); iter.hasNext();) {
            final ColumnGroup columnGroup = iter.next();

            final ColumnGroup replaceColumnGroup = currentGroupSet.find(columnGroup);

            if (replaceColumnGroup != null) {
                iter.remove();

                for (final NodeElement nodeElement : selectedNodeSet) {
                    if (nodeElement instanceof TableView) {
                        final TableView tableView = (TableView) nodeElement;
                        tableView.replaceColumnGroup(columnGroup, replaceColumnGroup);
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(final Event event) throws Exception {
        final DBObjectSet dbObjectSet = preImport();

        if (dbObjectSet != null) {
            final AbstractSelectImportedObjectDialog importDialog = createSelectImportedObjectDialog(dbObjectSet);

            final int result = importDialog.open();

            if (result == IDialogConstants.OK_ID) {
                loadData(importDialog.getSelectedDbObjects(), importDialog.isUseCommentAsLogicalName(), importDialog.isMergeWord(), importDialog.isMergeGroup());
                showData();

            } else if (result == IDialogConstants.BACK_ID) {
                this.execute(event);
            }
        }
    }
}
