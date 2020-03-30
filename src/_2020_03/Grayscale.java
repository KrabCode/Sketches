package _2020_03;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PImage;

import java.util.ArrayList;

/**
 * Created by Jakub 'Krab' Rak on 2020-03-21
 */
public class Grayscale extends KrabApplet {
    private PGraphics pg;
    ArrayList<PImage> grayscaleTextures = new ArrayList<>();

    public static void main(String[] args) {
        KrabApplet.main(String.valueOf(new Object(){}.getClass().getEnclosingClass()).split(" ")[1]);
    }

    public void settings() {
//        size(800, 800, P2D);
        fullScreen(P2D, 2);
    }

    public void setup() {
        surface.setAlwaysOnTop(true);
        loadImages();
        pg = createGraphics(width, height, P2D);
        pg.beginDraw();
        pg.background(0);
        pg.endDraw();
    }

    private void loadImages() {
        for (int i = 0; i < 6; i++) {
            grayscaleTextures.add(loadImage("images/grayscale_2/"+i+ ".png"));
        }
    }

    public void draw() {
        pg.beginDraw();
        String grayscaleTextureShader = "shaders/_2020_03/grayscaleTextures.glsl";
        uniform(grayscaleTextureShader).set("time", t);
        for (int i = 0; i < 6; i++) {
            uniform(grayscaleTextureShader).set("grayscale_"+i, grayscaleTextures.get(i));
        }
        hotFilter(grayscaleTextureShader, pg);
        pg.endDraw();
        background(0);
        image(pg, 0, 0);
        rec(pg);
        gui();
    }
}
