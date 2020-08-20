package _2020_08;

import applet.KrabApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

public class Landscape extends KrabApplet {
    private PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        fullScreen(P3D);
    }

    public void setup() {
    }

    public void draw() {
        pg = updateGraphics(pg);
        pg.beginDraw();
        pg.background(0);
        translateToCenter(pg);
        translate(pg);
        preRotate(pg);
//        lights(pg, 1);
        updateGrid();
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg, sliderInt("frames", 360));
        gui();
    }

    private void updateGrid() {
        int xCount = sliderInt("rows", 20);
        int zCount = sliderInt("cols", 20);
        PVector size = sliderXY("size");
        pg.resetShader();
        pg.stroke(picker("stroke").clr());
        pg.strokeWeight(slider("weight"));
        for (int zi = 0; zi < zCount; zi++) {
            pg.beginShape(TRIANGLE_STRIP);
            for (int xi = 0; xi < xCount; xi++) {
                float xNorm = norm(xi, 0, xCount-1);
                float z0Norm = norm(zi, 0, zCount-1);
                float z1Norm = norm(zi-1, 0, zCount-1);
                float x = -size.x+xNorm*size.x*2f;
                float z0 = -size.x+z0Norm*size.x*2f;
                float z1 = -size.x+z1Norm*size.x*2f;
                pg.fill(gradientColorAt("grad", z0Norm));
                pg.vertex(x, 0, z0);
                pg.fill(gradientColorAt("grad", z1Norm));
                pg.vertex(x, 0, z1);
            }
            pg.endShape();
        }
    }
}
