package org.insightech.er.editor.model.tracking;

import java.io.Serializable;
import java.util.Date;

import org.insightech.er.editor.model.diagram_contents.DiagramContents;
import org.insightech.er.editor.model.edit.CopyManager;

public class ChangeTracking implements Serializable {

    private static final long serialVersionUID = 4766921781666293191L;

    private final DiagramContents diagramContents;

    private Date updatedDate;

    private String comment;

    public ChangeTracking(final DiagramContents diagramContents) {
        final CopyManager copyManager = new CopyManager(null);

        this.diagramContents = copyManager.copy(diagramContents);
        updatedDate = new Date();
    }

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(final Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public DiagramContents getDiagramContents() {
        return diagramContents;
    }

}
