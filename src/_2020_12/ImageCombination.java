package _2020_12;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PImage;

import java.util.HashMap;
import java.util.Map;

public class ImageCombination extends KrabApplet {
    private PGraphics pg;
    String shader = "shaders/_2020_12/imageCombination.glsl";

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(1000, 1000, P3D);
    }

    public void setup() {

    }

    Map<String, PImage> pathsWithImages = new HashMap<String, PImage>();

    PImage getImage(String path) {
        if(!pathsWithImages.containsKey(path)) {
            pathsWithImages.put(path, loadImage(path));
        }
        return pathsWithImages.get(path);
    }

    public void draw() {
        pg = updateGraphics(pg);
        pg.beginDraw();
        pg.clear();
        updateShader(pg);
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg, sliderInt("frames", 360));
        gui();
    }

    private void updateShader(PGraphics pg) {
        uniform(shader).set("img_0", getImage("images/sky/daniel-gregoire-LhXrNC2dn6g-unsplash.jpg").get(floor(sliderXY("0").x), floor(sliderXY("0").y), 1000, 1000));
        uniform(shader).set("img_1", getImage("images/sky/dewang-gupta-ESEnXckWlLY-unsplash.jpg").get(floor(sliderXY("1").x), floor(sliderXY("1").y), 1000, 1000));
        uniform(shader).set("img_2", getImage("images/sky/eberhard-grossgasteiger-J9NDmBVhN04-unsplash.jpg").get(floor(sliderXY("2").x), floor(sliderXY("2").y), 1000, 1000));
        uniform(shader).set("img_3", getImage("images/sky/eberhard-grossgasteiger-NvesrDbsrL4-unsplash.jpg").get(floor(sliderXY("3").x), floor(sliderXY("3").y), 1000, 1000));
        uniform(shader).set("img_4", getImage("images/sky/pero-kalimero-9BJRGlqoIUk-unsplash.jpg").get(floor(sliderXY("4").x), floor(sliderXY("4").y), 1000, 1000));
        hotFilter(shader, pg);
    }
}
