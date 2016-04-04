package org.insightech.er.editor;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ResourceString;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.controller.command.category.ChangeCategoryNameCommand;
import org.insightech.er.editor.controller.editpart.element.ERDiagramEditPartFactory;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.validator.ValidateResult;
import org.insightech.er.editor.model.dbexport.ddl.validator.Validator;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.insightech.er.editor.model.settings.CategorySetting;
import org.insightech.er.editor.persistent.Persistent;
import org.insightech.er.editor.view.ERDiagramGotoMarker;
import org.insightech.er.editor.view.contributor.ERDiagramActionBarContributor;
import org.insightech.er.editor.view.dialog.category.CategoryNameChangeDialog;
import org.insightech.er.editor.view.outline.ERDiagramOutlinePage;
import org.insightech.er.editor.view.property_source.ERDiagramPropertySourceProvider;
import org.insightech.er.editor.view.tool.ERDiagramPaletteRoot;
import org.insightech.er.util.Format;

/**
 * <pre>
 * エディタークラス
 * カテゴリー毎にタブ（ページ）を作成する
 * 各タブ（ページ）には、{@link ERDiagramEditor} を割り当てる
 * </pre>
 */
public class ERDiagramMultiPageEditor extends MultiPageEditorPart {

    private IFile inputFile;

    private String inputFilePath;

    private ERDiagram diagram;

    private ERDiagramEditPartFactory editPartFactory;

    private ERDiagramOutlinePage outlinePage;

    private ERDiagramElementStateListener fElementStateListener;

    private final IGotoMarker gotoMaker;

    private final Map<IMarker, Object> markedObjectMap = new HashMap<IMarker, Object>();

    private final PropertySheetPage propertySheetPage;

    private final DefaultEditDomain editDomain;

    private final ERDiagramPaletteRoot pallet;

    public ERDiagramMultiPageEditor() {
        propertySheetPage = new PropertySheetPage();
        propertySheetPage.setPropertySourceProvider(new ERDiagramPropertySourceProvider(this));

        gotoMaker = new ERDiagramGotoMarker(this);
        editDomain = new DefaultEditDomain(this);
        pallet = new ERDiagramPaletteRoot();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createPages() {
        InputStream in = null;

        try {
            final IEditorInput input = getEditorInput();

            File file;
            if (input instanceof IFileEditorInput) {
                inputFile = ((IFileEditorInput) input).getFile();
                inputFilePath = inputFile.getLocation().toOSString();
                file = inputFile.getLocation().toFile();

                setPartName(inputFile.getName());

                if (!inputFile.isSynchronized(IResource.DEPTH_ONE)) {
                    inputFile.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
                }

                in = inputFile.getContents();

            } else {
                final URI uri = ((FileStoreEditorInput) input).getURI();
                file = new File(uri);
                inputFilePath = file.getCanonicalPath();

                setPartName(file.getName());

                in = new FileInputStream(file);
            }

            final Persistent persistent = Persistent.getInstance();
            diagram = persistent.load(in, file);

        } catch (final Exception e) {
            ERDiagramActivator.showExceptionDialog(e);

        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (final Exception e) {
                    ERDiagramActivator.showExceptionDialog(e);
                }
            }
        }

        if (diagram == null) {
            diagram = new ERDiagram(DBManagerFactory.getAllDBList().get(0));
            diagram.init();
        }

        diagram.getDiagramContents().getSettings().getTranslationSetting().load();

        diagram.setEditor(this);

        editPartFactory = new ERDiagramEditPartFactory();
        outlinePage = new ERDiagramOutlinePage(diagram);

        try {
            final ERDiagramEditor editor = new ERDiagramEditor(diagram, editPartFactory, outlinePage, editDomain, pallet);

            final int index = this.addPage(editor, getEditorInput());
            setPageText(index, ResourceString.getResourceString("label.all"));

        } catch (final PartInitException e) {
            ERDiagramActivator.showExceptionDialog(e);
        }

        // [ermasterr] Avoid expensive initialization, disable category tabs
        //initCategoryPages();

        initStartPage();

        addMouseListenerToTabFolder();

        validate();
    }

