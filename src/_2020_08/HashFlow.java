package _2020_08;

import applet.KrabApplet;
import processing.core.PGraphics;

public class HashFlow extends KrabApplet {
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
        hashShader();
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg, sliderInt("frames", 360));
        gui();
    }

    private void hashShader() {
        String hashShader = "shaders/_2020_08/hashFlow.glsl";
        uniform(hashShader).set("time", t);
        hotFilter(hashShader, pg);
    }
}
