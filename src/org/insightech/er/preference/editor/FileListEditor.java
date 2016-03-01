package org.insightech.er.preference.editor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.jface.preference.ListEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ResourceString;
import org.insightech.er.util.io.FileUtils;

public abstract class FileListEditor extends ListEditor {

    public static final String VALUE_SEPARATOR = "/";

    private String lastPath;

    private final Composite parent;

    private final Map<String, String> namePathMap;

    private final String extention;

    public FileListEditor(final String name, final String labelText, final Composite parent, final String extention) {
        super(name, labelText, parent);

        this.parent = parent;

        namePathMap = new HashMap<String, String>();

        setPreferenceStore(ERDiagramActivator.getDefault().getPreferenceStore());

        this.extention = extention;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getNewInputObject() {

        final FileDialog dialog = new FileDialog(getShell());

        if (lastPath != null) {
            if (new File(lastPath).exists()) {
                dialog.setFilterPath(lastPath);
            }
        }

        final String[] filterExtensions = new String[] {extention};
        dialog.setFilterExtensions(filterExtensions);

        final String filePath = dialog.open();
        if (filePath != null) {
            final File file = new File(filePath);
            final String fileName = file.getName();

            if (contains(fileName)) {
                final MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
                messageBox.setText(ResourceString.getResourceString("dialog.title.warning"));
                messageBox.setMessage(ResourceString.getResourceString("dialog.message.update.file"));

                if (messageBox.open() == SWT.CANCEL) {
                    return null;
                }

                namePathMap.put(fileName, filePath);
                return null;
            }

            namePathMap.put(fileName, filePath);
            try {
                lastPath = file.getParentFile().getCanonicalPath();
            } catch (final IOException e) {}

            return fileName;
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String[] parseString(final String stringList) {
        final StringTokenizer st = new StringTokenizer(stringList, VALUE_SEPARATOR);
        final List<String> list = new ArrayList<String>();

        while (st.hasMoreElements()) {
            list.add(st.nextToken());
        }
        return list.toArray(new String[list.size()]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String createList(final String[] items) {
        final StringBuilder path = new StringBuilder("");

        for (int i = 0; i < items.length; i++) {
            path.append(items[i]);
            path.append(VALUE_SEPARATOR);
        }

        return path.toString();
    }

    protected abstract String getStorePath(String name);

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doStore() {
        try {
            final File dir = new File(getStorePath(""));
            dir.mkdirs();

            for (final String name : namePathMap.keySet()) {
                final File from = new File(namePathMap.get(name));
                final File to = new File(getStorePath(name));
                FileUtils.copyFile(from, to);
            }

        } catch (final IOException e) {
            ERDiagramActivator.showErrorDialog(ResourceString.getResourceString("error.read.file"));
        }

        super.doStore();
    }

    private boolean contains(final String name) {
        final org.eclipse.swt.widgets.List list = getListControl(parent);

        final String[] items = list.getItems();

        for (final String item : items) {
            if (name.equals(item)) {
                return true;
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doLoad() {
        final org.eclipse.swt.widgets.List list = getListControl(parent);

        final String s = getPreferenceStore().getString(getPreferenceName());
        final String[] array = parseString(s);

        final Set<String> names = new HashSet<String>();

        for (int i = 0; i < array.length; i++) {
            final File file = new File(getStorePath(array[i]));
            if (file.exists() && !names.contains(array[i])) {
                list.add(array[i]);
                names.add(array[i]);
            }
        }
    }

}
