package org.insightech.er.editor.view.dialog.element.relation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.ResourceString;
import org.insightech.er.Resources;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.db.DBManager;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;

public class RelationDialog extends AbstractDialog {

    private final Relation relation;

    private Text nameText;

    private Text parentTableNameText;

    private Combo columnCombo;

    private Combo parentCardinalityCombo;

    private Combo childCardinalityCombo;

    private Combo onUpdateCombo;

    private Combo onDeleteCombo;

    private ColumnComboInfo columnComboInfo;

    public RelationDialog(final Shell parentShell, final Relation relation) {
        super(parentShell);

        this.relation = relation;
    }

    @Override
    protected void initLayout(final GridLayout layout) {
        super.initLayout(layout);

        layout.numColumns = 2;
        layout.makeColumnsEqualWidth = true;
        layout.verticalSpacing = Resources.VERTICAL_SPACING;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize(final Composite composite) {
        CompositeFactory.createLeftLabel(composite, "label.constraint.name", 2);
        nameText = CompositeFactory.createText(this, composite, null, 2, false, false);

        createMethodGroup(composite);

        final int size = createParentGroup(composite);
        createChildGroup(composite, size);
    }

    private void createMethodGroup(final Composite composite) {
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;

        final GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalSpan = 2;
        gridData.horizontalAlignment = GridData.FILL;

        final Group group = new Group(composite, SWT.NONE);
        group.setLayoutData(gridData);
        group.setLayout(gridLayout);
        group.setText(ResourceString.getResourceString("label.reference.operation"));

        createOnUpdateCombo(group);
        createOnDeleteCombo(group);
    }

    private void createOnUpdateCombo(final Group group) {
        onUpdateCombo = CompositeFactory.createCombo(this, group, "ON UPDATE", 1);

        final ERDiagram diagram = relation.getSource().getDiagram();
        final DBManager dbManager = DBManagerFactory.getDBManager(diagram);

        for (final String rule : dbManager.getForeignKeyRuleList()) {
            onUpdateCombo.add(rule);
        }
    }

    private void createOnDeleteCombo(final Group group) {
        onDeleteCombo = CompositeFactory.createCombo(this, group, "ON DELETE", 1);

        final ERDiagram diagram = relation.getSource().getDiagram();
        final DBManager dbManager = DBManagerFactory.getDBManager(diagram);

        for (final String rule : dbManager.getForeignKeyRuleList()) {
            onDeleteCombo.add(rule);
        }
    }

    private int createParentGroup(final Composite composite) {
        final GridLayout gridLayout = new GridLayout();
        gridLayout.verticalSpacing = 10;
        gridLayout.marginHeight = 10;

        final GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;

        final Group group = new Group(composite, SWT.NONE);
        group.setLayoutData(gridData);
        group.setLayout(gridLayout);
        group.setText(ResourceString.getResourceString("label.parent"));

        final Composite upperComposite = new Composite(group, SWT.NONE);
        upperComposite.setLayoutData(gridData);
        upperComposite.setLayout(gridLayout);

        final Label label1 = new Label(upperComposite, SWT.NONE);
        label1.setText(ResourceString.getResourceString("label.reference.table"));
        parentTableNameText = new Text(upperComposite, SWT.BORDER | SWT.READ_ONLY);
        parentTableNameText.setLayoutData(gridData);

        final Label label2 = new Label(upperComposite, SWT.NONE);
        label2.setText(ResourceString.getResourceString("label.reference.column"));
        createColumnCombo(upperComposite);

        createParentMandatoryGroup(group);

        upperComposite.pack();

        return upperComposite.getSize().y;
    }

    /**
     * This method initializes group1
     */
    private void createChildGroup(final Composite composite, final int size) {
        final GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = 10;
        gridLayout.verticalSpacing = 10;

        final GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;

        final Group group = new Group(composite, SWT.NONE);
        group.setLayoutData(gridData);
        group.setLayout(gridLayout);

        group.setText(ResourceString.getResourceString("label.child"));

        final Label filler = new Label(group, SWT.NONE);
        filler.setText("");
        final GridData fillerGridData = new GridData();
        fillerGridData.heightHint = size;
        filler.setLayoutData(fillerGridData);

        createChildMandatoryGroup(group);
    }

    private void createColumnCombo(final Composite parent) {
        final GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;

        columnCombo = new Combo(parent, SWT.READ_ONLY);
        columnCombo.setLayoutData(gridData);

        columnCombo.setVisibleItemCount(20);

        columnComboInfo = setReferencedColumnComboData(columnCombo, (ERTable) relation.getSourceTableView());
    }

    public static ColumnComboInfo setReferencedColumnComboData(final Combo columnCombo, final ERTable table) {
        final ColumnComboInfo info = new ColumnComboInfo();

        final int primaryKeySize = table.getPrimaryKeySize();

        if (primaryKeySize != 0) {
            columnCombo.add("PRIMARY KEY");
            info.complexUniqueKeyStartIndex = 1;
            info.candidatePK = true;

        } else {
            info.complexUniqueKeyStartIndex = 0;
            info.candidatePK = false;
        }

        for (final ComplexUniqueKey complexUniqueKey : table.getComplexUniqueKeyList()) {
            columnCombo.add(complexUniqueKey.getLabel());
        }

        info.columnStartIndex = info.complexUniqueKeyStartIndex + table.getComplexUniqueKeyList().size();

        for (final NormalColumn column : table.getNormalColumns()) {
            if (column.isUniqueKey()) {
                columnCombo.add(column.getLogicalName());
                info.candidateColumns.add(column);
            }
        }

        return info;
    }

    private void createParentMandatoryGroup(final Group parent) {
        final GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = 10;

        final GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;

        final Group group = new Group(parent, SWT.NONE);
        group.setLayout(gridLayout);
        group.setLayoutData(gridData);
        group.setText(ResourceString.getResourceString("label.mandatory"));

        parentCardinalityCombo = new Combo(group, SWT.NONE);
        parentCardinalityCombo.setLayoutData(gridData);

        parentCardinalityCombo.setVisibleItemCount(5);

        parentCardinalityCombo.add(Relation.PARENT_CARDINALITY_1);

        if (!relation.getForeignKeyColumns().get(0).isPrimaryKey()) {
            parentCardinalityCombo.add(Relation.PARENT_CARDINALITY_0_OR_1);
        }
    }

    private void createChildMandatoryGroup(final Group parent) {
        final GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = 10;

        final GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;

        final Group group = new Group(parent, SWT.NONE);
        group.setLayout(gridLayout);
        group.setLayoutData(gridData);
        group.setText(ResourceString.getResourceString("label.mandatory"));

        childCardinalityCombo = new Combo(group, SWT.NONE);
        childCardinalityCombo.setLayoutData(gridData);

        childCardinalityCombo.setVisibleItemCount(5);

        childCardinalityCombo.add("1..n");
        childCardinalityCombo.add("0..n");
        childCardinalityCombo.add(Relation.CHILD_CARDINALITY_1);
        childCardinalityCombo.add("0..1");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setData() {
        final ERTable sourceTable = (ERTable) relation.getSourceTableView();

        nameText.setText(Format.null2blank(relation.getName()));

        if (relation.getOnUpdateAction() != null) {
            onUpdateCombo.setText(relation.getOnUpdateAction());
        }
        if (relation.getOnDeleteAction() != null) {
            onDeleteCombo.setText(relation.getOnDeleteAction());
        }
        if (!Check.isEmpty(relation.getParentCardinality())) {
            parentCardinalityCombo.setText(relation.getParentCardinality());
        } else {
            parentCardinalityCombo.select(0);
        }
        if (!Check.isEmpty(relation.getChildCardinality())) {
            childCardinalityCombo.setText(relation.getChildCardinality());
        } else {
            childCardinalityCombo.select(0);
        }

        if (relation.isReferenceForPK()) {
            columnCombo.select(0);

        } else if (relation.getReferencedComplexUniqueKey() != null) {
            for (int i = 0; i < sourceTable.getComplexUniqueKeyList().size(); i++) {
                if (sourceTable.getComplexUniqueKeyList().get(i) == relation.getReferencedComplexUniqueKey()) {
                    columnCombo.select(i + columnComboInfo.complexUniqueKeyStartIndex);
                    break;
                }
            }

        } else {
            for (int i = 0; i < columnComboInfo.candidateColumns.size(); i++) {
                if (columnComboInfo.candidateColumns.get(i) == relation.getReferencedColumn()) {
                    columnCombo.select(i + columnComboInfo.columnStartIndex);
                    break;
                }
            }
        }

        if (relation.isReferedStrictly()) {
            columnCombo.setEnabled(false);
        }

        parentTableNameText.setText(relation.getSourceTableView().getLogicalName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void perfomeOK() {
        relation.setName(nameText.getText());

        final int index = columnCombo.getSelectionIndex();

        if (index < columnComboInfo.complexUniqueKeyStartIndex) {
            relation.setReferenceForPK(true);
            relation.setReferencedComplexUniqueKey(null);
            relation.setReferencedColumn(null);

        } else if (index < columnComboInfo.columnStartIndex) {
            final ComplexUniqueKey complexUniqueKey = ((ERTable) relation.getSourceTableView()).getComplexUniqueKeyList().get(index - columnComboInfo.complexUniqueKeyStartIndex);

            relation.setReferenceForPK(false);
            relation.setReferencedComplexUniqueKey(complexUniqueKey);
            relation.setReferencedColumn(null);

        } else {
            final NormalColumn sourceColumn = columnComboInfo.candidateColumns.get(index - columnComboInfo.columnStartIndex);

            relation.setReferenceForPK(false);
            relation.setReferencedComplexUniqueKey(null);
            relation.setReferencedColumn(sourceColumn);
        }

        relation.setOnDeleteAction(onDeleteCombo.getText());
        relation.setOnUpdateAction(onUpdateCombo.getText());
        relation.setChildCardinality(childCardinalityCombo.getText());
        relation.setParentCardinality(parentCardinalityCombo.getText());
    }

    @Override
    protected String getErrorMessage() {
        final String text = nameText.getText().trim();
        if (!Check.isAlphabet(text)) {
            return "error.constraint.name.not.alphabet";
        }

        return null;
    }

    @Override
    protected String getTitle() {
        return "dialog.title.relation";
    }

    public static class ColumnComboInfo {
        public List<NormalColumn> candidateColumns;

        public int complexUniqueKeyStartIndex;

        public int columnStartIndex;

        public boolean candidatePK;

        public ColumnComboInfo() {
            candidateColumns = new ArrayList<NormalColumn>();
        }

    }
}
