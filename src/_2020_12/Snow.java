package _2020_12;

import applet.KrabApplet;
import processing.core.PGraphics;

public class Snow extends KrabApplet {
    private PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        fullScreen(P3D);
    }

    public void setup() {
        surface.setAlwaysOnTop(true);
    }

    public void draw() {
        pg = updateGraphics(pg);
        pg.beginDraw();
        pg.image(gradient("background"), 0, 0);
        String shader = "shaders/_2020_12/snow.glsl";
        uniform(shader).set("paletteForeground", gradient("fg"));
        uniform(shader).set("paletteBackground", gradient("bg"));
        uniform(shader).set("time", t);
        hotFilter(shader, pg);
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg, sliderInt("frames", 360));
        gui();
    }
}
