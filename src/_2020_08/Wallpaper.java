package _2020_08;

import applet.KrabApplet;
import processing.core.PGraphics;

public class Wallpaper extends KrabApplet {
    private PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        fullScreen(P3D);
    }

    public void setup() {
    }

    public void draw() {
        pg = updateGraphics(pg);
        pg.beginDraw();
        pg.image(gradient("background"), 0, 0);
        hashPass(pg);
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg, sliderInt("frames", 360));
        gui();
    }

    protected void hashPass(PGraphics pg){
        String hashShader = "shaders/filters/hash.glsl";
        uniform(hashShader).set("time", t);
        uniform(hashShader).set("pixelate", sliderInt("pixelate", 100, 1, 10000));
        hotFilter(hashShader, pg);
    }
}
