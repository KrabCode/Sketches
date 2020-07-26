package _2020_07;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

import java.util.ArrayList;

public class TShirt extends KrabApplet {
    private PGraphics pg;
    private int currentIndex;
    private ArrayList<PImage> images = new ArrayList<>();
    float time = 0;
    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(1000, 1000, P3D);
//        fullScreen(P3D);
    }

    public void setup() {
        loadImages();
    }

    private void loadImages() {
        for(int i = 1; i <= 10; i++) {
            images.add(loadImage("images/cropCircles/" + i + ".jpg"));
        }
    }

    public void draw() {
        int resMultiplier = sliderInt("res mult", 1);
        pg = updateGraphics(pg, width*resMultiplier, height*resMultiplier);
        pg.beginDraw();
        String grainy = "shaders/_2020_07/TShirt/grainy.glsl";
        time += radians(1)*slider("time speed");
        uniform(grainy).set("time", time);
        uniform(grainy).set("gradient1", gradient("1"));
        uniform(grainy).set("gradient2", gradient("2"));
        uniform(grainy).set("gradient3", gradient("3"));


        // moms spaghetti

        group("image");
        currentIndex = sliderInt("image");
        currentIndex %= images.size();
        currentIndex = max(currentIndex, 0);
        uniform(grainy).set("image", images.get(currentIndex));
        uniform(grainy).set("imagePos", sliderXY("position"));
        uniform(grainy).set("imageScale", sliderXY("scale", 1f));
        uniform(grainy).set("imageIntensityBounds", sliderXY("bounds", 0.8f, 3));
        resetGroup();

        group("blur");
        uniform(grainy).set("innerEdge", slider("inner bound"));
        uniform(grainy).set("outerEdge", slider("outer bound"));
        uniform(grainy).set("intensity", slider("intensity"));
        resetGroup();


        uniform(grainy).set("constant", slider("constant", 0.5f));
        uniform(grainy).set("fbmStrength", slider("fbm strength", 0.5f));
        uniform(grainy).set("fbmScale", slider("fbm scale", 1.f));
        uniform(grainy).set("qStrength", slider("q strength"));
        uniform(grainy).set("rStrength", slider("r strength"));
        uniform(grainy).set("borderShape", sliderInt("border shape", 0));
        uniform(grainy).set("borderSize", slider("border size", 0.45f));
        uniform(grainy).set("borderTransition", slider("border transition", 0.05f));

        hotFilter(grainy, pg);
        pg.endDraw();
        image(pg, 0, 0, width, height);

        if(toggle("crosshairs")) {
            strokeWeight(3);
            stroke(0);
            line(width/2f, 0, width/2f, height);
            line(0, height/2f, width, height/2f);
        }

        rec(pg, sliderInt("frames", 360));
        gui();
    }

    public void keyPressed() {
        super.keyPressed();
    }
}
