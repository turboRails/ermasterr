package org.insightech.er.editor.model.diagram_contents.element.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ObjectModel;
import org.insightech.er.editor.model.ViewableModel;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;

public abstract class NodeElement extends ViewableModel implements ObjectModel {

    private static final long serialVersionUID = -5143984125818569247L;

    private Location location;

    private Location actualLocation;

    private List<ConnectionElement> incomings = new ArrayList<ConnectionElement>();

    private List<ConnectionElement> outgoings = new ArrayList<ConnectionElement>();

    private ERDiagram diagram;

    public NodeElement() {
        location = new Location(0, 0, 0, 0);
    }

    public void setDiagram(final ERDiagram diagram) {
        this.diagram = diagram;
    }

    public ERDiagram getDiagram() {
        return diagram;
    }

    public int getX() {
        return location.x;
    }

    public int getY() {
        return location.y;
    }

    public int getWidth() {
        return location.width;
    }

    public int getHeight() {
        return location.height;
    }

    public void setLocation(final Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return new Location(location.x, location.y, location.width, location.height);
    }

    public Location getActualLocation() {
        return actualLocation;
    }

    public void setActualLocation(final Location actualLocation) {
        this.actualLocation = actualLocation;
    }

    public List<ConnectionElement> getIncomings() {
        return incomings;
    }

    public List<ConnectionElement> getOutgoings() {
        return outgoings;
    }

    public void setIncoming(final List<ConnectionElement> relations) {
        incomings = relations;
    }

    public void setOutgoing(final List<ConnectionElement> relations) {
        outgoings = relations;
    }

    public void addIncoming(final ConnectionElement relation) {
        incomings.add(relation);
    }

    public void removeIncoming(final ConnectionElement relation) {
        incomings.remove(relation);
    }

    public void addOutgoing(final ConnectionElement relation) {
        outgoings.add(relation);
    }

    public void removeOutgoing(final ConnectionElement relation) {
        outgoings.remove(relation);
    }

    public List<NodeElement> getReferringElementList() {
        final List<NodeElement> referringElementList = new ArrayList<NodeElement>();

        for (final ConnectionElement connectionElement : getOutgoings()) {
            final NodeElement targetElement = connectionElement.getTarget();

            referringElementList.add(targetElement);
        }

        return referringElementList;
    }

    public List<NodeElement> getReferedElementList() {
        final List<NodeElement> referedElementList = new ArrayList<NodeElement>();

        for (final ConnectionElement connectionElement : getIncomings()) {
            final NodeElement sourceElement = connectionElement.getSource();

            referedElementList.add(sourceElement);
        }

        return referedElementList;
    }

    public void refreshSourceConnections() {
        if (isUpdateable()) {
            this.firePropertyChange("refreshSourceConnections", null, null);
        }
    }

    public void refreshTargetConnections() {
        if (isUpdateable()) {
            this.firePropertyChange("refreshTargetConnections", null, null);
        }
    }

    public void refreshCategory() {
        if (isUpdateable()) {
            if (diagram != null) {
                for (final Category category : diagram.getDiagramContents().getSettings().getCategorySetting().getSelectedCategories()) {
                    if (category.contains(this)) {
                        category.refreshVisuals();
                    }
                }
            }
        }
    }

    public void sortRelations() {
        Collections.sort(incomings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeElement clone() {
        final NodeElement clone = (NodeElement) super.clone();

        clone.location = location.clone();
        clone.setIncoming(new ArrayList<ConnectionElement>());
        clone.setOutgoing(new ArrayList<ConnectionElement>());

        return clone;
    }

}
