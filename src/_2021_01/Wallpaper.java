package _2021_01;

import applet.KrabApplet;
import processing.core.PGraphics;

public class Wallpaper extends KrabApplet {
    private PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        fullScreen(P3D);

    }

    public void setup() {
        surface.setSize(1000, 1440);
        surface.setLocation(displayWidth - width, 0);
    }

    public void draw() {
        pg = updateGraphics(pg);
        pg.beginDraw();
        String shader = "shaders/_2021_01/wallpaper.glsl";
        uniform(shader).set("time", t);
        hotFilter(shader, pg);
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg, sliderInt("frames", 360));
        gui();
    }
}
