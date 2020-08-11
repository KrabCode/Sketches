package _2020_08;

import applet.KrabApplet;
import processing.core.PGraphics;

public class SplashyMetaballs extends KrabApplet {
    private PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
//        fullScreen(P3D);
        size(1000,1000, P2D);
    }

    public void setup() {
        surface.setLocation(displayWidth-width, 0);
        surface.setAlwaysOnTop(true);
    }

    public void draw() {
        pg = updateGraphics(pg);
        pg.beginDraw();
        displayMetaballs();
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg, sliderInt("frames", 360));
        gui();
    }

    private void displayMetaballs() {
        String metaballShader = "shaders/_2020_08/splashyMetaball.glsl";
        uniform(metaballShader).set("time", t);
        uniform(metaballShader).set("gradient", gradient("gradient"));
        hotFilter(metaballShader, pg);
    }
}
