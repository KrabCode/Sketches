package _2020_06;

import applet.KrabApplet;
import processing.core.PGraphics;

public class GradientShader extends KrabApplet {
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
        framesToRecord = 360;
        FFMPEG_ENABLED = false;
    }

    public void draw() {
        pg.beginDraw();
        String shader = "shaders/_2020_06/GradientShader/gradient.glsl";
        uniform(shader).set("time", t);
        uniform(shader).set("gradient", gradient("gradient"));
        hotFilter(shader, pg);
        pg.image(gradient("over"), 0, 0);
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }
}
