package _2021_04;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PVector;
// https://en.wikipedia.org/wiki/Spirograph
public class Spirograph extends KrabApplet {
    private PGraphics pg;

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
        rec(pg, sliderInt("frames", 360));
        pg = updateGraphics(pg);
        pg.beginDraw();
        fadeToBlack(pg);
        updateSpirograph();
        pg.endDraw();
        image(pg, 0, 0);
        gui();
    }

    private void updateSpirograph() {
        float t = radians(frameCount);
        float pt = radians(frameCount-1);
        float armLength = slider("arm length", 100);
        float aLength = slider("a length",  20);
        float aSpeed = sliderInt("a speed", 8);

        float bLength = slider("b length",  10);
        float bSpeed = sliderInt("b speed", 16);

        float cLength = slider("c length",  5);
        float cSpeed = sliderInt("c speed", 24);

        translateToCenter(pg);
        pg.stroke(picker("stroke").clr());
        pg.strokeWeight(slider("weight", 1.99f));
        pg.noFill();
        pg.beginShape();
        float detail = slider("detail", 1000);
        for(int i = 0; i < detail; i++) {
            float norm = norm(i, 0, detail);

            PVector arm = new PVector();
            arm.add(armLength, 0, 0);
            arm.rotate(norm * TAU);

            PVector a = new PVector();
            a.add(aLength, 0, 0);
            a.rotate(norm * TAU * aSpeed);
            arm.add(a);



            pg.vertex(arm.x, arm.y);
        }
        pg.endShape(CLOSE);
    }

}
