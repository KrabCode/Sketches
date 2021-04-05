package _2021_04;

import applet.KrabApplet;
import processing.core.PGraphics;

public class Saturday extends KrabApplet {
    private PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        fullScreen(P3D);
    }

    public void setup() {
        toggleFullscreen();
    }

    public void draw() {
        pg = updateGraphics(pg);
        pg.beginDraw();
        String shader = "shaders/_2021_04/matrix.glsl";
        uniform(shader).set("time", t);
        hotFilter(shader, pg);
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg, sliderInt("frames", 360));
        gui();
    }


}
