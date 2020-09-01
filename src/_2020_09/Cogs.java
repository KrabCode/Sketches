package _2020_09;

import applet.KrabApplet;
import processing.core.PGraphics;

public class Cogs extends KrabApplet {
    private PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        fullScreen(P3D);
    }

    public void setup() {
        // TODO circle pack, make cogs fit?
    }

    public void draw() {
        pg = updateGraphics(pg);
        pg.beginDraw();
        pg.image(gradient("background"), 0, 0);

        pg.noStroke();
        pg.fill(picker("fill").clr());
        translateToCenter(pg);
        pg.rotate(t);
        float radius = slider("radius", 250);
        pg.ellipse(0, 0, radius*2, radius*2);
        pg.stroke(picker("stroke").clr());
        pg.strokeWeight(slider("weight", 3));
        int lineCount = sliderInt("line cogs", 20);
        float lineLength = slider("line length", 10);
        float innerRadius = radius - lineLength;
        float outerRadius = radius + lineLength;
        for (int lineIndex = 0; lineIndex < lineCount; lineIndex++) {
            float angle = map(lineIndex, 0, lineCount, 0, TAU);
            float x0 = innerRadius * cos(angle);
            float x1 = outerRadius * cos(angle);
            float y0 = innerRadius * sin(angle);
            float y1 = outerRadius * sin(angle);
            pg.line(x0, y0, x1, y1);
        }

        pg.endDraw();
        image(pg, 0, 0);
        rec(pg, sliderInt("frames", 360));
        gui();
    }


}
