package _2020_05;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class Cross extends KrabApplet {
    private PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
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
        pg.colorMode(HSB,1,1,1,1);
        backgroundShader();
        updateLighting();
        translateToCenter(pg);
        preRotate(pg);
        crossShader();
        drawCross();
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    private void updateLighting() {
        group("lights");
        HSBA ambientColor = picker("ambient");
        HSBA dirLightColor = picker("dir light color");
        HSBA specLightColor = picker("spec light color");
        PVector dirLightDir = sliderXYZ("dir dir");
        pg.lights();
        pg.lightSpecular(specLightColor.hue(), specLightColor.sat(), specLightColor.br());
        pg.shininess(slider("shine"));
        pg.directionalLight(dirLightColor.hue(), dirLightColor.sat(), dirLightColor.br(), dirLightDir.x,dirLightDir.y,dirLightDir.z);
        pg.ambient(ambientColor.hue(), ambientColor.sat(), ambientColor.br());
        pg.ambientLight(ambientColor.hue(), ambientColor.sat(), ambientColor.br());

    }

    private void drawCross() {
        group("cross");
        PVector sizeA = sliderXYZ("cross A", 50, 600, 50);
        PVector sizeB = sliderXYZ("cross B", 200, 50, 50);
        pg.strokeWeight(slider("weight"));
        pg.stroke(picker("stroke").clr());
        pg.fill(picker("fill").clr());
        drawBox("translate A", sizeA);
        drawBox("translate B", sizeB);
    }

    private void drawBox(String translateSliderName, PVector size) {
        pg.pushMatrix();
        translate(pg, translateSliderName);
        pg.box(size.x, size.y, size.z);
        pg.popMatrix();

    }

    private void backgroundShader() {
        String frag = "shaders/_2020_05/cross/backgroundNoise.glsl";
        uniform(frag).set("time", t);
        hotFilter(frag, pg);
    }


    private void crossShader() {
        String vert = "shaders/_2020_05/cross/LightVert.glsl";
        String frag = "shaders/_2020_05/cross/LightFrag.glsl";
        uniform(frag, vert).set("time", t);
        hotShader(frag, vert, pg);
    }

}
