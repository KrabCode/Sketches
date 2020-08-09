package _2020_08;

import applet.KrabApplet;
import com.logitech.gaming.LogiLED;
import processing.core.PGraphics;

public class KeyboardRainbow extends KrabApplet {
    private PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(21 * 100, 6 * 100, P2D);
//        fullScreen(P2D);
    }

    public void setup() {
        LogiLED.LogiLedInit();
        LogiLED.LogiLedSetTargetDevice(4);
        colorMode(HSB, 1, 1, 1, 1);
    }

    public void keyPressed() {
        super.keyPressed();
        LogiLED.LogiLedSetLightingForKeyWithKeyName(parseByte(key), 100, 100, 100);
    }

    public void draw() {
        pg = updateGraphics(pg);
        pg.colorMode(HSB, 1, 1, 1, 1);
        pg.beginDraw();
        HSBA global = picker("color");
        int clr = global.clr();
        background(clr);
        pg.endDraw();
        image(pg, 0, 0, width, height);
        LogiLED.LogiLedSetLighting(floor(red(clr)*100), floor(green(clr)*100), floor(blue(clr)*100));
        gui();
    }
}
