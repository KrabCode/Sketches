package _2020_06;

import applet.KrabApplet;
import processing.core.PGraphics;

public class CustomNoise extends KrabApplet {
    private PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(1000, 1000, P2D);
    }

    public void setup() {
        if (width < displayWidth) {
            surface.setAlwaysOnTop(true);
        }
        pg = createGraphics(width, height, P2D);
    }

    public void draw() {
        pg.beginDraw();
        pg.image(gradient("background"), 0, 0);
        String noise = "shaders/_2020_06/CustomNoise/customNoise.glsl";
        uniform(noise).set("time", t);
        hotFilter(noise, pg);
        pg.endDraw();
        image(pg, 0, 0, width, height);
        rec(pg);
        gui();
    }
}
