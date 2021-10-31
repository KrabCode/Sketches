package _2021_10;

import applet.KrabApplet;
import applet.PGraphics32;
import processing.core.PGraphics;

public class TextureTest extends KrabApplet {
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
        pg = PGraphics32.createGraphics(this, width, height);
    }

    public void draw() {
        pg.beginDraw();
        pg.image(gradient("background"), 0, 0);
        pg.endDraw();
        fbmDisplacePass(pg);
        chromaticAberrationBlurDirPass(pg);
        image(pg, 0, 0);
        gui();
        glowCursor();
        rec(g, sliderInt("frames", 360));
    }
}
