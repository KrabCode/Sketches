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
        bgImg = loadImage("images/test/trees.jpg");
        float size = 0.3f;
        toggleFullscreen(floor(4032 * size), floor(3024 * size));
        if (width < displayWidth) {
            surface.setAlwaysOnTop(true);
        }
    }

    public void draw() {
        pg = updateGraphics(pg);

        pg.beginDraw();
        pg.colorMode(RGB, 1, 1, 1, 1);
        pg.image(bgImg, 0, 0, width, height);
        PVector pos = sliderXY("pos", 0, 0);
        PVector size = sliderXY("size", 100, 100);
        PGraphics treeGraphics = getRectangleAsShadedCanvas(bgImg, "shaders/_2021_07/test/hand.glsl", pos, size);
        if (toggle("debug")) {
            pg.stroke(0, 0, 1);
            pg.strokeWeight(4);
            pg.rect(pos.x, pos.y, size.x, size.y);
        }
        pg.noStroke();
        float rectSize = 220;
        pg.fill(1, 0, 0);
        pg.rect(0, 0, rectSize, rectSize);
        pg.image(treeGraphics, 0, 0, rectSize, rectSize);
        pg.fill(0, 1, 0);
        pg.translate(rectSize, 0);
        pg.rect(0, 0, rectSize, rectSize);
        pg.image(treeGraphics, 0, 0, rectSize, rectSize);
        pg.fill(0, 0, 1);
        pg.translate(rectSize, 0);
        pg.rect(0, 0, rectSize, rectSize);
        pg.image(treeGraphics, 0, 0, rectSize, rectSize);
        pg.fill(0);
        pg.translate(rectSize, 0);
        pg.rect(0, 0, rectSize, rectSize);
        pg.image(treeGraphics, 0, 0, rectSize, rectSize);
        pg.translate(rectSize, 0);
        pg.fill(1);
        pg.rect(0, 0, rectSize, rectSize);
        pg.image(treeGraphics, 0, 0, rectSize, rectSize);
        pg.endDraw();
        image(pg, 0, 0, width, height);
        rec(pg);
        gui(false);
    }

    private PGraphics getRectangleAsShadedCanvas(PImage src, String shaderPath, PVector pos, PVector size) {
        return getRectangleAsShadedCanvas(src, shaderPath, floor(pos.x), floor(pos.y), floor(size.x), floor(size.y));
    }

    // image piece snapshot properties, maybe make a map with key: (src and coords) and value: (localImg and canvas)
    private PImage localImg;
    PGraphics canvas;
    int prevX, prevY, prevW, prevH;

    PGraphics getRectangleAsShadedCanvas(PImage src, String shaderPath, int x, int y, int w, int h) {
        canvas = updateGraphics(canvas, w, h, P3D);
        canvas.beginDraw();
        canvas.clear();
        hotShader(shaderPath, canvas);
        if(x != prevX || y != prevY || w != prevW || h != prevH){
            localImg = src.get(x,y,w,h);
        }
        canvas.image(localImg, 0, 0);
        canvas.endDraw();
        prevX = x;
        prevY = y;
        prevW = w;
        prevH = h;
        return canvas;
    }
}
