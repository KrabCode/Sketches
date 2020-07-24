package _2020_07;

import applet.KrabApplet;
import processing.core.PGraphics;

public class TShirt extends KrabApplet {
    private PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
//        size(1000, 1000, P3D);
        fullScreen(P3D);
    }

    public void setup() {

    }

    public void draw() {
        int resMultiplier = sliderInt("res mult", 1);
        pg = updateGraphics(pg, width*resMultiplier, height*resMultiplier);
        pg.beginDraw();
        String grainy = "shaders/_2020_07/TShirt/grainy.glsl";
        uniform(grainy).set("time", t*slider("time speed"));
        uniform(grainy).set("gradient1", gradient("1"));
        uniform(grainy).set("gradient2", gradient("2"));
        hotFilter(grainy, pg);
        pg.endDraw();
        image(pg, 0, 0, width, height);
        rec(pg, sliderInt("frames", 360));
        gui();
    }
}
