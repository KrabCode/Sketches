package _2020_09;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class Doggo extends KrabApplet {
    private PGraphics pg;
    private PImage imgDoggo;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(1400, 1400, P2D);
    }

    public void setup() {
        imgDoggo = loadImage("images/doggo/doggo.jpg");
//        surface.setLocation(displayWidth-width, 0);
    }

    public void draw() {
        pg = updateGraphics(pg);
        pg.beginDraw();

        String fur = "shaders/_2020_09/doggo.glsl";
        if(mousePressedOutsideGui) {
            uniform(fur).set("iMouse", (float) mouseX, (float) mouseY);
        }
        uniform(fur).set("t", t);
        uniform(fur).set("res", (float) width, (float) height);
        uniform(fur).set("imgDoggo", imgDoggo);
        hotFilter(fur, pg);

        pg.endDraw();
        image(pg, 0, 0);
        rec(pg, sliderInt("frames", 360));
        gui();
    }
}
