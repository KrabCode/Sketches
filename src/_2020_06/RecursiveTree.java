package _2020_06;

import applet.KrabApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

import java.util.ArrayList;

public class RecursiveTree extends KrabApplet {
    private PGraphics pg;
    private ArrayList<Branch> branches = new ArrayList<Branch>();
    private float recordTime;

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
        pg = createGraphics(width, height, P2D);
        frameRecordingDuration = 10000;
    }

    public void draw() {
        pg.beginDraw();
        fadeToBlack(pg);
        blurPass(pg);
        translateToCenter(pg);
        translate2D(pg);
        if(options("add", "replace").equals("add")){
            pg.blendMode(ADD);
        }else{
            pg.blendMode(REPLACE);
        }
        pg.stroke(picker("stroke").clr());
        buildTree();
        displayTree();
        pg.endDraw();
        colorSplit(pg, true);
        image(pg, 0, 0, width, height);
        rec(pg);
        gui();
    }

    private void displayTree() {
        int copies = sliderInt("copies", 8);
        for (int i = 0; i < copies; i++) {
            float theta = map(i, 0, copies, 0, TAU);
            pg.pushMatrix();
            pg.rotate(theta);
            for (Branch b : branches) {
                b.display();
            }
            pg.popMatrix();
        }
        if(button("print branch count")){
            println("branch count: " + branches.size());
        }
    }

    private void buildTree() {
        float size = slider("size", 100);
        float weight = slider("weight", 1);
        recordTime = t/sliderInt("time",30);
        println(recordTime);
        branches.clear();
        PVector dir = new PVector(0, -size);
        Branch branch = new Branch(new PVector(), dir, weight, 0);
        addBranchesRecursively(branch, size);
        branches.add(branch);
    }

    private void addBranchesRecursively(Branch parent, float size) {
        int generationCount = sliderInt("generations", 3);
        if (parent.generation >= generationCount || size < 0) {
            return;
        }
        int splitCount = sliderInt("split count", 3);
        float angleRange = slider("angle range", 0.2f)+TAU*recordTime;
        float newWeight = parent.weight+slider("weight change");
        float newSize = size + slider("size change");
        for (int splitIndex = 0; splitIndex < splitCount; splitIndex++) {
            float theta = PVector.sub(parent.target, parent.origin).heading();
            if(splitCount > 1){
                float splitNorm = norm(splitIndex, 0, splitCount-1);
                float angleOffset = map(splitNorm, 0, 1, -angleRange, angleRange);
                theta += angleOffset;
            }
            PVector newTarget = new PVector(newSize*cos(theta), newSize*sin(theta)).add(parent.target.copy());
            Branch branch = new Branch(parent.target.copy(), newTarget, newWeight,parent.generation+1);
            addBranchesRecursively(branch, newSize);
            branches.add(branch);
        }
    }

    private class Branch {
        PVector origin, target;
        float weight;
        int generation;

        Branch(PVector origin, PVector target, float weight, int generation) {
            this.origin = origin;
            this.target = target;
            this.weight = weight;
            this.generation = generation;
        }

        void display() {
            if(generation < sliderInt("hidden generations")){
                return;
            }
            pg.strokeWeight(weight);
            pg.line(origin.x, origin.y, target.x, target.y);
        }
    }
}
