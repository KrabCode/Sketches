package _2020_04;

import applet.KrabApplet;
import processing.core.PGraphics;

public class OrbitingParticles extends KrabApplet {
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
        frameRecordingDuration *= 2;
    }

    public void draw() {
        pg.beginDraw();
        fadeToBlack(pg);
        blurPass(pg);
        pg.translate(width*.5f, height*.5f);
        updateShader();
        int count = sliderInt("count", 100);
        pg.strokeWeight(slider("weight", 1));
        pg.stroke(picker("stroke").clr());
        for (int i = 0; i < count; i++) {
            pg.point(i,0);
        }
        pg.resetShader();
        pg.endDraw();
        resetShader();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    private void updateShader() {
        String frag = "shaders/_2020_04/particles/PointFrag.glsl";
        String vert = "shaders/_2020_04/particles/PointVert.glsl";
        uniform(frag, vert).set("time", t);
        hotShader(frag, vert, pg);
    }
}
