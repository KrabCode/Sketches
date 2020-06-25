package _2020_06;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class Unrelated extends KrabApplet {
    private PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
//        fullScreen(P2D);
        size(1000, 1000, P3D);
    }

    public void setup() {
        pg = createGraphics(width, height, P2D);
        if (width < displayWidth) {
            surface.setAlwaysOnTop(true);
            surface.setLocation(2560 - 1020, 20);
        }
    }

    public void draw() {
        frameRecordingDuration = sliderInt("frames", 1000);
        pg.beginDraw();
        if (button("clear")) {
            pg.clear();
        }
        fadeToBlack(pg);
        multiplyPass(pg);
        pg.pushStyle();
        translateToCenter(pg);
        translate2D(pg);
        pg.imageMode(CENTER);
        pg.blendMode(ADD);
        pg.image(gradient("ADD 1", width*2, height*2), 0, 0);
        pg.image(gradient("ADD 2", width*2, height*2), 0, 0);
        pg.blendMode(SUBTRACT);
        pg.image(gradient("SUBTRACT 1", width*2, height*2), 0, 0);
        pg.image(gradient("SUBTRACT 2", width*2, height*2), 0, 0);
        pg.popStyle();
        flowShader();
        pg.endDraw();
        resetShader();
        rgbSplitScaleAndOffset(pg);
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    private void multiplyPass(PGraphics pg) {
        String multiplyFrag = "shaders/filters/multiply.glsl";
        uniform(multiplyFrag).set("amt", slider("multiply", 1));
        hotFilter(multiplyFrag, pg);
    }

    void flowShader() {
        group("displace");
        String shaderPath = "shaders/_2020_06/Unrelated/fbmNoiseDisplace.glsl";
        uniform(shaderPath).set("time", t*slider("time", 1));
        uniform(shaderPath).set("timeSpeed", slider("time radius", 0.2f));
        uniform(shaderPath).set("angleOffset", slider("angle offset", 1));
        uniform(shaderPath).set("angleRange", slider("angle range", 2));
        uniform(shaderPath).set("freqs",sliderXYZ("noise details", 0.5f,3,20));
        uniform(shaderPath).set("amps",sliderXYZ("noise speeds", 1, .8f, .6f));
        hotFilter(shaderPath, pg);
        resetGroup();
    }
}
