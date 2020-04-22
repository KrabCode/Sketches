package _2020_04;

import applet.KrabApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PShape;

import java.util.ArrayList;

public class Instancing extends KrabApplet {
    private PGraphics pg;
    private ArrayList<PShape> pointArrays = new ArrayList<>();
    int pointCount = 0;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(800, 800, P3D);
//        fullScreen(P3D);
    }

    public void setup() {
        pg = createGraphics(width, height, P3D);
        pg.smooth(16);
        surface.setAlwaysOnTop(true);
        regenShape();
    }

    public void draw() {
        regenShape();
        pg.beginDraw();
        fadeToBlack(pg);
        pg.translate(width/2f, height/2f);
        translate(pg);
        rotate(pg);
        pg.hint(DISABLE_OPTIMIZED_STROKE);
        updateShader();
        for(PShape shape : pointArrays) {
            shape.setStroke(picker("stroke").clr());
            float weight = slider("weight", 1);
            shape.setStrokeWeight(max(weight, .1f));
            pg.shape(shape);
        }
        gaussBlurPass(pg);
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    private void regenShape() {
        int count = sliderInt("count", 500000);
        if(pointCount != count) {
            pointCount = count;
            int maxPshapePop = 100000;
            int pshapesNeeded = count / maxPshapePop;
            int pointIndex = 0;
            pointArrays.clear();
            for (int shapeIndex = 0; shapeIndex < pshapesNeeded; shapeIndex++) {
                PShape pointArray = createShape();
                pointArray.beginShape(POINTS);
                for (int j = 0; j < maxPshapePop; j++) {
                    pointArray.vertex(pointIndex++, 0, 0);
                }
                pointArray.endShape();
                pointArrays.add(pointArray);
            }
        }
    }

    private void updateShader() {
        String frag = "/shaders/_2020_04/instancing/PointFrag.glsl";
        String vert = "/shaders/_2020_04/instancing/PointVert.glsl";
        uniform(frag, vert).set("time", t);
        uniform(frag, vert).set("count", pointCount);
        hotShader(frag, vert, pg);
    }
}
