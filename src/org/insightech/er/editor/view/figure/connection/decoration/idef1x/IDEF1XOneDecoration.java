package org.insightech.er.editor.view.figure.connection.decoration.idef1x;

import org.eclipse.draw2d.geometry.PointList;
import org.insightech.er.editor.view.figure.connection.decoration.ERDecoration;

public class IDEF1XOneDecoration extends ERDecoration {

    public IDEF1XOneDecoration() {
        super();

        final PointList pointList = new PointList();

        setTemplate(pointList);
        setScale(1, 1);
    }

}
