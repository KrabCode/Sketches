package _2020_03;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import utils.OpenSimplexNoise;

/**
 * Created by Jakub 'Krab' Rak on 2020-03-25
 */
public class Handdrawn extends KrabApplet {
    OpenSimplexNoise noise = new OpenSimplexNoise();
    private PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(String.valueOf(new Object() {}.getClass().getEnclosingClass().getName()));
    }

    public void settings() {
        size(800, 800, P3D);
    }

    public void setup() {
        surface.setAlwaysOnTop(true);
        pg = createGraphics(width, height, P3D);
        pg.smooth(32);
        pg.beginDraw();
        pg.background(0);
        pg.textSize(256);
        pg.endDraw();
//        frameRecordingDuration *= 3;
    }

    public void draw() {
        pg.beginDraw();
        group("shaders");
        alphaFade(pg);
//        splitPass(pg);
        blurPass(pg);
        pg.translate(width * .5f, height * .5f);
//        rotateWithMouse(pg);
        PVector rotate = sliderXYZ("rotate");
        pg.rotateX(rotate.x);
        pg.rotateY(rotate.y);
        pg.rotateZ(rotate.z);
        drawPolarOrnament();
//        drawRect();
        //        drawGrid();
//        drawRays();
//        darkMask();
//        drawCircles();
        pg.endDraw();
        background(0);
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    private void drawPolarOrnament() {
        int levelCount = sliderInt("level count", 10);
        String[] types = new String[]{"dots", "sinewave", "rects", "triangles", /*"letters"*/};
        for (int level = 0; level < levelCount; level++) {
            float levelNorm = clampNorm(level, 0, levelCount - 1);
            float minRadius = slider("minRadius", 50);
            float maxRadius = slider("maxRadius", 400);
            float r = lerp(minRadius, maxRadius, levelNorm);
            float rNorm = norm(r,minRadius,maxRadius);
            float random = randomDeterministic(level);
            float timeSpeed = slider("time speed");
            float rNoiseInput = r * slider("r noise freq");
            float noiseValue = (float) (.5f+.5*noise.eval(
                    rNoiseInput + timeSpeed*cos(t),
                    rNoiseInput + timeSpeed*sin(t)));
            float noiseOffset = (1 - 2 * noiseValue) * slider("max offset", 1);
            String type = types[floor(random * types.length)];
            pg.strokeWeight(slider("weight"));
            int clr = lerpColor(picker("r0 stroke").clr(), picker("r1 stroke").clr(), rNorm);
            pg.stroke(clr);
            pg.fill(clr);
            int pixelCircleCircumference = ceil(TAU / angularDiameter(r, 1));
            float elementSize = sliderInt("size", 10) + rNorm*(slider("r affects size"));
            elementSize = max(elementSize, .001f);
            float angularElementSize = angularDiameter(r, elementSize);
            int elementCount = floor(TAU / angularElementSize);
            if (type.equals("sinewave")) {
                pg.noFill();
                pg.beginShape();
                for (int i = 0; i < pixelCircleCircumference; i++) {
                    float inorm = clampNorm(i, 0, pixelCircleCircumference - 1);
                    float theta = inorm * TAU;
                    float sineRadius = r + slider("sine amp") *
                            sin(theta * sliderInt("sine freq"));
                    pg.vertex(sineRadius * cos(theta+noiseOffset), sineRadius * sin(theta+noiseOffset));
                }
                pg.endShape();
            }
            if (type.equals("rects")) {
                for (int i = 0; i < elementCount; i++) {
                    float inorm = clampNorm(i, 0, elementCount);
                    float theta = inorm * TAU + noiseOffset;
                    float rectSize = slider("rect size", 1);
                    pg.push();
                    pg.rotate(theta);
                    pg.noStroke();
                    pg.translate(r, 0);
                    pg.rectMode(CENTER);
                    pg.rect(0, 0, elementSize * rectSize, elementSize * rectSize);
                    pg.pop();
                }
            }
            if (type.equals("dots")) {
                for (int i = 0; i < elementCount; i++) {
                    float inorm = clampNorm(i, 0, elementCount);
                    float theta = inorm * TAU + noiseOffset;
                    float dotSize = slider("dot size", 1);
                    pg.noStroke();
                    pg.push();
                    pg.rotate(theta);
                    pg.translate(r, 0);
                    pg.ellipse(0, 0, elementSize * dotSize, elementSize * dotSize);
                    pg.pop();
                }
            }
            if (type.equals("circle")) {
                pg.noFill();
                pg.beginShape();
                elementCount *= slider("circle detail", 1);
                for (int i = 0; i < elementCount; i++) {
                    float inorm = clampNorm(i, 0, elementCount-1);
                    float theta = inorm * TAU + noiseOffset;
                    pg.vertex(r*cos(theta), r*sin(theta));
                }
                pg.endShape();
            }

            if(type.equals("triangles")){
                pg.noFill();
                for (int i = 0; i < elementCount; i++) {
                    float inorm = clampNorm(i, 0, elementCount);
                    float theta = inorm * TAU + noiseOffset;
                    float triangleSize = slider("triangle size", 1);
                    pg.push();
                    pg.rotate(theta);
                    pg.translate(r, 0);
                    pg.rectMode(CENTER);
                    pg.triangle(elementSize * triangleSize*.5f, 0,
                            -elementSize * triangleSize*.5f, -elementSize * triangleSize*.5f,
                            -elementSize * triangleSize*.5f, elementSize * triangleSize*.5f);
                    pg.pop();
                }
            }
            if (type.equals("letters")) {
//                char randomLetter =  (char) (floor(randomDeterministic(level)*26)+'a');
                char randomLetter = 'A';
                for (int i = 0; i < elementCount; i++) {
                    float inorm = clampNorm(i, 0, elementCount);
                    float theta = inorm * TAU + noiseOffset;
                    float dotSize = slider("dot size", 1);
                    pg.noStroke();
                    pg.push();
                    pg.rotate(theta);
                    pg.translate(0, -r);
                    pg.textAlign(CENTER,CENTER);
                    pg.textSize(elementSize*slider("text size", 1));
                    pg.text(randomLetter, 0, 0);
                    pg.pop();
                }
            }
        }
    }

    private void drawRect() {
        group("rect");
        PVector pos = sliderXYZ("pos");
        pg.translate(pos.x, pos.y, pos.z);
        int detail = sliderInt("detail", 160);
        float size = slider("size");
        for (int xi = 0; xi < detail; xi++) {
            float x = map(xi, 0, detail - 1, -size, size);
            handdrawnLine(x, -size, x, size);
        }
        for (int yi = 0; yi < detail; yi++) {
            float y = map(yi, 0, detail - 1, -size, size);
            handdrawnLine(-size, y, size, y);
        }
    }

    private void drawGrid() {
        group("grid");
        int xCount = sliderInt("x count", 20);
        int yCount = sliderInt("y count", 20);
        for (int xi = 0; xi < xCount; xi++) {
            float x = map(xi, 0, xCount - 1, -width * .6f, width * .6f);
            handdrawnLine(x, -height * .6f, x, height * .6f);
        }
        for (int yi = 0; yi < yCount; yi++) {
            float y = map(yi, 0, yCount - 1, -height * .6f, height * .6f);
            handdrawnLine(-width * .6f, y, width * .6f, y);
        }
    }

    private void drawCircles() {
        group("circles");
        int count = sliderInt("count", 10);
        float lineSpacing = slider("line spacing");
        int rotatedCopies = sliderInt("rotated copies");
        for (int i = 0; i < count; i++) {
            group("circle " + i);
            PVector pos = sliderXY("pos");
            float r = slider("radius");
            group("circles");
            pg.push();
            pg.translate(pos.x, pos.y);
            pg.fill(0);
            pg.noStroke();
            pg.circle(0, 0, r * 2);
            pg.rotate(slider("rotate"));
            float lastY = -r - lineSpacing;
            for (int j = 0; j < rotatedCopies; j++) {
                pg.rotate(PI / rotatedCopies);
                for (float theta = -HALF_PI + slider("angle start"); theta < HALF_PI; theta += slider("tiny angle " +
                        "step", .01f)) {
                    float y = r * sin(theta);
                    if (abs(lastY - y) < lineSpacing) {
                        continue;
                    }
                    float x = r * cos(theta);
                    lastY = y;
                    handdrawnLine(-x, y, x, y);
                }
            }
            pg.pop();
        }
    }

    private void darkMask() {
        String darkMask = "darkMask.glsl";
        uniform(darkMask).set("time", t);
        hotFilter(darkMask, pg);
    }

    private void darkMask2() {
        String darkMask = "darkMask2.glsl";
        uniform(darkMask).set("time", t);
        hotFilter(darkMask, pg);
    }

    private void drawRays() {
        group("rays");
        int count = sliderInt("count", 10);
        float innerRadius = sliderInt("inner radius", 100);
        float outerRadius = sliderInt("outer radius", 400);
        for (int i = 0; i < count; i++) {
            float in = clampNorm(i, 0, count);
            float theta = in * TAU;
            float x0 = innerRadius * cos(theta);
            float y0 = innerRadius * sin(theta);
            float x1 = outerRadius * cos(theta);
            float y1 = outerRadius * sin(theta);
            handdrawnLine(x0, y0, x1, y1);
        }
    }

    private void handdrawnLine(float x0, float y0, float x1, float y1) {
        float detail = sliderInt("detail", 100);
        float amp = slider("amp");
        float freq = slider("freq");
        float speed = slider("speed");
        PVector move = sliderXY("move");
        pg.push();
        pg.noFill();
        pg.colorMode(HSB, 1, 1, 1, 1);
        HSBA hsba = picker("stroke");
        pg.strokeWeight(slider("weight"));
        pg.beginShape();
        for (int i = 0; i < detail; i++) {
            float in = clampNorm(i, 0, detail - 1);

            float x = lerp(x0, x1, in);
            float y = lerp(y0, y1, in);
            float noiseOffsetX = amp * (1 - 2 * (float) noise.eval(x * freq + 31.12f + move.x * t,
                    y * freq + 31.12f + move.y * t, t * speed));
            float noiseOffsetY = amp * (1 - 2 * (float) noise.eval(x * freq + 73.12f + move.x * t,
                    y * freq + 78.25f + move.y * t, t * speed));
            float noiseOffsetZ = amp * (1 - 2 * (float) noise.eval(x * freq + 43.12f + move.x * t,
                    y * freq + 68.25f + move.y * t, t * speed));
            x += noiseOffsetX;
            y += noiseOffsetY;
            float z = noiseOffsetZ;
            pg.stroke(hsba.hue() + (z * slider("z weight") + y * slider("y weight")) * slider("z hue"),
                    hsba.sat(), hsba.br(),
                    hsba.alpha());
            pg.vertex(x, y, z);
        }
        pg.endShape();
        pg.pop();
    }

    private float deterministicGaussian(float x) {
        return 1.f - 2.f * noise(x * slider("freq"));
    }
}
