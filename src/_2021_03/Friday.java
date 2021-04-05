package _2021_03;

import applet.KrabApplet;
import processing.core.PGraphics;

public class Friday extends KrabApplet {
    private PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        fullScreen(P3D);
    }

    public void setup() {
    }

    public void draw() {
        pg = updateGraphics(pg);
        pg.beginDraw();
        pg.image(gradient("background"), 0, 0);
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg, sliderInt("frames", 360));
        gui();
    }
}
