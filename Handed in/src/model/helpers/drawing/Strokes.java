package model.helpers.drawing;

import java.awt.*;

public enum Strokes {
    //TODO need better names!
    LOWERSTROKE(new BasicStroke(0.000005f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)),
    BORDERSTROKE(new BasicStroke(0.000009f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)),
    SMALLSTROKE(new BasicStroke(0.00002f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)),
    NORMALSTROKE(new BasicStroke(0.00004f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)),
    MEDIUMSTROKE(new BasicStroke(0.00006f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)),
    FATSTROKE(new BasicStroke(0.00008f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)),
    BIGSTROKE(new BasicStroke(0.00020f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)),

    DASHEDSTROKE(new BasicStroke(0.000005f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 0, new float[] {0.00005f},0)),
    DOTTEDSTROKE(new BasicStroke(0.000005f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 0, new float[] {0.00001f},0));

    private BasicStroke stroke;

    Strokes(BasicStroke stroke) {
        this.stroke = stroke;
    }

    public BasicStroke getStroke() {
        return this.stroke;
    }

    public BasicStroke getModifiedStroke(float modifier) {
        BasicStroke currentStroke = this.getStroke();
        return new BasicStroke(currentStroke.getLineWidth() * Math.min(modifier/10, 10), currentStroke.getEndCap(), currentStroke.getLineJoin()); //This will only get called by highways when this format makes sense
    }
}
