package _2020_08;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import utils.OpenSimplexNoise;

import java.util.ArrayList;

/**
 * Made in collaboration between Krabcode, Kggsa, Jauska and Jett
 * inspired by Allyson Grey, see https://www.allysongrey.com/art/watercolors/chaos-field
 *
 */
public class ChaosField_2 extends KrabApplet {
    private PGraphics pg;
    private PVector time = new PVector();
    private ArrayList<Grid> grids = new ArrayList<>();
    private OpenSimplexNoise noise = new OpenSimplexNoise();
    private int strokeColor;
    private float strokeWeight;
    private boolean showIndexes = true;
    private String GLOBAL_GROUP = "global";

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
        group(GLOBAL_GROUP);
        showIndexes = toggle("show indexes", true);
        time.x = cos(t);
        time.y = sin(t);
        time.mult(slider("noise speed"));
        pg = updateGraphics(pg, P2D);
        pg.colorMode(HSB,1,1,1,1);
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
        int gridCount = sliderInt("grid count", 1);
        if(grids.size() > gridCount){
            grids.remove(grids.size()-1);
        }
        if(grids.size() < gridCount){
            grids.add(new Grid(grids.size()+1));
        }
        for (Grid g : grids) {
            g.update();
            g.display();
        }
    }

    class Grid{
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
            regenerateTiles();
        }

        private void update() {
            gridName = "grid " + index;
            group(gridName);
            pos = sliderXY("position");
            rotation = slider("rotation");
            size = slider("size", width / 2f);
            tileSize = size / (float) rows;
            padding = slider("padding", 0);
            regenerateTiles();
            if(toggle("mouse edit")){
                editManually();
            }
        }

        private void editManually() {
            // TODO implement
        }

        private void regenerateTiles() {
            for(int xi = 0; xi < rows; xi++){
                for (int yi = 0; yi < cols; yi++) {
                    if(tiles[xi][yi] == null){
                        tiles[xi][yi] = new Tile();
                    }
                    Tile tile = tiles[xi][yi];
                    tile.generated = generateTransform(xi, yi);
                }
            }
        }

        private int getColor(int xi, int yi) {
            float norm = map(xi + yi, 0, rows+cols, 0, 1);
            group(GLOBAL_GROUP);
            norm += (t * sliderInt("color speed")) / TAU;
            norm %= 1;
            int clr = gradientColorAt("gradient", norm);
            group(gridName);
            return clr;
        }

        private Transform generateTransform(int xi, int yi) {
            float halfPaddingSum = (padding * max(rows, cols)) / 2f;
            float halfSize = size / 2f;
            float halfTile = tileSize / 2f;
            float x = map(xi, 0, cols-1, -halfSize-halfPaddingSum+halfTile, halfSize+halfPaddingSum-halfTile);
            float y = map(yi, 0, rows-1, -halfSize-halfPaddingSum+halfTile, halfSize+halfPaddingSum-halfTile);
            PVector noisePos = sliderXY("noise pos");
            float freq = slider("noise freq", 1) / 100f;
            float amp = slider("noise amp", 1);
            float noiseIntensity = amp * (float) noise.eval(noisePos.x + x*freq,noisePos.y + y*freq, time.x, time.y);
            float randomX = constrainedNoise(noiseIntensity, 1);
            float randomY = constrainedNoise(noiseIntensity, 2);
            x += randomX;
            y += randomY;
            float localRotation = constrainedNoise(noiseIntensity, 3)*slider("noise rotation");
            return new Transform(x, y, localRotation);
        }

        private float constrainedNoise(float noiseVal, int i) {
            // TODO still needs some work
            return (float) (noiseVal*noise.eval(noiseVal, i*8.213f));
        }

        void display() {
            pg.pushMatrix();
            pg.translate(pos.x, pos.y);
            pg.rotate(rotation);
            for(int xi = 0; xi < rows; xi++){
                for (int yi = 0; yi < cols; yi++) {
                    Tile tile = tiles[xi][yi];
                    pg.pushMatrix();
                    pg.translate(tile.generated.x, tile.generated.y);
                    pg.rotate(tile.generated.rotation);
                    pg.translate(tile.manual.x, tile.manual.y);
                    pg.rotate(tile.manual.rotation);
                    pg.stroke(strokeColor);
                    pg.strokeWeight(strokeWeight);
                    pg.fill(getColor(xi, yi));
                    pg.rectMode(CENTER);
                    pg.rect(0, 0, tileSize, tileSize);
                    pg.popMatrix();
                }
            }
            if(showIndexes){
                pg.fill(0, 0.5f);
                pg.textSize(size*.8f);
                pg.textAlign(CENTER,CENTER);
                pg.text(index, 0, 0);
            }
            pg.popMatrix();
        }
    }

    static class Tile{
        Transform generated  = new Transform();
        Transform manual = new Transform();
    }

    static class Transform{
        float x, y, rotation;

        public Transform() {

        }

        public Transform(float x, float y, float rotation) {
            this.x = x;
            this.y = y;
            this.rotation = rotation;
        }
    }
}
