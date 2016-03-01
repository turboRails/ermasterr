package org.insightech.er.wizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.IDE;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.wizard.page.NewDiagramWizardPage1;
import org.insightech.er.wizard.page.NewDiagramWizardPage2;

public class NewDiagramWizard extends Wizard implements INewWizard {

    private NewDiagramWizardPage1 page1;

    private NewDiagramWizardPage2 page2;

    private IStructuredSelection selection;

    private IWorkbench workbench;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean performFinish() {
        try {
            final String database = page2.getDatabase();

            page1.createERDiagram(database);

            final IFile file = page1.createNewFile();

            if (file == null) {
                return false;
            }

            final IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();

            IDE.openEditor(page, file, true);

        } catch (final Exception e) {
            ERDiagramActivator.showExceptionDialog(e);
        }

        return true;
    }

    @Override
    public void init(final IWorkbench workbench, final IStructuredSelection selection) {
        this.selection = selection;
        this.workbench = workbench;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPages() {
        page1 = new NewDiagramWizardPage1(selection);
        addPage(page1);

        page2 = new NewDiagramWizardPage2(selection);
        addPage(page2);
    }
}
