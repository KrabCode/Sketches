package _2020_05;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class MountainPeople extends KrabApplet {
    private PGraphics pg;
    private PImage img;
    private PGraphics ig;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(1000, 1000, P3D);
    }

    public void setup() {
        img = loadImage("images/people/fly_boy_full.png");
        ig = createGraphics(width, height, P2D);
        pg = createGraphics(width, height, P3D);
        if (width < displayWidth) {
            surface.setAlwaysOnTop(true);
            surface.setLocation(2560 - 1020, 20);
        }
    }

    public void draw() {
        pg.beginDraw();
        updateShader();
        pg.endDraw();
        background(0);
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    void updateShader() {
        String shader = "shaders/_2020_05/mountainPeople/shader.glsl";
        uniformRamp(shader);
        group("image");
        uniform(shader).set("time", t);
        ig.beginDraw();
        translateToCenter(ig);
        ig.imageMode(CENTER);
        ig.pushMatrix();
        translate2D(ig);
        ig.rotate(slider("rotation")+t*slider("rot spd"));
        translate2D(ig, "translate after");
        ig.scale(slider("scale", 1));
        float yOffset = slider("y offset");
        ig.image(img, yOffset,0);
        ig.popMatrix();
        ig.pushMatrix();
        translate2D(ig);
        ig.rotate(PI+slider("rotation")+t*slider("rot spd"));
        translate2D(ig, "translate after");
        ig.scale(slider("scale", 1));
        ig.image(img, -yOffset,0);
        ig.popMatrix();
        ig.endDraw();

        uniform(shader).set("img", ig);
        hotFilter(shader, pg);
    }
}
