import applet.KrabApplet;
import processing.core.PVector;

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

        colorMode(HSB,1,1,1,1);
        HSBA myColor = picker("my color");
        float hue = myColor.hue();
        float sat = myColor.sat();
        float br = myColor.br();
        float a = myColor.alpha();
        stroke(hue, sat, br, a);

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