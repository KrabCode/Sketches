package _2020_04;

import applet.KrabApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import utils.OpenSimplexNoise;

import java.util.ArrayList;

public class Smoke extends KrabApplet {
    private ArrayList<Particle> particles = new ArrayList<>();
    private ArrayList<Particle> toRemove = new ArrayList<>();
    private ArrayList<Float> terrain = new ArrayList<>();
    private OpenSimplexNoise noise = new OpenSimplexNoise();
    private PGraphics pg;
    private PImage train, wheel;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(800, 800, P3D);
    }

    public void setup() {
        train = loadImage("data/images/smoke/moneyvlacek.png");
        wheel = loadImage("data/images/smoke/kolo.png");
        pg = createGraphics(width, height, P3D);
        pg.colorMode(HSB, 1, 1, 1, 1);
        frameRecordingDuration *= 2;
        surface.setAlwaysOnTop(true);
    }

    public void draw() {
        background(0);
        pg.beginDraw();
        updateBackground();
        pg.hint(PConstants.DISABLE_DEPTH_TEST);
        updateTrain();
        updateForeground();
        updateParticles();
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    private void updateForeground() {
        group("foreground");
        updateShader(true);
        updateLines();
        pg.resetShader();
    }

    private void updateBackground() {
        group("background");
        updateShader(false);
        updateLines();
        pg.resetShader();
    }

    private void updateLines() {
        int lineCount = sliderInt("line count", 8);
        int vertexCount = sliderInt("vertex count", 100);
        HSBA color0 = picker("color 0");
        HSBA color1 = picker("color 1");
        float buffer = 50;
        for (int i = 0; i < lineCount; i++) {
            float inorm = clampNorm(i, 0, lineCount - 1);
            float y = lerp(slider("top Y", -buffer), slider("bottom Y", height + buffer), inorm);
            pg.stroke(lerpColor(color0.clr(), color1.clr(), inorm));
            pg.noFill();
            pg.strokeWeight(slider("weight"));
            pg.beginShape();
            for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
                float vertexNorm = clampNorm(vertexIndex, 0, vertexCount);
                float x = lerp(-buffer, width + buffer, vertexNorm);
                pg.vertex(x, y);
            }
            pg.endShape();
        }
    }

    private void updateShader(boolean foreground) {
        String frag = "shaders/_2020_04/smoke/LineFrag.glsl";
        String vert = "shaders/_2020_04/smoke/LineVert.glsl";
        if (foreground) {
            frag = "shaders/_2020_04/smoke/ForegroundLineFrag.glsl";
            vert = "shaders/_2020_04/smoke/ForegroundLineVert.glsl";
        }
        uniform(frag, vert).set("time", t);
        hotShader(frag, vert, pg);
    }


    private void updateTrain() {
        group("train");
        drawSmallWheels();
        drawTrain();
        drawBigWheels();
    }

    private void drawTrain() {
        PVector trainPos = sliderXY("pos");
        pg.pushMatrix();
        pg.imageMode(CENTER);
        pg.scale(slider("train scale", 1));
        pg.image(train, trainPos.x, trainPos.y);
        pg.popMatrix();
    }

    private void drawSmallWheels() {
        PVector smallWheelPos = sliderXY("small wheel pos").copy();
        float smallOffset = slider("small offset");
        float smallerOffset = slider("smaller offset");
        float smallestOffset = slider("smallest offset");
        float x = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                float rotationOffset = randomDeterministic(i * 20 + j * 40) * TAU * 2;
                PVector pos = smallWheelPos.copy();
                pos.x += x;
                if (j % 2 == 0) {
                    x += smallestOffset;
                } else {
                    x += smallerOffset;
                }
                pg.pushMatrix();
                pg.translate(pos.x, pos.y);
                pg.rotate(rotationOffset + t * slider("wheel speed", 1));
                pg.scale(slider("small wheel scale", 1));
                pg.imageMode(CENTER);
                pg.image(wheel, 0, 0);
                pg.popMatrix();
            }
            x += smallOffset;
        }

    }

    private void drawBigWheels() {
        PVector wheelPos0 = sliderXY("wheel pos");
        float wheelOffset = slider("wheel offset", 20);
        for (int i = 0; i < 3; i++) {
            PVector pos = wheelPos0.copy();
            pos.x += i * wheelOffset;
            float rotationOffset = randomDeterministic(i * 20) * TAU * 2;
            pg.pushMatrix();
            pg.imageMode(CENTER);
            pg.translate(pos.x, pos.y);
            pg.rotate(rotationOffset + t * slider("wheel speed", 1));
            pg.scale(slider("wheel scale", 1));
            pg.image(wheel, 0, 0);
            pg.popMatrix();
        }
    }

    void updateParticles() {
        group("smoke");
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
