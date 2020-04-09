package _2020_04;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PShape;
import processing.core.PVector;

public class TriangleStripTower extends KrabApplet {
    private PGraphics pg;
    private PShape grid;
    private PVector count = new PVector();
    private PVector size = new PVector();

    public static void main(String[] args) {
        KrabApplet.main(String.valueOf(new Object() {}.getClass().getEnclosingClass().getName()));
    }

    public void settings() {
        size(800, 800, P3D);
//        fullScreen(P3D, 2);
    }

    public void setup() {
        pg = createGraphics(width, height, P3D);
    }

    public void draw() {
        pg.beginDraw();
        alphaFade(pg);
        pg.translate(width / 2f, height / 2f);
        pg.translate(sliderXYZ("translate").x, sliderXYZ("translate").y, sliderXYZ("translate").z);
        blurPass(pg);
        updateShader();
        pg.lights();
        mouseRotation(pg);
        updateShape();
        grid.setStroke(picker("stroke").clr());
        grid.setStrokeWeight(slider("weight"));
        grid.setFill(false);
        pg.shape(grid);
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    private void updateShader() {
        String frag = "shaders/_2020_04/LineFrag.glsl";
        String vert = "shaders/_2020_04/LineVert.glsl";
        uniform(frag, vert).set("time", t);
        hotShader(frag, vert, pg);
    }

    private void updateShape() {
        PVector intendedCount = new PVector(sliderInt("x count", 10), sliderInt("y count", 10), sliderInt("z count", 10));
        PVector intendedSize = sliderXYZ("size");
        if (!intendedCount.equals(count) || !intendedSize.equals(size)) {
            count = intendedCount.copy();
            size = intendedSize.copy();
            regenerateShape();
        }
    }

    private void regenerateShape() {
        grid = createShape(GROUP);
        for (int yi = 0; yi < count.y; yi++) {
            for (int zi = 0; zi < count.z; zi++) {
                PShape strip = createShape();
                strip.beginShape(TRIANGLE_STRIP);
                for (int xi = 0; xi < count.x; xi++) {
                    float x = map(xi, 0, count.x, -size.x, size.x);
                    float y = map(yi, 0, count.y, -size.y, size.y);
                    float z = map(zi, 0, count.z, -size.z, size.z);
                    float neighbourZ = map(zi + 1, 0, count.z, -size.z, size.z);
                    strip.vertex(x, y, neighbourZ);
                    strip.vertex(x, y, z);
                }
                strip.endShape();
                grid.addChild(strip);
            }
        }
    }
}
