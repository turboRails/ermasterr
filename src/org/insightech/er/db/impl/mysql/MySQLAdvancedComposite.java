package org.insightech.er.db.impl.mysql;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.view.dialog.element.table_view.tab.AdvancedComposite;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;

public class MySQLAdvancedComposite extends AdvancedComposite {

    private Combo engineCombo;

    private Combo characterSetCombo;

    private Combo collationCombo;

    private Text primaryKeyLengthOfText;

    public MySQLAdvancedComposite(final Composite parent) {
        super(parent);
    }

    @Override
    protected void initComposite() {
        super.initComposite();

        engineCombo = createEngineCombo(this, dialog);

        characterSetCombo = CompositeFactory.createCombo(dialog, this, "label.character.set", 1);
        characterSetCombo.setVisibleItemCount(20);

        collationCombo = CompositeFactory.createCombo(dialog, this, "label.collation", 1);
        collationCombo.setVisibleItemCount(20);

        primaryKeyLengthOfText = CompositeFactory.createNumText(dialog, this, "label.primary.key.length.of.text", 1, 30, true);
    }

    public static Combo createEngineCombo(final Composite parent, final AbstractDialog dialog) {
        final Combo combo = CompositeFactory.createCombo(dialog, parent, "label.storage.engine", 1);
        combo.setVisibleItemCount(20);

        initEngineCombo(combo);

        return combo;
    }

    private static void initEngineCombo(final Combo combo) {
        combo.add("");
        combo.add("MyISAM");
        combo.add("InnoDB");
        combo.add("Memory");
        combo.add("Merge");
        combo.add("Archive");
        combo.add("Federated");
        combo.add("NDB");
        combo.add("CSV");
        combo.add("Blackhole");
        combo.add("CSV");
    }

    private void initCharacterSetCombo() {
        characterSetCombo.add("");

        for (final String characterSet : MySQLDBManager.getCharacterSetList()) {
            characterSetCombo.add(characterSet);
        }
    }

    @Override
    protected void setData() {
        super.setData();

        initCharacterSetCombo();

        engineCombo.setText(Format.toString(((MySQLTableProperties) tableViewProperties).getStorageEngine()));

        final String characterSet = ((MySQLTableProperties) tableViewProperties).getCharacterSet();

        characterSetCombo.setText(Format.toString(characterSet));

        collationCombo.add("");

        for (final String collation : MySQLDBManager.getCollationList(Format.toString(characterSet))) {
            collationCombo.add(collation);
        }

        collationCombo.setText(Format.toString(((MySQLTableProperties) tableViewProperties).getCollation()));

        primaryKeyLengthOfText.setText(Format.toString(((MySQLTableProperties) tableViewProperties).getPrimaryKeyLengthOfText()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validate() throws InputException {
        final String engine = engineCombo.getText();
        ((MySQLTableProperties) tableViewProperties).setStorageEngine(engine);

        final String characterSet = characterSetCombo.getText();
        ((MySQLTableProperties) tableViewProperties).setCharacterSet(characterSet);

        final String collation = collationCombo.getText();
        ((MySQLTableProperties) tableViewProperties).setCollation(collation);

        final String str = primaryKeyLengthOfText.getText();
        Integer length = null;

        try {
            if (!Check.isEmptyTrim(str)) {
                length = Integer.valueOf(str);
            }
        } catch (final Exception e) {
            throw new InputException("error.column.length.degit");
        }

        ((MySQLTableProperties) tableViewProperties).setPrimaryKeyLengthOfText(length);

        if (table != null) {
            for (final NormalColumn primaryKey : table.getPrimaryKeys()) {
                final SqlType type = primaryKey.getType();

                if (type != null && type.isFullTextIndexable() && !type.isNeedLength(diagram.getDatabase())) {
                    if (length == null || length == 0) {
                        throw new InputException("error.primary.key.length.empty");
                    }
                }
            }
        }

        return super.validate();
    }

    @Override
    protected void addListener() {
        characterSetCombo.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                final String selectedCollation = collationCombo.getText();

                collationCombo.removeAll();
                collationCombo.add("");

                for (final String collation : MySQLDBManager.getCollationList(characterSetCombo.getText())) {
                    collationCombo.add(collation);
                }

                final int index = collationCombo.indexOf(selectedCollation);

                collationCombo.select(index);
            }
        });
    }
}
