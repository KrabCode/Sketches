package _2020_05;

import applet.KrabApplet;
import processing.core.PGraphics;

public class NoiseSplit_2 extends KrabApplet {
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
        framesToRecord = 360*sliderInt("video length");
        pg.beginDraw();
        fadeToBlack(pg);
        blurPass(pg);
        updateNoise();
        drawText();
        pg.endDraw();
        rgbSplitScaleAndOffset(pg);
        image(pg, 0, 0, width, height);
        rec(pg);
        gui();
    }

    private void drawText() {
        group("text");
        translateToCenter(pg);
        translate2D(pg);
        pg.textAlign(CENTER, CENTER);
        pg.textSize(slider("text size", 64));
        pg.fill(picker("fill").clr());
        pg.text(textInput("middle text"), 0, 0);
    }


    private void updateNoise() {
        String noiseShader = "shaders/_2020_05/noiseSplit/noise_2.glsl";
        uniformRamp(noiseShader);
        uniform(noiseShader).set("time", t);
        uniform(noiseShader).set("colorStrength", slider("color strength",.08f));
        hotFilter(noiseShader, pg);
    }
}
