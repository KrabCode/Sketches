package _2020_06;

import applet.KrabApplet;
import processing.core.PGraphics;

public class Raymarch4D extends KrabApplet {
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
        pg.beginDraw();
        framesToRecord = sliderInt("frames");
        fadeToBlack(pg);
        String shader = "shaders/_2020_06/Raymarch4D/raymarch4D.glsl";
        uniform(shader).set("time", t);
        hotFilter(shader, pg);
        chromaticAberrationPass(pg);
        fbmDisplacePass(pg);
        pg.endDraw();
        rgbSplitScaleAndOffset(pg);
        image(pg, 0, 0);
        rec(pg);
        gui();
    }
}
