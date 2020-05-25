package _2020_05;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class ColorSplit extends KrabApplet {
    private PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(1000, 1000, P3D);
    }

    public void setup() {
        pg = createGraphics(width, height, P3D);
        surface.setAlwaysOnTop(true);
    }

    public void draw() {
        pg.beginDraw();
        pg.background(0);
        style(pg);
        PVector size = sliderXY("size", 200);
        pg.ellipse(width/2f, height/2f, size.x, size.y);
        pg.endDraw();
        colorSplit(pg, true);
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

}
