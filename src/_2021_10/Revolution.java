package _2021_10;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PVector;

@SuppressWarnings("DuplicatedCode")
public class Revolution extends KrabApplet {
    private PGraphics pg;

    public static void main(String[] args) {
        main(java.lang.invoke.MethodHandles.lookup().lookupClass());
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
        fadeToBlack(pg);
        group("circles");
        translateToCenter(pg);
        pg.stroke(picker("stroke",0,0,0,0).clr());
        pg.fill(picker("fill", 1).clr());
        drawCircles();
        resetGroup();
        chromaticAberrationBlurDirPass(pg);
        pg.endDraw();
        image(pg, 0, 0);
        gui();
        glowCursor();
        rec(g, sliderInt("frames", 360));
    }

    float time = 0;

    private void drawCircles() {
        time += slider("time speed") * radians(1);
        translate(pg, "translate");
        preRotate(pg, "rotate");

        pg.sphereDetail(sliderInt("sphere detail", 14));
        int count = sliderInt("count", 100);
        for (int index = 0; index < count; index++) {
            float i = norm(index, 0, count);
            float freq = i * slider("freq", 0.1f);
            PVector noiseMag = sliderXYZ("noise mag");
            float x = noiseMag.x * simplex(i*freq, time);
            float y = noiseMag.y * simplex(i*freq + 1694.312f, time);
            float z = noiseMag.z * simplex(i*freq - 1194.312f, time);
            float w = slider("w", 10);
            pg.pushMatrix();
            pg.translate(x,y,z);
            pg.sphere(w);
            pg.popMatrix();
        }
    }
}

