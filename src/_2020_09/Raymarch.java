package _2020_09;

import applet.KrabApplet;
import processing.core.PGraphics;

public class Raymarch extends KrabApplet {
    private PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        fullScreen(P3D);
    }

    public void setup() {
    }

    @SuppressWarnings("DuplicatedCode")
    public void draw() {
        pg = updateGraphics(pg);
        pg.beginDraw();
        String raymarch = "shaders/_2020_09/raymarch.glsl";
        int raymarchShader = sliderInt("version");

        uniform(raymarch).set("time", t);
        uniform(raymarch).set("gradient", gradient("gradient"));
        hotFilter(raymarch, pg);
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg, sliderInt("frames", 360));
        gui();
    }
}
