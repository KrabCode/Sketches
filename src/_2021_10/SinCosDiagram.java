package _2021_10;

import applet.KrabApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PVector;

import java.util.ArrayList;

public class SinCosDiagram extends KrabApplet {

    PGraphics pg;

    ArrayList<PFont> fonts = new ArrayList<PFont>();
    int currentFontIndex = 189;
    int lastFontIndex = currentFontIndex;

    float textSize = 32;
    float xWaveTextOffset = -24;
    float yWaveTextOffset = -10;
    float yRulerTextOffset = -80;

    float sketchWidth, sketchHeight,
            rulerWidth, rulerHeightOuter, rulerHeightInner,
            waveHeight, waveHeightHalf,
            xLeft, xRight, xMid, yMid, xMidLeft, xMidRight;
    float t;

    int bgColor = 12;
    int rulerColor = 120;
    int rulerTextColor = 255;
    int pieGraphFillColor = 120;
    int pieGraphOutlineColor = 255;
    int waveColor = 255;
    float pieGraphDiameter = 40;

    ArrayList<PVector> line = new ArrayList<PVector>();

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
    }

    public void draw() {
        render();
    }

    void render() {
        updateFont();
        updateSliders();
        updateSizes();

        pg = updateGraphics(pg);
        pg.colorMode(RGB, 255, 255, 255, 100);
        pg.beginDraw();
        pg.strokeCap(SQUARE);
        pg.textSize(textSize);
        pg.background(bgColor);
        pg.pushMatrix();
//        pg.translate(width / 2f - rulerWidth / 2, height / 2f - rulerHeightOuter);
        translate(pg, "pos");

        pg.stroke(rulerColor);
        pg.strokeWeight(slider("ruler inner weight", 2));
        drawRuler();

        float waveWeight = slider("waveWeight", 1.99f);
        pg.strokeWeight(waveWeight);
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

//        drawManualLine(waveWeight);

        pg.endDraw();
        clear();
        image(pg, 0, 0);
        gui();
        rec(pg);
        if (toggle("cross")) {
            strokeWeight(3);
            stroke(255, 0, 0);
            line(0, height / 2f, width, height / 2f);
            stroke(0, 0, 255);
            line(width / 2f, 0, width / 2f, height);
        }
    }

    private void drawManualLine(float waveWeight) {

        if (button("line.clear()")) {
            line.clear();
        }
        if (mousePressedOutsideGui) {
            line.add(new PVector(mouseX, mouseY));
        }
        if (toggle("draw line")) {

            pg.beginShape();
            pg.noFill();
            pg.strokeWeight(waveWeight);
            pg.stroke(waveColor);
            for (PVector pVector : line) {
                pg.vertex(pVector.x, pVector.y);
            }
            pg.endShape();

        }
    }

    private void updateFont() {
        currentFontIndex = sliderInt("font index", 189, 0, fonts.size() - 1);
        if (currentFontIndex != lastFontIndex) {
            setCurrentFont();
            lastFontIndex = currentFontIndex;
        }
    }

    private void updateSizes() {
        group("sizes");
        sketchWidth = width;
        sketchHeight = height;
        rulerWidth = slider("width", 400);
        rulerHeightOuter = 130;
        rulerHeightInner = 130;
        waveHeight = slider("height", 200);
        waveHeightHalf = waveHeight / 2;
        xLeft = sketchWidth / 2 - rulerWidth / 2;
        xRight = sketchWidth / 2 + rulerWidth / 2;
        xMid = sketchWidth / 2f;
        yMid = sketchHeight / 2f;
        xMidLeft = lerp(xLeft, xRight, 0.25f);
        xMidRight = lerp(xLeft, xRight, 0.75f);
        textSize = slider("text size", 32);
        pieGraphDiameter = slider("pie graph size", 40);
        resetGroup();
    }

    private void updateSliders() {
        group("offsets");
        xWaveTextOffset = slider("xWaveTextOffset", -24);
        yWaveTextOffset = slider("yWaveTextOffset", -10);
        yRulerTextOffset = slider("yRulerTextOffset", -80);

        group("colors");
        rulerColor = picker("rulerColor", 120 / 255f).clr();
        rulerTextColor = picker("rulerTextColor", 1).clr();
        pieGraphFillColor = picker("pieGraphFillColor", 120 / 255f).clr();
        pieGraphOutlineColor = picker("pieGraphOutlineColor", 1).clr();
        waveColor = picker("wave color", 1).clr();
        resetGroup();
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
        pg.pushMatrix();
        translate(pg, "zero text pos");
        pg.text("0", xLeft, yRulerTextOffset - rulerHeightOuter);
        pg.popMatrix();
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
        pg.pushMatrix();
        pg.translate(0, yMid); // align to the text
        drawRulerPieGraph(0, xLeft, yRulerTextOffset - rulerHeightInner, pieGraphDiameter);
        drawRulerPieGraph(0.25f, xMidLeft, yRulerTextOffset - rulerHeightInner, pieGraphDiameter);
        drawRulerPieGraph(0.5f, xMid, yRulerTextOffset - rulerHeightInner, pieGraphDiameter);
        drawRulerPieGraph(0.75f, xMidRight, yRulerTextOffset - rulerHeightInner, pieGraphDiameter);
        drawRulerPieGraph(1, xRight, yRulerTextOffset - rulerHeightOuter, pieGraphDiameter);
        pg.popMatrix();
    }

    void drawRulerPieGraph(float maxNorm, float x, float y, float size) {
        pg.pushMatrix();
        pg.translate(x, y);
        pg.fill(pieGraphFillColor);
        pg.stroke(pieGraphOutlineColor);
        if(maxNorm > 0 && maxNorm < 1){
            float n = size / 2;
            int detail = 100;
            pg.beginShape();
            pg.vertex(0,0);
            for (int i = 0; i < detail; i++) {
                float iNorm = norm(i, 0, detail-1);
                if(iNorm >= maxNorm ){
                    break;
                }
                float angle = -iNorm * TAU;
                pg.vertex(n * cos(angle), n * sin(angle));
            }
            pg.endShape(CLOSE);
        }else if(maxNorm >= 1f){
            pg.ellipse(0,0,size,size);
        }
//        pg.arc(0, 0, size, size, -maxNorm * TAU, 0);
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
        for (float x = xLeft; x <= xRight; x++) {
            float xNorm = norm(x, xLeft, xRight) + t;
            float waveNorm = (sineWave ? sin(TAU * xNorm) : cos(TAU * xNorm));
            waveNorm *= -1; // because negative Y is up in processing
            float y = yMid + waveHeightHalf * waveNorm;
            if (sineWave && xNorm > slider("x end", 1)) {
                break;
            }
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
}
