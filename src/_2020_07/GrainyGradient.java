package _2020_07;

import applet.KrabApplet;
import processing.core.PGraphics;

public class GrainyGradient extends KrabApplet {
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
            surface.setLocation(0,0);
        }
    }

    public void draw() {
        pg = updateGraphics(pg);
        pg.beginDraw();
        String grainy = "shaders/_2020_07/GrainyGradient/grainy.glsl";
        uniform(grainy).set("time", t*slider("time speed"));
        uniform(grainy).set("gradient", gradient("gradient"));
        hotFilter(grainy, pg);
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }
}
