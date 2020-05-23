package _2020_05;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class RepoCard extends KrabApplet {
    private PGraphics pg;
    private PImage template;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(1280,640, P3D);
    }

    public void setup() {
        pg = createGraphics(width, height, P3D);
        surface.setAlwaysOnTop(true);
        template = loadImage("images/repoCard/template.png");
    }

    public void draw() {
        pg.beginDraw();
        pg.background(template);
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }
}
