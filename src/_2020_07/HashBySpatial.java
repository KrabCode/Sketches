package _2020_07;

import applet.KrabApplet;
import processing.core.PGraphics;

public class HashBySpatial extends KrabApplet {
    private PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(1000, 1000, P3D);
    }

    public void setup() {
    }

    public void draw() {
        pg = updateGraphics(pg);
        pg.beginDraw();
        pg.image(gradient("background"), 0, 0);
        String atagenHash = "shaders/noise/hashBySpatial.glsl";
        uniform(atagenHash).set("time", t);
        hotFilter(atagenHash, pg);
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg, sliderInt("frames", 360));
        gui();
    }
}
