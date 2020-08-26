package _2020_08;

import applet.KrabApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;
import utils.OpenSimplexNoise;

public class Landscape extends KrabApplet {
    private PGraphics pg;
    OpenSimplexNoise noise = new OpenSimplexNoise();

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
        fadeToBlack(pg);
        landscapeHash(pg);
        translateToCenter(pg);
        translate(pg);
        preRotate(pg);
//        lights(pg, 1);
        fbmDisplacePass(pg);
        updateGrid();
        pg.endDraw();
        rgbSplitScaleAndOffset(pg);
        image(pg, 0, 0);
        rec(pg, sliderInt("frames", 360));
        gui();
    }

    private void landscapeHash(PGraphics pg) {
        String hash = "shaders\\_2020_08\\hashLandscape.glsl";
        uniform(hash).set("time", t);
        uniform(hash).set("gradient", gradient("hash"));
        hotFilter(hash, pg);
    }

    private void updateGrid() {
        int xCount = sliderInt("rows", 20);
        int zCount = sliderInt("cols", 20);
        PVector freq = sliderXY("freq", .1f);
        PVector amp = sliderXY("amp", 20);

        PVector size = sliderXY("size");
        PVector time = new PVector(cos(t), sin(t)).mult(slider("time radius", 1));
        PVector moveTime = new PVector(cos(t), sin(t)).mult(slider("move radius", 1));
        pg.resetShader();
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

                float y0 =  amp.x * ((float) noise.eval(x*freq.x+moveTime.x, z0*freq.x+moveTime.y, 10+time.x, 12+time.y));
                float y1 =  amp.x * ((float) noise.eval(x*freq.x+moveTime.x, z1*freq.x+moveTime.y, 10+time.x, 12+time.y));

                y0 += amp.y * ((float) noise.eval(x*freq.y+moveTime.x, z0*freq.y+moveTime.y, 13-time.x, 17-time.y));
                y1 += amp.y * ((float) noise.eval(x*freq.y+moveTime.x, z1*freq.y+moveTime.y, 13-time.x, 17-time.y));

                pg.stroke(gradientColorAt("stroke", z0Norm));
                pg.fill(gradientColorAt("fill", z0Norm));
                pg.vertex(x, y0, z0);
                pg.stroke(gradientColorAt("stroke", z1Norm));
                pg.fill(gradientColorAt("fill", z1Norm));
                pg.vertex(x, y1, z1);
            }
            pg.endShape();
        }
    }
}
