package _2020_04;

import applet.KrabApplet;
import processing.core.PGraphics;

public class Grid extends KrabApplet {
    private PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(new Object() {}.getClass().getEnclosingClass().getName());
    }

    public void settings() {
        size(800, 800, P3D);
    }

    public void setup() {
        pg = createGraphics(width, height, P3D);
        surface.setAlwaysOnTop(true);
    }

    public void draw() {
        pg.beginDraw();
        if(toggle("fade color")){
            fadeToBlack(pg);
        }else{
            fadeToWhite(pg);
        }
        updateShader(pg);
        pg.strokeWeight(slider("weight", 1));
        pg.stroke(picker("stroke").clr());
        pg.noFill();
        int count = sliderInt("count", 100);
        float buffer = slider("buffer", 50);
        int detail = sliderInt("detail", 100);
        for (int xi = 0; xi < count; xi++) {
            float x = map(xi, 0, count-1, -buffer, width+buffer);
            pg.beginShape();
            for (int i = 0; i < detail; i++) {
                float y = map(i, 0, detail-1, -buffer, height+buffer);
                pg.vertex(x, y);
            }
            pg.endShape();
        }
        for (int yi = 0; yi < count; yi++) {
            pg.beginShape();
            float y = map(yi, 0, count-1, -buffer, height+buffer);
            for (int i = 0; i < detail; i++) {
                float x = map(i, 0, detail-1, -buffer, width+buffer);
                pg.vertex(x,y);
            }
            pg.endShape();
        }
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    private void updateShader(PGraphics pg) {
        String frag = "shaders/_2020_04/grid/LineFrag.glsl";
        String vert = "shaders/_2020_04/grid/LineVert.glsl";
        uniform(frag, vert).set("time", t);
        hotShader(frag, vert, pg);
    }
}
