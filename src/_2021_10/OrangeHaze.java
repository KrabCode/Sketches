package _2021_10;

import applet.KrabApplet;
import processing.core.PGraphics;

public class OrangeHaze extends KrabApplet {

    PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        fullScreen(P3D);
    }

    public void setup() {
        toggleFullscreen();
        pg = createGraphics(width, height, P3D);
    }

    public void draw() {
        pg.beginDraw();
        pg.image(gradient("background"), 0, 0);
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg, sliderInt("frames", 360));
        gui();
    }
}
