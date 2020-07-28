package _2020_07;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import utils.OpenSimplexNoise;

// inspired by Allyson Grey, see https://www.allysongrey.com/art/watercolors/chaos-field
public class ChaosField extends KrabApplet {
    private PGraphics pg;
    private OpenSimplexNoise noise = new OpenSimplexNoise();

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(1000, 1000, P2D);
    }

    public void setup() {
        if (width < displayWidth) {
            surface.setAlwaysOnTop(true);
        }
    }

    public void draw() {
        pg = updateGraphics(pg);
        pg.beginDraw();
        translateToCenter(pg);
        pg.colorMode(HSB, 1, 1, 1, 1);
        pg.background(slider("bg"));
        updateGrids(pg);
        pg.endDraw();
        image(pg, 0, 0, width, height);
        rec(pg);
        gui();
    }

    void updateGrids(PGraphics pg) {
        // draw n overlapping grids of 'hand drawn' squares at random positions and scales
        // a gradient spans the grid diagonally, coloring each square in the appropriate sub-gradient
        int count = sliderInt("count", 12);
        for (int yi = -2; yi <= count; yi++) {
            float yNorm = norm(yi, 0, count);
            float baseY = -height/2f + yNorm * height;
            float h = height / (float) count;
            for (int xi = -2; xi <= count; xi++) {
                float xNorm = norm(xi, 0, count);
                float baseX = -width / 2f + xNorm * width;
                float freq = slider("angle freq");
                float noiseAngle = slider("angle range") * ((float) noise.eval(baseX * freq, baseY * freq, 0, 0));
                float offsetFreq = slider("offset freq");
                float offsetMag = slider("offset mag") * ((float) noise.eval(baseX * offsetFreq, baseY * offsetFreq,
                        0, 0));
                PVector noiseOffset = PVector.fromAngle(noiseAngle).mult(offsetMag);
                float x = baseX + noiseOffset.x;
                float y = baseY + noiseOffset.y;
                float w = width / (float) count;
                pg.noStroke();
                pg.beginShape();
                pg.fill(gradientColorAt("foreground", norm(baseX + baseY, -width, width)));
                pg.vertex(x,y);
                pg.fill(gradientColorAt("foreground", norm(baseX + w + baseY, -width, width)));
                pg.vertex( x + w, y);
                pg.fill(gradientColorAt("foreground", norm(baseX + w + baseY + h, -width, width)));
                pg.vertex(x + w, y + h);
                pg.fill(gradientColorAt("foreground", norm(baseX + baseY + h, -width, width)));
                pg.vertex(x, y + h);
                pg.endShape(CLOSE);
            }
        }
    }

    void vertexFilled(PGraphics pg, float x, float y) {

    }
}
