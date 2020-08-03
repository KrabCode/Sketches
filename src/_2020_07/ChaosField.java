package _2020_07;

import applet.KrabApplet;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import utils.OpenSimplexNoise;

import java.util.ArrayList;
import java.util.Comparator;

// Made in collaboration between Krabcode and Kggsa
// inspired by Allyson Grey, see https://www.allysongrey.com/art/watercolors/chaos-field

/*
 * Kggsa's specification:
 *
 * In Allyson Grey's chaos, all of the patterning is the same 10 by 10 grid of squares.
 * They are layered top to bottom with a few huge squares at the bottom, more smaller squares as you go up the layers
 * and with the most of the tiny squares at the top, I count 12 different sizes of squares, which means at least 12
 * layers,
 *  and it seems to be pretty much an invere expontial-ish relationship between size and the number of grids at that
 * size,
 *  allthough of course it seems she continued until the entire plane was filled with no blank spots regardless of
 * the pattern.
 *
 * Also, the grids are all somewhere between their original packing, and fully exploded, and the explosions are random,
 * some have sections cut up in only one direction, some have most of the tiles spaced apart with few clumped together,
 * and some are entirely scattered across their small regions, idk how you would be able to implement different
 * levels of randomness with the explosions though.
 *  And within each grid, the individual squares are randomly rotated and it seems the smaller the squares are,
 *  the more randomly rotated they are as the largest squares barely have any rotation (within their grids)
 *  whereas the smallest ones swing between +20° and -20°
 *
 * Lastly, it would be great if you could customize the 19 color palette,
 *  with squares, triangles, and hexagons, any 10 by 10 (by 10) arrangement leads to a total of 19 colors.
 *  And with the circles I'm thinking it would be even better to just have a second option with any of the square,
 * triangle, or hexagon layouts
 *  to replace the individual tiles with circles because the structure of the grids is just as important to the look
 * and feel of the image
 *  as the tile shape themselves and I think circles on a square layout would look as cool as circles on a triangle
 * layout and on a hexagon layout.
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
    ArrayList<Float> absGaussians = new ArrayList<Float>();
    private PGraphics pg;
    private OpenSimplexNoise noise = new OpenSimplexNoise();
    private PVector zero = new PVector();

    public static void main(String[] args) {
//        KrabApplet.main("_2020_07.ChaosField");
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
//        size(1000, 1000, P2D);
        fullScreen(P2D);
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
        pg.background(picker("bg").clr());
        updateGrids(pg);
        pg.endDraw();
        image(pg, 0, 0, width, height);
        rec(pg);
        gui();
    }

    void updateGrids(PGraphics pg) {
        int gridCount = sliderInt("grid count", 10);
        if(button("reset gaussians")) {
            absGaussians.clear();
        }
        for (int i = 0; i < gridCount; i++) {
            group("grids");
            PVector pos = new PVector(-width / 2f + width * hash(7.214f + i * 310.124f),
                    -height / 2f + height * hash(12.114f + i * 143.987f));
            PVector time = new PVector(cos(t), sin(t)).mult(slider("noise time", 1));
            pg.pushMatrix();
            pg.translate(pos.x, pos.y);
            pg.rotate(slider("global rotate", 2) * hash(i * 32.1215f));
            float gauss = getGaussian(i);
            float scale = slider("scale const", 0.1f) + slider("scale gauss", 0.9f) * gauss;
            pg.scale(scale);
            int tileCount = sliderInt("tile count", 10);
            for (int yi = 0; yi < tileCount; yi++) {
                float yNorm = norm(yi, 0, tileCount-1);
                float baseY = (-min(height, width) / 2f) + yNorm * min(height, width);
                float h = min(width, height) / (float) tileCount;
                for (int xi = 0; xi < tileCount; xi++) {
                    float xNorm = norm(xi, 0, tileCount-1);
                    float baseX = (-width / 2f) + xNorm * min(height, width);

                    group("tiles");

                    float noiseFreq = slider("noise freq", 0.001f);
                    float noiseX = (slider("scale amp mult", 100)*(1.f-scale) + slider("noise amp", 10)) *
                            (.5f+.5f*(float)noise.eval( i*112.212f+baseX*noiseFreq, i*220.345f+baseY*noiseFreq,time.x,time.y));
                    float noiseY = (slider("scale amp mult", 100)*(1.f-scale) + slider("noise amp", 10)) *
                            (.5f+.5f*(float)noise.eval( i*212.212f+baseX*noiseFreq, i*180.345f+baseY*noiseFreq,time.x,time.y));

                    PVector fromCenter = PVector.sub(new PVector(baseX, baseY), zero).normalize().mult(slider(
                            "from center"));
                    float localRotation = radians(noiseX+noiseY)*slider("noise rotation mult");
                    pg.pushMatrix();
                    resetGroup();
                    float w = min(width,height) / (float) tileCount;
                    float x = baseX + fromCenter.x + noiseX;
                    float y = baseY + fromCenter.y + noiseY;
                    pg.fill(gradientColorAt("fill", norm(baseX + baseY, -min(height, width), min(height, width))));
                    pg.stroke(picker("stroke").clr());
                    pg.strokeWeight(slider("weight", 1));
                    if(toggle("no stroke")){
                        pg.noStroke();
                    }
                    pg.rectMode(CENTER);
                    pg.translate(x, y);
                    pg.rotate(localRotation);
                    pg.rect(0,0, w, h);
                    pg.popMatrix();
                }
            }
            pg.popMatrix();
        }
    }

    private float getGaussian(int i) {
        while (i >= absGaussians.size() - 1) {
            absGaussians.add(abs(randomGaussian()));
        }
        absGaussians.sort(new Comparator<Float>() {
            public int compare(Float o1, Float o2) {
                if(abs(o1-o2) < 0.01f){
                    return 0;
                }
                return (o1) > (o2) ? -1 : 1;
            }
        });
        return absGaussians.get(i);
    }
}
