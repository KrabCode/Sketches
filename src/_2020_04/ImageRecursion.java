package _2020_04;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import utils.OpenSimplexNoise;

public class ImageRecursion extends KrabApplet {
    private PGraphics pg;
    private PImage img;
    private OpenSimplexNoise noise = new OpenSimplexNoise();

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(2666/3,2430/3, P2D);
    }

    public void setup() {
        pg = createGraphics(width, height, P2D);
        img = loadImage("images/clouds.jpg");
//        frameRecordingStarted = 1;

    }

    public void draw() {
        PImage previous = pg.get();
        pg.beginDraw();
        pg.translate(width * .5f, height * .5f);
        pg.imageMode(CENTER);
        pg.image(img, 0, 0);
        float rotateTimeRadius = slider("rotate time");
        float rotateTimeSpeed = slider("time speed", 1);
        pg.rotate((float) (slider("rotate amplitude") * noise.eval(rotateTimeRadius*cos(rotateTimeSpeed*t), rotateTimeRadius*sin(rotateTimeSpeed*t))));
        float scaleTimeRadius = slider("scale time");
        pg.scale(1 - slider("scale amplitude") * abs((float) noise.eval(scaleTimeRadius*cos(rotateTimeSpeed*t), scaleTimeRadius*sin(rotateTimeSpeed*t))));
        pg.image(previous, 0, 0);
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }
}
