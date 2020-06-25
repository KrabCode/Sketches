package _2020_06;

import applet.KrabApplet;
import processing.core.PGraphics;

public class Unrelated extends KrabApplet {
    private PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
//        fullScreen(P2D);
        size(1000, 1000, P3D);
    }

    public void setup() {
        pg = createGraphics(width, height, P2D);
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
        translateToCenter(pg);
        translate2D(pg);
        pg.imageMode(CENTER);
        pg.blendMode(ADD);
        pg.image(gradient("ADD 1", width*2, height*2), 0, 0);
        pg.image(gradient("ADD 2", width*2, height*2), 0, 0);
        pg.blendMode(SUBTRACT);
        pg.image(gradient("SUBTRACT 1", width*2, height*2), 0, 0);
        pg.image(gradient("SUBTRACT 2", width*2, height*2), 0, 0);
        pg.popStyle();
        fbmDisplacePass(pg);
        pg.endDraw();
        resetShader();
        rgbSplitScaleAndOffset(pg);
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

}
