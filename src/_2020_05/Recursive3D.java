package _2020_05;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class Recursive3D extends KrabApplet {
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
        pg.colorMode(HSB, 1, 1, 1, 1);
        ramp(pg);
        translateToCenter(pg);
        translate(pg);
        preRotate(pg);
        lights(pg);
        drawRecursiveShapeStart();
        for (int i = 0; i < sliderInt("shape count"); i++) {
            preRotate(pg, "rotation between");
            drawRecursiveShapeStart();
        }
        sliderXYZ("rotate Δ").add(sliderXYZ("rotate ΔΔ"));
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    private void drawRecursiveShapeStart() {
        group("recursive");
        if (toggle("skip")) {
            return;
        }
        pg.pushMatrix();
        pg.noStroke();
        drawRecursiveShape(
                sliderXYZ("orig translate").copy(),
                sliderXYZ("orig rotate").copy(),
                sliderXYZ("orig size", 1000, 50, 50).copy(),
                0);
        pg.popMatrix();
    }

    private void drawRecursiveShape(PVector translate, PVector rotate, PVector size, int iteration) {
        float minSize = slider("min size", 10);
        if (size.x < minSize || size.y < minSize || size.z < minSize) {
            return;
        }

        pg.translate(translate.x, translate.y, translate.z);
        pg.rotateX(rotate.x);
        pg.rotateY(rotate.y);
        pg.rotateZ(rotate.z);
        if (!size.equals(sliderXYZ("orig size")) && sliderInt("skip iters", 1) < iteration) {
            HSBA baseColor = picker("fill");
            float dHue = slider("d hue");
            float dSat = slider("d sat");
            float dBr = slider("d br");
            int finalColor = pg.color(hueModulo(baseColor.hue() + dHue * (iteration+sliderInt("hueOffset"))),
                    constrain(baseColor.sat() + dSat * (iteration+sliderInt("satOffset")), 0, 1),
                    constrain(baseColor.br() + dBr * (iteration+sliderInt("brOffset")), 0, 1),
                    baseColor.alpha());
            pg.fill(finalColor);
            pg.ambient(finalColor);
            pg.box(size.x, size.y, size.z);
        }
        PVector sizeDelta = sliderXYZ("3D shrink Δ", -10);
        if (toggle("same shrink", true)) {
            float uniformSizeDelta = slider("1D shrink Δ", -10);
            sizeDelta.x = uniformSizeDelta;
            sizeDelta.y = uniformSizeDelta;
            sizeDelta.z = uniformSizeDelta;
        }
        sizeDelta.x = min(sizeDelta.x, -.1f);
        sizeDelta.y = min(sizeDelta.y, -.1f);
        sizeDelta.z = min(sizeDelta.z, -.1f);
        drawRecursiveShape(sliderXYZ("translate Δ"), sliderXYZ("rotate Δ"), size.add(sizeDelta), ++iteration);
    }

}
