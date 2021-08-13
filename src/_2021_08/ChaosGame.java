package _2021_08;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PVector;

import java.util.ArrayList;

public class ChaosGame extends KrabApplet {
    private PGraphics pg;
    private PVector pos = new PVector(random(width), random(height));
    private ArrayList<PVector> points = new ArrayList<>();
    int pointCountTemp;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        fullScreen(P3D);
    }

    public void setup() {
        toggleFullscreen(1000, 1000);
    }

    public void draw() {
        pg = updateGraphics(pg);
        pg.beginDraw();
        pg.image(gradient("background"), 0, 0);
        int steps = sliderInt("steps", 1);
        int pointCount = sliderInt("point count", 3);
        if (frameCount < 8 || button("reset") || pointCount != pointCountTemp) {
            pg.background(0);
        }
        pg.strokeWeight(slider("weight", 1.8f));
        float radius = slider("radius", width * 0.3f);
        points.clear();
        for (int i = 0; i < pointCount; i++) {
            float norm = norm(i, 0, pointCount);
            float theta = norm * TAU;
            PVector point = new PVector(
                    width / 2f + radius * cos(theta),
                    height / 2f + radius * sin(theta)
            ).add(sliderXY(i + "").copy());
            if (toggle("show points")) {
                pg.ellipse(point.x, point.y, 20, 20);
            }
            points.add(point);
        }
        pointCountTemp = pointCount;
        pg.noFill();
        pg.stroke(0, 0, 255);

        pg.stroke(255);
        for (int i = 0; i < steps; i++) {
            PVector moveTo = points.get(floor(random(pointCount)));
            pos.x = lerp(pos.x, moveTo.x, 0.5f);
            pos.y = lerp(pos.y, moveTo.y, 0.5f);
            pg.point(pos.x, pos.y);
        }
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg, sliderInt("frames", 360));
        gui();
    }
}
