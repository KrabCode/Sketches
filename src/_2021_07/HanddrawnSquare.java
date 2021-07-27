package _2021_07;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

public class HanddrawnSquare extends KrabApplet {
    private PGraphics pg;
    private PImage bg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        fullScreen(P3D);
    }

    public void setup() {
        int originalResolutionX = 3024;
        int originalResolutionY = 4032;
        float scale = 0.5f;
        int w = floor(originalResolutionX * scale);
        int h = floor(originalResolutionY * scale);
        toggleFullscreen(w, h);
        bg = loadImage("images/square/square.jpg");
        bg.resize(w, h);
    }

    float time = 0;

    public void draw() {
        pg = updateGraphics(pg);
        pg.beginDraw();
        pg.imageMode(CORNER);
        pg.image(bg, 0, 0);
        String chromaKeyShader = "shaders/filters/chromaKey.glsl";
        uniform(chromaKeyShader).set("keepBlack", false);
        PGraphics canvas = getRectangleAsShadedCanvas(bg, chromaKeyShader, sliderXY("pos"), sliderXY("size", 10));
        PVector center = sliderXY("center", width/2f, height/2f);
        int copies = sliderInt("copies", 10);
        float scale = slider("scale change", 1);
        float rotation = slider("rotation base");
        float timeSpeed = slider("rotation speed");
        time += timeSpeed;
        PVector offset = sliderXY("position change");
        pg.translate(center.x, center.y);
        pg.imageMode(CENTER);
        for (int i = 0; i < copies; i++) {
            pg.scale(scale);
            pg.translate(offset.x, offset.y);
            pg.rotate(rotation + time);
            pg.image(canvas, 0, 0);
        }
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg, sliderInt("frames", 360));
        gui();
    }
}
