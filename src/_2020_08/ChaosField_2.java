package _2020_08;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import utils.OpenSimplexNoise;

import java.util.ArrayList;

/**
 * Made in collaboration between Krabcode, Kggsa, Jauska and Jett
 * inspired by Allyson Grey, see https://www.allysongrey.com/art/watercolors/chaos-field
 */

//TODO load more than 3 grids at startup, why is this even happening
public class ChaosField_2 extends KrabApplet {
    private PGraphics pg;
    private final String GLOBAL_GROUP = "global";
    private final PVector time = new PVector();
    private final ArrayList<Grid> grids = new ArrayList<>();
    private final OpenSimplexNoise noise = new OpenSimplexNoise();
    private int strokeColor;
    private float strokeWeight;
    int gridCount = 1;
    int tileMode = 0;

    public static void main(String[] args) {
        KrabApplet.main("_2020_08.ChaosField_2");
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
        group(GLOBAL_GROUP);
        time.x = cos(t);
        time.y = sin(t);
        time.mult(slider("noise speed"));
        pg = updateGraphics(pg, P2D);
        if (frameCount == 5) {
            pg.textSize(320);
        }
        pg.colorMode(HSB, 1, 1, 1, 1);
        pg.beginDraw();
        pg.image(gradient("background"), 0, 0);
        translateToCenter(pg);
        updateStroke();
        updateGrids();
        pg.endDraw();
        image(pg, 0, 0, width, height);
        rec(pg);
        gui();
    }

    private void updateStroke() {
        strokeColor = picker("stroke", 0).clr();
        strokeWeight = slider("weight (< 2)", 1.9f);
    }

    private void updateGrids() {
        tileMode = 0;
        String tileModeString = options("squares", "triangles", "circles", "hexagons");
        if(tileModeString.equals("triangles")) {
            tileMode = 1;
        }
        if(tileModeString.equals("circles")) {
            tileMode = 2;
        }
        if(tileModeString.equals("hexagons")) {
            tileMode = 3;
        }
        int toAnimate = sliderInt("show grids", 1, 0, 1000);
        if(button("add grid")) {
            gridCount++;
        }
        if (grids.size() > gridCount) {
            grids.remove(grids.size() - 1);
        }
        if (grids.size() < gridCount) {
            grids.add(new Grid(grids.size() + 1));
        }
        for (int i = gridCount-1; i >= max(0, gridCount-toAnimate); i--) {
            Grid g = grids.get(i);
            g.update();
            g.display();
        }
    }

    class Grid {
        PVector pos;
        float size, tileSize, rotation, padding;
        int rows, cols;
        Tile[][] tiles;
        int index;
        String gridName;

        Grid(int index) {
            this.index = index;
            rows = 10;
            cols = 10;
            tiles = new Tile[rows][cols];
            update();
        }

        private void update() {
            gridName = "grid " + index;
            group(gridName);
            pos = sliderXY("position");
            rotation = slider("rotation");
            size = slider("size",  width / 2f);
            tileSize = size / (float) rows;
            padding = slider("padding");
            updateTiles();
            if (toggle("mouse edit")) {
                editManually();
            }
            if (button("reset edits")) {
                resetEdits();
            }
            resetGroup();
        }

        private void resetEdits() {
            for (int xi = 0; xi < rows; xi++) {
                for (int yi = 0; yi < cols; yi++) {
                    tiles[xi][yi].manual.x = 0;
                    tiles[xi][yi].manual.y = 0;
                    tiles[xi][yi].manual.rotation = 0;
                }
            }
        }

        private void editManually() {
            if (!mousePressed) {
                unlockAllTilesForDragging();
            } else {
                Tile dragged = getFirstDraggedTile();
                if (dragged != null) {
                    mouseMove(dragged);
                    return;
                }
                for (int xi = 0; xi < rows; xi++) {
                    for (int yi = 0; yi < cols; yi++) {
                        Tile tile = tiles[xi][yi];
                        float x = tile.generated.x + tile.manual.x;
                        float y = tile.generated.y + tile.manual.y;
                        if (dist(mouseX - width / 2f, mouseY - height / 2f, x, y) < tileSize) {
                            mouseMove(tile);
                            tile.dragLocked = true;
                            break;
                        }
                    }
                }
            }
        }

        private Tile getFirstDraggedTile() {
            for (int xi = 0; xi < rows; xi++) {
                for (int yi = 0; yi < cols; yi++) {
                    Tile tile = tiles[xi][yi];
                    if (tile.dragLocked) {
                        return tile;
                    }
                }
            }
            return null;
        }

