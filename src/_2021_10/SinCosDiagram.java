package _2021_10;

import applet.KrabApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.event.MouseEvent;

import java.util.ArrayList;
import java.util.Date;

public class SinCosDiagram extends KrabApplet {

    PGraphics pg;

    int sketchWidth = 600;
    int sketchHeight = 600;
    int rulerWidth = 400;
    int rulerHeightOuter = 130;
    float rulerHeightInner = 130;

    float waveHeight = 200;
    float waveHeightHalf = waveHeight / 2;

    int xLeft = sketchWidth / 2 - rulerWidth / 2;
    int xRight = sketchWidth / 2 + rulerWidth / 2;

    float xMid = sketchWidth / 2f;
    float yMid = sketchHeight / 2f;
    float xMidLeft = lerp(xLeft, xRight, 0.25f);
    float xMidRight = lerp(xLeft, xRight, 0.75f);

    ArrayList<PFont> fonts = new ArrayList<PFont>();
    int currentFontIndex = 189;

    float textSize = 32;
    float xWaveTextOffset = -24;
    float yWaveTextOffset = -10;
    float yRulerTextOffset = -80;

    float t;

    int bgColor = 12;
    int rulerColor = 120;
    int rulerTextColor = 255;
    int pieGraphFillColor = 120;
    int pieGraphOutlineColor = 255;
    int waveColor = 255;

    public static void main(String[] args) {
        main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        fullScreen(P2D);
        smooth(16);
    }

    public void setup() {
        toggleFullscreen();
        loadAvailableFonts();
        background(10);
        pg.noFill();
        render();
    }

    public void draw() {
        render();
    }

    void render() {
        updateSliders();

        pg = updateGraphics(pg);
        pg.colorMode(RGB,255,255,255,100);
        pg.beginDraw();
        pg.strokeCap(SQUARE);
        pg.textSize(textSize);
        pg.background(bgColor);
        pg.pushMatrix();
            translateToCenter(pg);
            translate(pg, "pos");

            pg.stroke(rulerColor);
            pg.strokeWeight(slider("ruler inner weight", 2));
            drawRuler();

            pg.strokeWeight(slider("waveWeight", 1.99f));
            pg.noFill();
            pg.stroke(waveColor);
            drawWaves();

            // hide the wave line overlap on the sides
            pg.stroke(bgColor);
            pg.strokeWeight(slider("overlap weight", 2.5f));
            drawRulerVerticalOuter();

            pg.stroke(rulerColor);
            pg.strokeWeight(slider("ruler outer weight", 2));
            drawRulerVerticalOuter();

            pg.noFill();
            pg.stroke(rulerColor);
            rulerPieGraphs();

            pg.fill(rulerTextColor);
            pg.noStroke();
            rulerText();
        pg.popMatrix();

        if (toggle("cross")) {

            pg.strokeWeight(3);
            pg.stroke(255,0,0);
            pg.line(height / 2f, 0, height / 2f, width);
            pg.stroke(0,0,255);
            pg.line(0, width / 2f, height, width / 2f);
        }

        pg.endDraw();
        image(pg, 0, 0);
        gui();
        rec(pg);
    }

    private void updateSliders() {
        textSize = slider("text size", 32);
        xWaveTextOffset = slider("xWaveTextOffset", -24);
        yWaveTextOffset = slider("yWaveTextOffset", -10);
        yRulerTextOffset = slider("yRulerTextOffset", -80);

        rulerColor = picker("rulerColor", 120 / 255f).clr();
        rulerTextColor = picker("rulerTextColor", 1).clr();
        pieGraphFillColor = picker("pieGraphFillColor", 120 / 255f).clr();
        pieGraphOutlineColor = picker("pieGraphOutlineColor", 1).clr();
        waveColor = picker("wave color", 1).clr();
    }

    void drawRuler() {
        drawHorizontalLine();
        drawRulerVerticalOuter();
        drawRulerVerticalInner();
    }

    void drawHorizontalLine() {
        pg.line(xLeft, yMid, xRight, yMid);
    }

    void drawRulerVerticalOuter() {
        pg.pushMatrix();
        pg.translate(0, yMid);
        float n = rulerHeightOuter;
        pg.line(xLeft, n, xLeft, -n);
        pg.line(xRight, n, xRight, -n);
        pg.popMatrix();
    }

