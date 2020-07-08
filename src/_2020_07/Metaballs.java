package _2020_07;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class Metaballs extends KrabApplet {
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
        pg = preparePGraphics(pg);
        pg.beginDraw();
        fadeToBlack(pg);
        chromaticAberrationPass(pg);
        updateLissajousMetaballs();
        pg.endDraw();
        rgbSplitScaleAndOffset(pg);
        clear();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    private void updateLissajousMetaballs() {
        group("lissajous");
        int ballCount = sliderInt("ball count", 100, 1, 1000);
        PVector freq = sliderXY("freq", 1.254f, 3.14159f);
        PVector amp = sliderXY("amp", 300);
        float maxAngle = slider("max angle", TAU);
        float[] positions = new float[ballCount * 2];
        for (int i = 0; i < ballCount * 2; i += 2) {
            float iNorm = norm(i, 0, ballCount);
            float x = amp.x * cos((iNorm * maxAngle + t) * freq.x);
            float y = amp.y * sin((iNorm * maxAngle + t) * freq.y);
            positions[i]     = width  / 2f + x;
            positions[i + 1] = height / 2f + y;
        }
        group("metaballs");
        String metaballShader = "shaders/_2020_07/metaballs.glsl";
        uniform(metaballShader).set("gradient", gradient("gradient"));
        uniform(metaballShader).set("positions", positions, 2);
        uniform(metaballShader).set("posCount", ballCount);
        hotFilter(metaballShader, pg);
        resetShader();
        resetGroup();
    }
}
