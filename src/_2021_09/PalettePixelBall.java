package _2021_09;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import utils.OpenSimplexNoise;

public class PalettePixelBall extends KrabApplet {
    // https://iquilezles.org/www/articles/palettes/palettes.htm

    OpenSimplexNoise noise = new OpenSimplexNoise();

    PVector a = new PVector(0.5f, 0.5f, 0.5f);
    PVector b = new PVector(0.5f, 0.5f, 0.5f);
    PVector c = new PVector(1.0f, 1.0f, 1.0f);
    PVector d = new PVector(0.00f, 0.10f, 0.20f);

    PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        fullScreen(P3D);
    }

    public void setup() {
        toggleFullscreen();
        surface.setAlwaysOnTop(true);
    }

    public void draw() {
        pg = updateGraphics(pg);
        pg.beginDraw();
        pg.noStroke();
        a = sliderXYZ("a", a.x, a.y, a.z); // .add(sliderXYZ("speed a"));
        b = sliderXYZ("b", b.x, b.y, b.z); // .add(sliderXYZ("speed b"));
        c = sliderXYZ("c", c.x, c.y, c.z); // .add(sliderXYZ("speed c"));
        d = sliderXYZ("d", d.x, d.y, d.z); // .add(sliderXYZ("speed d"));
        int count = sliderInt("x count", 100);
        float size = map(1, 0, count-1, 0, width);
        float freq = slider("freq", 0.01f);
        float speed = slider("speed", 0.1f);
        float scale = slider("scale", 1);
        float amp = slider("amp", 1);
        for (int xi = 0; xi < count; xi++) {
            for (int yi = 0; yi < count; yi++) {
                int length = abs(- count / 2 + xi) +
                        abs(- count / 2 + yi);
                float x = map(xi, 0, count-1, 0, width);
                float y = map(yi, 0, count-1, 0, height);
                float distance = dist(x,y,width/2f, height/2f);
                float pct = norm(length, 0, count ) / 2f + norm(distance, 0, width/2f) / 2f;
                pct *= scale;
                float n = amp * (float) noise.eval(x*freq,y*freq,t*speed);
                int myColor = palette(pct + n, a, b, c, d);
                pg.fill(myColor);
                pg.rect(x,y,size, size);
            }
        }
        pg.endDraw();
        clear();
        image(pg, 0, 0);
        gui();
        rec(pg);
    }

    int palette(float t, PVector a, PVector b, PVector c, PVector d) {
        PVector rgb = PVector.add(a, mult(b, cos(PVector.mult(PVector.add(d, PVector.mult(c, t)), TAU))));
        colorMode(RGB, 1, 1, 1, 1);
        return color(rgb.x, rgb.y, rgb.z);
    }

    PVector mult(PVector a, PVector b) {
        return new PVector(a.x * b.x, a.y * b.y, a.z * b.z);
    }

    PVector cos(PVector input) {
        return new PVector(cos(input.x), cos(input.y), cos(input.z));
    }

}
