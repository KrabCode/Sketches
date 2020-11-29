package _2020_11;

import applet.KrabApplet;
import processing.core.PGraphics;

public class Julia extends KrabApplet {
    private PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
//        size(800, 800, P3D);
        fullScreen(P3D);
    }

    public void setup() {
        toggleFullscreen();
    }

    public void draw() {
        pg = updateGraphics(pg);
        pg.beginDraw();
        String shader = "shaders/_2020_11/julia.glsl";
        uniform(shader).set("time", t);
        uniform(shader).set("detailBase", (float)sliderInt("detail base", 200));
        uniform(shader).set("detailRange", (float)sliderInt("detail range", 50));
        uniform(shader).set("uvPos", sliderXY("view position", 0));
        uniform(shader).set("zoom", slider("zoom", 0.5f));
        uniform(shader).set("cPos", sliderXY("fractal position", 0.1f));
        uniform(shader).set("cRange", slider("fractal range", 0));
        uniform(shader).set("palette", gradient("palette"));
        hotFilter(shader, pg);
        pg.endDraw();
        clear();
        image(pg, 0, 0);
        rec(pg, sliderInt("frames", 360));
        gui();
    }
}
