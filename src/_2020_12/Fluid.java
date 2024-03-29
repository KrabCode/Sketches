package _2020_12;

import applet.KrabApplet;
import processing.core.PGraphics;

public class Fluid extends KrabApplet {
    private PGraphics pg;
    private PGraphics fluid;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(800,800, P2D);
    }

    public void setup() {
        pg = createGraphics(1600,1600,P2D);
        fluid = createGraphics(1600,1600,P2D);
    }

    public void draw() {
        updateFluid();
        displayFluid();
        clear();
        image(pg, 0, 0, width, height);
        rec(pg, sliderInt("frames", 360));
        gui(false);
    }

    private void displayFluid() {
        pg.beginDraw();
        String displayShader = "shaders/_2020_12/fluidDisplay.glsl";
        uniform(displayShader).set("fluid", fluid);
        hotFilter(displayShader, pg);
        pg.endDraw();
    }

    private void updateFluid() {
        fluid.beginDraw();
        String fluidShader = "shaders/_2020_12/fluid.glsl";
        uniform(fluidShader).set("time", t);
        uniform(fluidShader).set("mouse", (float) mouseX * 2, (float) (1600 - mouseY * 2), mousePressed ? 1f : 0f);
        hotFilter(fluidShader, fluid);
        fluid.endDraw();
    }


}
