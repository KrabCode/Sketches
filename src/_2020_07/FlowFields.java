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
        pg = updateGraphics(pg);
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
        private PVector center = new PVector(width/2f, height/2f);

        P() {
            pos = randomOffscreenPoint();
            spd = new PVector();
        }

        private PVector randomOffscreenPoint() {
            PVector p = new PVector(20, 20);
            float buffer = slider("spawn buffer", 500);
            while(isPointInRect(p.x, p.y, 0,0,width,height)) {
                p.x = random(-buffer, width+buffer);
                p.y = random(-buffer, height+buffer);
            }
            return p;
        }

        public void update() {
            float angle = noise(pos.x, pos.y, slider("freq", .1f), slider("range", TAU));
            spd.add(PVector.fromAngle(angle+slider("angle offset")).mult(slider("amp", .5f)));
            PVector toCenter = PVector.sub(center, pos);
            spd.add(toCenter.normalize().mult(slider("to center")));
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
