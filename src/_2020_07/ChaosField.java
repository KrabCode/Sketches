package _2020_07;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import utils.OpenSimplexNoise;

import java.util.ArrayList;

// Made in collaboration between Krabcode and Kggsa
// inspired by Allyson Grey, see https://www.allysongrey.com/art/watercolors/chaos-field

/*
* Kggsa's specification:
*
* In Allyson Grey's chaos, all of the patterning is the same 10 by 10 grid of squares.
* They are layered top to bottom with a few huge squares at the bottom, more smaller squares as you go up the layers
* and with the most of the tiny squares at the top, I count 12 different sizes of squares, which means at least 12 layers,
*  and it seems to be pretty much an invere expontial-ish relationship between size and the number of grids at that size,
*  allthough of course it seems she continued until the entire plane was filled with no blank spots regardless of the pattern.
*
* Also, the grids are all somewhere between their original packing, and fully exploded, and the explosions are random,
* some have sections cut up in only one direction, some have most of the tiles spaced apart with few clumped together,
* and some are entirely scattered across their small regions, idk how you would be able to implement different levels of randomness with the explosions though.
*  And within each grid, the individual squares are randomly rotated and it seems the smaller the squares are,
*  the more randomly rotated they are as the largest squares barely have any rotation (within their grids)
*  whereas the smallest ones swing between +20° and -20°
*
* Lastly, it would be great if you could customize the 19 color palette,
*  with squares, triangles, and hexagons, any 10 by 10 (by 10) arrangement leads to a total of 19 colors.
*  And with the circles I'm thinking it would be even better to just have a second option with any of the square, triangle, or hexagon layouts
*  to replace the individual tiles with circles because the structure of the grids is just as important to the look and feel of the image
*  as the tile shape themselves and I think circles on a square layout would look as cool as circles on a triangle layout and on a hexagon layout.
* ---------
*
* Krab's notes:
*
* Independent layout and tile logic & sliders
*
* draw n overlapping grids of 'hand drawn' squares at random positions and scales
* gradient spans each grid diagonally, coloring each square in the appropriate sub-gradient
*
* */
public class ChaosField extends KrabApplet {
    private PGraphics pg;
    private OpenSimplexNoise noise = new OpenSimplexNoise();
    ArrayList<Float> absGaussians = new ArrayList<Float>();

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
        pg = updateGraphics(pg, P2D);
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
        int gridCount = sliderInt("grid count", 10);
        for (int i = 0; i < gridCount; i++) {
            PVector pos = new PVector(-width/2f + width*hash(7.214f+i*310.124f), -height/2f + height*hash(12.114f+i*143.987f));
            pg.pushMatrix();
            pg.translate(pos.x, pos.y);
            pg.rotate(slider("global rotate", 2)*hash(i*32.1215f));
            float gauss =  getGaussian(i);
            float scale = slider("scale const", 0.1f) + slider("scale gauss",0.9f) * gauss;
            pg.scale(scale);
            int tileCount = sliderInt("tile count", 10);
            for (int yi = -2; yi <= tileCount; yi++) {
                float yNorm = norm(yi, 0, tileCount);
                float baseY = -height/2f + yNorm * height;
                float h = height / (float) tileCount;
                for (int xi = -2; xi <= tileCount; xi++) {
                    float xNorm = norm(xi, 0, tileCount);
                    float baseX = -width / 2f + xNorm * width;

                    PVector fromCenter = PVector.sub(new PVector(baseX,baseY), new PVector()).normalize();
                    fromCenter.mult(slider("explode from center", 1));
//                    pg.translate(fromCenter.x, fromCenter.y);

                    float freq = slider("angle freq");
                    float noiseAngle = slider("angle range") *
                            ((float) noise.eval(baseX * freq, baseY * freq, 0, 0));
                    float offsetFreq = slider("offset freq");
                    float offsetMag = slider("offset noise strength") *
                            ((float) noise.eval(baseX * offsetFreq, baseY * offsetFreq,0, 0));
                    PVector noiseOffset = PVector.fromAngle(noiseAngle).mult(offsetMag + slider("offset const", 0.1f) + slider("offset gauss",0.9f) * gauss);
                    float w = width / (float) tileCount;
                    float x = baseX + noiseOffset.x;
                    float y = baseY + noiseOffset.y;
                    pg.fill(gradientColorAt("fill", norm(baseX + baseY, -width, width)));
                    pg.stroke(picker("stroke").clr());
                    pg.rectMode(CORNER);
                    pg.rect(x, y, w, h);
                }
            }
            pg.popMatrix();
        }
    }

    private float getGaussian(int i) {
        while(i >= absGaussians.size()-1) {
            absGaussians.add(abs(randomGaussian()));
        }
        return absGaussians.get(i);
    }
}
