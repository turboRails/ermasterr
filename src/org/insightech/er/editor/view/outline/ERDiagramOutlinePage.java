package org.insightech.er.editor.view.outline;

import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.parts.ScrollableThumbnail;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.dnd.AbstractTransferDragSourceListener;
import org.eclipse.gef.dnd.AbstractTransferDropTargetListener;
import org.eclipse.gef.dnd.TemplateTransfer;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;
import org.insightech.er.editor.controller.editpart.outline.ERDiagramOutlineEditPartFactory;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.view.action.outline.index.CreateIndexAction;
import org.insightech.er.editor.view.action.outline.notation.type.ChangeOutlineViewToBothAction;
import org.insightech.er.editor.view.action.outline.notation.type.ChangeOutlineViewToLogicalAction;
import org.insightech.er.editor.view.action.outline.notation.type.ChangeOutlineViewToPhysicalAction;
import org.insightech.er.editor.view.action.outline.orderby.ChangeOutlineViewOrderByLogicalNameAction;
import org.insightech.er.editor.view.action.outline.orderby.ChangeOutlineViewOrderByPhysicalNameAction;
import org.insightech.er.editor.view.action.outline.sequence.CreateSequenceAction;
import org.insightech.er.editor.view.action.outline.tablespace.CreateTablespaceAction;
import org.insightech.er.editor.view.action.outline.trigger.CreateTriggerAction;
import org.insightech.er.editor.view.drag_drop.ERDiagramOutlineTransferDropTargetListener;
import org.insightech.er.editor.view.drag_drop.ERDiagramTransferDragSourceListener;

public class ERDiagramOutlinePage extends ContentOutlinePage {

    // ページをアウトラインとサムネイルに分離するコンポジット
    private SashForm sash;

    private final TreeViewer viewer;

    private final ERDiagram diagram;

    private LightweightSystem lws;

    private ScrollableThumbnail thumbnail;

    private GraphicalViewer graphicalViewer;

    private final ActionRegistry outlineActionRegistory;

    private ActionRegistry registry;

    public ERDiagramOutlinePage(final ERDiagram diagram) {
        // GEFツリービューワを使用する
        super(new TreeViewer());

        viewer = (TreeViewer) getViewer();
        this.diagram = diagram;

        outlineActionRegistory = new ActionRegistry();
        registerAction(viewer, outlineActionRegistory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createControl(final Composite parent) {
        sash = new SashForm(parent, SWT.VERTICAL);

        // コンストラクタで指定したビューワの作成
        viewer.createControl(sash);

        // EditPartFactory の設定
        final ERDiagramOutlineEditPartFactory editPartFactory = new ERDiagramOutlineEditPartFactory();
        viewer.setEditPartFactory(editPartFactory);

        // グラフィカル・エディタのルート・モデルをツリー・ビューワにも設定
        viewer.setContents(diagram);

        final Canvas canvas = new Canvas(sash, SWT.BORDER);
        // サムネイル・フィギュアを配置する為の LightweightSystem
        lws = new LightweightSystem(canvas);

        resetView(registry);

        final AbstractTransferDragSourceListener dragSourceListener = new ERDiagramTransferDragSourceListener(viewer, TemplateTransfer.getInstance());
        viewer.addDragSourceListener(dragSourceListener);

        diagram.refreshOutline();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Control getControl() {
        // アウトライン・ビューをアクティブにした時にフォーカスが設定されるコントロールを返す
        return sash;
    }

    private void showThumbnail() {
        // RootEditPartのビューをソースとしてサムネイルを作成
        final ScalableFreeformRootEditPart editPart = (ScalableFreeformRootEditPart) graphicalViewer.getRootEditPart();

        if (thumbnail != null) {
            thumbnail.deactivate();
        }

        thumbnail = new ScrollableThumbnail((Viewport) editPart.getFigure());
        thumbnail.setSource(editPart.getLayer(LayerConstants.PRINTABLE_LAYERS));

        lws.setContents(thumbnail);

    }

    private void initDropTarget() {
        final AbstractTransferDropTargetListener dropTargetListener = new ERDiagramOutlineTransferDropTargetListener(graphicalViewer, TemplateTransfer.getInstance());

        graphicalViewer.addDropTargetListener(dropTargetListener);
    }

    public void setCategory(final EditDomain editDomain, final GraphicalViewer graphicalViewer, final MenuManager outlineMenuMgr, final ActionRegistry registry) {
        this.graphicalViewer = graphicalViewer;
        viewer.setContextMenu(outlineMenuMgr);

        // エディット・ドメインの設定
        viewer.setEditDomain(editDomain);
        this.registry = registry;

        if (getSite() != null) {
            resetView(registry);
        }
    }

    private void resetAction(final ActionRegistry registry) {
        // アウトライン・ページで有効にするアクション
        final IActionBars bars = getSite().getActionBars();

        String id = ActionFactory.UNDO.getId();
        bars.setGlobalActionHandler(id, registry.getAction(id));

        id = ActionFactory.REDO.getId();
        bars.setGlobalActionHandler(id, registry.getAction(id));

        id = ActionFactory.DELETE.getId();
        bars.setGlobalActionHandler(id, registry.getAction(id));

        bars.updateActionBars();
    }

    private void resetView(final ActionRegistry registry) {
        showThumbnail();
        initDropTarget();
        resetAction(registry);
    }

    private void registerAction(final TreeViewer treeViewer, final ActionRegistry actionRegistry) {
        final IAction[] actions = {new CreateIndexAction(treeViewer), new CreateSequenceAction(treeViewer), new CreateTriggerAction(treeViewer), new CreateTablespaceAction(treeViewer), new ChangeOutlineViewToPhysicalAction(treeViewer), new ChangeOutlineViewToLogicalAction(treeViewer), new ChangeOutlineViewToBothAction(treeViewer), new ChangeOutlineViewOrderByPhysicalNameAction(treeViewer), new ChangeOutlineViewOrderByLogicalNameAction(treeViewer)};

        for (final IAction action : actions) {
            actionRegistry.registerAction(action);
        }
    }

    public ActionRegistry getOutlineActionRegistory() {
        return outlineActionRegistory;
    }

    @Override
    public EditPartViewer getViewer() {
        return super.getViewer();
    }

}
