package _2021_03;

import applet.KrabApplet;
import processing.core.PGraphics;

public class PixelWallpaper extends KrabApplet {
    private PGraphics pg;

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
        String shaderPath = "shaders/_2021_03/pixelWallpaper.glsl";
        uniform(shaderPath).set("time", t);
        hotFilter(shaderPath);
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg, sliderInt("frames", 360));
        gui(false);
    }
}
