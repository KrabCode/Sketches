package _2021_09;

import applet.KrabApplet;
import processing.core.PGraphics;

public class Exclusion extends KrabApplet {
    PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        fullScreen(P3D);
    }

    public void setup() {
        toggleFullscreen();
        surface.setAlwaysOnTop(true);
    }

    public void draw() {
        pg = updateGraphics(pg);
        pg.beginDraw();
        pg.image(gradient("background"), 0, 0);
        String shaderPath = "shaders/_2021_09/exclusion.glsl";
        uniform(shaderPath).set("time", t);
        hotFilter(shaderPath, pg);
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg, sliderInt("frames", 360));
        gui(false);
    }
}
