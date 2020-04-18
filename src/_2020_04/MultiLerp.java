package _2020_04;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class MultiLerp extends KrabApplet {
    private PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(new Object() {
        }.getClass().getEnclosingClass().getName());
    }

    public void settings() {
        size(800, 800, P2D);
    }

    public void setup() {
        pg = createGraphics(width, height, P2D);
        surface.setAlwaysOnTop(true);
    }

    public void draw() {
        pg.beginDraw();
        pg.background(0);
        pg.beginShape();
        pg.strokeWeight(5);
        pg.stroke(255);
        pg.noFill();
        int count = 150;
        for (int i = 0; i < count; i++) {
            float norm = clampNorm(i, 0, count-1);
            PVector pos = lerpMany(norm, sliderXY("1"), sliderXY("2"), sliderXY("3"));
            pg.vertex(pos.x, pos.y);
        }
        pg.endShape();
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }


}
