package _2020_04;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PMatrix3D;
import processing.core.PVector;
import utils.OpenSimplexNoise;

public class QuadBall extends KrabApplet {
    private PGraphics pg;
    private OpenSimplexNoise noiseGenerator = new OpenSimplexNoise();

    public static void main(String[] args) {
        KrabApplet.main(String.valueOf(new Object() {
        }.getClass().getEnclosingClass()).split(" ")[1]);
    }

    public void settings() {
        size(800, 800, P3D);
    }

    public void setup() {
        pg = createGraphics(width, height, P3D);
        pg.smooth(16);
        surface.setAlwaysOnTop(true);
    }

    public void draw() {
        pg.beginDraw();
        group("shaders");
        alphaFade(pg);
        blurPass(pg);
        pg.translate(width / 2f, height / 2f);
        pg.translate(sliderXYZ("translate").x, sliderXYZ("translate").y, sliderXYZ("translate").z);
        pg.lights();
        mouseRotation(pg);
        PVector timeRotation = sliderXYZ("rotate");
        PMatrix3D temp = new PMatrix3D();
        temp.rotateX(timeRotation.x * t);
        temp.rotateY(timeRotation.y * t);
        temp.rotateZ(timeRotation.z * t);
        pg.applyMatrix(temp);
        group("ball 1");
        nkingBall(sliderInt("diameter", 400), sliderInt("n", 10), true);
        group("ball 2");
        nkingBall(sliderInt("diameter", 400), sliderInt("n", 10), false);
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    void nkingBall(float r, int subDiv, boolean noise) {
        pg.stroke(picker("stroke").clr());
        pg.fill(picker("fill").clr());
        pg.strokeWeight(slider("weight"));
        if (toggle("no fill")) {
            pg.noFill();
        }

        if (subDiv < 2) subDiv = 2;
        PVector[] dirs = {
                new PVector(1, 0, 0), new PVector(-1, 0, 0),
                new PVector(0, 1, 0), new PVector(0, -1, 0),
                new PVector(0, 0, 1), new PVector(0, 0, -1)
        };
        PVector[] us = {
                new PVector(0, 1, 0), new PVector(0, 0, 1),
                new PVector(0, 0, 1), new PVector(1, 0, 0),
                new PVector(1, 0, 0), new PVector(0, 1, 0)
        };
        PVector[] vs = {
                new PVector(0, 0, 1), new PVector(0, 1, 0),
                new PVector(1, 0, 0), new PVector(0, 0, 1),
                new PVector(0, 1, 0), new PVector(1, 0, 0)
        };
        pg.beginShape(QUADS);
        PVector up = new PVector();
        PVector vp = new PVector();
        for (int i = 0; i < 6; i++) {
            for (int u = 0; u < subDiv - 1; u++) {
                for (int v = 0; v < subDiv - 1; v++) {
                    for (int j = 0; j < 4; j++) {
                        float uOffs = (j / 2 ^ j % 2);
                        float vOffs = j / 2;
                        float uAngle = map(u + uOffs, 0, subDiv - 1, -QUARTER_PI, QUARTER_PI);
                        float vAngle = map(v + vOffs, 0, subDiv - 1, -QUARTER_PI, QUARTER_PI);
                        up.set(us[i]);
                        up.mult(tan(uAngle));
                        vp.set(vs[i]);
                        vp.mult(tan(vAngle));
                        PVector p = dirs[i].copy();
                        p.add(up);
                        p.add(vp);
                        p.setMag(r + (noise?fbm(p):0));
                        pg.normal(p.x, p.y, p.z);
                        pg.vertex(p.x, p.y, p.z);
                    }
                }
            }
        }
        pg.endShape();
    }

    private float fbm(PVector p) {
        group("fbm");
        float noise = 0;
        for (int i = 0; i < sliderInt("octaves", 2); i++) {
            float freq = slider("frequency " + i, 1);
            float amp = slider("amp " + i, 1);
            noise += amp * (1 - 2 * noiseGenerator.eval(p.x * freq, p.y * freq, p.z * freq));
        }
        noise = constrain(noise, slider("noise clamp min", 0), slider("noise clamp max", 100));
        return noise;
    }

}
