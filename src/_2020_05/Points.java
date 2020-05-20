package _2020_05;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import utils.OpenSimplexNoise;

import java.util.ArrayList;

public class Points extends KrabApplet {
    private PGraphics pg;
    OpenSimplexNoise noiseGenerator = new OpenSimplexNoise();
    ArrayList<ArrayList<PVector>> trails = new ArrayList<>();

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
//        size(1000, 1000, P3D);
        fullScreen(P3D);
    }

    public void setup() {
        pg = createGraphics(width, height, P3D);
        surface.setAlwaysOnTop(true);
        frameRecordingDuration *= 2;
    }

    public void draw() {
        pg.beginDraw();
        pg.colorMode(HSB, 1, 1, 1, 1);
        String blendMode = options("add", "replace");
        if (blendMode.equals("add")) {
            pg.blendMode(ADD);
        } else {
            pg.blendMode(REPLACE);
        }
        fadeToBlack(pg);
        blurPass(pg);
        translateToCenter(pg);
        updateTrails();
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    private void updateTrails() {
        float seedRange = slider("seed range");
        int trailCount = max(0, sliderInt("trails", 10));
        updateTrailCount(trailCount);
        float timeRadius = slider("time radius");
        float timeX = timeRadius * cos(t*.5f);
        float timeY = timeRadius * sin(t*.5f);
        for (int trailIndex = 0; trailIndex < trailCount; trailIndex++) {
            float trailSeed = hash(trailIndex) * slider("seed multiplier");
            float x = (float) (seedRange * (-1 + 2 * noiseGenerator.eval(timeX, timeY, trailSeed)));
            float y = (float) (seedRange * (-1 + 2 * noiseGenerator.eval(timeX, timeY, 180 + trailSeed)));
            updateTrail(trailIndex, x, y);
        }
    }

    private void updateTrailCount(int trailCount) {
        while (trails.size() < trailCount) {
            trails.add(new ArrayList<>());
        }
        while (trails.size() > trailCount) {
            trails.remove(trails.size() - 1);
        }
    }

    private void updateTrail(int trailIndex, float x, float y) {
        int maxHistorySize = sliderInt("history size", 10);
        ArrayList<PVector> history = trails.get(trailIndex);
        if (history.size() > maxHistorySize) {
            history.remove(0);
        }
        if (frameCount % max(1, sliderInt("history skips")) == 0) {
            history.add(new PVector(x, y));
        }
        int mirrorCount = sliderInt("mirrors", 4);
        for (int i = 0; i < mirrorCount; i++) {
            pg.pushMatrix();
            pg.rotate(map(i, 0, mirrorCount, 0, TAU));
            drawTrail(trailIndex, history);
            pg.point(x, y);
            pg.popMatrix();
        }
    }

    private void drawTrail(int particleIndex, ArrayList<PVector> history) {
        HSBA base = picker("stroke");
        pg.noFill();
        pg.beginShape();
        float hue = hueModulo(base.hue() + slider("hue range") * hash(30 * particleIndex + 8.1218f));
        float sat = base.sat() + slider("sat range") * hash(30 * particleIndex + 18.154f);
        float br = base.br() + slider("br range") * hash(30 * particleIndex + 28.648f);
        for (int i = 0; i < history.size(); i++) {
            PVector p = history.get(i);
            float iNorm = norm(i, 0, history.size() - 1);
            float varyingSaturation = sat * (1 - iNorm) + slider("const sat");
            pg.strokeWeight(slider("const weight") + slider("norm weight") * iNorm);
            pg.stroke(hue, constrain(varyingSaturation, 0, 1), constrain(br, 0, 1));
            pg.vertex(p.x, p.y);
        }
        pg.endShape();
    }
}
