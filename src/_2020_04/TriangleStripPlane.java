package _2020_04;

import applet.KrabApplet;
import processing.core.PConstants;
import processing.core.PGraphics;

public class TriangleStripPlane extends KrabApplet {
    private PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(800, 800, P3D);
//        fullScreen(P3D, 2);
    }

    public void setup() {
        surface.setAlwaysOnTop(true);
        pg = createGraphics(width, height, P3D);
        pg.colorMode(HSB, 1,1,1,1);
    }

    public void draw() {
        pg.beginDraw();
        updateShader();
        updateLines();
        resetShader();
        blurPass(pg);
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    private void updateLines() {
        int count = sliderInt("count", 150);
        float buffer = 50;
        for (int i = 0; i < count; i++) {
            float inorm = clampNorm(i, 0, count - 1);
            float inormNext = clampNorm(i+1, 0, count-1);
            float y = lerp(slider("top Y", -buffer), slider("bottom Y", height + buffer), inorm);
            float nextY = lerp(slider("top Y", -buffer), slider("bottom Y", height + buffer), inormNext);
            pg.beginShape(TRIANGLE_STRIP);
            pg.fill(1);
            pg.noStroke();
            for (int vertexIndex = 0; vertexIndex < count; vertexIndex++) {
                float vertexNorm = clampNorm(vertexIndex, 0, count-1);
                float x = lerp(-buffer, width + buffer, vertexNorm);
                pg.vertex(x, y);
                pg.vertex(x, nextY);
            }
            pg.endShape();
        }
    }

    private void updateShader() {
        String frag = "shaders/_2020_04/lines/ColorFrag.glsl";
        String vert = "shaders/_2020_04/lines/ColorVert.glsl";
        uniformColorPalette(frag, vert);
        uniform(frag, vert).set("time", t);
        hotShader(frag, vert, pg);
    }
}
