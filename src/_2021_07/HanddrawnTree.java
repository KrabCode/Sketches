package _2021_07;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

import java.util.ArrayList;

public class HanddrawnTree extends KrabApplet {
    private PGraphics pg;
    private PImage img;
    int windowWidth = 1089;  // 3024 * 0.36;
    int windowHeight = 1452; // 4032 * 0.36;
    Branch tree = new Branch(PI);

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        fullScreen(P3D);
    }

    public void setup() {
        toggleFullscreen(2560 - windowWidth, 1440 - windowHeight, windowWidth, windowHeight);
        img = loadImage("images/tree/tree.jpg");
        img.resize(windowWidth, windowHeight);
        surface.setAlwaysOnTop(true);
    }


    public void draw() {
        pg = updateGraphics(pg, img.width, img.height, P3D);
        pg.beginDraw();
        pg.imageMode(CORNER);
        pg.image(img, 0, 0);
        reloadPieces();
        translate2D(pg, "center");
        tree.update();
        if(button("replant")){
            tree = new Branch(PI);
        }
        pg.endDraw();
        imageMode(CORNER);
        image(pg, 0, 0);
        rec(pg, sliderInt("frames", 360));
        gui();
    }

    private void reloadPieces() {
        String chromaKeyShader = "shaders/filters/chromaKey.glsl";
        uniform(chromaKeyShader).set("black", false);
        uniform(chromaKeyShader).set("useBounds", true);
        uniform(chromaKeyShader).set("lowBound", slider("low bound"));
        uniform(chromaKeyShader).set("highBound", slider("high bound", 1f));
        if (toggle("update pieces", true)) {
            for (int i = 0; i < slider("max pieces", 7); i++) {
                group("" + i);
                PGraphics piece = getRectangleAsShadedCanvas(img, chromaKeyShader, i, sliderXY("x,y"), sliderXY("w,h", 100));
                PVector pos = sliderXY("pos", width / 2f, height / 2f);
                if (toggle("debug")) {
                    pg.imageMode(CENTER);
                    pg.image(piece, pos.x, pos.y);
                }
            }
        }
        resetGroup();
    }

    class Branch{
        ArrayList<Branch> children = new ArrayList<>();
        float angle;
        int frameCreated = frameCount;
        int growthPeriod = 120;
        float lengthOffsetSpread = slider("20");
        float lengthOffset = random(-lengthOffsetSpread, lengthOffsetSpread);
        float length = 100 + lengthOffset;
        float prevGrowthNorm = 0;

        public Branch(float angle) {
            this.angle = angle;
        }

        void update(){
            length = slider("length", 100);

            pg.rotate(angle);
            draw();
            pg.pushMatrix();
            pg.translate(0, length);
            pg.scale(slider("scaledown",0.8f));
            for(Branch child : children){
                child.update();
            }
            pg.popMatrix();
        }

        void draw(){
            float growthNorm = map(frameCount, frameCreated, frameCreated + growthPeriod, 0, 1);
            float growthIndex = min(floor(growthNorm), sliderInt("max pieces", 7));
            growthNorm = min(growthNorm, 1);
            if(prevGrowthNorm != 1 && growthNorm == 1 && toggle("grow", true)){
                for (int i = 0; i < random(1, 3); i++) {
                    children.add(new Branch(randomGaussian() * slider("branch spread", HALF_PI)));
                }
            }
            prevGrowthNorm = growthNorm;
            pg.strokeWeight(1+growthIndex + growthNorm);
            pg.stroke(255);
            pg.line(0, 0, 0, growthNorm * length);
            pg.fill(255);
        }
    }
}
