package org.insightech.er.editor.model.diagram_contents.element.connection;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;

public abstract class ConnectionElement extends AbstractModel implements Comparable<ConnectionElement> {

    private static final long serialVersionUID = -5418951773059063716L;

    protected NodeElement source;

    protected NodeElement target;

    private int sourceXp;

    private int sourceYp;

    private int targetXp;

    private int targetYp;

    private int[] color;

    private List<Bendpoint> bendPoints = new ArrayList<Bendpoint>();

    public ConnectionElement() {
        sourceXp = -1;
        sourceYp = -1;
        targetXp = -1;
        targetYp = -1;

        setColor(0, 0, 0);
    }

    @Override
    public int compareTo(final ConnectionElement other) {
        final NodeElement source1 = getSource();
        final NodeElement source2 = other.getSource();

        if (source1 != source2) {
            if (source1 == null) {
                return 1;
            }
            if (source2 == null) {
                return -1;
            }

            if (!(source1 instanceof TableView)) {
                return 1;
            }
            if (!(source2 instanceof TableView)) {
                return -1;
            }

            final TableView tableView1 = (TableView) source1;
            final TableView tableView2 = (TableView) source2;

            return tableView1.compareTo(tableView2);
        }

        final NodeElement target1 = getTarget();
        final NodeElement target2 = other.getTarget();

        if (target1 != target2) {
            if (target1 == null) {
                return 1;
            }
            if (target2 == null) {
                return -1;
            }

            if (!(target1 instanceof TableView)) {
                return 1;
            }
            if (!(target2 instanceof TableView)) {
                return -1;
            }

            final TableView tableView1 = (TableView) target1;
            final TableView tableView2 = (TableView) target2;

            return tableView1.compareTo(tableView2);
        }

        return 0;
    }

    public NodeElement getSource() {
        return source;
    }

    public void setSource(final NodeElement source) {
        if (this.source != null) {
            this.source.removeOutgoing(this);
        }

        this.source = source;

        if (this.source != null) {
            this.source.addOutgoing(this);
        }
    }

    public void setSourceAndTarget(final NodeElement source, final NodeElement target) {
        this.source = source;
        this.target = target;
    }

    public void setTarget(final NodeElement target) {
        if (this.target != null) {
            this.target.removeIncoming(this);
        }

        this.target = target;

        if (this.target != null) {
            this.target.addIncoming(this);
        }
    }

    public NodeElement getTarget() {
        return target;
    }

    public void delete() {
        source.removeOutgoing(this);
        target.removeIncoming(this);
    }

    public void connect() {
        if (source != null) {
            source.addOutgoing(this);
        }
        if (target != null) {
            target.addIncoming(this);
        }
    }

    public void addBendpoint(final int index, final Bendpoint point) {
        bendPoints.add(index, point);
    }

    public void setBendpoints(final List<Bendpoint> points) {
        bendPoints = points;
    }

    public List<Bendpoint> getBendpoints() {
        return bendPoints;
    }

    public void removeBendpoint(final int index) {
        bendPoints.remove(index);
    }

    public void replaceBendpoint(final int index, final Bendpoint point) {
        bendPoints.set(index, point);
    }

    public int getSourceXp() {
        return sourceXp;
    }

    public void setSourceLocationp(final int sourceXp, final int sourceYp) {
        this.sourceXp = sourceXp;
        this.sourceYp = sourceYp;
    }

    public int getSourceYp() {
        return sourceYp;
    }

    public int getTargetXp() {
        return targetXp;
    }

    public void setTargetLocationp(final int targetXp, final int targetYp) {
        this.targetXp = targetXp;
        this.targetYp = targetYp;
    }

    public int getTargetYp() {
        return targetYp;
    }

    public boolean isSourceAnchorMoved() {
        if (sourceXp != -1) {
            return true;
        }

        return false;
    }

    public boolean isTargetAnchorMoved() {
        if (targetXp != -1) {
            return true;
        }

        return false;
    }

    public void setColor(final int red, final int green, final int blue) {
        color = new int[3];
        color[0] = red;
        color[1] = green;
        color[2] = blue;
    }

    public int[] getColor() {
        return color;
    }

    public void refreshBendpoint() {
        if (isUpdateable()) {
            this.firePropertyChange("refreshBendpoint", null, null);
        }
    }

    @Override
    public ConnectionElement clone() {
        final ConnectionElement clone = (ConnectionElement) super.clone();

        final List<Bendpoint> cloneBendPoints = new ArrayList<Bendpoint>();
        for (final Bendpoint bendPoint : bendPoints) {
            cloneBendPoints.add((Bendpoint) bendPoint.clone());
        }

        clone.bendPoints = cloneBendPoints;

        if (color != null) {
            clone.color = new int[] {color[0], color[1], color[2]};
        }

        return clone;
    }
}