        void mouseMove(Tile tile) {
            tile.manual.x += mouseX - pmouseX;
            tile.manual.y += mouseY - pmouseY;
        }

        private void unlockAllTilesForDragging() {
            for (int xi = 0; xi < rows; xi++) {
                for (int yi = 0; yi < cols; yi++) {
                    tiles[xi][yi].dragLocked = false;
                }
            }
        }

        private void updateTiles() {
            for (int xi = 0; xi < rows; xi++) {
                for (int yi = 0; yi < cols; yi++) {
                    if (tiles[xi][yi] == null) {
                        tiles[xi][yi] = new Tile();
                    }
                    generateTransform(xi, yi);
                }
            }
        }

        private void generateTransform(int xi, int yi) {
            float halfPaddingSum = (padding * max(rows, cols)) / 2f;
            float halfSize = size / 2f;
            float halfTile = tileSize / 2f;
            float x = map(xi, 0, cols - 1, -halfSize - halfPaddingSum + halfTile, halfSize + halfPaddingSum - halfTile);
            float y = map(yi, 0, rows - 1, -halfSize - halfPaddingSum + halfTile, halfSize + halfPaddingSum - halfTile);
            PVector noisePos = sliderXY("noise pos");
            float freq = slider("noise freq", 1) / 1000f;
            float amp = slider("noise amp", 1);
            float noiseIntensity = (float) noise.eval(noisePos.x + x * freq, noisePos.y + y * freq);
            float threshold = -1 + 2 * slider("threshold (0-1)", 0, 1);
            if (noiseIntensity < threshold) {
                noiseIntensity = 0;
            } else {
                noiseIntensity -= threshold;
                noiseIntensity = max(0, noiseIntensity);
                noiseIntensity *= amp;
            }
            x += constrainedNoise(noiseIntensity, 1);
            y += constrainedNoise(noiseIntensity, 2);
            float localRotation = constrainedNoise(noiseIntensity, 3) * (slider("noise rotation") / TAU);
            tiles[xi][yi].generated.x = x;
            tiles[xi][yi].generated.y = y;
            tiles[xi][yi].generated.rotation = localRotation;
        }

        private float constrainedNoise(float noiseVal, int i) {
            return (float) (noiseVal * noise.eval(time.x, time.y, noiseVal + i * 875.213f));
        }

        void display() {
            pg.pushMatrix();
            pg.translate(pos.x, pos.y);
            pg.rotate(rotation);
            for (int xi = 0; xi < rows; xi++) {
                for (int yi = 0; yi < cols; yi++) {
                    Tile tile = tiles[xi][yi];
                    pg.pushMatrix();
                    pg.translate(tile.generated.x, tile.generated.y);
                    pg.translate(tile.manual.x, tile.manual.y);
                    pg.rotate(tile.generated.rotation);
                    pg.stroke(strokeColor);
                    pg.strokeWeight(strokeWeight);
                    pg.fill(getColor(xi, yi));
                    if(tileMode == 0) {
                        pg.rectMode(CENTER);
                        pg.rect(0, 0, tileSize, tileSize);
                    }else if(tileMode == 1) {
                        pg.triangle(tileSize*.5f, tileSize*.5f, -tileSize*.5f, -tileSize*.5f, tileSize*.5f, -tileSize*.5f);
                    }else if(tileMode == 2) {
                        pg.ellipse(0, 0, tileSize, tileSize);
                    }else if(tileMode == 3) {
                        pg.beginShape();
                        for (int i = 0; i < 6; i++) {
                            float angle = map(i, 0, 6, 0, TAU);
                            float x = tileSize * cos(angle);
                            float y = tileSize * sin(angle);
                            pg.vertex(x,y);
                        }
                        pg.endShape();
                    }
                    pg.popMatrix();
                }
            }
            pg.popMatrix();
        }

        private int getColor(int xi, int yi) {
            float norm = map(xi + yi, 0, rows + cols, 0, 1);
            group(GLOBAL_GROUP);
            norm += (t * sliderInt("color speed")) / TAU;
            norm %= 1;
            int clr = gradientColorAt("gradient", norm);
            group(gridName);
            return clr;
        }
    }

    static class Tile {
        public boolean dragLocked = false;
        Transform generated = new Transform();
        Transform manual = new Transform();
    }

    static class Transform {
        float x, y, rotation;
    }
}
