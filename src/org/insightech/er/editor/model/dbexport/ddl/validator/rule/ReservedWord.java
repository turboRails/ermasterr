package org.insightech.er.editor.model.dbexport.ddl.validator.rule;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import org.insightech.er.ERDiagramActivator;

public class ReservedWord {

    private static Set<String> reservedWords = new HashSet<String>();

    static {
        final ResourceBundle bundle = ResourceBundle.getBundle(ERDiagramActivator.PLUGIN_ID + ".reserved_word");

        final Enumeration<String> keys = bundle.getKeys();

        while (keys.hasMoreElements()) {
            reservedWords.add(keys.nextElement());
        }
    }

    public static boolean isReservedWord(final String str) {
        return reservedWords.contains(str);
    }
}
