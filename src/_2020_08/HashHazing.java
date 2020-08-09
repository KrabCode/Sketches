package _2020_08;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PImage;

/**
 * Attempt to replicate a landscape oil painting with a tree with falling leaves, some terrain and a nice depth gradient
 */
public class HashHazing extends KrabApplet {
    private PGraphics pg;
    private PImage img;
    float timeOffset = 0;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        fullScreen(P3D);
    }

    public void setup() {
        img = loadImage("E:\\Sketches\\out\\image\\20200808-235407_LoadingIcon_1.jpg");
    }

    public void draw() {
        pg = updateGraphics(pg);
        pg.beginDraw();
//        updateTerrain();
        updateHash();
        pg.endDraw();
        rgbSplitScaleAndOffset(pg);
        image(pg, 0, 0);
        rec(pg, sliderInt("frames", 360));
        gui();
    }

    private void updateHash() {
        String hash = "shaders/_2020_08/grainyFilter.glsl";
        uniform(hash).set("time", t);
        uniform(hash).set("gradient", gradient("gradient"));
        uniform(hash).set("img", img);
        uniform(hash).set("graininess", slider("grain strength", .1f));
        uniform(hash).set("grainSize", slider("grain size", 200));
        hotFilter(hash, pg);
    }

    private void updateTerrain() {
        int triangleStripCount = sliderInt("count", 8);
        timeOffset = t / sliderInt("time", 1, 1, 10);
        float prevY = 0;
        for (int stripIndex = 0; stripIndex < triangleStripCount; stripIndex++) {
            float iNorm = norm(stripIndex, 0, triangleStripCount - 1);
            float y = iNorm * height;
            int triangleStripDetail = sliderInt("triangle detail", 30);
            pg.beginShape(TRIANGLE_STRIP);
            for (int triangleIndex = 0; triangleIndex < triangleStripDetail; triangleIndex++) {
                float x = map(triangleIndex, 0, triangleStripDetail-1, 0, width);
                float slope = abs(width/2f-x) * slider("slope");
                float colorNorm = (iNorm + timeOffset + slope) % 1;
                pg.noStroke();
                pg.fill(gradientColorAt("gradient", colorNorm));
                pg.vertex(x, y);
                pg.vertex(x, prevY);
            }
            pg.endShape();
            prevY = y;
        }
    }
}
