package _2020_04;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PShape;

import java.util.ArrayList;

public class Instancing_2 extends KrabApplet {
    private PGraphics pg;
    private ArrayList<PShape> pointArrays;
    int pointCount = 500000;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(1000,1000, P3D);
//        fullScreen(P3D);
    }

    public void setup() {
        pg = createGraphics(width, height, P3D);
        pg.smooth(8);
        surface.setAlwaysOnTop(true);
        pointArrays = shapes(pointCount, POINTS);
    }

    public void draw() {
        pg.beginDraw();
        fadeToBlack(pg);
        if(toggle("translate to middle")) {
            pg.translate(width/2f, height/2f);
        }
        translate(pg);
        preRotate(pg);
        pg.hint(DISABLE_OPTIMIZED_STROKE);
        updateShader();
        for(PShape shape : pointArrays) {
            float weight = slider("weight", 1);
            shape.setStrokeWeight(max(weight, .1f));
            pg.shape(shape);
        }
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    private void updateShader() {
        String frag = "/shaders/_2020_04/instancing_2/PointFrag.glsl";
        String vert = "/shaders/_2020_04/instancing_2/PointVert.glsl";
        uniform(frag, vert).set("time", t);
        uniform(frag, vert).set("count", pointCount);
        uniformColorPalette(frag, vert);
        hotShader(frag, vert, pg);
    }
}
