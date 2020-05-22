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
        size(1000, 1000, P3D);
    }

    public void setup() {
        pg = createGraphics(width, height, P3D);
        surface.setAlwaysOnTop(true);
    }

    public void draw() {
        pg.beginDraw();
        pg.colorMode(HSB,1,1,1,1);
        backgroundShader();
        translateToCenter(pg);
        translate(pg);
        updateLighting();
        lightShader();
        preRotate(pg);
        drawSeaOfBoxes();
        group("recursive");
        drawRecursiveShape(
                sliderXYZ("orig translate").copy(),
                sliderXYZ("orig rotate").copy(),
                sliderXYZ("orig size", 600, 50, 600).copy());
        sliderXYZ("rotate").add(sliderXYZ("rotate delta"));
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
        resetCurrentGroup();
    }

    private void drawRecursiveShape(PVector translate, PVector rotate, PVector size) {
        float minSize = slider("min size", 10);
        if(size.x < minSize || size.y < minSize || size.z < minSize) {
            return;
        }
        pg.rotateX(rotate.x);
        pg.rotateY(rotate.y);
        pg.rotateZ(rotate.z);
        pg.translate(translate.x, translate.y, translate.z);
        pg.box(size.x, size.y, size.z);
        PVector sizeMult = sliderXYZ("size mult", .9f);
        drawRecursiveShape(sliderXYZ("translate"), sliderXYZ("rotate"), new PVector(size.x*sizeMult.x, size.y*sizeMult.y, size.z*sizeMult.z));
    }


    private void drawSeaOfBoxes() {
        // grid of columns on the XZ plane with fbm informed Y size

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


    private void lightShader() {
        String vert = "shaders/_2020_05/cross/LightVert.glsl";
        String frag = "shaders/_2020_05/cross/LightFrag.glsl";
        uniform(frag, vert).set("time", t);
        hotShader(frag, vert, pg);
    }

}
