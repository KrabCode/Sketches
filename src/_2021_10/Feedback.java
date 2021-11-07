package _2021_10;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class Feedback extends KrabApplet {
    private PGraphics pg;

    public static void main(String[] args) {
        main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        fullScreen(P3D);
    }

    public void setup() {
        toggleFullscreen();
        surface.setAlwaysOnTop(true);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public void draw() {
        pg = updateGraphics(pg);
        pg.beginDraw();
        pg.blendMode(ADD);
        fadeToBlack(pg);
        translateToCenter(pg);
        pg.fill(picker("fill", 1, 0).clr());
        pg.stroke(picker("stroke", 1).clr());
        pg.strokeWeight(slider("weight", 1.99f));
        pg.strokeCap(ROUND);
        pg.beginShape();
        float radius = slider("radius", 30);
        int edgeCount = sliderInt("edge count", 8);
        for (int index = 0; index < edgeCount; index++) {
            float i = norm(index, 0, edgeCount);
            float angle = i * TAU;
            pg.vertex(radius*cos(angle), radius*sin(angle));
        }
        pg.endShape(CLOSE);
        fbmDisplacePass(pg);
        chromaticAberrationBlurDirPass(pg);
        pg.imageMode(CENTER);
        pg.rotate(slider("rotate"));
        PVector scale = sliderXY("scale", 1);
        if(toggle("lock scale")){
            scale.y = scale.x;
        }
        pg.scale(scale.x, scale.y);
        pg.image(pg, 0, 0);
        pg.endDraw();
        image(pg, 0, 0);
        gui();
        glowCursor();
        group("record");
        rec(g, sliderInt("frames", 360));
        resetGroup();
    }
}

