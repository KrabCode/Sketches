package _2021_10;

import applet.KrabApplet;
import processing.core.PGraphics;

public class Ctvercuji extends KrabApplet{
    private PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        fullScreen(P3D);
    }

    public void setup() {
        toggleFullscreen();
    }

    public void draw() {
        pg = updateGraphics(pg);
        pg.beginDraw();
        pg.imageMode(CENTER);
        pg.colorMode(HSB,1,1,1,1);
        pg.blendMode(BLEND);
        pg.textAlign(CENTER, CENTER);
        pg.textSize(slider("font size", 64));
        translateToCenter(pg);
        pg.image(gradient("background"), 0, 0);
        pg.fill(1);
        pg.text("od soboty čtvercuji\njsem úplně high", 0, 0);

        pg.noFill();
        pg.strokeCap(SQUARE);
        pg.strokeWeight(slider("weight", 3));
        picker("fill").addHue(slider("add hue"));
        pg.rectMode(CENTER);
        int count = sliderInt("count", 100);
        float size = slider("size", 10);
        HSBA hsba = picker("fill");
        for (int i = 0; i < count; i++) {
            float x = map(i, 0, count - 1, -width/2f, width/2f);
            float hash = hash(i*slider("hash", 1431));
            pg.pushMatrix();
            float y = -height*0.8f+(hash * height*2 + t * hash * slider("fall speed"))%(height * 2);
            pg.translate(x,y);
            pg.rotate(hash * TAU * 4 + -t + t * 2 * hash);
            pg.stroke(hueModulo(hsba.hue() + hash * slider("color variation", 0.1f)), hsba.sat(), hsba.br(), hsba.alpha());
            pg.rect(0,0,size,size);
            pg.popMatrix();
        }
pg.endDraw();
        image(pg, 0, 0);
        rec(pg, sliderInt("frames", 360));
        gui();
    }
}
