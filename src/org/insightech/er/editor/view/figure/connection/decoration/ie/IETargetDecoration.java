package org.insightech.er.editor.view.figure.connection.decoration.ie;

import org.eclipse.draw2d.geometry.PointList;
import org.insightech.er.editor.view.figure.connection.decoration.ERDecoration;

public class IETargetDecoration extends ERDecoration {

    public IETargetDecoration() {
        super();

        final PointList pointList = new PointList();

        pointList.addPoint(-13, -12);
        pointList.addPoint(-13, 0);
        pointList.addPoint(-1, -12);
        pointList.addPoint(-13, 0);
        pointList.addPoint(-1, 12);
        pointList.addPoint(-13, 0);
        pointList.addPoint(-13, 12);

        setTemplate(pointList);
        setScale(1, 1);
    }

}
