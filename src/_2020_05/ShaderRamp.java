package _2020_05;

import applet.KrabApplet;
import processing.core.PGraphics;

public class ShaderRamp extends KrabApplet {

    private PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(1000, 1000, P2D);
    }

    public void setup() {
        surface.setAlwaysOnTop(true);
        pg = createGraphics(width, height, P2D);
        pg.beginDraw();
        pg.background(0);
        pg.endDraw();
    }

    public void draw() {
        pg.beginDraw();
        pg.background(0);
        rampShader();
        pg.endDraw();
        colorSplit(pg, true);
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    private void rampShader() {
        String frag = "shaders/_2020_05/ramp.glsl";
        uniform(frag).set("time", t);
        uniformRamp(frag, "shader ramp", 4);
        hotFilter(frag, pg);
    }
}
