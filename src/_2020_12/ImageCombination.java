package _2020_12;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

import java.util.HashMap;
import java.util.Map;

public class ImageCombination extends KrabApplet {
    private PGraphics pg;
    String shader = "shaders/_2020_12/imageCombination.glsl";
    Map<String, PImage> pathsWithImages = new HashMap<String, PImage>();

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(1200, 1200, P3D);
    }

    public void setup() {

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
        group("0");
        setUniforms("0", "images/sky/daniel-gregoire-LhXrNC2dn6g-unsplash.jpg");
        group("1");
        setUniforms("1", "images/sky/dewang-gupta-ESEnXckWlLY-unsplash.jpg");
        group("2");
        setUniforms("2", "images/sky/eberhard-grossgasteiger-J9NDmBVhN04-unsplash.jpg");
        group("3");
        setUniforms("3", "images/sky/eberhard-grossgasteiger-NvesrDbsrL4-unsplash.jpg");
        group("4");
        setUniforms("4", "images/sky/pero-kalimero-9BJRGlqoIUk-unsplash.jpg");
        hotFilter(shader, pg);
        resetGroup();
    }

    void setUniforms(String group, String path) {
        PImage img = getImage(path);
        uniform(shader).set("img_" + group, img);
        uniform(shader).set("pos_" + group, sliderXY("pos"));
        uniform(shader).set("size_" + group, sliderXY("size", 1, img.width / (float) img.height));
    }

    PImage getImage(String path) {
        if (!pathsWithImages.containsKey(path)) {
            pathsWithImages.put(path, loadImage(path));
        }
        return pathsWithImages.get(path);
    }

}
