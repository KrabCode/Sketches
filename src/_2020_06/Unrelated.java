package _2020_06;

import applet.KrabApplet;
import processing.core.PGraphics;

public class Unrelated extends KrabApplet {
    private PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(1000, 1000, P3D);
    }

    public void setup() {
        pg = createGraphics(width, height, P3D);
        if (width < displayWidth) {
            surface.setAlwaysOnTop(true);
            surface.setLocation(2560 - 1020, 20);
        }
    }

    public void draw() {
        frameRecordingDuration = sliderInt("frames", 1000);
        pg.beginDraw();
        if (button("clear")) {
            pg.clear();
        }
        fadeToBlack(pg);

        multiplyPass(pg);

        pg.pushStyle();
        pg.blendMode(ADD);
        translateToCenter(pg);
        translate2D(pg);
        pg.imageMode(CENTER);
        pg.image(gradient("ADD", width*2, height*2), 0, 0);
//        pg.pushMatrix();
//        translateToCenter(pg);
//        drawMandala();
//        pg.popMatrix();
        pg.blendMode(SUBTRACT);
        pg.image(gradient("SUBTRACT", width*2, height*2), 0, 0);
        pg.popStyle();
        flowShader();
        pg.endDraw();
        resetShader();
        rgbSplitScaleAndOffset(pg);
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    private void multiplyPass(PGraphics pg) {
        String multiplyFrag = "shaders/filters/multiply.glsl";
        uniform(multiplyFrag).set("amt", slider("multiply", 1));
        hotFilter(multiplyFrag, pg);
    }

    void flowShader() {
        String shaderPath = "shaders/_2020_06/Unrelated/unrelated" + (options("1", "2").equals("2") ? "_2" : "") + ".glsl";
        uniform(shaderPath).set("time", t);
        hotFilter(shaderPath, pg);
    }

    private void drawMandala() {
        group("mandala");
        translate2D(pg);
        preRotate(pg, "rotate", t);
        pg.blendMode(options("add", "sub").equals("add") ? ADD : SUBTRACT);
        int count = sliderInt("count");
        float baseRadius = slider("base radius");
        float freq = slider("freq");
        float amp = slider("amp");
        float size = slider("size");
        for (int i = 0; i < count; i++) {
            float iNorm = norm(i, 0, count);
            float r = baseRadius + amp * sin(freq * t + iNorm * TAU * slider("angle offset"));
            float theta = map(i, 0, count, 0, TAU);
            pg.strokeWeight(slider("weight"));
            pg.stroke(picker("stroke").clr());
            pg.fill(picker("fill").clr());
            pg.ellipse(r * cos(theta), r * sin(theta), size, size);
        }
        resetGroup();
    }
}
