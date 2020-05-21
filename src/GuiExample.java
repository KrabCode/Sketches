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
        PVector translate = sliderXYZ("translate");
        PVector rotate = sliderXYZ("rotate");
        translate(width / 2f + translate.x, height / 2f + translate.y, translate.z);
        rotateX(rotate.x);
        rotateY(rotate.y);
        rotateZ(rotate.z);

        group("style");
        boolean noBackground = toggle("no background");
        int backgroundColor = picker("background").clr();
        if(!noBackground) {
            background(backgroundColor);
        }
        String style = options("stroke & fill", "no stroke", "no fill");
        fill(picker("fill").clr());
        stroke(picker("stroke").clr());
        strokeWeight(slider("stroke weight"));
        if (style.equals("no fill")) {
            noFill();
        } else if (style.equals("no stroke")) {
            noStroke();
        }
        PVector size = sliderXYZ("size", 200);
        box(size.x, size.y, size.z);

        gui();
        rec();
    }
}