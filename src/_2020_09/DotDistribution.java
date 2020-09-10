package _2020_09;

import applet.KrabApplet;
import processing.core.PGraphics;

import java.util.ArrayList;

public class DotDistribution extends KrabApplet {
    private PGraphics pg;
    ArrayList<Float> gaussians = new ArrayList<Float>();
    ArrayList<Float> randoms = new ArrayList<Float>();

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
    }

    public void draw() {
        pg = updateGraphics(pg);
        pg.beginDraw();
        pg.background(picker("bg", 1).clr());
        pg.stroke(picker("stroke", 0).clr());
        pg.strokeWeight(slider("weight", 3));
        updateDots(pg);
        pg.endDraw();
        image(pg, 0, 0, width, height);
        rec(pg);
        gui();
    }

    private void updateDots(PGraphics pg) {
        if(button("reset gauss")) {
            gaussians.clear();
        }
        int count = sliderInt("count", 100);
        float xRadius = slider("x radius", 1000);
        float yRadius = slider("y radius", 200);
        for (int i = 1; i <= count; i++) {
            float x = xRadius*constRandom(i);
            float y = yRadius*abs(constGaussian(i));
            pg.point(x,y);
        }
    }


    private float constRandom(int i) {
        while(randoms.size() <= i){
            randoms.add(random(1));
        }
        return randoms.get(i);
    }


    private float constGaussian(int i) {
        while(gaussians.size() <= i){
            gaussians.add(randomGaussian());
        }
        return gaussians.get(i);
    }

}
