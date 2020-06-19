package _2020_06;

import applet.KrabApplet;
import com.jogamp.opengl.math.Matrix4;
import com.jogamp.opengl.math.Quaternion;
import processing.core.PGraphics;
import processing.core.PMatrix3D;
import processing.core.PVector;

public class EllipseSphere extends KrabApplet {
    private PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(1000, 1000, P3D);
    }

    public void setup() {
        pg = createGraphics(width, height, P3D);
        if (width < displayWidth) {
            surface.setAlwaysOnTop(true);
            surface.setLocation(2560 - 1020, 20);
        }
    }

    public void draw() {
        pg.beginDraw();
        pg.image(gradient("background"), 0, 0);
        translateToCenter(pg);
        preRotate(pg);
        ellipseSphere();
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    private void ellipseSphere() {
        pg.noFill();
        pg.stroke(255);
        float r = slider("r", 220);
        float step = slider("step", 15);
        float rowAngularDiameter = angularDiameter(r, step);
        int rowCount = floor(PI / rowAngularDiameter);
        for (int i = 0; i < rowCount; i++) {
            //go from north pole to south pole
            float polarAngle = map(i, 0, rowCount - 1, -HALF_PI, HALF_PI); // wikipedia also calls this inclination
            // to find the radius at this latitude I simplify the sphere to a circle where the result is the x at this polar angle and radius
            float latRadius = r * cos(polarAngle);
            float columnAngularStep = angularDiameter(latRadius, step);
            int columnCount = max(1, floor(TWO_PI / columnAngularStep)); //at least 1 column should be shown at poles
            for (int j = 0; j <= columnCount; j++) {
                //go all the way around the sphere
                float columnAngle = map(j, 0, columnCount, 0, TWO_PI); // wikipedia also calls this azimuth
                pg.pushMatrix();
                pg.rotateY(columnAngle);
                pg.rotateZ(polarAngle);
                pg.translate(r, 0, 0);
                pg.rotateY(HALF_PI);
                pg.ellipse(0, 0, step, step);
                pg.popMatrix();
            }
        }
    }
}
