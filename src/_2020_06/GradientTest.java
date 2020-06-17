package _2020_06;

import applet.KrabApplet;
import processing.core.PGraphics;

public class GradientTest extends KrabApplet {
    private PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(1000, 1000, P2D);
//        fullScreen(P2D);
    }

    public void setup() {
        if (width < displayWidth) {
            surface.setLocation(displayWidth-1020, 20);
            surface.setAlwaysOnTop(true);
        }
        pg = createGraphics(width, height, P2D);
    }

    public void draw() {
        pg.beginDraw();
        pg.image(gradient("grad",4, GradientType.VERTICAL), 0, 0);
        pg.endDraw();
        image(pg, 0, 0, width, height);
        rec(pg);
        gui();
    }
}
