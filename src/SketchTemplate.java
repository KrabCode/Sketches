

import applet.KrabApplet;
import processing.core.PGraphics;

public class SketchTemplate extends KrabApplet {
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
        pg.image(gradient("background"), 0, 0);
        pg.endDraw();
        chromaticAberrationBlurPass(pg);
        image(pg, 0, 0);
        gui();
        glowCursor();
        rec(g, sliderInt("frames", 360));
    }
}

