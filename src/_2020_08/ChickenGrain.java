package _2020_08;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

public class ChickenGrain extends KrabApplet {
    private PGraphics pg;
    private PGraphics bg;
    private Chicken chicken;
    private PImage chickenBody;
    private PImage chickenHead;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        fullScreen(P3D);
    }

    public void setup() {
        chickenBody = loadImage("images\\chickens\\chicken_body_gray.png");
        chickenHead = loadImage("images\\chickens\\chicken_head_gray.png");
        chicken = new Chicken();
    }

    public void draw() {
        pg = updateGraphics(pg);
        pg.beginDraw();
        updateBackground();
        updateChickens();
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg, sliderInt("frames", 360));
        gui();
    }

    private void updateBackground() {
        bg = updateGraphics(bg, P2D);
        bg.beginDraw();
        hashPass(bg);
        fbmDisplacePass(bg);
        bg.endDraw();
        pg.imageMode(CORNER);
        pg.image(bg, 0, 0);
    }

    private void hashPass(PGraphics pg) {
        String hash = "shaders\\_2020_08\\hashRiver.glsl";
        uniform(hash).set("time", t);
        hotFilter(hash, pg);
    }

    private void updateChickens() {
        chicken.update();
        chicken.draw(pg);
    }

    class Chicken {
        PGraphics cg;

        void update() {
            cg = updateGraphics(cg, 150, 150, P2D);
            cg.beginDraw();
            cg.clear();
            cg.imageMode(CENTER);
            cg.pushMatrix();
            translateToCenter(cg);
            cg.image(chickenBody, 0, 0);
            translate2D(cg, "pre rotate");
            cg.pushMatrix();
            float rotation = slider("base rot") + slider("peck rot") * abs(sin(t * slider("peck speed", 1)));
            cg.rotate(rotation);
            translate2D(cg, "post rotate");
            cg.scale(slider("head scale", 0.88f));
            cg.image(chickenHead, 0, 0);
            cg.popMatrix();
            if (toggle("debug")) {
                cg.strokeWeight(5);
                cg.fill(255, 0, 0);
                cg.point(0, 0);
            }
            cg.popMatrix();

            translateToCenter(cg);
            rainbowPass(cg);
            cg.endDraw();
        }

        private void rainbowPass(PGraphics cg) {
            String rainbowChicken = "shaders\\_2020_08\\rainbowChicken.glsl";
            uniform(rainbowChicken).set("time", t);
            uniform(rainbowChicken).set("gradient", gradient("gradient"));
            hotFilter(rainbowChicken, cg);
        }

        void draw(PGraphics pg) {
            pg.pushStyle();
            pg.imageMode(CENTER);
            translate2D(pg, "pos");
            pg.scale(slider("scale"));
            pg.image(cg, 0, 0);
            pg.popStyle();
        }
    }
}
