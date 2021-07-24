package _2021_07;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import utils.OpenSimplexNoise;

import java.util.ArrayList;

public class HanddrawnParticles extends KrabApplet {
    private PGraphics pg;
    private PImage backgroundImage;
    int w = 3024;
    int h = 4032;
    float size = 0.35f; // change to 0.4 for production
    OpenSimplexNoise simplexNoise = new OpenSimplexNoise();
    PVector alphaA, alphaB, alphaC, alphaD, worldRectOrigin, worldRectSize;
    ArrayList<PGraphics> imageGraphics = new ArrayList<>();
    ArrayList<Particle> particles = new ArrayList<>();
    ArrayList<Particle> particlesToRemove = new ArrayList<>();
    ArrayList<PImage> particleImages = new ArrayList<>();

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        fullScreen(P3D);
    }

    public void setup() {
        backgroundImage = loadImage("images/handDrawnFlowField/notepad_small.jpg");
        for (int i = 0; i < 10; i++) {
            imageGraphics.add(createGraphics(40, 40, P2D));
            particleImages.add(loadImage("images/handDrawnFlowField/particles/" + i + ".jpg"));
        }
        toggleFullscreen(floor(w * size), ceil(h * size));
        surface.setAlwaysOnTop(true);
    }

    public void draw() {
        pg = updateGraphics(pg, P3D);
        pg.beginDraw();
        pg.colorMode(RGB, 1, 1, 1, 1);
        pg.imageMode(CORNER);
        pg.image(backgroundImage, 0, 0, width, height);
        updateImageGraphics();
        updateDrawFlowField();
        pg.endDraw();
        image(pg, 0, 0, width, height);
        group("recording");
        rec(pg, sliderInt("frames", 360));
        gui(false);
    }

    private void updateImageGraphics() {
        for (int i = 0; i < imageGraphics.size(); i++) {
            PGraphics ig = imageGraphics.get(i);
            ig.beginDraw();
            ig.clear();
            ig.imageMode(CENTER);
            translateToCenter(ig);
            hotShader("shaders/_2021_07/handDrawnNoiseField/blackToTransparent.glsl", ig);
            ig.image(particleImages.get(i), 0, 0);
            ig.endDraw();
        }
    }

    private void updateDrawFlowField() {
        group("alpha");
        alphaA = sliderXY("A");
        alphaB = sliderXY("B");
        alphaC = sliderXY("C");
        alphaD = sliderXY("D");
        if (toggle("debug")) {
            pg.noFill();
            pg.strokeWeight(3);
            pg.stroke(0.5f, 0.5f, 1);
            triangle(alphaA, alphaD, alphaB);
            triangle(alphaC, alphaB, alphaD);
        }

        group("world");
        worldRectOrigin = sliderXY("world origin");
        worldRectSize = sliderXY("world size");
        if (toggle("debug")) {
            pg.noFill();
            pg.strokeWeight(3);
            pg.stroke(1f, 0.5f, 0.5f);
            pg.rect(worldRectOrigin.x, worldRectOrigin.y, worldRectSize.x, worldRectSize.y);
        }

        group("particles");
        int particleCount = sliderInt("max count", 50);
        int particleSpawnPerFrame = sliderInt("spawn per frame", 1);
        if (particles.size() < particleCount) {
            for (int i = 0; i < particleSpawnPerFrame; i++) {
                particles.add(new Particle());
            }
        }
        for (Particle p : particles) {
            p.update();
        }
        particles.removeAll(particlesToRemove);
        particlesToRemove.clear();
    }

    void triangle(PVector a, PVector b, PVector c) {
        pg.triangle(a.x, a.y, b.x, b.y, c.x, c.y);
    }

    class Particle {
        int imageIndex = floor(random(10));
        PVector pos = new PVector(
                worldRectOrigin.x + random(worldRectSize.x),
                worldRectOrigin.y + random(worldRectSize.y)
        );

        PVector spd = new PVector();

        int alphaMax = 100;
        int alphaMin = 0;
        int alpha = alphaMin;

        Particle() {

        }

        void update() {
            move();
            collide();
            draw();
        }

        private void collide() {
            boolean isInsideWorld = isPointInRect(pos.x, pos.y, worldRectOrigin, worldRectSize);
            if (!isInsideWorld) {
                particlesToRemove.add(this);
            }
            boolean isInsideAlphaTriangles =
                    isPointInTriangle(alphaA, alphaD, alphaB, pos) ||
                    isPointInTriangle(alphaC, alphaB, alphaD, pos);
            float alphaChange = slider("alpha change", 10);
            if(isInsideAlphaTriangles){
                alpha += alphaChange;
            }else{
                alpha -= alphaChange;
            }
            alpha = constrain(alpha, alphaMin, alphaMax);
        }

        void move() {
            float noiseFreq = slider("noise freq", 0.1f);
            float noiseAmp = slider("noise angle range", TAU);
            float speed = slider("speed", 1);
            float drag = slider("drag", 0.98f);
            float angle = noiseAmp * (float) simplexNoise.eval(pos.x * noiseFreq, pos.y * noiseFreq);
            PVector acc = PVector.fromAngle(angle).mult(speed);
            spd.add(acc);
            spd.mult(drag);
            pos.add(spd);
        }

        void draw() {
//            hotShader("shaders/_2021_07/handDrawnNoiseField/blackToTransparent.glsl", pg);
            pg.pushStyle();
            pg.pushMatrix();
            pg.imageMode(CENTER);
            PImage img = imageGraphics.get(imageIndex);
            pg.translate(pos.x, pos.y);
            pg.scale(slider("scale", 0.8f));
            pg.tint(1, 1, 1, norm(alpha, alphaMin, alphaMax));
            pg.image(img, 0, 0);
            pg.popMatrix();
            pg.popStyle();
        }

        boolean isPointInTriangle(PVector a, PVector b, PVector c, PVector p){
            return isPointInTriangle(a.x, a.y, b.x, b.y, c.x, c.y, p.x, p.y);
        }
        // TRIANGLE/POINT from https://www.crhallberg.com/CollisionDetection/Website/tri-point.html
        boolean isPointInTriangle(float x1, float y1, float x2, float y2, float x3, float y3, float px, float py) {
            // get the area of the triangle
            float areaOrig = abs((x2 - x1) * (y3 - y1) - (x3 - x1) * (y2 - y1));

            // get the area of 3 triangles made between the point
            // and the corners of the triangle
            float area1 = abs((x1 - px) * (y2 - py) - (x2 - px) * (y1 - py));
            float area2 = abs((x2 - px) * (y3 - py) - (x3 - px) * (y2 - py));
            float area3 = abs((x3 - px) * (y1 - py) - (x1 - px) * (y3 - py));

            // if the sum of the three areas equals the original,
            // we’re inside the triangle!
            // (< 1 due to floating point issues in Javascript)
            return Math.abs((area1 + area2 + area3) - areaOrig) < 1;
        }

        protected boolean isPointInRect(float px, float py, PVector pos, PVector size) {
            float rx = pos.x, ry = pos.y, rw = size.x, rh = size.y;
            return px >= rx && px <= rx + rw && py >= ry && py <= ry + rh;
        }
    }
}


