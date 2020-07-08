package _2020_07;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import utils.OpenSimplexNoise;

import java.util.ArrayList;

public class FlowFields extends KrabApplet {
    private final ArrayList<P> particles = new ArrayList<>();
    private final OpenSimplexNoise noise = new OpenSimplexNoise();
    private PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
//        size(1000, 1000, P3D);
        fullScreen(P3D);
    }

    public void setup() {

    }

    public void draw() {
        pg = preparePGraphics(pg);
        pg.beginDraw();
        if (frameCount < 3 || button("redraw bg")) {
            pg.background(picker("bg").clr());
        }
        updateParticles();
        pg.endDraw();
        imageMode(CENTER);
        image(pg, width / 2f, height / 2f);
        rec(g);
        gui();
    }

    private void updateParticles() {
        group("particles");
        int count = sliderInt("count", 100);
        if (button("reset")) {
            particles.clear();
        }
        while (particles.size() < count) {
            particles.add(new P());
        }
        for (P p : particles) {
            p.update();
        }
    }

    class P {
        PVector pos, spd;

        P() {
            float x = random(-width/2f, width+width/2f);
            float y = random(-height, -height/2f);
            pos = new PVector(x, y);            //TODO random point off screen
            spd = new PVector();
        }

        public void update() {
            float angle = noise(pos.x, pos.y, slider("freq", .1f), slider("range", TAU));
            spd.add(PVector.fromAngle(angle+slider("angle offset")).mult(slider("amp", .5f)));
            spd.mult(slider("drag", .95f));
            PVector prevPos = pos.copy();
            pos.add(spd);

            pg.strokeWeight(slider("weight", 2));
            pg.stroke(picker("stroke").clr());
            pg.line(prevPos.x, prevPos.y, pos.x, pos.y);
        }

        float noise(float x, float y, float freq, float range) {
            return range * (float) noise.eval(x * freq, y * freq);
        }
    }
}
