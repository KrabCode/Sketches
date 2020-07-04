package _2020_07;

import applet.KrabApplet;
import processing.core.PApplet;
import processing.core.PGraphics;

public class FullscreenToggle extends KrabApplet {
    PGraphics pg;

    public static void main(String[] args) {
        PApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        fullScreen(P3D);
    }


    public void draw() {
        pg = matchPGraphicsToSketchSize(pg);
        pg.beginDraw();
        pg.image(gradient("background"), 0, 0);
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

}
