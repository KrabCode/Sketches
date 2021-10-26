package _2021_10;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class Crowd extends KrabApplet {
    private PGraphics pg;
    PVector p = new PVector(); // player position
    PVector c = new PVector(); // camera position
    PVector spd = new PVector();
    float t;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        fullScreen(P2D);
    }

    public void setup() {

    }

    public void draw() {
        pg = updateGraphics(pg, P2D);
        pg.beginDraw();
        pg.colorMode(HSB, 1, 1, 1, 1);
        pg.imageMode(CORNER);
        pg.image(gradient("background"), 0, 0, width, height);

        t = radians(frameCount);
        pg.translate(width/2f-c.x, height/2f-c.y);
        grid();
        player();
        cameraFollow();

        pg.endDraw();
        image(pg, 0, 0);
        rec(pg, sliderInt("frames", 360));
        gui();
    }

    void grid() {
        pg.stroke(0,0,0.5f);
        pg.strokeWeight(6);
        pg.strokeCap(SQUARE);
        int count = 100;
        float size = (height * 10);
        for (int i = 0; i < count; i++) {
            float n = map(i, 0, count-1, -size, size);
            pg.line(n, -size, n, size);
            pg.line(-size, n, size, n);
        }
    }


    void player() {
        PVector pOffset = PVector.sub(c, p);
        float targetAngle = atan2(
                height/2f-mouseY-pOffset.y,
                width/2f-mouseX-pOffset.x
        ) + PI;
        PVector target = PVector.fromAngle(targetAngle);
        float d = dist(mouseX, mouseY,
                width/2f  - pOffset.x,
                height/2f - pOffset.y
        );
        d = min(d, min(height, width));
        d = d * slider("mouse dist", 0.1f);
        target.mult(d);
        if (mousePressed) {
            spd = PVector.lerp(spd, target, slider("lerp amt", 0.05f));
        }
        p.add(spd);
        spd.mult(slider("drag", .98f));
        pg.noStroke();
        pg.stroke(1);
        pg.strokeWeight(5);
        pg.fill(0, 0, .1f);
        drawArrow(targetAngle);
    }

    private void drawArrow(float targetAngle) {
        pg.pushMatrix();
        pg.translate(p.x, p.y);
        pg.rotate(targetAngle);
        pg.beginShape();
        float w = 50;
        float h = 100;
        pg.vertex(h, 0);
        pg.vertex(0, w);
        pg.vertex(15, 0);
        pg.vertex(0, -w);
        pg.endShape(CLOSE);
        pg.popMatrix();

    }

    void cameraFollow() {
        float lerpAmt = 0.07f;
        c.x = lerp(c.x, p.x, lerpAmt);
        c.y = lerp(c.y, p.y, lerpAmt);
    }
}
