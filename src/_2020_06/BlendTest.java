package _2020_06;

import applet.KrabApplet;
import processing.core.PGraphics;

public class BlendTest extends KrabApplet {
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
        pg.image(gradient("background"), 0, 0);
        pg.text(textInput("text"), width / 2f, height / 2f);
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }
}
