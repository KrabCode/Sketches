package _2021_10;

import applet.KrabApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

public class CyclicImageMandala extends KrabApplet {
    private PGraphics pg;
    float time;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(1000, 1000, P2D);
    }

    public void setup() {
        if (width < displayWidth) {
            surface.setAlwaysOnTop(true);
        }
        pg = updateGraphics(pg);
    }

    public void draw() {
        time += radians(slider("speed", 1));
        pg = updateGraphics(pg);
        pg.beginDraw();
        fadeToBlack(pg);
        translateToCenter(pg);
        if(toggle("add")){
            pg.blendMode(ADD);
        }else{
            pg.blendMode(BLEND);
        }
        pg.noStroke();
        float pointSizeMin = slider("point size min", 20);
        float pointSizeMax = slider("point size max", 60);
        int rowCount = sliderInt("row count", 4);
        int basePointCount = sliderInt("point count", 4);
        int pointCountBonusPerRow = sliderInt("points per row", 4);
        float maxRadius = slider("radius", 400);
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            float rowNorm = norm(rowIndex, 0, rowCount - 1);
            float rowAngle = map(rowIndex, 0, rowCount - 1, -PI, PI);
            float rowRadius = maxRadius * cos(rowAngle + time);
            float pointSize = map(abs(rowRadius), 0, maxRadius, pointSizeMin, pointSizeMax);
            int pointCount = basePointCount + rowIndex * pointCountBonusPerRow;
            for (int pointIndex = 0; pointIndex < pointCount; pointIndex++) {
                pg.pushMatrix();
                pg.fill(gradientColorAt("fill", rowNorm));
                float pointAngle = map(pointIndex, 0, pointCount, 0, TAU);
                pointAngle += PI * sin(rowAngle + time) * (rowIndex % 2 == 0 ? -1 : 1);
                pg.rotate(pointAngle);
                pg.translate(rowRadius, 0);
                pg.ellipse(0, 0, pointSize, pointSize);
                pg.popMatrix();
            }
        }
        pg.endDraw();
        image(pg, 0, 0, width, height);
        rec(pg);
        gui();
    }
}
