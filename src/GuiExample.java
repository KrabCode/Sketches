import applet.KrabApplet;
import processing.core.PVector;

// this class is referenced from the GUI Manual in readme

public class GuiExample extends KrabApplet {

    public static void main(String[] passedArgs) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(1000, 1000, P3D);
    }

    public void setup() {
        rectMode(CENTER);
        surface.setAlwaysOnTop(true); //remove me

    }

    public void draw() {
        group("console");
        if (button("hello world")) {
            println("Hello, world!");
        }

        group("transform");
        String projection = options("perspective", "orthographic");
        if (projection.equals("perspective")) {
            perspective();
        } else if (projection.equals("orthographic")) {
            ortho();
        }
        PVector translate = sliderXYZ("translate");
        PVector rotate = sliderXYZ("rotate");
        translate(width / 2f + translate.x, height / 2f + translate.y, translate.z);
        rotateX(rotate.x);
        rotateY(rotate.y);
        rotateZ(rotate.z);

        group("style");
        background(picker("background").clr());
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

        gui();
        rec();
    }
}