package _2020_06;

import applet.KrabApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;
import utils.OpenSimplexNoise;

public class GlowyParticles extends KrabApplet {
    private final OpenSimplexNoise noise = new OpenSimplexNoise();
    private PGraphics pg;


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
        frameRecordingDuration = 360*sliderInt("record frames");
        pg.beginDraw();
        drawPoints();
        pg.endDraw();
        rgbSplitScaleAndOffset(pg);
        image(pg, 0, 0, width, height);
        rec(pg);
        gui();
    }

    private float noise(PVector time, float x, float y, float mag, float freq) {
        return (float) (mag * noise.eval(time.x, time.y, x * freq, y * freq));
    }

    private void drawPoints() {
        float count = slider("count", 60);
        float size = slider("size", 1000);
        float baseWeight = slider("weight");
        PVector time = new PVector(cos(t), sin(t));
        fadeToBlack(pg);
        blurPass(pg);
        translateToCenter(pg);
        translate(pg, "move 0");
        preRotate(pg, "rot 0");
        PVector timeRotate = sliderXYZ("rot t");
        pg.rotateX(t * timeRotate.x);
        pg.rotateY(t * timeRotate.y);
        pg.rotateZ(t * timeRotate.z);
        translate(pg, "move 1");
        pg.strokeWeight(baseWeight);
        pointShader(pg);
        pg.beginShape(POINTS);

        for (int xi = 0; xi < count; xi++) {
            for(int yi = 0; yi < count; yi++){
                float x = map(xi, 0, count-1, -size, size);
                float y = map(yi, 0, count-1, -size, size);
                PVector move0 = sliderXY("move");
                float z = noise(time.copy().mult(slider("speed")),
                        x+t*move0.x, y+t*move0.y,
                        slider("mag"), slider("freq"));
                if (toggle("abs z")) {
                    z = -abs(z);
                }
                pg.vertex(x, y, z);
            }
        }
        pg.endShape();
        pg.resetShader();
    }

    private void pointShader(PGraphics pg) {
        pg.hint(PConstants.DISABLE_OPTIMIZED_STROKE);
        String pointFrag = "shaders/_2020_06/GlowyParticles/PointFrag.glsl";
        String pointVert = "shaders/_2020_06/GlowyParticles/PointVert.glsl";
        uniformRamp(pointFrag, pointVert, "point ramp", 5);
        hotShader(pointFrag, pointVert, pg);
    }
}
