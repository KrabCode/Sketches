package _2020_05;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class Gradient extends KrabApplet {
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
        pg.colorMode(HSB, 1, 1, 1, 1);
        fadeToBlack(pg);
        drawText();
        pg.endDraw();
        rgbSplitScaleAndOffset(pg);
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    private void drawText() {
        translateToCenter(pg);
        translate2D(pg);
        if(options("align left", "align center").equals("align left")) {
            pg.textAlign(LEFT, CENTER);
        }else {
            pg.textAlign(CENTER, CENTER);
        }
        String text = textInput("text input", "Hello world!");
        PVector shadowOffset = sliderXY("shadow offset");
        HSBA shadowColor = picker("shadow");
        shadowColor.addHue(slider("hue speed"));
        pg.fill(shadowColor.hue(), shadowColor.sat(), shadowColor.br(), shadowColor.alpha());
        pg.textSize(slider("shadow size", 64));
        pg.text(text, shadowOffset.x, shadowOffset.y);
        blurPass(pg);
        pg.textSize(slider("text size", 64));
        pg.fill(picker("fill").clr());
        pg.text(text, 0, 0);
    }
}
