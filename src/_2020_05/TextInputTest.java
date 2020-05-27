package _2020_05;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class TextInputTest extends KrabApplet {
    private PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(1000, 1000, P3D);
    }

    public void setup() {
        pg = createGraphics(width, height, P3D);
        if (width < displayWidth) {
            surface.setAlwaysOnTop(true);
            surface.setLocation(2560 - 1020, 20);
        }
    }

    public void draw() {
        pg.beginDraw();
        ramp(pg);
        fadeToBlack(pg);
        translateToCenter(pg);
        pg.textAlign(CENTER, CENTER);
        pg.textSize(slider("text size", 64));
        String text = textInput("text input", "Hello world!");
        PVector shadowOffset = sliderXY("shadow offset");
        pg.fill(0);
        pg.text(text, shadowOffset.x, shadowOffset.y);
        pg.fill(255);
        pg.text(text, 0, 0);
        pg.endDraw();
        colorSplit(pg, true);
        image(pg, 0, 0);
        rec(pg);
        gui();
    }
}
