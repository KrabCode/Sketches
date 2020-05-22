package _2020_05;

import applet.KrabApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;
import utils.OpenSimplexNoise;

public class Blocky extends KrabApplet {
    private PGraphics pg;
    private OpenSimplexNoise noise = new OpenSimplexNoise();

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
        frameRecordingDuration = sliderInt("recording frames", 360);
        pg.beginDraw();
        pg.colorMode(HSB, 1, 1, 1, 1);
        if(options("blend: add", "blend: replace").equals("blend: add")) {
            pg.blendMode(ADD);
        }else {
            pg.blendMode(REPLACE);
        }
        fadeToBlack(pg);
        splitPass(pg);
        translateToCenter(pg);
        translate(pg, "translate 1");
        updateLighting();
        lightShader();
        preRotate(pg);
        translate(pg, "translate 2");
        drawSeaOfBoxes();
        drawRecursiveShapeStart();
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
        pg.directionalLight(dirLightColor.hue(), dirLightColor.sat(), dirLightColor.br(), dirLightDir.x, dirLightDir.y, dirLightDir.z);
        pg.ambient(ambientColor.hue(), ambientColor.sat(), ambientColor.br());
        pg.ambientLight(ambientColor.hue(), ambientColor.sat(), ambientColor.br());
        resetCurrentGroup();
    }

    private void drawRecursiveShapeStart() {
        group("recursive");
        if(toggle("skip")) {
            return;
        }
        pg.pushMatrix();
        pg.strokeWeight(slider("weight"));
        pg.stroke(picker("stroke").clr());
        pg.fill(picker("fill").clr());
        drawRecursiveShape(
                sliderXYZ("orig translate").copy(),
                sliderXYZ("orig rotate").copy(),
                sliderXYZ("orig size", 600, 50, 600).copy());
        sliderXYZ("rotate Δ").add(sliderXYZ("rotate ΔΔ"));
        pg.popMatrix();
    }

    private void drawRecursiveShape(PVector translate, PVector rotate, PVector size) {
        float minSize = slider("min size", 10);
        if (size.x < minSize || size.y < minSize || size.z < minSize) {
            return;
        }
        pg.translate(translate.x, translate.y, translate.z);
        pg.rotateX(rotate.x);
        pg.rotateY(rotate.y);
        pg.rotateZ(rotate.z);
        if (!size.equals(sliderXYZ("orig size")) || toggle("draw first")) {
            pg.box(size.x, size.y, size.z);
        }
        PVector sizeDelta = sliderXYZ("size Δ", -10);
        drawRecursiveShape(sliderXYZ("translate Δ"), sliderXYZ("rotate Δ"), size.add(sizeDelta));
    }


    private void drawSeaOfBoxes() {
        // grid of columns on the XZ plane with fbm informed Y size
        group("sea");
        pg.pushMatrix();
        pg.noStroke();
        HSBA fill = picker("fill");

        translate(pg, "translate");
        preRotate(pg, "sea rotate");
        PVector size = sliderXY("grid size");
        PVector boxSize = new PVector(slider("col width"), slider("col height"), slider("col width"));
        float timeRadius = slider("time");
        PVector time = new PVector(timeRadius*cos(t), timeRadius*sin(t));
        float maxDist = slider("max dist", 1500);
        int count = sliderInt("count", 10);
        PVector noiseFreqs = sliderXY("noise freqs");
        PVector noiseMags = sliderXY("noise mags");
        PVector constSpeed = sliderXY("noise speed");
        PVector varSpeedRadius = sliderXY("noise speed radius");
        PVector windSpeed = new PVector(constSpeed.x+varSpeedRadius.x*time.x, constSpeed.y+varSpeedRadius.y*time.y);
        for (int xi = 0; xi < count; xi++) {
            for (int yi = 0; yi < count; yi++) {
                float x = map(xi, 0, count - 1, -size.x, size.x);
                float z = map(yi, 0, count - 1, -size.y, size.y);
                if(toggle("check dist") && dist(x,z,0,0) > maxDist) {
                    continue;
                }
                pg.pushMatrix();
                double y = noise.eval(noiseFreqs.x*x+windSpeed.x,noiseFreqs.x*z+windSpeed.y,0,0)*noiseMags.x;
                y += noise.eval(noiseFreqs.y*x+windSpeed.x,noiseFreqs.y*z+windSpeed.y,0,0)*noiseMags.y;
//                y += noise.eval(noiseFreqs.z*x+windSpeed.x*t,noiseFreqs.z*z+windSpeed.y*t,time.x,time.y)*noiseMags.z;
                float hueOffset = (float)y*slider("hue offset");
                float satOffset = (float)y*slider("sat offset");
                float brOffset = (float)y*slider("br offset");
                pg.fill(hueModulo(fill.hue()+hueOffset), constrain(satOffset+fill.sat(), 0, 1), constrain(brOffset+fill.br(),0,1), fill.alpha());
                pg.translate(x, (float) y, z);
                pg.box(boxSize.x, boxSize.y, boxSize.z);
                pg.popMatrix();
            }
        }
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
