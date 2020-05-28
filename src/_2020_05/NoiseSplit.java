package _2020_05;

import applet.KrabApplet;
import processing.core.PFont;
import processing.core.PGraphics;

public class NoiseSplit extends KrabApplet {
    private PGraphics pg;
    private PFont courierNew;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(1000, 1000, P2D);
//        fullScreen(P2D);
    }

    public void setup() {
        if (width < displayWidth) {
            surface.setAlwaysOnTop(true);
        }
        pg = createGraphics(width, height, P2D);
        courierNew = createFont("Courier New Bold", 64);
    }

    public void draw() {
        pg.beginDraw();
        fadeToBlack(pg);
        blurPass(pg);
        updateNoise();
        pg.textAlign(LEFT,TOP);
        pg.fill(picker("fill").clr());
        pg.textFont(courierNew);
        pg.textSize(slider("text size", 64));
        pg.textLeading(slider("spacing"));
        translateToCenter(pg);
        translate2D(pg);
        pg.text(textInput("main text"), 0, 0);
        pg.endDraw();
        PGraphics split = colorSplit(pg, true);
        clear();
        image(split, 0, 0, width, height);
        frameRecordingDuration = sliderInt("video frames", frameRecordingDuration);
        rec(split);
        gui();
    }

    private void updateNoise() {
        String noiseShader = "shaders/_2020_05/noiseSplit/noise.glsl";
        uniformRamp(noiseShader);
        uniform(noiseShader).set("time", t);
        uniform(noiseShader).set("colorStrength", slider("color strength",.08f));
        uniform(noiseShader).set("translateAtan", sliderXY("angle center"));
        hotFilter(noiseShader, pg);
    }
}