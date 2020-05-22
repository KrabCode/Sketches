package _2020_04;

import applet.KrabApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

public class LitGrid extends KrabApplet {
    private PGraphics pg;
    private int count = 100;
    private PVector size = new PVector(1,1);

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
        pg.background(0);
        if(toggle("lit")) {
            pg.lights();
            pg.lightSpecular(255,255,255);
            pg.ambientLight(0,0,0);
            PVector dirLight = sliderXYZ("dir light").copy().normalize();
            float dirLightStrength = slider("dir light strength");
            pg.directionalLight(dirLightStrength, dirLightStrength, dirLightStrength, dirLight.x, dirLight.y, dirLight.z);
        }
        translateToCenter(pg);
        translate(pg, "global pos");
        preRotate(pg);
        translate(pg, "local pos");
        pg.hint(PConstants.DISABLE_OPTIMIZED_STROKE);
        updateShader();
        drawQuadStrip();
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    // TODO make PShape
    private void drawQuadStrip() {
        count = sliderInt("count", 100);
        size = sliderXY("size", 1000);
        pg.stroke(picker("stroke").clr());
        pg.fill(picker("fill").clr());
        if (toggle("no stroke")) {
            pg.noStroke();
        }
        if (toggle("no fill")) {
            pg.noFill();
        }
        for (int zi = 0; zi < count; zi++) {
            pg.beginShape(PConstants.QUAD_STRIP);
            for (int xi = 0; xi < count; xi++) {
                float x = map(xi, 0, count - 1, -size.x, size.x);
                float z = map(zi, 0, count - 1, -size.y, size.y);
                float nextZ = map(zi + 1, 0, count - 1, -size.y, size.y);
                pg.vertex(x, 0, z);
                pg.vertex(x, 0, nextZ);
            }
            pg.endShape();
        }
    }

    private void updateShader() {
        String frag = "shaders/_2020_04/litGrid/LightFrag.glsl";
        String vert = "shaders/_2020_04/litGrid/LightVert.glsl";
        uniform(frag, vert).set("time", t);
        uniform(frag, vert).set("count", count);
        uniform(frag, vert).set("size", size.x, size.z);
        hotShader(frag, vert, pg);
    }
}
