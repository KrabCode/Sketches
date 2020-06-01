package _2020_06;

import applet.KrabApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;
import utils.OpenSimplexNoise;

public class NegativeSpace extends KrabApplet {
    private PGraphics pg;
    private OpenSimplexNoise noise = new OpenSimplexNoise();



    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(1000, 1000, P3D);
    }

    public void setup() {
        if (width < displayWidth) {
            surface.setAlwaysOnTop(true);
        }
        pg = createGraphics(width, height, P3D);
    }

    public void draw() {
        pg.beginDraw();
        drawPoints();
        pg.endDraw();
        colorSplit(pg, true);
        image(pg, 0, 0, width, height);
        rec(pg);
        gui();
    }

    private void drawPoints() {
        float particleCount = slider("count");
        float baseWeight = slider("weight");
        PVector freq = sliderXY("freq", 0.1f);
        PVector zFreq = sliderXY("z freq", 0.1f);
        PVector mag = sliderXYZ("mag", 1000, 1000, 50);
        PVector timeRadiusXY = sliderXY("xy speed");
        PVector timeRadiusZ = sliderXY("z speed");
        PVector xyTime = new PVector(timeRadiusXY.x * cos(t), timeRadiusXY.y * sin(t));
        PVector zTime = new PVector(timeRadiusZ.x * cos(t),timeRadiusZ.y * sin(t));
        fadeToBlack(pg);
        blurPass(pg);
        translateToCenter(pg);
        translate(pg, "move 0");
        preRotate(pg, "rot 0");
        PVector timeRotate = sliderXYZ("rot t");
        pg.rotateX(t*timeRotate.x);
        pg.rotateY(t*timeRotate.y);
        pg.rotateZ(t*timeRotate.z);
        translate(pg, "move 1");
        pg.stroke(picker("stroke").clr());
        pg.strokeWeight(baseWeight);
        pointShader(pg);
        pg.beginShape(POINTS);
        for (int i = 0; i < particleCount; i++) {
            float x = (float) (mag.x * noise.eval(xyTime.x, xyTime.y, i * freq.x));
            float y = (float) (mag.y * noise.eval(xyTime.x, xyTime.y, i * freq.y + 12.2345));
            float z = (float) (mag.z * noise.eval(zTime.x, zTime.y, x*zFreq.x, y*zFreq.y));
            if(toggle("abs z")) {
                z = -abs(z);
            }
            pg.vertex(x, y, z);
        }
        pg.endShape();
        pg.resetShader();
    }

    private void pointShader(PGraphics pg) {
        pg.hint(PConstants.DISABLE_OPTIMIZED_STROKE);
        String pointFrag = "shaders/_2020_06/NegativeSpace/PointFrag.glsl";
        String pointVert = "shaders/_2020_06/NegativeSpace/PointVert.glsl";
        uniformRamp(pointFrag, pointVert, "point ramp", 5);
        hotShader(pointFrag, pointVert, pg);
    }
}
