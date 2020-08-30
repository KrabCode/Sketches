package _2020_08;

import applet.KrabApplet;
import processing.core.PGraphics;

public class Perspective extends KrabApplet {
    private PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
//        fullScreen(P3D);
        size(1000, 1000, P2D);
        noSmooth();
    }

    public void setup() {
        surface.setAlwaysOnTop(true);
    }

    public void draw() {
        pg = updateGraphics(pg, P2D);
        pg.beginDraw();
        updateShader(pg);
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg, sliderInt("frames", 360));
        gui();
    }

    private void updateShader(PGraphics pg) {
        String lineShader = "shaders/_2020_08/perspective.glsl";
        uniform(lineShader).set("time", t);
        uniform(lineShader).set("gradient", gradient("gradient"));
        hotFilter(lineShader, pg);
    }
}
