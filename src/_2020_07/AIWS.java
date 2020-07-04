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
        fullScreen(P3D);
    }

    public void setup() {
        if (width < displayWidth) {
            surface.setAlwaysOnTop(true);
        }
        boyImage = loadImage("images/people/boy_crop.png");
        boy = createGraphics(boyImage.width, boyImage.height, P2D);
    }

    public void draw() {
        pg = matchPGraphicsToSketchSize(pg);
        bg = matchPGraphicsToSketchSize(bg);
        frameRecordingDuration = sliderInt("frames", 1000);
        updateBackground();
        image(pg, 0, 0, width, height);
        rec(pg);
        gui();
    }

    private void updateBackground() {
        bg.beginDraw();
        fadeToBlack(bg);
        blurPass(pg);
        bg.image(gradient("gradient"), 0, 0);
        float size = slider("size", 1);
        fbmDisplacePass(bg);
        bg.endDraw();
        resetGroup();

        pg.beginDraw();
        pg.imageMode(CENTER);
        pg.pushMatrix();
        translateToCenter(pg);
        pg.image(bg, 0, 0);
        int copies = sliderInt("copies", 5);
        float minScale = slider("minimum scale", .5f);
        for (int i = 0; i < copies; i++) {
            pg.pushMatrix();
            pg.scale(1-map(i, 0, copies-1, 0, 1-minScale));
            pg.image(bg, 0, 0);
            pg.popMatrix();
        }
        pg.popMatrix();
        updateBoy();
        translate2D(pg);
        pg.image(boy, 0, 0, boy.width*size, boy.height*size);
        pg.endDraw();
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
}
