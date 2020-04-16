package _2020_04;

import applet.KrabApplet;
import com.sun.javafx.geom.Ellipse2D;
import processing.core.PGraphics;
import processing.core.PVector;

import java.util.ArrayList;


// TODO find more easing functions here: https://gizma.com/easing/

public class EaseInOutExpo extends KrabApplet {
    private PGraphics pg;
    PVector pos;
    int frameAnimationStarted = -300;
    private ArrayList<PVector> targets = new ArrayList<>();
    private ArrayList<PVector> origins = new ArrayList<>();

    public static void main(String[] args) {
        KrabApplet.main(new Object() {}.getClass().getEnclosingClass().getName());
    }

    public void settings() {
        size(800, 800, P2D);
    }

    public void setup() {
        pg = createGraphics(width, height, P2D);
        pos = new PVector(width, height).mult(.5f);
    }

    @Override
    public void mousePressed() {
        super.mousePressed();
        println("NOW");
        origins.clear();
        origins.addAll(targets);
        targets.clear();
        int count = sliderInt("count", 50);
        for (int i = 0; i < count; i++) {
            if(origins.size() < count){
                origins.add(new PVector(mouseX, mouseY));
            }
            float gaussOffset = slider("gauss offset", 100);
            targets.add(new PVector(mouseX + gaussOffset * randomGaussian(), mouseY  + gaussOffset * randomGaussian()));
            frameAnimationStarted = frameCount;
        }

    }

    public void draw() {
        pg.beginDraw();
        alphaFade(pg);

        for (PVector targetPos : targets) {
            PVector startPos = origins.get(targets.indexOf(targetPos));
            float frameDuration = slider("duration");
            if (targetPos != null) {
                pos.x = easeInOutExpo(frameCount - frameAnimationStarted, startPos.x, targetPos.x - startPos.x, frameDuration);
                pos.y = easeInOutExpo(frameCount - frameAnimationStarted, startPos.y, targetPos.y - startPos.y, frameDuration);
            }

            pg.noStroke();
            pg.fill(picker("fill").clr());
            float size = slider("size", 30);
            pg.ellipse(pos.x, pos.y, size, size);
        }

        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }


}
