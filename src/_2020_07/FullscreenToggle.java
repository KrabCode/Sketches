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

    private void updateCanvasSize() {
        if (pg == null || pg.width != width || pg.height != height) {
            pg = createGraphics(width, height, P3D);
        }
    }

    public void draw() {
        updateCanvasSize();
        pg.beginDraw();
        pg.image(gradient("background"), 0, 0);
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

}
