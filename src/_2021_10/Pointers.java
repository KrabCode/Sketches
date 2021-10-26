package _2021_10;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import utils.OpenSimplexNoise;

public class Pointers extends KrabApplet {
    private PGraphics pg;
    OpenSimplexNoise noiseGen = new OpenSimplexNoise();
    //int cursorColor = color(155, 126, 75);
    int cursorColor = 255;
    float size = 35;
    int xCount = 25;
    int yCount = 25;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        fullScreen(P3D);
        smooth(16);
    }

    public void setup() {
        toggleFullscreen();
    }

    public void draw() {
        pg = updateGraphics(pg);
        pg.beginDraw();
        pg.image(gradient("background"), 0, 0);
        render();
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg, sliderInt("frames", 360));
        gui();
    }

    private void render() {
        //background(69, 54, 48);
        pg.translate(width / 2f, height / 2f);
        float t = radians(frameCount) * 0.5f;
        float n = max(width, height) + slider("n", size);
        float freq = slider("freq", 0.0005f);
        float tr = slider("time radius", 1.5f);
        size = slider("size", size);
        xCount = sliderInt("x count", xCount);
        yCount = sliderInt("y count", yCount);
        float range = slider("range", PI + QUARTER_PI);
        gradient("cursor");
        float skipDist = slider("skip dist", n / 2);
        for (int xi = 0; xi < xCount; xi++) {
            for (int yi = 0; yi < yCount; yi++) {
                float x = map(xi, 0, xCount - 1, -n / 2, n / 2);
                float y = map(yi, 0, yCount - 1, -n / 2, n / 2);
                float d = dist(x, y, 0, 0);
                if (d > skipDist) {
                    continue;
                }
                //float angle = atan2(y, x);
                pg.pushMatrix();
                float noise = simplexNoise(x, y, freq, tr * 0.25f, t);
                noise += 0.5 * simplexNoise(x + 75, y + 210, freq * 5, tr * 0.5f, t);
                noise += 0.25 * simplexNoise(x + 323, y + 789, freq * 10, tr, t);

                cursorColor = gradientColorAt("cursor", 0.5f + 0.5f * noise);
                pg.translate(x, y);
                pg.rotate(-HALF_PI + range * noise);
                drawCursor();
                pg.popMatrix();
            }
        }
    }

    float simplexNoise(float x, float y, float freq, float tr, float t) {
        return (float) noiseGen.eval(x * freq, y * freq, tr * cos(t), tr * sin(t));
    }

    void drawCursor() {
        PVector a = new PVector(0.4f, 0).mult(size); // right
        PVector b = new PVector(-0.2f, -0.2f).mult(size); // top
        PVector c = new PVector(-0.2f, 0.2f).mult(size);  // bottom
        pg.noStroke();
        pg.fill(cursorColor);
        pg.triangle(a.x, a.y, b.x, b.y, c.x, c.y);
        pg.strokeWeight(slider("leg weight", 3));
        if (toggle("legs")) {
            pg.stroke(cursorColor);
            pg.line(-0.4f * size, 0, 0, 0);
        }

    }

    void drawCross() {
        pg.noStroke();
        pg.fill(cursorColor);
        pg.beginShape();
        float r0 = size * 0.5f;
        float r1 = size * 0.2f;
        for (int i = 0; i < 8; i++) {
            float iNorm = norm(i, 0, 8);
            float theta = iNorm * TAU;
            float r = r1;
            if (i % 2 == 0) {
                r = r0;
            }
            pg.vertex(r * cos(theta), r * sin(theta));
        }
        pg.endShape(CLOSE);
    }

}
