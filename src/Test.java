import processing.core.PApplet;

public class Test extends PApplet {

    int radius = 300;

    public static void main(String[] passedArgs) {
        PApplet.main("Test");
    }

    public void settings() {                            //!
        size(1000, 1000, P3D);
    }

    public void draw() {
        background(150, 150, 150);
        translate(width/2, height/2);
        float t = radians(frameCount);
        rotateX(t);
        rotateY(t*.5f);
        rotateZ(t*.25f);
        box(radius, radius*.25f, radius*.5f);
    }
}