    void drawRulerVerticalInner() {
        pg.pushMatrix();
        pg.translate(0, yMid);
        float n = rulerHeightInner;
        pg.line(xMidLeft, n, xMidLeft, -n);
        pg.line(xMid, n, xMid, -n);
        pg.line(xMidRight, n, xMidRight, -n);
        pg.popMatrix();
    }

    void rulerText() {
        pg.pushMatrix();
        pg.translate(0, yMid);
        pg.textAlign(CENTER, CENTER);
        //pg.text("", xLeft, yRulerTextOffset - rulerHeightOuter);
        //pg.text("1/4τ", xMidLeft, yRulerTextOffset - rulerHeightInner);
        pg.pushMatrix();
        translate(pg, "pi text pos");
        pg.text("π", xMid, yRulerTextOffset - rulerHeightInner);
        pg.popMatrix();
        //pg.text("3/4τ", xMidRight, yRulerTextOffset - rulerHeightInner); // trailing spaces to center the slash
        pg.pushMatrix();
        translate(pg, "tau text pos");
        pg.text("τ", xRight, yRulerTextOffset - rulerHeightOuter);
        pg.popMatrix();
        pg.popMatrix();
    }

    void rulerPieGraphs() {
        float diameter = 40;
        pg.pushMatrix();
        pg.translate(0, yMid); // align to the text
        drawRulerPieGraph(0, xLeft, yRulerTextOffset - rulerHeightInner, diameter);
        drawRulerPieGraph(0.25f, xMidLeft, yRulerTextOffset - rulerHeightInner, diameter);
        drawRulerPieGraph(0.5f, xMid, yRulerTextOffset - rulerHeightInner, diameter);
        drawRulerPieGraph(0.75f, xMidRight, yRulerTextOffset - rulerHeightInner, diameter);
        drawRulerPieGraph(1, xRight, yRulerTextOffset - rulerHeightOuter, diameter);
        pg.popMatrix();
    }

    void drawRulerPieGraph(float maxNorm, float x, float y, float size) {
        pg.pushMatrix();
        pg.translate(x, y);
        pg.noStroke();
        pg.fill(pieGraphFillColor);
        pg.arc(0, 0, size, size, 0, maxNorm * TAU);
        pg.stroke(pieGraphOutlineColor);
        pg.noFill();
        pg.ellipse(0, 0, size, size);
        pg.popMatrix();
    }

    void drawWaves() {
        for (int i = 0; i < 2; i++) {
            drawWave(i == 0);
        }
        pg.textAlign(RIGHT, CENTER);
        pg.text("sin", xLeft + xWaveTextOffset, yMid + yWaveTextOffset);
        pg.text("cos", xLeft + xWaveTextOffset, yMid - waveHeightHalf + yWaveTextOffset);
    }

    void drawWave(boolean sineWave) {
        pg.beginShape();
        for (int x = xLeft; x <= xRight; x++) {
            float xNorm = norm(x, xLeft, xRight) + t;
            float waveNorm = (sineWave ? sin(TAU * xNorm) : cos(TAU * xNorm));
            waveNorm *= -1; // because negative Y is up in processing
            float y = yMid + waveHeightHalf * waveNorm;
            pg.vertex(x, y);
        }
        pg.endShape();
    }

    void setCurrentFont() {
        PFont font = fonts.get(currentFontIndex);
        if (pg == null) {
            pg = updateGraphics(pg);
        }
        pg.beginDraw();
        pg.textFont(font);
        pg.endDraw();
        println("Font set to " + currentFontIndex + " " + font.getName());
    }

    void loadAvailableFonts() {
        String[] fontNames = PFont.list();
        for (String fontName : fontNames) {
            fonts.add(createFont(fontName, textSize));
            println(fonts.size() - 1 + " " + fontName);
        }
        println("\nFonts loaded\nChange font using the mouse wheel\n");
        setCurrentFont();
    }


    public void mouseWheel(MouseEvent event) {
        super.mouseWheel(event);
        float e = event.getCount();
        if (e > 0) {
            currentFontIndex -= 1;
        } else if (e < 0) {
            currentFontIndex += 1;
        }
        if (currentFontIndex < 0) {
            currentFontIndex = fonts.size() - 1;
        }
        currentFontIndex %= fonts.size();
        setCurrentFont();
    }
}
