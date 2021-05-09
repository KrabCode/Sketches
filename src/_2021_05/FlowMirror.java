package _2021_05;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PVector;

import java.util.ArrayList;

public class FlowMirror extends KrabApplet {
    private PGraphics pg;
    ArrayList<P> ps = new ArrayList<P>();
    ArrayList<P> bin = new ArrayList<P>();
    int count = 500;
    float r = 5;
    float timeSpeed = 0.1f;
    float freq = 0.01f;
    float amp = 3;
    float t;
    float m = 50;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        fullScreen(P3D);
    }

    public void setup() {
//        fullScreen(P2D);
        toggleFullscreen();
        pg = updateGraphics(pg);
        pg.beginDraw();
        pg.colorMode(HSB, 1, 1, 1, 1);
        pg.background(0);
        pg.endDraw();
        for(int i = 0; i < count; i++){
            P p = new P();
            p.pos.x = random(width);
            p.pos.y = random(height+m);
            ps.add(p);
        }
    }

    public void draw() {
        freq = slider("freq", freq);
        amp = slider("amp", amp);
        r = slider("r", r);
        count = sliderInt("count", count);
        timeSpeed = slider("time speed", timeSpeed);
        gradientColorAt("clr", 0);
        t = radians(frameCount)*timeSpeed;
        pg = updateGraphics(pg);
        pg.beginDraw();
        fade();
        drawParticles();
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg, sliderInt("frames", 360));
        gui();
    }

    void fade() {
        if (frameCount % 2 != 0) {
            return;
        }
        pg.blendMode(SUBTRACT);
        pg.noStroke();
        pg.fill(1, 0.025f);
        pg.rect(0, 0, width, height);
        pg.blendMode(BLEND);
    }

    void drawParticles() {
        if (ps.size() < count && 0 == (frameCount % sliderInt("add skip", 3))) {
            ps.add(new P());
        }
        for (P p : ps) {
            p.draw();
        }
        ps.removeAll(bin);
        bin.clear();
    }

    class P {
        PVector pos = new PVector(random(-m*2, width+m*2), random(height, height+m));
        int clr = gradientColorAt("clr", random(1));

        void draw() {
            pos.add(noiseMove());
            boundsCheck();
            pg.noStroke();
            pg.fill(clr);
            pg.ellipse(pos.x, pos.y,r,r);
            pg.ellipse(width-pos.x, pos.y, r, r);
        }

        void boundsCheck() {
            if (pos.x > width+m*2 ||
                    pos.y > height+m ||
                    pos.x < -m*2 ||
                    pos.y < -m) {
                bin.add(this);
            }
        }

        PVector noiseMove() {
            float x = pos.x*freq;
            float y = pos.y*freq;
            float theta = (-1+2*noise(x, y, t))*HALF_PI - HALF_PI;
            return PVector.fromAngle(theta).mult(amp);
        }
    }
}
