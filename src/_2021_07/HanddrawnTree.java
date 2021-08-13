package _2021_07;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

import java.util.ArrayList;

public class HanddrawnTree extends KrabApplet {
    private PGraphics pg;
    private PImage photo;
    int maxPieces;
    int windowWidth = 1089;  // 3024 * 0.36;
    int windowHeight = 1452; // 4032 * 0.36;
    int treeDepth = 1;
    Branch tree = new Branch(0, PI);
    float branchSpread = 0;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        fullScreen(P3D);
    }

    public void setup() {
        toggleFullscreen(2560 - windowWidth, 1440 - windowHeight, windowWidth, windowHeight);
        photo = loadImage("images/tree/tree.jpg");
        photo.resize(windowWidth, windowHeight);
        surface.setAlwaysOnTop(true);
    }


    public void draw() {

        pg = updateGraphics(pg, photo.width, photo.height, P3D);
        pg.beginDraw();
        pg.imageMode(CORNER);
        pg.image(photo, 0, 0);
        maxPieces = sliderInt("max pieces", 7);
        branchSpread = slider("branch spread", 0.6f);
        reloadPieces();
        translate2D(pg, "center");
        treeDepth = findTreeDepth();
//        println("tree depth " + treeDepth);
        if (button("replant")) {
            tree = new Branch(0, PI);
        }
        tree.update();
        pg.endDraw();
        imageMode(CORNER);
        image(pg, 0, 0);
        rec(pg, sliderInt("frames", 360));
        gui();
    }

    private int findTreeDepth() {
        return findTreeDepthRecursively(tree, 0);
    }

    private int findTreeDepthRecursively(Branch branch, int depth) {
        if (branch == null) {
            return -1;
        }
        int max = depth;
        for (int i = 0; i < branch.children.size(); i++) {
            max = max(depth, findTreeDepthRecursively(branch.children.get(i), depth));
        }
        return 1 + max;
    }

    private void reloadPieces() {
        if (toggle("update pieces", true)) {
            for (int i = 0; i < maxPieces; i++) {
                group("" + i);
                PGraphics piece = getRectangleAsShadedCanvas(photo, updateChromakeyShader(slider("transparency")), i, sliderXY("x,y"), sliderXY("w,h", 100));
                PVector pos = sliderXY("pos", width / 2f, height / 2f);
                if (toggle("debug")) {
                    pg.imageMode(CENTER);
                    pg.image(piece, pos.x, pos.y);
                }
            }
        }
        resetGroup();
    }

    private String updateChromakeyShader(float transparency) {
        float lowBoundFullyTransparent = 1f;
        float lowBoundFullyVisible = 0.35f;
        float lowBound = map(transparency, 0, 1, lowBoundFullyVisible, lowBoundFullyTransparent);
//        lowBound = 0.3f;
        String chromaKeyShader = "shaders/filters/chromaKey.glsl";
        uniform(chromaKeyShader).set("black", false);
        uniform(chromaKeyShader).set("useBounds", true);
        uniform(chromaKeyShader).set("lowBound", lowBound);
        uniform(chromaKeyShader).set("highBound", 1f);
        return chromaKeyShader;
    }

    class Branch {
        ArrayList<Branch> children = new ArrayList<>();
        float angle;
        int frameCreated = frameCount;
        int growthPeriod = 120;
        float lengthOffsetSpread = slider("length offset spread", 20);
        float lengthOffset = -lengthOffsetSpread + 2 * randomGaussian() * lengthOffsetSpread;
        float length = 100 + lengthOffset;
        float prevGrowthNorm = 0;
        int depth;

        public Branch(int depth, float angle) {
            this.depth = depth;
            this.angle = angle;
        }

        void update() {
            length = slider("length", 100);
            pg.translate(0, length);
            pg.rotate(angle);
            if (toggle("scale")) {
                pg.scale(slider("scaledown", 1));
            }
            draw();

            for (Branch child : children) {
                pg.pushMatrix();
                child.update();
                pg.popMatrix();
            }
        }

        void draw() {
            float growthNorm = map(frameCount, frameCreated, frameCreated + growthPeriod, 0, 1);
            float growthIndex = min(floor(growthNorm), sliderInt("max pieces", 7));

            float currentPieceTransparency =  constrain((0.99f-growthNorm)%1f, 0, 1);

            growthNorm = min(growthNorm, 1);
            if (shouldHaveChildren()) {
                haveChildren();
            }
            prevGrowthNorm = growthNorm;

            if (toggle("debug", true)) {
                pg.strokeWeight(1 + growthIndex + growthNorm);
                pg.stroke(255);
                pg.line(0, 0, 0, growthNorm * length);
                pg.fill(255);
                return;
            }

            int imageIndex = treeDepth - depth - 1;
            imageIndex = constrain(imageIndex, 0, maxPieces - 1);
            group("" + imageIndex);

            PGraphics piece = getRectangleAsShadedCanvas(photo, updateChromakeyShader(currentPieceTransparency),
                    imageIndex, sliderXY("x,y"), sliderXY("w,h", 100));
            resetGroup();
            pg.imageMode(CORNER);
            pg.pushMatrix();
            pg.rotate(PI);
            pg.image(piece, -piece.width/2f, -piece.height / 2f);
            pg.popMatrix();
//            pg.textSize(60);
//            pg.text(depth, 20, 0);
        }

        private boolean shouldHaveChildren() {
            return treeDepth < maxPieces - 1 && frameCount == frameCreated + growthPeriod && toggle("grow", true);
        }

        private void haveChildren() {
            for (int i = 0; i < sliderInt("child per branch", 3); i++) {
                children.add(new Branch(depth + 1, randomGaussian() * branchSpread));
            }
        }
    }
}
