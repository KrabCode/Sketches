package _2020_06;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import utils.OpenSimplexNoise;

import java.util.ArrayList;

public class Chromab extends KrabApplet {
    private PGraphics pg;

    private OpenSimplexNoise noise = new OpenSimplexNoise();
    private ArrayList<Point> points = new ArrayList<>();
    private ArrayList<Point> pointsToRemove = new ArrayList<>();

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
//        fullScreen(P3D);
        size(1000, 1000, P3D);
    }

    public void setup() {
        pg = createGraphics(width, height, P3D);
        if (width < displayWidth) {
            surface.setAlwaysOnTop(true);
            surface.setLocation(2560 - 1020, 20);
        }
        framesToRecord = 360 * 2;
    }

    public void draw() {
        pg.beginDraw();
        if (button("clear")) {
            pg.clear();
        }
        pg.pushMatrix();
        translateToCenter(pg);
        updatePoints();
        displayPoints();
        pg.popMatrix();
        blurPass(pg);
        chromaticAberrationPass(pg);
        fbmDisplacePass(pg);

        group("color filters");
        fadeToBlack(pg);
        multiplyPass(pg);
        pg.blendMode(ADD);
        pg.image(gradient("add"), 0, 0);
        pg.blendMode(SUBTRACT);
        pg.image(gradient("sub"), 0, 0);
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    private void updatePoints() {
        group("points");
        pg.blendMode(options("blend", "add").equals("blend") ? BLEND : ADD);
        int count = sliderInt("count", 100, 0, Integer.MAX_VALUE);
        float baseRadius = slider("base radius");
        float r = slider("gauss radius", 150);
        while (points.size() < count) {
            points.add(new Point(baseRadius+randomGaussian() * r,baseRadius+ randomGaussian() * r));
        }
        while (points.size() > count) {
            points.remove(points.size() - 1);
        }
        for (Point p : points) {
            p.update();
        }
        points.removeAll(pointsToRemove);
        pointsToRemove.clear();
    }


    private void displayPoints() {
        int copies = sliderInt("copies", 8);
        for (int i = 0; i < copies; i++) {
            float theta = map(i, 0, copies, 0, TAU);
            pg.pushMatrix();
            pg.rotate(theta);
            for (Point p : points) {
                p.display();
            }
            pg.popMatrix();
        }
    }

    class Point {
        private final int frameCreated;
        private final PVector pos, spd = new PVector();
        private final float randomWeight;

        Point(float x, float y) {
            frameCreated = frameCount;
            this.pos = new PVector(x, y);
            randomWeight = hash(x * y * x * y);
        }

        void update() {
            PVector toCenter = pos.copy().rotate(PI);
            spd.add(toCenter.copy().normalize().rotate(HALF_PI).mult(slider("side force")));
            spd.add(toCenter.copy().normalize().mult(slider("center force")));
            float gaussForce = slider("gauss force");
            spd.add(randomGaussian()*gaussForce, randomGaussian()*gaussForce);
            spd.mult(slider("drag", .95f));
            pos.add(spd);
            float freq = slider("freq", 0.1f);
            float angleVar = slider("angle var", 1);
            float noiseAngle = noise(pos.x, pos.y, freq, angleVar, t);
            PVector noise = PVector.fromAngle(noiseAngle).mult(slider("noise mag"));
            pos.add(noise);
            if (PVector.dist(pos, new PVector()) > max(width, height) * 1.5f) {
                pointsToRemove.add(this);
            }
        }

        private float noise(float x, float y, float freq, float mag, float t) {
            return (float) (mag * noise.eval(x * freq, y * freq, t));
        }

        public void display() {
            HSBA stroke = picker("stroke");
            pg.pushStyle();
            pg.colorMode(HSB, 1, 1, 1, 1);
            float fadeIn = clampNorm(frameCount, frameCreated, frameCreated + slider("fade in frames", 60));
            pg.stroke(stroke.hue(), stroke.sat(), stroke.br(), stroke.alpha() * fadeIn);
            pg.strokeWeight(slider("weight", 1) + randomWeight * slider("weight random"));
            pg.point(pos.x, pos.y);
            pg.popStyle();
        }
    }
}
