package _2021_01;

import applet.KrabApplet;
import processing.core.PFont;
import processing.core.PGraphics;

public class ShirtAtan extends KrabApplet {
    private PGraphics pg;
    private PGraphics fg;
    private PFont helvetica;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        fullScreen(P3D);
    }

    public void setup() {
        toggleFullscreen();
        helvetica = createFont("fonts/minion-pro/MinionPro-Bold.otf", 40, true);

    }

    public void draw() {
        fg = updateGraphics(pg);
        updateForeground();
        pg = updateGraphics(pg);
        pg.beginDraw();

        updateShader();
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    private void updateShader() {
        String path = "shaders/_2021_01/atanGradient.glsl";
        uniform(path).set("fg", fg);
        uniform(path).set("gradient", gradient("gradient"));
        hotFilter(path, pg);
    }

    private void updateForeground() {
        fg.beginDraw();
        fg.clear();
        group("symbol");
        fg.stroke(255);
        fg.noFill();
        float diameter = slider("size", 50);
        translateToCenter(fg);
        translate2D(fg, "pos");
//        fg.ellipse(0, 0, diameter, diameter);

        float radius = diameter / 2f;
//        float bigSpokeInner = slider("big spoke in", 1) * radius;
//        float bigSpokeOuter = slider("big spoke out", 0.1f)* radius;
//        float smallSpoke = slider("small spoke", 0.2f) * radius;
        float textOffset = slider("text offset", 1.5f) * radius;

        fg.textFont(helvetica, slider("font size", 40));
        fg.textAlign(CENTER, CENTER);
        int count = 8;
        for(int i = 0; i < count; i++) {
            float rad = map(i, 0, count, 0, TAU);
//            boolean isBig =  i % 2 == 0;
//            float outerRadius = radius + (isBig ? bigSpokeOuter : smallSpoke);
//            float innerRadius = radius - (isBig ? bigSpokeInner : smallSpoke);
//            fg.line(innerRadius * cos(rad), innerRadius * sin(rad),
//                    outerRadius * cos(rad),outerRadius * sin(rad));
            String text = getText(i);
            fg.text(text, textOffset*cos(rad), textOffset * sin(rad));
        }

        fg.endDraw();
        resetGroup();
    }

    private String getText(int i) {
        switch(i) {
            case 0: return "0\nπ * 2";
            case 1: return "";
            case 2: return "π * 1.5";
            case 3: return "";
            case 4: return "π";
            case 5: return "";
            case 6: return "π * 0.5";
            case 7: return "";
            default:
                throw new IllegalStateException("Unexpected value: " + i);
        }
    }
}
