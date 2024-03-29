package _2020_05;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import utils.OpenSimplexNoise;

public class Blocky extends KrabApplet {
    private PGraphics pg;
    private OpenSimplexNoise noise = new OpenSimplexNoise();

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(1000, 1000, P3D);
//        fullScreen(P3D);
    }

    public void setup() {
        pg = createGraphics(width, height, P3D);
        if(width < displayWidth) {
            surface.setAlwaysOnTop(true);
        }
    }

    public void draw() {
        framesToRecord = sliderInt("recording frames", 360);
        pg.beginDraw();
        pg.colorMode(HSB, 1, 1, 1, 1);
        ramp(pg, "background", 4);
        translateToCenter(pg);
        translate(pg, "translate");
        lights(pg, 2);
        preRotate(pg);
        translate(pg, "translate 2");
        group("clouds");
        drawSeaOfBoxes();
        group("sea");
        drawSeaOfBoxes();
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    private void drawSeaOfBoxes() {
        // grid of columns on the XZ plane with fbm informed Y size
        if (toggle("skip")) {
            return;
        }
        pg.pushMatrix();
        pg.noStroke();
        HSBA fill = picker("fill");
        translate(pg, "translate");
        preRotate(pg, "rotate");
        PVector size = sliderXY("grid size");
        PVector boxSize = new PVector(slider("col width"), slider("col height"), slider("col width"));
        float timeRadius = slider("time");
        PVector time = new PVector(timeRadius * cos(t), timeRadius * sin(t));
        float maxDist = slider("max dist", 1500);
        int count = sliderInt("count", 10);
        PVector noiseFreqs = sliderXY("noise freqs");
        PVector noiseMags = sliderXY("noise mags");
        PVector constSpeed = sliderXY("noise speed");
        PVector varSpeedRadius = sliderXY("noise speed radius");
        PVector windSpeed = new PVector(constSpeed.x * t + varSpeedRadius.x * time.x, constSpeed.y * t + varSpeedRadius.y * time.y);
        for (int xi = 0; xi < count; xi++) {
            for (int yi = 0; yi < count; yi++) {
                float x = map(xi, 0, count - 1, -size.x, size.x);
                float z = map(yi, 0, count - 1, -size.y, size.y);
                if (toggle("check dist") && dist(x, z, 0, 0) > maxDist) {
                    continue;
                }
                pg.pushMatrix();
                double y = noise.eval(noiseFreqs.x * x + windSpeed.x, noiseFreqs.x * z + windSpeed.y, 0, 0) * noiseMags.x;
                y += noise.eval(noiseFreqs.y * x + windSpeed.x, noiseFreqs.y * z + windSpeed.y, 0, 0) * noiseMags.y;
                float hueOffset = abs((float) y) * slider("hue offset");
                float satOffset = abs((float) y) * slider("sat offset");
                float brOffset = abs((float) y) * slider("br offset");
                pg.fill(hueModulo(fill.hue() + hueOffset),
                        constrain(satOffset + fill.sat(),0, 1),
                        constrain(brOffset + fill.br(), 0, 1),
                        constrain(fill.alpha()*(float)y*slider("alpha y")+slider("alpha y offset"), 0, 1));
                pg.translate(x, 0, z);
                if (boxSize.y + (float) y > 0 || !toggle("abs")) {
                    pg.box(boxSize.x, boxSize.y + (float) y, boxSize.z);
                }
                pg.popMatrix();
            }
        }
        pg.popMatrix();
    }

}
