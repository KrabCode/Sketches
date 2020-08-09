package _2020_08;

import applet.KrabApplet;
import com.logitech.gaming.LogiLED;
import processing.core.PGraphics;
import processing.core.PVector;

public class KeyboardRainbow extends KrabApplet {
    private PGraphics pg;
    byte[] bitmap = new byte[LogiLED.LOGI_LED_BITMAP_SIZE];
    int cols = 21;
    int rows = 6;
    float time;
    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(21 * 100, 6 * 100, P2D);
        noSmooth();
//        fullScreen(P2D);
    }

    public void setup() {
        LogiLED.LogiLedInit();
        LogiLED.LogiLedSetTargetDevice(4);
        colorMode(RGB, 255, 255, 255, 255);
    }

    public void draw() {
        pg = updateGraphics(pg);
        pg.beginDraw();
        pg.noSmooth();
        time += radians(1) * slider("time");

        PVector dir = sliderXY("dir", 1);
        for (int xi = 0; xi < cols; xi++) {
            for (int yi = 0; yi < rows; yi++) {
                int i = (xi + yi * cols) * 4;
                float x = norm(xi, 0, cols-1);
                float y = norm(yi, 0, rows-1);
                int gradColor = gradientColorAt("gradient", (x * dir.x + y * dir.y + time / TAU) % 1);
                bitmap[i] = (byte) blue(gradColor);
                bitmap[i + 1] = (byte) green(gradColor);
                bitmap[i + 2] = (byte) red(gradColor);
                bitmap[i + 3] = (byte) alpha(gradColor);

                pg.fill(gradColor);
                pg.noStroke();
                pg.rect(map(xi, 0, cols-1, 0, pg.width), map(yi, 0, rows-1,  0, pg.height), 200,200);
            }
        }

        LogiLED.LogiLedSetLightingFromBitmap(bitmap);
        pg.endDraw();
        image(pg, 0, 0, width, height);
        gui();
    }


}
