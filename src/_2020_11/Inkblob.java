package _2020_11;

import applet.KrabApplet;
import processing.core.PGraphics;

public class Inkblob extends KrabApplet {
    private PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        fullScreen(P2D);
    }

    public void setup() {

    }

    public void draw() {
        pg = updateGraphics(pg, width, height, P2D);
        pg.beginDraw();
        HSBA bgColor = picker("background", 0);
        HSBA strokeColor = picker("stroke", 1);
        HSBA eraserColor = picker("eraser", 0);
        float weight = slider("weight", 20);
        int detail = sliderInt("detail", 500);
        if(frameCount < 5 || button("reset")) {
            pg.clear();
            pg.background(bgColor.clr());
        }
        if(mousePressedOutsideGui && mouseButton == LEFT) {
            pg.fill(strokeColor.clr());
            pg.noStroke();
            drawLerpedEllipse(detail, weight);
        }
        if(mousePressedOutsideGui && mouseButton == RIGHT) {
            pg.fill(eraserColor.clr());
            pg.noStroke();
            drawLerpedEllipse(detail, weight);
        }
        pg.endDraw();
        clear();
        image(pg, 0, 0, width, height);
        String blobs = "E:\\Sketches\\data\\shaders\\_2020_11\\blobs.glsl";
        group("filter");
        uniform(blobs).set("time", t);
        uniform(blobs).set("timeRadius", slider("time radius", 20));
        uniform(blobs).set("timeSpeed", slider("time speed", .025f));
        uniform(blobs).set("noiseFrequency", slider("noise frequency", 10));
        uniform(blobs).set("noiseMagnitude", slider("noise amplitude", .0025f));
        uniform(blobs).set("noiseAngleRange", slider("noise angle range", TAU));
        uniform(blobs).set("noiseFreqMultiplier", slider("noise freq mult", 2));
        uniform(blobs).set("noiseAmpMultiplier", slider("noise amp mult", .5f));
        uniform(blobs).set("noiseDetail", sliderInt("noise detail", 6));

        hotFilter(blobs, g);
        resetShader();
        rec(g, sliderInt("frames", 360));
        gui();
    }

    private void drawLerpedEllipse(float detail, float weight) {
        for(int i = 0; i < detail; i++) {
            float normI = norm(i, 0, detail-1);
            float x = lerp(pmouseX, mouseX, normI);
            float y = lerp(pmouseY, mouseY, normI);
            pg.ellipse(x, y, weight, weight);
        }
    }
}
