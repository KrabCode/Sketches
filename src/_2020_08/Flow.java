package _2020_08;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PVector;

import java.util.ArrayList;

public class Flow extends KrabApplet {
    private PGraphics pg;
    ArrayList<Particle> particles = new ArrayList<Particle>();
    ArrayList<Particle> particlesToRemove = new ArrayList<Particle>();

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
//        fullScreen(P3D);
        size(800, 800, P2D);
    }

    public void setup() {
    }

    public void draw() {
        pg = updateGraphics(pg);
        pg.beginDraw();
        fadeToBlack(pg);
        translateToCenter(pg);
        pg.blendMode(ADD);
        pg.noFill();
        pg.strokeWeight(slider("weight"));
        pg.stroke(picker("stroke").clr());
        drawParticles();
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg, sliderInt("frames", 360));
        gui();
    }

    private void drawParticles() {
        int count = sliderInt("count", 100, 1, 100000000);
        while (particles.size() > count) {
            particles.remove(0);
        }
        while (particles.size() < count) {
            particles.add(new Particle());
        }
        for (Particle p : particles) {
            p.update();
        }
        particles.removeAll(particlesToRemove);
        particlesToRemove.clear();
    }

    class Particle {
        int frameCreated = frameCount;
        PVector pos = randomOffscreenPosition();
        PVector spd = new PVector();

        void update() {
            if (frameCreated + slider("death delay", 120) < frameCount && !isPointOnScreen(pos)) {
                particlesToRemove.add(this);
                return;
            }
            PVector acc = sliderXY("force constant").copy();
            PVector noisePos = sliderXY("noise pos");
            PVector freq = sliderXY("frequency");
            float angle = slider("base angle") + slider("angle range") * noise((noisePos.x + pos.x) * freq.x, (noisePos.y + pos.y) * freq.y);
            float amp = slider("amp", .5f);
            acc.x += amp * cos(angle);
            acc.y += amp * sin(angle);
            spd.add(acc);
            float dampen = slider("dampen", .95f);
            spd.mult(dampen);
            float prevX = pos.x;
            float prevY = pos.y;
            pos.add(spd);
            pg.line(prevX, prevY, pos.x, pos.y);
        }

        private PVector randomOffscreenPosition() {
            float range = width * slider("spawn range", 2);
            PVector r = new PVector(random(-range, range), random(-range, range));
            while (isPointOnScreen(r)) {
                r.x = random(-range, range);
                r.y = random(-range, range);
            }
            return r;
        }

        private boolean isPointOnScreen(PVector p) {
            return isPointInRect(p.x, p.y, -width / 2f, -height / 2f, width, height);
        }
    }

//
//    void drawSines() {
//        int sineCount = sliderInt("sine count", 100);
//        float freq = slider("freq");
//        int xDetail = sliderInt("x detail", 100);
//        for (int i = 0; i < sineCount; i++) {
//            pg.beginShape();
//            for (int xi = 0; xi < xDetail; xi++) {
//                float xNorm = norm(xi, 0, xDetail-1);
//                float amp = slider("base amp") + slider("x amp") * xNorm;
//                float x = xNorm * width;
//                float g = getGaussian(i);
//                float y = pg.height / 2f + amp * sin(xNorm*freq) + xNorm * g * slider("random");
//                pg.vertex(x,y);
//            }
//            pg.endShape();
//        }
//    }
//
//    ArrayList<Float> gaussians = new ArrayList<>();
//    float getGaussian(int seed) {
//        if(gaussians.size()-1 >= seed) {
//            return gaussians.get(seed);
//        }
//        float newGaussian = randomGaussian();
//        gaussians.add(newGaussian);
//        return newGaussian;
//    }
}
