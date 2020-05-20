package _2020_05;

import applet.KrabApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;
import utils.OpenSimplexNoise;

import java.util.ArrayList;

public class Points extends KrabApplet {
    private PGraphics pg;
    OpenSimplexNoise noiseGenerator = new OpenSimplexNoise();
    ArrayList<ArrayList<PVector>> histories = new ArrayList<>();

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(1000, 1000, P3D);
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
        updatePoints();
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    private void updatePoints() {
        float seedRange = slider("seed range");
        int spawnCount = max(0, sliderInt("spawns", 10));
        updateHistoriesSize(spawnCount);
        float timeRadius = slider("time radius");
        float timeX = timeRadius * cos(t*.5f);
        float timeY = timeRadius * sin(t*.5f);
        for (int spawnIndex = 0; spawnIndex < spawnCount; spawnIndex++) {
            float spawnSeed = randomDeterministic(spawnIndex) * slider("seed multiplier");
            float x = (float) (seedRange * (-1 + 2 * noiseGenerator.eval(timeX, timeY, spawnSeed)));
            float y = (float) (seedRange * (-1 + 2 * noiseGenerator.eval(timeX, timeY, 180 + spawnSeed)));
            updateSpawnHistory(spawnIndex, x, y);
        }
    }

    private void updateHistoriesSize(int spawnCount) {
        while (histories.size() < spawnCount) {
            histories.add(new ArrayList<>());
        }
        while (histories.size() > spawnCount) {
            histories.remove(histories.size() - 1);
        }
    }

    private void updateSpawnHistory(int spawnIndex, float x, float y) {
        int maxHistorySize = sliderInt("history size", 10);
        ArrayList<PVector> history = histories.get(spawnIndex);
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
            drawHistory(spawnIndex, history);
            pg.point(x, y);
            pg.popMatrix();
        }
    }

    private void drawHistory(int spawnIndex, ArrayList<PVector> history) {
        HSBA base = picker("stroke");
        pg.noFill();
        pg.beginShape();
        float hue = hueModulo(base.hue() + slider("hue range") * randomDeterministic(30 * spawnIndex + 8.1218f));
        float sat = base.sat() + slider("sat range") * randomDeterministic(30 * spawnIndex + 18.154f);
        float br = base.br() + slider("br range") * randomDeterministic(30 * spawnIndex + 28.648f);
        for (int i = 0; i < history.size(); i++) {
            PVector p = history.get(i);
            float iNorm = norm(i, 0, history.size() - 1);
            float varyingSaturation = sat * (1 - iNorm) + slider("const sat");
            pg.strokeWeight(slider("norm weight") * iNorm + slider("min weight"));
            pg.stroke(hue, constrain(varyingSaturation, 0, 1), constrain(br, 0, 1));
            pg.vertex(p.x, p.y);
        }
        pg.endShape();
    }
}
