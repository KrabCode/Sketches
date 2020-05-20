package _2020_04;

import applet.KrabApplet;
import processing.core.PGraphics;

public class WaterRipple extends KrabApplet {

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }



    PGraphics pg;
    PGraphics a;
    PGraphics temp;

    public void settings() {
//        size(800, 800, P2D);
        fullScreen(P3D);
    }

    public void setup() {
        frameRecordingDuration *= 2;
//        surface.setAlwaysOnTop(true);
//        surface.setLocation(1920 - 820, 20);
        pg = createGraphics(width, height, P2D);
        pg.beginDraw();
        pg.background(0);
        pg.endDraw();
        a = createGraphics(width, height, P2D);
        a.beginDraw();
        a.background(0);
        a.endDraw();
        temp = createGraphics(width, height, P2D);
        temp.beginDraw();
        temp.background(0);
        temp.endDraw();
    }

    public void draw() {
        pg.beginDraw();
        updateShader();
        drawToPg();
        pg.endDraw();
        image(pg, 0, 0);
        paletteFilter(g);
        rec(g);
        updateBuffers();
        gui();
    }

    private void paletteFilter(PGraphics g) {
        String palette = "shaders/_2020_04/waterRipple/palette.glsl";
        uniformColorPalette(palette);
        hotFilter(palette, g);
    }

    private void updateShader() {
        String waterRipple = "shaders/_2020_04/waterRipple/waterRipple.glsl";
        uniform(waterRipple).set("damp", slider("damp", .99f));
        uniform(waterRipple).set("a", a);
        uniform(waterRipple).set("b", pg);
        uniform(waterRipple).set("time", t);
        hotFilter(waterRipple, pg);
    }

    private void drawToPg() {
        if(mousePressed) {
            pg.noStroke();
            pg.fill(255);
            float r = slider("r", 30);
            pg.ellipse(mouseX, mouseY, r, r);
        }
        drawRain();
        /*
        float baseR = slider("r", 10);
        int count = sliderInt("count", 10);
        float rAmp = slider("r amp", 1);
        float rFreq = slider("r freq", 1);
        pg.translate(width * .5f, height * .5f);
        for (int n = 0; n < 2; n++) {
            pg.rotate(n % 2 == 0 ? t : -t);
            pg.noStroke();
            pg.fill(255);
            for (int i = 0; i < count; i++) {
                float inorm = norm(i, 0, count - 1);
                float a = inorm * TAU;
                float r = baseR + rAmp * sin((n % 2 == 0 ? PI : 0) + t * rFreq);
                float size = slider("size");
                pg.ellipse(r * cos(a), r * sin(a), size, size);
            }
        }
*/
    }

    private void drawRain() {
        int frameSkip = constrain(sliderInt("frame skip", 1), 1, 100);
        if(frameCount % frameSkip == 0) {
            float rainRadius = lerp(slider("rain min r"), slider("rain max r"), random(1));
            for (int i = 0; i < sliderInt("count"); i++) {
                pg.ellipseMode(CENTER);
                pg.fill(255);
                pg.noStroke();
                pg.ellipse(random(width), random(height), rainRadius, rainRadius);
            }
        }
    }

    private void updateBuffers() {
        temp.beginDraw();
        temp.image(a, 0, 0);
        temp.endDraw();

        a.beginDraw();
        a.image(pg, 0, 0);
        a.endDraw();

        pg.beginDraw();
        pg.image(temp, 0, 0);
        pg.endDraw();
    }
}
