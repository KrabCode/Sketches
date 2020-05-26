package _2020_05;

import applet.KrabApplet;
import processing.core.PGraphics;

public class NoiseSplit extends KrabApplet {
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
        fadeToBlack(pg);
        updateNoise();
        pg.endDraw();
        PGraphics split = colorSplit(pg, false);
        clear();
        image(split, 0, 0, width, height);
        rec(split);
        gui();
    }

    private void updateNoise() {
        String noiseShader = "shaders/_2020_05/noiseSplit/noise.glsl";
        uniformRamp(noiseShader);
        uniform(noiseShader).set("time", t);
        hotFilter(noiseShader, pg);
    }
}
