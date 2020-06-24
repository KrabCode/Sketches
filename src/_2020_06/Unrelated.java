package _2020_06;

import applet.KrabApplet;
import processing.core.PConstants;
import processing.core.PGraphics;

public class Unrelated extends KrabApplet {
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
        if(button("clear")){
            pg.clear();
        }
        fadeToBlack(pg);
        translate2D(pg);
        pg.pushStyle();
        pg.blendMode(ADD);
        pg.image(gradient("ADD"), 0, 0);
        pg.blendMode(SUBTRACT);
        pg.image(gradient("SUBTRACT"), 0, 0);
        pg.popStyle();
        updateShader();
        pg.endDraw();
        resetShader();
        rgbSplitScaleAndOffset(pg);
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    void updateShader() {
        String shaderPath = "shaders/_2020_06/Unrelated/unrelated.glsl";
        uniform(shaderPath).set("time", t);
        hotFilter(shaderPath, pg);
    }
}
