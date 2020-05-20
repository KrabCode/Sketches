package _2020_05;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PVector;

import java.util.ArrayList;

public class Trip extends KrabApplet {
    private PGraphics pg;
    private ArrayList<P> particles = new ArrayList<>();
    private ArrayList<P> toRemove = new ArrayList<>();

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        fullScreen(P3D);
//        size(1000, 1000, P3D);
    }

    public void setup() {
        pg = createGraphics(width, height, P3D);
//        surface.setAlwaysOnTop(true);
//        surface.setLocation(2560 - 820, 20);
        frameRecordingDuration *= 4;
    }

    public void draw() {
        pg.beginDraw();
        pg.colorMode(HSB, 1, 1, 1, 1);
        group("shaders");
        fadeToBlack(pg);
        blurPass(pg);
        pg.translate(width / 2f, height / 2f);
        updateParticles();
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }


    private void updateParticles() {
        group("particles");
        int spawnFrameSkip = max(1, sliderInt("spawn frame skip", 1));
        int spawnCount = sliderInt("spawn count");
        String blendMode = options("blend: replace", "blend: add");
        if (blendMode.equals("blend: replace")) {
            pg.blendMode(REPLACE);
        } else {
            pg.blendMode(ADD);
        }
        if (frameCount % spawnFrameSkip == 0) {
            for (int i = 0; i < max(0, spawnCount); i++) {
                particles.add(new P());
            }
        }
        for (P p : particles) {
            p.update();
        }
        particles.removeAll(toRemove);
        toRemove.clear();
    }

    class P {
        PVector pos, spd = new PVector();
        int frameCreated = frameCount;
        float hueOffset =       randomGaussian(); // random(-1, 1);
        float satOffset =       randomGaussian(); // random(-1, 1);
        float brOffset =        randomGaussian(); // random(-1, 1);
        float weightOffset =    randomGaussian(); // random(-1, 1);

        P() {
            pos = spawnPos();
        }

        void update() {
            group("particles");
            int lifeSpan = sliderInt("life span", 60);
            float lifeNorm = clampNorm(frameCount, frameCreated, frameCreated + lifeSpan);
            updateForces();
            updateStroke(lifeNorm);
            drawMirroredPoint();
            if (frameCount - lifeSpan > frameCreated) {
                toRemove.add(this);
            }
        }

        private void drawMirroredPoint() {
            int mirrors = sliderInt("mirrors", 1);
            for (int i = 0; i < mirrors; i++) {
                pg.pushMatrix();
                float angleOffset = map(i, 0, mirrors, 0, TAU);
                pg.rotate(angleOffset);
                pg.point(pos.x, pos.y);
                pg.popMatrix();
            }
        }

        private void updateForces() {
            PVector acc = new PVector();
            PVector toCenter = pos.copy().normalize();
            PVector toSide = toCenter.copy().rotate(HALF_PI);
            group("noise");
            float noiseFreq = slider("noise freq");
            float noiseAngle = noise(pos.x * noiseFreq, pos.y * noiseFreq, t * slider("noise time")) * slider("noise angle variance");
            PVector noise = PVector.fromAngle(noiseAngle + slider("noise angle offset"));
            group("forces");
            acc.add(sliderXY("force uniform").copy());
            acc.add(toCenter.mult(slider("force to center")));
            String sidewaysOption = options("sin(t)", "constant");
            float sidewaysMagnitude = slider("force sideways");
            if (sidewaysOption.equals("constant")) {
                acc.add(toSide.mult(sidewaysMagnitude));
            } else {
                float power = sin(t * slider("sin(t) frequency") * sidewaysMagnitude);
                acc.add(toSide.mult(power));
            }
            acc.add(noise.mult(slider("force noise")));
            spd.add(acc);
            spd.mult(slider("drag", .95f));
            pos.add(spd);
        }

        private void updateStroke(float lifeNorm) {
            group("colors");
            HSBA hsba = picker("stroke");
            float hue = hueModulo(hsba.hue() + hueOffset * slider("hue variance"));
            float sat = constrain(hsba.sat() + satOffset * slider("saturation variance"), 0, 1);
            float br = constrain(hsba.br() + brOffset * slider("brightness variance"), 0, 1);
            float fadeInDuration = slider("fade in [0-0.5]", .2f);
            float fadeInNorm = constrain(map(lifeNorm, 0, fadeInDuration, 0, 1), 0, 1);
            float fadeOutDuration = slider("fade out [0-0.5]", .2f);
            float fadeOutNorm = 1 - constrain(map(lifeNorm, 1 - fadeOutDuration, 1, 0, 1), 0, 1);
            float alpha = hsba.alpha();
            if (lifeNorm < .5f) {
                alpha *= fadeInNorm;
            } else {
                alpha *= fadeOutNorm;
            }
            pg.stroke(hue, sat, br, alpha);
            pg.strokeWeight(slider("weight") + weightOffset * slider("weight variance"));
        }
    }

    private PVector spawnPos() {
        return new PVector(randomGaussian(), randomGaussian()).mult(slider("spawn range"));
    }
}
