package _2020_06;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import utils.OpenSimplexNoise;

import java.util.ArrayList;

public class SpringManager extends KrabApplet {
    private PGraphics pg;

    ArrayList<Spring> springs = new ArrayList<>();
    
    private OpenSimplexNoise noise = new OpenSimplexNoise();

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
        blurPass(pg);
        chromaticAberrationPass(pg);
        fadeToBlack(pg);
        translateToCenter(pg);
        translate2D(pg);
        updateSprings();
        pg.endDraw();
        colorSplit(pg, true);
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    private void updateSprings() {
        int springCount = sliderInt("spring count", 10, 0, Integer.MAX_VALUE);
        while(springCount > springs.size()){
            Spring s = new Spring();
            s.pos = new PVector(randomGaussian(), randomGaussian()).mult(slider("gauss"));
            springs.add(s);
        }
        while(springCount < springs.size()) {
            springs.remove(springs.size()-1);
        }
        for(Spring s : springs) {
            s.update();
        }
    }


    private float noise(float i, float freq, float mag, PVector time) {
        return (float) (mag*noise.eval(i*freq, time.x, time.y));
    }
    
    class Spring {
        PVector pos = new PVector();
        ArrayList<PVector> vectors = new ArrayList<>();

        void update() {
            int count = sliderInt("count", 10, 0, Integer.MAX_VALUE);
            float x;
            float y;
            PVector step = sliderXY("step");
            for (int i = 0; i < count; i++) {
                x = i * step.x+pos.x;
                y = i * step.y+pos.y;
                if (vectors.size() <= i) {
                    vectors.add(new PVector(x, y));
                }
            }
            while(vectors.size() > count) {
                vectors.remove(vectors.size()-1);
            }
            pg.beginShape();
            pg.strokeWeight(slider("weight", 4));
            pg.stroke(picker("stroke").clr());
            pg.noFill();
            PVector freq = sliderXY("freq");
            PVector angleVar = sliderXY("angle var");
            PVector mag = sliderXY("mag");
            PVector time = PVector.fromAngle(t).mult(slider("time radius"));
            for (int i = 0; i < vectors.size(); i++) {
                PVector curr = vectors.get(i);
                PVector prev = new PVector();
                if(i == 0) {
                    curr = pos.copy();
                    prev = pos.copy();
                }
                if (i > 0) {
                    prev = vectors.get(i - 1);
                }
                if(PVector.dist(curr, prev) > min(step.x, step.y)) {
                    curr.x = lerp(curr.x, prev.x, slider("lerp"));
                    curr.y = lerp(curr.y, prev.y, slider("lerp"));
                }
                curr.add(sliderXY("force"));
                curr.add(PVector.fromAngle(noise(pos.x+pos.y+i, freq.x, angleVar.x, time)).mult(mag.x));
                curr.add(PVector.fromAngle(noise(pos.x+pos.y+i, freq.y, angleVar.y, time)).mult(mag.y));
                pg.vertex(curr.x, curr.y);
            }
            pg.endShape();
        }
    }
}
