package _2020_04;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PMatrix3D;
import processing.core.PShape;
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
        alphaFade(pg);
        blurPass(pg);
        pg.translate(width / 2, height / 2);
        pg.translate(sliderXYZ("translate").x, sliderXYZ("translate").y, sliderXYZ("translate").z);
        mouseRotation(pg);
        PVector timeRotation = sliderXYZ("rotate");
        PMatrix3D temp = new PMatrix3D();
        temp.rotateX(timeRotation.x * t);
        temp.rotateY(timeRotation.y * t);
        temp.rotateZ(timeRotation.z * t);
        pg.applyMatrix(temp);
        pg.stroke(picker("stroke").clr());
        pg.fill(picker("fill").clr());
        pg.strokeWeight(slider("weight"));
        if (toggle("no fill")) {
            pg.noFill();
        }
        drawQuadBall(sliderInt("diameter", 400), sliderInt("n", 10));
        pg.camera();
        pg.translate(sliderXYZ("moon translate").x, sliderXYZ("moon translate").y, sliderXYZ("moon translate").z);
        pg.stroke(picker("moon stroke").clr());
        pg.fill(picker("moon fill").clr());
        drawQuadBall(sliderInt("moon diameter", 200), sliderInt("moon n", 5));
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    void drawQuadBall(float diam, int n) {
        int divs = 1 + 2 * n;
        float divSize = diam / divs;
        float rad = diam / 2;
        pg.beginShape(QUADS);
        PVector corner = new PVector();
        PVector v = new PVector();
        float freq = slider("freq", 1);
        float amp = slider("amp", 100);
        // XY
        float noise = 0;
        for (int j = -1; j <= 1; j += 2) {
            corner.set(-rad, -rad, rad * j);
            for (int i = 0; i < divs * divs; i++) {
                v.set(corner.x, corner.y, corner.z);

                noise = (float) noiseGenerator.eval(v.x * freq, v.y * freq, v.z * freq);
                v.setMag(rad + noise * amp);


                pg.vertex(v.x, v.y, v.z);

                v.set(corner.x + divSize, corner.y, corner.z);

                noise = (float) noiseGenerator.eval(v.x * freq, v.y * freq, v.z * freq);
                v.setMag(rad + noise * amp);


                pg.vertex(v.x, v.y, v.z);

                v.set(corner.x + divSize, corner.y + divSize, corner.z);

                noise = (float) noiseGenerator.eval(v.x * freq, v.y * freq, v.z * freq);
                v.setMag(rad + noise * amp);


                pg.vertex(v.x, v.y, v.z);

                v.set(corner.x, corner.y + divSize, corner.z);

                noise = (float) noiseGenerator.eval(v.x * freq, v.y * freq, v.z * freq);
                v.setMag(rad + noise * amp);


                pg.vertex(v.x, v.y, v.z);

                corner.add(divSize, 0, 0);
                if (corner.x > rad - divSize / 2) {
                    corner.x = -rad;
                    corner.y += divSize;
                }
            }

            //XZ
            corner.set(-rad, rad * j, -rad);
            for (int i = 0; i < divs * divs; i++) {
                v.set(corner.x, corner.y, corner.z);

                noise = (float) noiseGenerator.eval(v.x * freq, v.y * freq, v.z * freq);
                v.setMag(rad + noise * amp);


                pg.vertex(v.x, v.y, v.z);

                v.set(corner.x + divSize, corner.y, corner.z);

                noise = (float) noiseGenerator.eval(v.x * freq, v.y * freq, v.z * freq);
                v.setMag(rad + noise * amp);


                pg.vertex(v.x, v.y, v.z);

                v.set(corner.x + divSize, corner.y, corner.z + divSize);

                noise = (float) noiseGenerator.eval(v.x * freq, v.y * freq, v.z * freq);
                v.setMag(rad + noise * amp);


                pg.vertex(v.x, v.y, v.z);

                v.set(corner.x, corner.y, corner.z + divSize);

                noise = (float) noiseGenerator.eval(v.x * freq, v.y * freq, v.z * freq);
                v.setMag(rad + noise * amp);


                pg.vertex(v.x, v.y, v.z);

                corner.add(divSize, 0, 0);
                if (corner.x > rad - divSize / 2) {
                    corner.x = -rad;
                    corner.z += divSize;
                }
            }

            // YZ
            corner.set(rad * j, -rad, -rad);
            for (int i = 0; i < divs * divs; i++) {
                v.set(corner.x, corner.y, corner.z);

                noise = (float) noiseGenerator.eval(v.x * freq, v.y * freq, v.z * freq);
                v.setMag(rad + noise * amp);


                pg.vertex(v.x, v.y, v.z);

                v.set(corner.x, corner.y + divSize, corner.z);

                noise = (float) noiseGenerator.eval(v.x * freq, v.y * freq, v.z * freq);
                v.setMag(rad + noise * amp);


                pg.vertex(v.x, v.y, v.z);

                v.set(corner.x, corner.y + divSize, corner.z + divSize);

                noise = (float) noiseGenerator.eval(v.x * freq, v.y * freq, v.z * freq);
                v.setMag(rad + noise * amp);


                pg.vertex(v.x, v.y, v.z);

                v.set(corner.x, corner.y, corner.z + divSize);

                noise = (float) noiseGenerator.eval(v.x * freq, v.y * freq, v.z * freq);
                v.setMag(rad + noise * amp);


                pg.vertex(v.x, v.y, v.z);

                corner.add(0, divSize, 0);
                if (corner.y > rad - divSize / 2) {
                    corner.y = -rad;
                    corner.z += divSize;
                }
            }
        }
        pg.endShape();
    }

    void nkingBall(float r, int subDiv) {
        if (subDiv < 2) subDiv = 2;
        float n = (subDiv - 1) * .5f;
        PVector[] dirs = {
                new PVector(n, 0, 0), new PVector(-n, 0, 0),
                new PVector(0, n, 0), new PVector(0, -n, 0),
                new PVector(0, 0, n), new PVector(0, 0, -n)
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
        beginShape(QUADS);
        PVector up = new PVector();
        PVector vp = new PVector();
        for (int i = 0; i < 6; i++) {
            for (int u = 0; u < subDiv - 1; u++) {
                for (int v = 0; v < subDiv - 1; v++) {
                    for (int j = 0; j < 4; j++) {
                        float offs = (subDiv - 1) * .5f;
                        float uPos = u - offs + (j / 2 ^ j % 2);
                        float vPos = v - offs + j / 2;
                        up.set(us[i]);
                        up.mult(uPos);
                        vp.set(vs[i]);
                        vp.mult(vPos);
                        PVector p = dirs[i].copy();
                        p.add(up);
                        p.add(vp);
                        p.setMag(r);
                        //res.normal(p.x, p.y, p.z);
                        vertex(p.x, p.y, p.z);
                    }
                }
            }
        }
        endShape();
    }

}
