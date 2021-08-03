package _2021_07;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

public class HanddrawnTest extends KrabApplet {
    private PGraphics pg;
    private PImage bgImg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        fullScreen(P2D);
    }

    public void setup() {
//        bgImg = loadImage("images/test/trees.jpg");
        bgImg = loadImage("images/handDrawnFlowField/notepad_small.jpg");
        bgImg.resize(floor(bgImg.width * scale), floor(bgImg.height * scale));
        toggleFullscreen(floor(1200 * scale), floor(1600 * scale));
        if (width < displayWidth) {
            surface.setAlwaysOnTop(true);
        }
    }
    float scale = 0.8f;

    public void draw() {
        background(0);
        pg = updateGraphics(pg);

        pg.beginDraw();
        pg.colorMode(RGB, 1, 1, 1, 1);
        pg.image(bgImg, 0, 0);
        group("a");
        PVector posA = sliderXY("pos", 0, 0);
        PVector sizeA = sliderXY("size", 100, 100);;
        PVector targetA = sliderXY("target");
        boolean debugA = toggle("debug a");
        group("b");
        PVector posB = sliderXY("pos", 0, 0);
        PVector sizeB = sliderXY("size", 100, 100);
        PVector targetB = sliderXY("target");
        boolean debugB = toggle("debug b");
        String chromaKey = "shaders/filters/chromaKey.glsl";
        resetGroup();
        uniform(chromaKey).set("black", options("black", "white").equals("black"));
        PGraphics treeGraphicsA = getRectangleAsShadedCanvas(bgImg, chromaKey, 0, posA, sizeA);
        PGraphics treeGraphicsB = getRectangleAsShadedCanvas(bgImg, chromaKey, 0, posB, sizeB);
        if (debugA) {
            pg.stroke(0, 0, 1);
            pg.strokeWeight(4);
            pg.noFill();
            pg.rect(posA.x, posA.y, sizeA.x, sizeA.y);
        }
        if (debugB) {
            pg.stroke(0, 1, 0);
            pg.strokeWeight(4);
            pg.noFill();
            pg.rect(posB.x, posB.y, sizeB.x, sizeB.y);
        }
        pg.noStroke();
        pg.image(treeGraphicsA, targetA.x, targetA.y);
        pg.image(treeGraphicsB, targetB.x, targetB.y);
        pg.endDraw();
        image(pg, 0, 0, width, height);
        rec(pg);
        gui(false);
    }
}
