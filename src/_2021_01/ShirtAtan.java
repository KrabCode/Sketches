package _2021_01;

import applet.KrabApplet;
import processing.core.PFont;
import processing.core.PGraphics;

public class ShirtAtan extends KrabApplet {
    private PGraphics pg;
    private PGraphics fg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    int finalWidth = 3600;
    int finalHeight = 4600;
    float lastTextSize = 0;
    String shaderPath = "shaders/_2021_01/atanGradient.glsl";
    String shaderCode;


    public void settings() {
        size(floor(finalWidth / 3.5f), floor(finalHeight / 3.5f), P2D);
    }

    public void setup() {

    }

    public void draw() {
        pg = updateGraphics(pg, finalWidth, finalWidth);
        fg = updateGraphics(fg, finalWidth, finalHeight);
        pg.beginDraw();
        updateShader();
        pg.endDraw();
        fg.beginDraw();
        fg.background(picker("background").clr());
        fg.image(pg, 0, 0, finalWidth, finalWidth);
        updateForeground();
        fg.endDraw();
        image(fg, 0, 0, width, height);
        rec(fg);
        gui();
    }

    private void updateShader() {
        uniform(shaderPath).set("time", t);
        uniform(shaderPath).set("gradient", gradient("gradient"));
        hotFilter(shaderPath, pg);
    }

    private void updateForeground() {
        group("code");
        translate2D(fg, "pos");
        lazyLoadFont();
        fg.textAlign(LEFT, CENTER);
        fg.fill(picker("text color").clr());
        fg.text(lazyGetShaderCode(), 0, 0);
        resetGroup();
    }

    private void lazyLoadFont() {
        float textSize = slider("font size", 40);
        if(lastTextSize != textSize) {
            fg.textFont(createFont("fonts/RobotoMono-Medium.ttf", textSize));
        }
        lastTextSize = textSize;
    }

    String lazyGetShaderCode() {
        if (shaderCode != null) {
            return shaderCode;
        }
        String[] code = loadStrings(shaderPath);
        StringBuilder concat = new StringBuilder();
        for (String line : code) {
            concat.append(line);
            concat.append("\n");
        }
        shaderCode = concat.toString();
        return shaderCode;
    }
}
