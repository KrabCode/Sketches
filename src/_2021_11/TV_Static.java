package _2021_11;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class TV_Static extends KrabApplet {
    private PGraphics pg;

    public static void main(String[] args) {
        main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        fullScreen(P3D);
    }

    public void setup() {
        toggleFullscreen(300, 300);
        surface.setAlwaysOnTop(true);
    }

    float time = 0;

    public void draw() {
        time += radians(slider("time", 1));
        pg = updateGraphics(pg);
        pg.beginDraw();
        pg.clear();

        group("static");
        String shader = "shaders/_2021_11/tvStatic.glsl";

        PVector offset = sliderXY("offset", 200)
                .add(sliderXY("offset speed"));
        uniform(shader).set("time", time);
        uniform(shader).set("blackPoint", slider("black point", 0f));
        uniform(shader).set("whitePoint", slider("white point", 1.8f));
        uniform(shader).set("pixelate", (float) sliderInt("pixelate", 150));
        PVector freq = sliderXY("freq", 0.05f, 1.0f);
        uniform(shader).set("freq", freq.x, freq.y);
        uniform(shader).set("offset", offset.x, offset.y);

        hotFilter(shader, pg);
        resetGroup();

        chromaticAberrationBlurDirPass(pg);
        pg.endDraw();
        image(pg, 0, 0);
        gui();
        glowCursor();
        rec(g, sliderInt("frames", 360));
    }
}

