package _2020_03;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import utils.OpenSimplexNoise;

/**
 * Created by Jakub 'Krab' Rak on 2020-03-29
 */
public class NoiseFieldDots extends KrabApplet {
    private OpenSimplexNoise noise = new OpenSimplexNoise();
    private PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(String.valueOf(new Object() {}.getClass().getEnclosingClass().getName()));
    }

    public void settings() {
        size(800, 800, P2D);
    }

    public void setup() {
        surface.setAlwaysOnTop(true);
        pg = createGraphics(width, height, P2D);
        pg.smooth(16);
        pg.beginDraw();
        pg.background(0);
        pg.endDraw();
    }

    public void draw() {
        pg.beginDraw();
        alphaFade(pg);
        PVector move = sliderXY("move");
        int count = sliderInt("count", 100);
        for (int xi = 0; xi < count; xi++) {
            for (int yi = 0; yi < count; yi++) {
                float weight = slider("weight", 1);
                float x = map(xi, 0, count - 1, 0, width);
                float y = map(yi, 0, count - 1, 0, height);
                float tr = slider("tr", 1);
                float freq = slider("freq");
                float n = fbm(x * freq + move.x * t, y * freq + move.y * t, tr * cos(t), tr * sin(t));
                if(toggle("abs n")){
                    n = abs(n);
                    n = constrain(n, slider("n min", -1), slider("n max", 1));
                }
                n = pow(n, slider("pow", 1));
                int clr = lerpColor(picker("low color").clr(), picker("high color").clr(), n);
                pg.stroke(clr);
                pg.strokeWeight(weight + (slider("amp") * (.5f + .5f * n)));
                pg.point(x, y);
            }
        }
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    private float fbm(float x, float y, float z, float w) {
        float sum = 0;
        float defaultFreq = 1;
        float defaultAmp = 1;
        for (int i = 0; i < sliderInt("octaves", 3); i++) {
            float freq = slider("freq " + i, defaultFreq);
            sum += slider("amp " + i, defaultAmp) * noise.eval(x * freq, y * freq, z, w);
            defaultAmp *= .5f;
            defaultFreq *= 2f;
        }
        return sum;
    }
}
