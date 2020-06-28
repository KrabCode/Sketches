package _2020_06;

import applet.KrabApplet;
import processing.core.PGraphics;

import java.util.ArrayList;

public class FlowField extends KrabApplet {
    private PGraphics pg;
    private ArrayList<Particle> particles = new ArrayList<>();

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
        pg.image(gradient("background"), 0, 0);
        updateParticles();
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    private void updateParticles() {

    }

    // particles in flow field + blend mode add + feedback flow

    class Particle {

    }
}
