package _2021_07;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class EscherHandDrawing extends KrabApplet {

    private PGraphics pg;
    private PGraphics hand;
    private PImage handImg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        fullScreen(P3D);
    }

    public void setup() {
        toggleFullscreen();
        surface.setAlwaysOnTop(true);
        handImg = loadImage("images/hand/hand2.jpg");
        hand = createGraphics(3000,3000, P3D);
        hand.smooth(16);
    }

    public void draw() {
        pg = updateGraphics(pg, P3D);
        pg.smooth(16);
        pg.beginDraw();
        pg.image(gradient("background"), 0, 0);
        hand.beginDraw();
        String shader = "shaders/_2021_07/hand/hand.glsl";
        uniform(shader).set("time", radians(frameCount));
        uniform(shader).set("img", handImg);
        hotFilter(shader, hand);
        hand.endDraw();
        translateToCenter(pg);
        float size = slider("size", 1000);
        int count = sliderInt("count", 10);
        float scale = slider("scale", 1);
        float angle = slider("angle", QUARTER_PI);
        for (int i = 0; i < count; i++) {
//            float iNorm = norm(i, 0, count-1);
            pg.scale(scale);
            pg.rotate(angle);
            pg.image(hand, 0, 0, size, size);
        }
        hotFilter("shaders/filters/antiAlias.glsl", pg);
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg, sliderInt("frames", 360));
        gui();
    }
}
