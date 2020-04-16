package _2020_04;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import utils.OpenSimplexNoise;

import java.util.ArrayList;

public class Smoke extends KrabApplet {
    private ArrayList<Particle> particles = new ArrayList<>();
    private ArrayList<Particle> toRemove = new ArrayList<>();
    private OpenSimplexNoise noise = new OpenSimplexNoise();
    private PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(new Object() {}.getClass().getEnclosingClass().getName());
    }

    public void settings() {
        size(800, 800, P2D);
//        fullScreen(P2D);
    }

    public void setup() {
        pg = createGraphics(width, height, P2D);
        frameRecordingDuration *= 2;
    }

    public void draw() {
        background(0);
        pg.beginDraw();
        pg.background(0);
        pg.translate(width * .5f, height * .5f);
        updateParticles();
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    void updateParticles() {
        int spawnCount = sliderInt("spawn count", 4);
        if (frameCount % sliderInt("spawn modulo", 1) == 0) {
            for (int i = 0; i < spawnCount; i++) {
                float inorm = clampNorm(i, 0, spawnCount);
                float radius = slider("circle radius");
                float angle = inorm * TAU + t * slider("rotate speed");
                PVector spawnPos = new PVector(radius, 0).rotate(angle);
                particles.add(new Particle(spawnPos));
            }
        }
        for (Particle p : particles) {
            p.update();
        }
        particles.removeAll(toRemove);
        toRemove.clear();
        if (button("clear particles")) {
            particles.clear();
        }
    }

    class Particle {
        PVector pos;
        PVector spd = new PVector();
        int lifeStarted = frameCount;

        public Particle(PVector spawnPos) {
            this.pos = spawnPos;
        }

        public void update() {
            PVector acc = sliderXY("static force").copy();
            PVector toCenter = PVector.sub(new PVector(), pos).normalize().setMag(slider("centralize"));
            acc.add(toCenter);
            acc.add(noise(pos));
            spd.add(acc);
            spd.mult(slider("drag", 1));
            pos.add(spd);
            int lifeSpan = sliderInt("life span", 60);
            float lifeNorm = clampNorm(frameCount, lifeStarted, lifeStarted + lifeSpan);
            pg.fill(lerpColor(picker("color 0").clr(), picker("color 1").clr(), lifeNorm));
            pg.noStroke();
            float r = lerp(slider("radius 0", 0), slider("radius 1", 60), lifeNorm);
            pg.ellipse(pos.x, pos.y, r, r);
            if (lifeNorm >= 1) {
                toRemove.add(this);
            }
        }

        private PVector noise(PVector pos) {
            float noiseMag = slider("noise mag");
            float noiseFreq = slider("noise freq", .1f);
            float speed = slider("noise speed", 1);
            float x = (float) (noiseMag * (1 - 2 * noise.eval(pos.x * noiseFreq, pos.y * noiseFreq, pos.z * noiseFreq, t * speed)));
            float y = (float) (noiseMag * (1 - 2 * noise.eval(98 + pos.x * noiseFreq, 98 + pos.y * noiseFreq, 98 + pos.z * noiseFreq, t * speed)));
            return new PVector(x, y);
        }
    }
}
