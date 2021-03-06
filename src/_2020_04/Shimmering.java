package _2020_04;

import applet.KrabApplet;
import processing.core.PGraphics;

public class Shimmering extends KrabApplet {
    private PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(800, 800, P2D);
    }

    public void setup() {
        pg = createGraphics(width, height, P2D);
        surface.setAlwaysOnTop(true);
    }

    public void draw() {
        pg.beginDraw();
        pg.background(0);
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }
}
