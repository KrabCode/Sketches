package _2020_05;

import applet.KrabApplet;
import processing.core.PGraphics;

public class Freeform extends KrabApplet {
    private PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
//        size(1000, 1000, P3D);
        fullScreen(P3D);
    }

    public void setup() {
        pg = createGraphics(width, height, P3D);
//        surface.setAlwaysOnTop(true);
    }

    public void draw() {
        background(0);
        pg.beginDraw();
        updateFilter();
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    private void updateFilter() {
        String frag = "shaders/_2020_05/noise.glsl";
        uniformColorPalette(frag);
        uniform(frag).set("time", t);
        hotFilter(frag, pg);
    }

    private void drawBackground() {
        pg.beginShape();
        pg.noStroke();
        pg.fill(picker("top").clr());
        pg.vertex(0,0);
        pg.vertex(width,0);
        pg.fill(picker("bot").clr());
        pg.vertex(width,height);
        pg.vertex(0, height);
        pg.endShape(CLOSE);
    }
}
