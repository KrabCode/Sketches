package _2020_05;

import applet.KrabApplet;
import processing.core.PGraphics;

public class Pyramid extends KrabApplet {
    private PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(800, 800, P3D);
    }

    public void setup() {
        pg = createGraphics(width, height, P3D);
        surface.setAlwaysOnTop(true);
    }

    public void draw() {
        pg.beginDraw();
        pg.background(0);
        translateToCenter(pg);
        preRotate(pg);
        pg.strokeWeight(slider("weight", 1));
        pg.stroke(picker("stroke").clr());
        pg.fill(picker("fill").clr());
        pg.beginShape(TRIANGLE_FAN);
        int corners = sliderInt("corners", 4);
        float radius = slider("radius", 100);
        float elevation = slider("elevation", 50);
        pg.vertex(0,elevation,0);
        for (int i = 0; i <= corners; i++) {
            float angle = (TWO_PI/corners)*i;
            pg.vertex(radius*cos(angle), 0, radius*sin(angle));
        }
        pg.endShape();
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }
}
