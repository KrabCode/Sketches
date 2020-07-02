package _2020_06;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Collection;

public class Bubbles extends KrabApplet {
    private PGraphics pg;
    private ArrayList<Bubble> bubbles = new ArrayList<>();
    private ArrayList<Bubble> bubbleToRemove = new ArrayList<>();
    PVector center;
    String bubbleShader = "shaders/_2020_06/Bubbles/bubbles.glsl";;

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
        center = new PVector(width, height).mult(.5f);
    }

    public void draw() {
        frameRecordingDuration = sliderInt("rec frames", 360);
        pg.beginDraw();
        fadeToBlack(pg);
        updateBubbles();
        chromaticAberrationPass(pg);
        fbmDisplacePass(pg);
        pg.endDraw();
        rgbSplitScaleAndOffset(pg);
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    private void updateBubbles() {
        group("bubbles");
        if (frameCount % sliderInt("spawn %", 2, 1, 1000) == 0) {
            for (int i = 0; i < sliderInt("spawn count", 5); i++) {
                bubbles.add(new Bubble());
            }
        }

        uniform(bubbleShader).set("gradient", gradient("spawn gradient"));
        for (Bubble b : bubbles) {
            b.update();
        }
        bubbles.removeAll(bubbleToRemove);
        bubbleToRemove.clear();
        pg.resetShader();
        resetGroup();
    }

    class Bubble {
        PVector pos, spd = new PVector();
        int frameBorn = frameCount;
        float randomSize = random(-1, 1);

        Bubble() {
            pos = new PVector(random(width), random(height));
        }

        void update() {
            float lifespan = slider("lifespan", 200);
            float lifeNorm = clampNorm(frameCount, frameBorn, frameBorn + lifespan);
            float fadeInDuration = slider("fade in", 0.2f);
            float fadeOutDuration = slider("fade out", 0.2f);

            PVector toCenter = PVector.sub(center, pos).normalize();
            spd.add(toCenter.copy().mult(slider("force to center")));
            spd.add(toCenter.copy().rotate(HALF_PI).mult(slider("force sideways")));
            spd.add(sliderXY("force constant"));
            spd.mult(slider("drag", .95f));
            pos.add(spd);

            float alpha = 1;
            if (lifeNorm < fadeInDuration) {
                alpha = clampNorm(lifeNorm, 0, fadeInDuration);
            }
            if (lifeNorm > 1 - fadeOutDuration) {
                alpha = 1 - clampNorm(lifeNorm, 1 - fadeOutDuration, 1);
            }
            float size = slider("size", 2) + randomSize * slider("random size");

            pg.pushStyle();
            if(toggle("ADD")) {
                 pg.blendMode(ADD);
            }
            uniform(bubbleShader).set("alpha", alpha);
            hotShader(bubbleShader, pg);
            pg.tint(1, alpha);
            pg.fill(1);
            pg.noStroke();
            pg.ellipse(pos.x, pos.y, size, size);
            if (lifeNorm >= 1) {
                bubbleToRemove.add(this);
            }
            pg.popStyle();
        }
    }
}
