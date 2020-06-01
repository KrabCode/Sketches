package _2020_06;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import utils.OpenSimplexNoise;

public class NegativeSpace extends KrabApplet {
    private PGraphics pg;
    private OpenSimplexNoise noise = new OpenSimplexNoise();



    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(1000, 1000, P2D);
    }

    public void setup() {
        if (width < displayWidth) {
            surface.setAlwaysOnTop(true);
        }
        pg = createGraphics(width, height, P2D);
    }

    public void draw() {
        pg.beginDraw();
        redrawAsNeeded();
        pg.endDraw();
        image(pg, 0, 0, width, height);
        rec(pg);
        gui();
    }

    private void redrawAsNeeded() {
        float particleCount = slider("count");
        float noiseFreq = slider("noise freq");
        float noiseMag = slider("noise mag");
        float baseWeight = slider("weight");
        float radius = slider("radius", 100);
        float speed = slider("speed", 1);
        float xFreq = slider("x freq");
        float yFreq = slider("y freq");
        pg.background(0);
        translateToCenter(pg);
        pg.stroke(picker("stroke").clr());
        pg.strokeWeight(baseWeight);
        pg.beginShape(POINTS);
        for (int i = 0; i < particleCount; i++) {
            float x = radius*cos(i*xFreq+speed*t);
            float y = radius*sin(i*yFreq+speed*t);
            pg.vertex(x,y);
        }
        pg.endShape();
        pg.noStroke();
        pg.fill(0);
        float rectSize = slider("size");
        pg.ellipse(0,0,rectSize,rectSize);
    }

}
