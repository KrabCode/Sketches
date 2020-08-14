package _2020_08;

import applet.KrabApplet;
import processing.core.PGraphics;

public class Atan2 extends KrabApplet {
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
        pg.clear();
        String atanShader = "shaders/_2020_08/atan.glsl";
        uniform(atanShader).set("time", t);
        uniform(atanShader).set("grad", gradient("grad"));
        hotFilter(atanShader, pg);
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg, sliderInt("frames", 360));
        gui();
    }
}
