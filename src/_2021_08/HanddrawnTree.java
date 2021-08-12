package _2021_08;

import applet.KrabApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

import java.util.ArrayList;

public class HanddrawnTree extends KrabApplet {
    float length, gauss;
    private PImage bg;
    private Branch root;
    private PGraphics pg;
    private int childCount = 2;
    private int maxGen = 4;
    private float childSpread = PI * 0.25f;
    private float t = 0;
    private float genScale = 1;
    private float firstBranchOffset;
    private boolean stretchVertically;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        fullScreen(P2D);
    }

    public void setup() {
        bg = loadImage("images/tree/tree_2.jpg");
        root = new Branch(0, 0);
        toggleFullscreen(1000, 1000);
    }

    public void draw() {
        t = radians(frameCount) * 0.2f;
        length = slider("length", 100);
        gauss = slider("gaussian", 20);
        genScale = slider("gen scale", 1);
        stretchVertically = toggle("stretch", true);
        firstBranchOffset = slider("first branch offset");
        int currentChildCount = sliderInt("child count", 2);
        int currentMaxGen = sliderInt("max gen", 4);
        float currentChildSpread = slider("child spread", PI * 0.25f);
        if(currentChildCount != childCount || currentChildSpread != childSpread || currentMaxGen != maxGen){
            childCount = currentChildCount;
            childSpread = currentChildSpread;
            maxGen = currentMaxGen;
            root = new Branch(0, 0);
        }

        pg = updateGraphics(pg);
        pg.beginDraw();
        pg.background(0);
        pg.imageMode(CORNER);
        pg.image(bg, 0, 0, width, height);
        translateToCenter(pg);
        translate2D(pg, "center");
        pg.stroke(255);
        root.update();
        pg.endDraw();
        image(pg, 0, 0, width, height);
        rec(pg);
        gui();
    }

    class Branch {
        int gen;
        float genNorm, rot, randomGaussian, randomGaussian2;
        ArrayList<Branch> children = new ArrayList<Branch>();

        Branch(int gen, float rot) {
            this.gen = gen;
            this.rot = rot;
            genNorm = 1 - norm(gen, 0, maxGen-1);
            randomGaussian = randomGaussian();
            randomGaussian2 = randomGaussian();
        }

        void update() {
            if (gen < maxGen && children.isEmpty()) {
                grow();
            }
            pg.pushMatrix();
            float freq = slider("freq", 0.01f);
            float x = screenX(0, 0) * freq;
            float y = screenY(0, 0) * freq;
            pg.rotate(rot + randomGaussian2 * slider("rot offset")
                    + (1-genNorm) * (slider("wind rot") * (-1 + 2 * noise(t + x + y))));
            pg.strokeWeight(1+genNorm * 10);

            float nextY = -length + gauss * randomGaussian;
            nextY *= genNorm * genScale;
            int imageIndex = min(gen, 3);
            group("gen " + imageIndex);
            if (toggle("debug", false)) {
                pg.line(0, 0, 0, nextY);
            } else {
                String chromaKeyShader = "shaders/_2021_08/chromaKey.glsl";
                uniform(chromaKeyShader).set("black", toggle("black"));
                PGraphics img = getRectangleAsShadedCanvas(pg, chromaKeyShader, imageIndex, sliderXY("pos", width / 2f, height / 2f), sliderXY("size", 200));
                lineImage(img, 0, 0, 0, nextY);
            }
            pg.translate(0, nextY);
            for (int i = 0; i < children.size(); i++) {
                Branch b = children.get(i);
                if(gen == 0){
                    if(i == 0){
                        pg.translate(-firstBranchOffset, 0);
                    }else{
                        pg.translate(firstBranchOffset, 0);
                    }
                }
                b.update();
            }
            resetGroup();
            pg.popMatrix();
        }

        void grow() {
            for (int i = 0; i < childCount; i++) {
                float norm = norm(i, 0, childCount - 1);
                float angle = map(norm, 0, 1, -childSpread, childSpread);
                children.add(new Branch(gen + 1, angle));
            }
        }

        void lineImage(PImage img, float ax, float ay, float bx, float by) {
            float cx = lerp(ax, bx, 0.5f);
            float cy = lerp(ay, by, 0.5f);

            float theta = -atan2(by - ay, bx - ax);
            pg.pushMatrix();
            pg.translate(cx, cy);
            pg.rotate(theta - HALF_PI);
            pg.imageMode(CENTER);
            if (stretchVertically) {
                float d = dist(ax, ay, bx, by);
                pg.image(img, 0, 0, img.width, d);
            } else {
                pg.image(img, 0, 0);
            }
            pg.popMatrix();
        }

    }
}
