package readme;

import applet.KrabApplet;
import processing.core.PImage;
import processing.core.PVector;

// this class is referenced from the GUI Manual in readme

public class GuiExample extends KrabApplet {

    boolean recordableCursorMode = false;
    private PImage cursorImage;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(1000, 1000, P3D);
    }

    public void setup() {
        rectMode(CENTER);
        framesToRecord = 100000;
        if (recordableCursorMode) {
            cursorImage = loadImage("images/cursor/cursor.png");
        }
    }

    public void draw() {
        group("transform");
        pushMatrix();
        String projection = options("perspective", "orthographic");
        if (projection.equals("perspective")) {
            perspective();
        } else if (projection.equals("orthographic")) {
            ortho();
        }
        image(gradient("background"), 0, 0);
        hint(DISABLE_DEPTH_TEST);
        PVector translate = sliderXYZ("translate");
        PVector rotate = sliderXYZ("rotate");
        translate(width / 2f + translate.x, height / 2f + translate.y, translate.z);
        rotateX(rotate.x);
        rotateY(rotate.y);
        rotateZ(rotate.z);
        group("style");
        fill(picker("fill").clr());
        stroke(picker("stroke").clr());
        strokeWeight(slider("stroke weight"));
        if (toggle("no fill")) {
            noFill();
        }
        if (toggle("no stroke")) {
            noStroke();
        }
        PVector size = sliderXYZ("size", 200);
        box(size.x, size.y, size.z);
        popMatrix();

        group("text");
        PVector textPos = sliderXY("translate");
        fill(picker("fill").clr());
        textSize(slider("size", 64));
        text(textInput("main text"), textPos.x, textPos.y);

        gui();

        if (recordableCursorMode) {
            noCursor();
            imageMode(CORNER);
            image(cursorImage, mouseX, mouseY, 15.16f * 2, 24 * 2);
        }

        rec();
    }
}