package _2021_08;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import utils.OpenSimplexNoise;

public class HanddrawnMandala extends KrabApplet {
    private PGraphics pg;
    private PImage bg, fg;
    float time = 0;
    private OpenSimplexNoise osn = new OpenSimplexNoise();

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        fullScreen(P3D);
    }

    public void setup() {
        bg = loadImage("images/handdrawn/bg.jpg");
        fg = loadImage("images/handdrawn/fg.jpg");
        toggleFullscreen();
    }

    public void draw() {
        pg = updateGraphics(pg);
        pg.beginDraw();
        pg.imageMode(CORNER);
        pg.tint(255, slider("transparency", 100));
        pg.image(bg, 0, 0, width, height);
        pg.noTint();
        translateToCenter(pg);
        translate2D(pg, "center");
        int particles = sliderInt("count", 100);
        int imageCount = 4;
        float radius = slider("radius", 350);
        float scl = slider("scale", 1);
        float dotRatio = slider("dot ratio", 1);
        int mirrors = sliderInt("mirrors", 1);
        time += radians(1) * slider("time", 1);
        for (int i = 0; i < particles; i++) {
            int imageIndex = i % imageCount;
            if(i > particles * dotRatio){
                imageIndex = 3;
            }
            PGraphics img = get(imageIndex);
            float theta = map(i, 0, particles -1, 0, TAU);
            float x = radius * cos(slider("x", 12.1f ) * theta + time);
            float y = radius * sin(slider("y", 8.3f ) * theta + time);
            for (int j = 0; j < mirrors; j++) {
                float mirrorAngle = map(j, 0, mirrors, 0, TAU);
                pg.pushMatrix();
                pg.imageMode(CENTER);
                pg.rotate(mirrorAngle);
                pg.translate(x, y);
                pg.rotate(i + time);
                pg.scale(scl);
                pg.image(img, 0, 0);
                pg.popMatrix();
            }
        }
        resetGroup();
        chromaticAberrationPass(pg);
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg, sliderInt("frames", 360));
        gui();
    }

    PGraphics get(int i){
        String chromaKeyShader = "shaders/_2021_08/chromaKey.glsl";
        uniform(chromaKeyShader).set("black", toggle("black"));
        uniform(chromaKeyShader).set("useBounds", true);
        uniform(chromaKeyShader).set("lowBound", slider("low bound", 0.35f));
        uniform(chromaKeyShader).set("highBound", slider("high bound", 1f));
        group("i " + i);
        PGraphics result = getRectangleAsShadedCanvas(fg, chromaKeyShader, i, sliderXY("pos"), sliderXY("size", 250));
        resetGroup();
        return result;
    }
}
