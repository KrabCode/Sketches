package _2020_06;

import applet.KrabApplet;
import processing.core.PGraphics;

public class GradientSea extends KrabApplet {
    private PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
//        size(1000,1000, P2D);
        fullScreen(P2D);
    }

    public void setup() {
        if (width < displayWidth) {
            surface.setLocation(displayWidth-1020,20);
            surface.setAlwaysOnTop(true);
        }
        pg = createGraphics(width, height, P2D);
    }

    public void draw() {
        pg.beginDraw();
        framesToRecord = sliderInt("record frames", 360);
        String shader = "shaders/_2020_06/GradientShader/gradient.glsl";
        uniform(shader).set("time", t);
        uniform(shader).set("background", gradient("gradient"));
        uniform(shader).set("noiseTex", gradient("noise"));
        hotFilter(shader, pg);
        pg.image(gradient("over"), 0, 0);
        pg.endDraw();
        clear();
        image(pg, 0, 0, width, height);
        rec(pg);
        gui();
    }
}
