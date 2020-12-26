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
        fullScreen(P3D);
    }

    public void setup() {
        toggleFullscreen();
    }

    public void draw() {
        updateFluid();
        displayFluid();
        clear();
        image(pg, 0, 0);
        rec(pg, sliderInt("frames", 360));
        gui(false);
    }

    private void displayFluid() {
        pg = updateGraphics(pg, P3D);
        pg.beginDraw();
        String displayShader = "shaders/_2020_12/fluidDisplay.glsl";
        uniform(displayShader).set("fluid", fluid);
        hotFilter(displayShader, pg);
        pg.endDraw();
    }

    private void updateFluid() {
        fluid = updateGraphics(fluid, P3D);
        fluid.beginDraw();
        String fluidShader = "shaders/_2020_12/fluid.glsl";
        uniform(fluidShader).set("time", t);
        uniform(fluidShader).set("mouse", (float)mouseX, (float)mouseY);
        hotFilter(fluidShader, fluid);
        fluid.endDraw();
    }


}
