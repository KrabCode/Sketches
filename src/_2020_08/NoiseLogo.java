package _2020_08;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import utils.OpenSimplexNoise;

public class NoiseLogo extends KrabApplet {
    PGraphics pg;
    OpenSimplexNoise noise = new OpenSimplexNoise();

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        fullScreen(P3D);
    }

    public void setup() {
        toggleFullscreen();
    }

    public void draw() {
        pg = updateGraphics(pg);
        pg.beginDraw();
        pg.background(0);
        updateNoise();
        pg.endDraw();
        image(pg, 0, 0);
        int frames = sliderInt("frames", 360, 1, Integer.MAX_VALUE);

        rec(pg, frames);
        gui();
    }

    private void updateNoise() {
        int count = sliderInt("count", 1024 / 6);
        int colorCount = sliderInt("colors", 3, 1, 15);
        PVector time = new PVector(slider("time radius"), 0);
        time.rotate(t);
        float freq = slider("freq", 0.01f);
        float distFreq = slider("dist freq", 0);
        float amp = slider("amp", 1);
        float s = (width / (float) count) + 5;
        for (int xi = 0; xi < count; xi++) {
            for (int yi = 0; yi < count; yi++) {
                float x = map(xi, 0, count - 1, 0, width);
                float y = map(yi, 0, count - 1, 0, height);
                float d = dist(xi, yi, count/2f, count/2f);
                float myFreq = freq;
                if(distFreq > 0) {
                   myFreq += distFreq * d;
                }
                float n = amp * (float) noise.eval(myFreq * x, myFreq * y, time.x, time.y);
                if (toggle("abs")) {
                    n = abs(n);
                } else {
                    n = .5f + .5f * n;
                }
                n *= colorCount;
                n = floor(n);
                n /= colorCount;
                pg.fill(gradientColorAt("gradient", n));
                pg.noStroke();
                pg.rect(x, y, s, s);
            }
        }
    }
}
