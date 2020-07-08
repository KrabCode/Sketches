package _2020_07;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PImage;

// https://en.wikipedia.org/wiki/Alice_in_Wonderland_syndrome

public class AIWS extends KrabApplet {
    private PGraphics pg;
    private PGraphics bg;
    private PGraphics boy;
    private PImage boyImage;

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
        bg = createGraphics(width, height, P2D);
        boyImage = loadImage("images/people/boy_crop.png");
        boy = createGraphics(boyImage.width, boyImage.height, P2D);
    }

    public void draw() {
        frameRecordingDuration = sliderInt("frames", 1000);
        updatePGraphics();
        image(pg, 0, 0, width, height);
        image(pg, 0, 0, width, height);
        rec(pg);
        gui();
    }

    private void updateBoy() {
        boy.beginDraw();
        boy.clear();
        boy.image(boyImage, 0, 0);
        String whiteToTransparent = "shaders/_2020_07/AIWS/whiteToTransparent.glsl";
        uniform(whiteToTransparent).set("gradient", gradient("boy"));
        hotFilter(whiteToTransparent, boy);
        boy.endDraw();
        resetGroup();
    }

    private void updatePGraphics() {
        pg.beginDraw();
        int count = sliderInt("count", 4);
        for (int i = 0; i < count; i++) {
            pg.image(gradient("gradient " + i, 0), 0, 0);
        }
        updateBoy();
        translate2D(pg);
        float size = slider("size");
        pg.image(boy, 0, 0, boy.width*size, boy.height*size);
        pg.endDraw();
    }
}