    private void initStartPage() {
        final int pageIndex = diagram.getPageIndex();
        setActivePage(pageIndex);

        if (pageIndex > 0) {
            pageChange(pageIndex);
        }

        final ERDiagramEditor activeEditor = getActiveEditor();
        final ZoomManager zoomManager = (ZoomManager) activeEditor.getAdapter(ZoomManager.class);
        zoomManager.setZoom(diagram.getZoom());

        activeEditor.setLocation(diagram.getX(), diagram.getY());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Composite createPageContainer(final Composite parent) {
        try {
            final IWorkbenchPage page = getSite().getWorkbenchWindow().getActivePage();

            if (page != null) {
                page.showView(IPageLayout.ID_OUTLINE);
            }

        } catch (final PartInitException e) {
            ERDiagramActivator.showExceptionDialog(e);
        }

        return super.createPageContainer(parent);
    }

    public void initCategoryPages() {
        final CategorySetting categorySettings = diagram.getDiagramContents().getSettings().getCategorySetting();

        final List<Category> selectedCategories = categorySettings.getSelectedCategories();

        if (getActivePage() > selectedCategories.size()) {
            setActivePage(0);
            pageChange(0);
        }

        while (getPageCount() > selectedCategories.size() + 1) {
            final IEditorPart editorPart = getEditor(selectedCategories.size() + 1);
            editorPart.dispose();
            removePage(selectedCategories.size() + 1);
            // by dispose(), activetool is set to null.
            editDomain.loadDefaultTool();
        }

        try {
            for (int i = 1; i < getPageCount(); i++) {
                final Category category = selectedCategories.get(i - 1);
                setPageText(i, Format.null2blank(category.getName()));
            }

            for (int i = getPageCount(); i < selectedCategories.size() + 1; i++) {
                final Category category = selectedCategories.get(i - 1);

                final ERDiagramEditor diagramEditor = new ERDiagramEditor(diagram, editPartFactory, outlinePage, editDomain, pallet);

                this.addPage(diagramEditor, getEditorInput());

                setPageText(i, Format.null2blank(category.getName()));

                setRetargetActions(diagramEditor);
                if (getActiveEditor() != null) {
                    getActiveEditor().resetEditDomain();
                }
            }

        } catch (final PartInitException e) {
            ERDiagramActivator.showExceptionDialog(e);
        }
    }

    private void setRetargetActions(final ERDiagramEditor newEditor) {
        final ERDiagramActionBarContributor actionBarContributor = newEditor.getActionBarContributor();

        actionBarContributor.initRetargetActions(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doSave(final IProgressMonitor monitor) {
        final ZoomManager zoomManager = (ZoomManager) getActiveEditor().getAdapter(ZoomManager.class);
        final double zoom = zoomManager.getZoom();
        diagram.setZoom(zoom);

        final ERDiagramEditor activeEditor = getActiveEditor();
        final Point location = activeEditor.getLocation();
        diagram.setLocation(location.x, location.y);

        final Persistent persistent = Persistent.getInstance();

        try {
            diagram.getDiagramContents().getSettings().getModelProperties().setUpdatedDate(new Date());

            final InputStream source = persistent.createInputStream(diagram);

            if (inputFile != null) {
                if (!inputFile.exists()) {
                    inputFile.create(source, true, monitor);

                } else {
                    inputFile.setContents(source, true, false, monitor);
                }
            }

        } catch (final Exception e) {
            ERDiagramActivator.showExceptionDialog(e);
        }

        for (int i = 0; i < getPageCount(); i++) {
            final IEditorPart editor = getEditor(i);
            editor.doSave(monitor);
        }

        validate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doSaveAs() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pageChange(final int newPageIndex) {
        final ERDiagramEditor currentEditor = getActiveEditor();
        currentEditor.removeSelection();

        super.pageChange(newPageIndex);

        // for (int i = 0; i < this.getPageCount(); i++) {
        // ERDiagramEditor editor = (ERDiagramEditor) this.getEditor(i);
        // editor.removeSelection();
        // }

        final ERDiagramEditor newEditor = (ERDiagramEditor) getEditor(newPageIndex);
        newEditor.changeCategory();

        final Category category = getPageCategory(newPageIndex);
        diagram.setCurrentCategory(category, newPageIndex);

        diagram.refreshWithConnection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ERDiagramEditor getActiveEditor() {
        return (ERDiagramEditor) super.getActiveEditor();
    }

    public void selectRootEditPart() {
        final GraphicalViewer viewer = getActiveEditor().getGraphicalViewer();
        viewer.deselectAll();
        viewer.appendSelection(viewer.getRootEditPart());
    }

    public Category getPageCategory(final int page) {
        final List<Category> categories = diagram.getDiagramContents().getSettings().getCategorySetting().getSelectedCategories();

        if (page == 0) {
            return null;
        }

        return categories.get(page - 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
        super.init(site, input);
        fElementStateListener = new ERDiagramElementStateListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        fElementStateListener.disposeDocumentProvider();
        super.dispose();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setInputWithNotify(final IEditorInput input) {
        super.setInputWithNotify(input);
    }

    private void validate() {
        if (diagram.getDiagramContents().getSettings().isSuspendValidator()) {
            if (inputFile != null) {
                try {
                    inputFile.deleteMarkers(null, true, IResource.DEPTH_INFINITE);
                } catch (final CoreException e) {
                    ERDiagramActivator.showExceptionDialog(e);
                }
            }

        } else {
            final IWorkspaceRunnable editorMarker = new IWorkspaceRunnable() {
                @Override
                public void run(final IProgressMonitor monitor) throws CoreException {
                    if (inputFile != null) {
                        inputFile.deleteMarkers(null, true, IResource.DEPTH_INFINITE);
                        clearMarkedObject();

                        final Validator validator = new Validator();
                        final List<ValidateResult> errorList = validator.validate(diagram);

                        for (final ValidateResult error : errorList) {
                            final IMarker marker = inputFile.createMarker(IMarker.PROBLEM);

                            marker.setAttribute(IMarker.MESSAGE, error.getMessage());
                            marker.setAttribute(IMarker.TRANSIENT, true);
                            marker.setAttribute(IMarker.LOCATION, error.getLocation());
                            marker.setAttribute(IMarker.SEVERITY, error.getSeverity());
                            setMarkedObject(marker, error.getObject());
                        }

                        final List<ValidateResult> todoList = validateTodo();

                        for (final ValidateResult todo : todoList) {
                            final IMarker marker = inputFile.createMarker(IMarker.TASK);

                            marker.setAttribute(IMarker.MESSAGE, todo.getMessage());
                            marker.setAttribute(IMarker.TRANSIENT, true);
                            marker.setAttribute(IMarker.LOCATION, todo.getLocation());
                            marker.setAttribute(IMarker.SEVERITY, todo.getSeverity());
                            setMarkedObject(marker, todo.getObject());
                        }
                    }
                }
            };

            try {
                ResourcesPlugin.getWorkspace().run(editorMarker, null);
            } catch (final CoreException e) {
                ERDiagramActivator.showExceptionDialog(e);
            }
        }
    }

    private List<ValidateResult> validateTodo() {
        final List<ValidateResult> resultList = new ArrayList<ValidateResult>();

        for (final ERTable table : diagram.getDiagramContents().getContents().getTableSet()) {

            String description = table.getDescription();
            resultList.addAll(createTodo(description, table.getLogicalName(), table));

            for (final NormalColumn column : table.getNormalColumns()) {
                description = column.getDescription();
                resultList.addAll(createTodo(description, table.getLogicalName(), table));
            }

            for (final Index index : table.getIndexes()) {
                description = index.getDescription();
                resultList.addAll(createTodo(description, index.getName(), index));
            }
        }

        for (final View view : diagram.getDiagramContents().getContents().getViewSet().getList()) {

            String description = view.getDescription();
            resultList.addAll(createTodo(description, view.getName(), view));

            for (final NormalColumn column : view.getNormalColumns()) {
                description = column.getDescription();
                resultList.addAll(createTodo(description, view.getLogicalName(), view));
            }
        }

        for (final Trigger trigger : diagram.getDiagramContents().getTriggerSet().getObjectList()) {

            final String description = trigger.getDescription();
            resultList.addAll(createTodo(description, trigger.getName(), trigger));
        }

        for (final Sequence sequence : diagram.getDiagramContents().getSequenceSet().getObjectList()) {

            final String description = sequence.getDescription();
            resultList.addAll(createTodo(description, sequence.getName(), sequence));
        }

        return resultList;
    }

    private List<ValidateResult> createTodo(final String description, final String location, final Object object) {
        final List<ValidateResult> resultList = new ArrayList<ValidateResult>();

        if (description != null) {
            final StringTokenizer tokenizer = new StringTokenizer(description, "\n\r");

            while (tokenizer.hasMoreElements()) {
                final String token = tokenizer.nextToken();
                final int startIndex = token.indexOf("// TODO");

                if (startIndex != -1) {
                    final String message = token.substring(startIndex + "// TODO".length()).trim();

                    final ValidateResult result = new ValidateResult();

                    result.setLocation(location);
                    result.setMessage(message);
                    result.setObject(object);

                    resultList.add(result);
                }
            }
        }

        return resultList;
    }

    public void setCurrentCategoryPageName() {
        final Category category = getPageCategory(getActivePage());
        setPageText(getActivePage(), Format.null2blank(category.getName()));
    }

    private void addMouseListenerToTabFolder() {
        final CTabFolder tabFolder = (CTabFolder) getContainer();

        tabFolder.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDoubleClick(final MouseEvent mouseevent) {

                final Category category = getPageCategory(getActivePage());

                if (category != null) {
                    final CategoryNameChangeDialog dialog = new CategoryNameChangeDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), category);

                    if (dialog.open() == IDialogConstants.OK_ID) {
                        final ChangeCategoryNameCommand command = new ChangeCategoryNameCommand(diagram, category, dialog.getCategoryName());
                        execute(command);
                    }
                }

                super.mouseDoubleClick(mouseevent);
            }
        });
    }

    private void execute(final Command command) {
        final ERDiagramEditor selectedEditor = getActiveEditor();

        selectedEditor.getGraphicalViewer().getEditDomain().getCommandStack().execute(command);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getAdapter(final Class type) {
        if (type == ERDiagram.class) {
            return diagram;

        } else if (type == IGotoMarker.class) {
            return gotoMaker;
        }

        else if (type == IPropertySheetPage.class) {
            return propertySheetPage;
        }

        return super.getAdapter(type);
    }

    public Object getMarkedObject(final IMarker marker) {
        return markedObjectMap.get(marker);
    }

    public void setMarkedObject(final IMarker marker, final Object markedObject) {
        markedObjectMap.put(marker, markedObject);
    }

    public void clearMarkedObject() {
        markedObjectMap.clear();
    }

    public void refreshPropertySheet() {
        propertySheetPage.refresh();
    }

    public void refreshProject() {
        if (inputFile != null) {
            final IProject project = inputFile.getProject();

            try {
                project.refreshLocal(IResource.DEPTH_INFINITE, null);

            } catch (final CoreException e) {
                ERDiagramActivator.showExceptionDialog(e);
            }
        }
    }

    public String getDiagramFilePath() {
        return inputFilePath;
    }

    public String getBasePath() {
        if (inputFile != null) {
            return inputFile.getProject().getLocation().toOSString();
        }

        return new File(inputFilePath).getParent();
    }

    public String getDefaultCharset() {
        if (inputFile != null) {
            final IProject project = inputFile.getProject();

            try {
                final Charset defautlCharset = Charset.forName(project.getDefaultCharset());
                return defautlCharset.displayName();

            } catch (final CoreException e) {}
        }

        return Charset.defaultCharset().displayName();
    }

}
