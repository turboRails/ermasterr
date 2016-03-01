package org.insightech.er.editor.view.figure.connection.decoration.ie;

import org.eclipse.draw2d.geometry.PointList;
import org.insightech.er.editor.view.figure.connection.decoration.ERDecoration;

public class IEOneDecoration extends ERDecoration {

    public IEOneDecoration() {
        super();

        final PointList pointList = new PointList();

        pointList.addPoint(-13, -12);
        pointList.addPoint(-13, 12);

        setTemplate(pointList);
        setScale(1, 1);
    }

}
