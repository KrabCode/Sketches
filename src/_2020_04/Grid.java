package _2020_04;

import applet.KrabApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PMatrix;
import processing.core.PVector;
import processing.opengl.PGraphicsOpenGL;

public class Grid extends KrabApplet {
    private PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(800, 800, P3D);
    }

    public void setup() {
        pg = createGraphics(width, height, P3D);
        surface.setAlwaysOnTop(true);
    }

    public void draw() {
        pg.beginDraw();
        fadeToBlack(pg);
        pg.translate(width*.5f, height*.5f);
        translate(pg);
        rotate(pg);
        pg.hint(DISABLE_OPTIMIZED_STROKE);
        updateShader(pg);
        pg.strokeWeight(slider("weight", 1));
        pg.stroke(picker("stroke").clr());
        pg.noFill();
        int count = sliderInt("count", 100);
        float size = slider("size", 300);
        pg.beginShape(POINTS);
        for (int xi = 0; xi < count; xi++) {
            for (int zi = 0; zi < count; zi++) {
                float x = map(xi, 0, count-1, -size, size);
                float z = map(zi, 0, count-1, -size, size);
                pg.vertex(x, 0, z);
            }
        }
        pg.endShape();
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    private void updateShader(PGraphics pg) {
        String frag = "shaders/_2020_04/grid/PointFrag.glsl";
        String vert = "shaders/_2020_04/grid/PointVert.glsl";
        uniform(frag, vert).set("time", t);
        hotShader(frag, vert, pg);
    }
}
