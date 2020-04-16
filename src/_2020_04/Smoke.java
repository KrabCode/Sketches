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
        updateParticles();
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    void updateParticles() {
        if (frameCount % sliderInt("spawn modulo", 1) == 0) {
                particles.add(new Particle(sliderXY("pos").copy()));
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
            int lifeSpan = sliderInt("life span", 60);
            float lifeNorm = clampNorm(frameCount, lifeStarted, lifeStarted + lifeSpan);
            PVector acc = PVector.lerp(sliderXY("static force 0").copy(), sliderXY("static force 1"), lifeNorm);
            PVector toCenter = PVector.sub(new PVector(), pos).normalize().setMag(slider("centralize"));
            acc.add(toCenter);
            acc.add(noise(pos, lifeNorm));
            spd.add(acc);
            spd.mult(slider("drag", 1));
            pos.add(spd);
            pg.fill(lerpColor(picker("color 0").clr(), picker("color 1").clr(), lifeNorm));
            pg.noStroke();
            float r = lerp(slider("radius 0", 0), slider("radius 1", 60), lifeNorm);
            pg.ellipse(pos.x, pos.y, r, r);
            if (lifeNorm >= 1) {
                toRemove.add(this);
            }
        }

        private PVector noise(PVector pos, float lifeNorm) {
            float noiseMag = lerp(slider("noise mag 0", 1), slider("noise mag 1", 0), lifeNorm);
            float noiseFreq = lerp(slider("noise freq 0", .5f), slider("noise freq 1", 0f), lifeNorm);
            float speed = slider("noise speed", 1);
            float x = (float) (noiseMag * (1 - 2 * noise.eval(pos.x * noiseFreq, pos.y * noiseFreq, pos.z * noiseFreq, t * speed)));
            float y = (float) (noiseMag * (1 - 2 * noise.eval(98 + pos.x * noiseFreq, 98 + pos.y * noiseFreq, 98 + pos.z * noiseFreq, t * speed)));
            return new PVector(x, y);
        }
    }
}
