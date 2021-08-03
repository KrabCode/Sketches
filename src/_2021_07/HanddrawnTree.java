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
    ArrayList<PGraphics> pieces = new ArrayList<>();
    ArrayList<ArrayList<Character>> treeGenerations = new ArrayList<>();
    ArrayList<Character> axiom = charsAsList("[F]4-F".toCharArray());

    String ruleString = /* F = */  "|[+F][-F]"; // maybe swap the L, R
    float baseAngle = radians(45);

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

    float lastGenerationCount = 0;

    public void draw() {
        pg = updateGraphics(pg, img.width, img.height, P2D);
        pg.beginDraw();
        pg.imageMode(CORNER);
        pg.image(img, 0, 0);
        updateTreeDepth();
        reloadPieces();
        drawTree();
        pg.endDraw();
        imageMode(CORNER);
        image(pg, 0, 0);
        rec(pg, sliderInt("frames", 360));
        gui();
    }

    private void updateTreeDepth() {
        int maxGenerations = sliderInt("max generations", 3);
        if (maxGenerations != lastGenerationCount) {
            generateAllLSystems(maxGenerations);
        }
        lastGenerationCount = maxGenerations;
    }

    private void generateAllLSystems(int maxGenerations) {
        ArrayList<Character> nextTree = new ArrayList<>();
        ArrayList<Character> tree = new ArrayList<>(axiom);
        ArrayList<Character> rule = charsAsList(ruleString.toCharArray());
        for (int genIndex = 0; genIndex < maxGenerations; genIndex++) {
            for (char c : tree) {
                if (c == 'F') {
                    nextTree.addAll(rule);
                } else {
                    nextTree.add(c);
                }
            }
            tree = new ArrayList<>(nextTree);
            nextTree.clear();
            treeGenerations.add(new ArrayList<>(tree));
        }

        StringBuilder sb = new StringBuilder();
        for (Character character : tree) {
            sb.append(character);
        }
        println(sb.toString());
    }

    ArrayList<Character> charsAsList(char[] chars) {
        ArrayList<Character> list = new ArrayList<>();
        for (char aChar : chars) {
            list.add(aChar);
        }
        return list;
    }

    // F = draw fixed line and translate
    // | = draw depth line and translate
    // [ = push
    // ] = pop
    // +-0.23# = rotate right(offset)
    // -+0.1# = rotate left(offset)

    private void drawTree() {
        group("tree");
        baseAngle = radians(slider("base angle", 45));
        int depth = 0;
        int maxDepth = 3;
        int currentGenerationIndex = sliderInt("generation", 1);
        currentGenerationIndex = constrain(currentGenerationIndex, 0, treeGenerations.size() - 1);
        ArrayList<Character> tree = treeGenerations.get(currentGenerationIndex);
        float baseLength = slider("base length", 100);
        translate2D(pg, "center");
        pg.rotate(slider("rotate"));
        pg.imageMode(CENTER);
        pg.strokeWeight(1.99f);
        pg.stroke(255);
//        pg.ellipse(0,0,50,50);
        for (int i = 0, treeSize = tree.size(); i < treeSize; i++) {
            Character character = tree.get(i);
            PGraphics piece = pieces.get(floor(map(depth, 0, maxDepth, 0, pieces.size()-1)));
            if (character == 'F') {
                pg.image(piece, 0, 0);
                pg.translate(0, piece.height * baseLength);
            } else if (character == '|') {
                float ratio = 13f / 20;
                float depthNorm = norm(depth, 0, maxDepth) / ratio;
//                pg.line(0, 0, 0, baseLength*depthNorm);
                pg.image(piece, 0, 0, 0, piece.height * depthNorm);
                pg.translate(0, baseLength*depthNorm);
            } else if (character == '[') {
                pg.pushMatrix();
                depth++;
                maxDepth = max(depth, maxDepth);
            } else if (character == ']') {
                pg.popMatrix();
                depth--;
            } else if (character == '+' || character == '-') {
                int orientation = (character == '+' ? 1 : -1);
                Character previousChar = tree.get(i - 1);
                int repeats = 1;
                try {
                    repeats = Integer.parseInt(previousChar.toString());
                } catch (Exception ex) {
                    // shh
                }
                for (int repeat = 0; repeat < repeats; repeat++) {
                    pg.rotate(baseAngle * orientation);
                }

            }
        }
        resetGroup();
    }

    private void reloadPieces() {
        String chromaKeyShader = "shaders/filters/chromaKey.glsl";
        uniform(chromaKeyShader).set("black", false);
        uniform(chromaKeyShader).set("useBounds", true);
        uniform(chromaKeyShader).set("lowBound", slider("low bound"));
        uniform(chromaKeyShader).set("highBound", slider("high bound", 1f));
        if (toggle("update pieces", true)) {
            pieces.clear();
            for (int i = 0; i < 7; i++) {
                group("" + i);
                PGraphics piece = getRectangleAsShadedCanvas(img, chromaKeyShader, i, sliderXY("x,y"), sliderXY("w,h", 100));
                pieces.add(piece);
                PVector pos = sliderXY("pos", width / 2f, height / 2f);
                if (toggle("debug")) {
                    pg.imageMode(CENTER);
                    pg.image(piece, pos.x, pos.y);
                }
            }
        }
        resetGroup();
    }
}
