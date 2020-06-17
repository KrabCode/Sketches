package _2020_06;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import utils.OpenSimplexNoise;

public class Fireball extends KrabApplet {
    private PGraphics pg;
    private final OpenSimplexNoise noise = new OpenSimplexNoise();

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
        frameRecordingDuration = sliderInt("record frames");
        pg.beginDraw();
        ramp(pg);
        fadeToBlack(pg);
        blurPass(pg);
        translateToCenter(pg);
        pg.pushMatrix();
        translate(pg);
        preRotate(pg);
        PVector axis = sliderXYZ("axis");
        pg.rotate(slider("speed") * t, axis.x, axis.y, axis.z);
        pointShader();
        spiralSphereWithNoise(pg);
        pg.popMatrix();
        bepis();
        pg.endDraw();
        rgbSplitScaleAndOffset(pg);
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    private void bepis() {
        group("text");
        String text = textInput("value", "bepis");
        pg.textSize(slider("font size", 64));
        pg.textAlign(CENTER, CENTER);
        pg.fill(picker("fill").clr());
        translateToCenter(pg);
        translate(pg, "pos");
        pg.text(text, 0, 0);
    }

    private void pointShader() {
        hint(DISABLE_OPTIMIZED_STROKE);
        String frag = "shaders/_2020_06/Fireball/PointFrag.glsl";
        String vert = "shaders/_2020_06/Fireball/PointVert.glsl";
        uniformRamp(frag, vert, "depth ramp", 4);
        hotShader(frag, vert, pg);
    }

    private float noise(float time, float x, float y, float mag, float freq) {
        return (float) (mag * noise.eval( x * freq, y * freq,  time));
    }

    protected void spiralSphereWithNoise(PGraphics pg) {
        group("sphere");
//        pg.beginShape(POINTS);
        PVector noiseSpeed = sliderXYZ("n speed").copy().mult(t);

        pg.stroke(picker("stroke").clr());
        pg.strokeWeight(slider("weight", 5));
        pg.noFill();
        float N = slider("count", 3000);
        float s = 3.6f / sqrt(N);
        float dz = 2.0f / N;
        float lon = 0;
        float scl = slider("scale", 260);
        float z = (1 - dz / 2);
        for (int k = 0; k < N; k++) {
            float x = cos(lon)* scl;
            float y = sin(lon)* scl;
            float r = sqrt(1 - z * z);
            float time = t*slider("time");
            float mag = slider("mag");
            float freq = slider("freq");
            float noiseOffset = slider("noise offset");
            float myX = x + noise(time, z, lon, mag, freq);
            float myY = y + noise(time, z-noiseOffset, lon-noiseOffset, mag, freq);
            float myZ = z + noise(time, z+noiseOffset, lon+noiseOffset, mag, freq);
            pg.point(myX * r , myY * r , myZ*scl);
            z = z - dz;
            lon = lon + s / r;
        }
//        pg.endShape();
        pg.noStroke();
        if (!toggle("hollow")) {
            pg.fill(0);
            pg.sphereDetail(floor(slider("core detail", 20)));
            pg.sphere(slider("scale") - slider("core size", 5));
        }
        resetGroup();
    }
}
