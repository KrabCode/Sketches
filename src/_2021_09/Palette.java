package _2021_09;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class Palette extends KrabApplet {
    // https://iquilezles.org/www/articles/palettes/palettes.htm

    PVector a = new PVector(0.5f,   0.5f, 0.5f);
    PVector b = new PVector(0.5f,   0.5f, 0.5f);
    PVector c = new PVector(1.0f,   1.0f, 1.0f);
    PVector d = new PVector(0.00f, 0.10f, 0.20f);

    PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings(){
        fullScreen(P3D);
    }

    public void setup() {
        toggleFullscreen();
        surface.setAlwaysOnTop(true);
    }

    public void draw() {
        pg = updateGraphics(pg);
        a = sliderXYZ("a", a.x, a.y, a.z);
        b = sliderXYZ("b", b.x, b.y, b.z);
        c = sliderXYZ("c", c.x, c.y, c.z);
        d = sliderXYZ("d", d.x, d.y, d.z);
        pg.loadPixels();
        for (int y = 0; y < height; y++) {
            float yNorm = norm(y, 0, height);
            int rowColor = palette(yNorm, a, b, c, d);;
            for(int x = 0; x < width; x++){
                int i = x + y * width;
                pg.pixels[i] = rowColor;
            }
        }
        pg.updatePixels();
        clear();
        image(pg, 0, 0);
        gui();
    }

    int palette(float t, PVector a, PVector b, PVector c, PVector d ) {
        PVector rgb = PVector.add(a, mult(b, cos(PVector.mult(PVector.add(d, PVector.mult(c, t)), TAU))));
        colorMode(RGB, 1, 1, 1, 1);
        return color(rgb.x, rgb.y, rgb.z);
    }

    PVector mult(PVector a, PVector b) {
        return new PVector(a.x * b.x, a.y * b.y, a.z * b.z);
    }

    PVector cos(PVector input) {
        return new PVector(cos(input.x), cos(input.y), cos(input.z));
    }

}
