package _2020_11;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class FlowMap extends KrabApplet {
    private PGraphics pg;
    private PImage flow;
    private PImage img;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        fullScreen(P3D);
    }

    public void setup() {
        flow = loadImage("images\\jason-wong-NCb50hjk-pQ-unsplash.jpg");
//        flow = loadImage("images\\IMG_20201117_224609.jpg");
        img = loadImage("images\\jason-wong-NCb50hjk-pQ-unsplash.jpg");
//        img = loadImage("images\\IMG_20201117_224609.jpg");
    }

    public void draw() {
        pg = updateGraphics(pg);
        pg.beginDraw();
        pg.background(0);
        String flowMap = "shaders\\_2020_11\\flowMap.glsl";
        uniform(flowMap).set("time", t);
        uniform(flowMap).set("img", img);
        uniform(flowMap).set("flow", flow);
        uniform(flowMap).set("imgRes", (float) img.width, (float) img.height);
        hotFilter(flowMap, pg);
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg, sliderInt("frames", 360));
        gui();
    }
}
