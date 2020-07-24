package _2020_07;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PImage;

import java.util.ArrayList;

public class TShirt extends KrabApplet {
    private PGraphics pg;
    private int currentIndex;
    private ArrayList<PImage> images = new ArrayList<>();

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
//        size(1000, 1000, P3D);
        fullScreen(P3D);
    }

    public void setup() {
        loadImages();
    }

    private void loadImages() {
        for(int i = 1; i <= 5; i++) {
            images.add(loadImage("images/cropCircles/" + i + ".jpg"));
        }
    }

    public void draw() {
        int resMultiplier = sliderInt("res mult", 1);
        pg = updateGraphics(pg, width*resMultiplier, height*resMultiplier);
        pg.beginDraw();
        String grainy = "shaders/_2020_07/TShirt/grainy.glsl";
        uniform(grainy).set("time", t*slider("time speed"));
        uniform(grainy).set("gradient1", gradient("gradient"));
        uniform(grainy).set("image", images.get(currentIndex));
        group("image");
        if(button("next image")) {
            currentIndex++;
            currentIndex %= images.size();
        }
        uniform(grainy).set("imageStrength", slider("strength", .2f));
        uniform(grainy).set("imagePos", sliderXY("position"));
        uniform(grainy).set("imageScale", sliderXY("scale", 1f));
        resetGroup();
        uniform(grainy).set("constant", slider("constant", 0.5f));
        uniform(grainy).set("fbmStrength", slider("fbm strength", 0.5f));
        uniform(grainy).set("fbmScale", slider("fbm scale", 1.f));
        uniform(grainy).set("qStrength", slider("q strength"));
        uniform(grainy).set("rStrength", slider("r strength"));
        hotFilter(grainy, pg);
        pg.endDraw();
        image(pg, 0, 0, width, height);
        rec(pg, sliderInt("frames", 360));
        gui();
    }

    public void keyPressed() {
        super.keyPressed();
    }
}
