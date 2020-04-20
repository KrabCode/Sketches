package _2020_04;

import applet.KrabApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

@SuppressWarnings("DuplicatedCode")
public class TriangleStripClouds extends KrabApplet {
    private PGraphics pg;
    PImage img;

    String frag = "shaders/_2020_04/clouds/ColorFrag.glsl";
    String vert = "shaders/_2020_04/clouds/ColorVert.glsl";

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(800, 800, P3D);
//        fullScreen(P3D, 2);
    }

    public void setup() {
        surface.setAlwaysOnTop(true);
        img = loadImage(randomImageUrl(800));
        pg = createGraphics(width, height, P3D);
        pg.colorMode(HSB, 1,1,1,1);
    }

    public void draw() {
        pg.beginDraw();
        pg.background(0);
        pg.translate(width*.5f, height*.5f);
        mouseRotation(pg);
        updateShader(true);
        updatePlane();
        updateShader(false);
        updatePlane();
        resetShader();
        pg.resetShader();
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    private void updatePlane() {
        int count = sliderInt("count", 150);
        float size = slider("size", 450);
        for (int i = 0; i < count; i++) {
            float inorm = clampNorm(i, 0, count - 1);
            float inormNext = clampNorm(i+1, 0, count-1);
            float y = lerp(-size, size, inorm);
            float nextY = lerp(-size, size, inormNext);
            pg.beginShape(TRIANGLE_STRIP);
            pg.fill(picker("fill").clr());
            pg.noStroke();
            for (int vertexIndex = 0; vertexIndex < count; vertexIndex++) {
                float vertexNorm = clampNorm(vertexIndex, 0, count-1);
                float x = lerp(-size, size, vertexNorm);
                pg.attrib("jpos", x, y);
                pg.vertex(x, y);
                pg.attrib("jpos", x, nextY);
                pg.vertex(x, nextY);
            }
            pg.endShape();
        }
    }

    private void updateShader(boolean orientation) {
        uniform(frag, vert).set("orientation", orientation);
        uniform(frag, vert).set("time", t);
        hotShader(frag, vert, pg);
    }
}
