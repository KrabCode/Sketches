package _2021_10;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PVector;

import java.util.ArrayList;

@SuppressWarnings("DuplicatedCode")
public class Crowd_2 extends KrabApplet {

    ArrayList<P> ps = new ArrayList<P>();
    ArrayList<P> psBin = new ArrayList<P>();
    PGraphics pg;
    PVector playerPos = new PVector(); // player position
    PVector cameraPos = new PVector(); // camera position
    PVector playerSpeed = new PVector();
    PVector playerTarget = new PVector();
    PVector playerScreenPos = new PVector();
    float playerTargetAngle;
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
        t = radians(frameCount);
        pg = updateGraphics(pg, P2D);
        pg.beginDraw();
        pg.colorMode(HSB, 1, 1, 1, 1);
        pg.imageMode(CORNER);
        pg.image(gradient("background"), 0, 0, width, height);
        cameraPosition();
        drawBackgroundGrid();
        updateCrowd();
        drawCrowd();
        updateDrawPlayer();
        cameraFollow();
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg, sliderInt("frames", 360));
        gui();
    }

    void drawBackgroundGrid() {
        pg.stroke(0,0,0);
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

    void updateDrawPlayer() {
        PVector pOffset = PVector.sub(cameraPos, playerPos);
        float d = dist(mouseX, mouseY,
                width/2f  - pOffset.x,
                height/2f - pOffset.y
        );
        playerTargetAngle = atan2(
                height/2f-mouseY-pOffset.y,
                width/2f-mouseX-pOffset.x
        ) + PI;
        playerTarget = PVector.fromAngle(playerTargetAngle);
        d = min(d, min(height/2, width/2));
        d = d * slider("mouse dist", 0.1f);
        playerTarget.mult(d);
        if (mousePressed) {
            playerSpeed = PVector.lerp(playerSpeed, playerTarget, slider("player lerp", 0.05f));
        }
        playerPos.add(playerSpeed);
        playerSpeed.mult(slider("drag", .98f));
        drawPlayer();
    }

    void drawPlayer(){
        pg.noStroke();
        pg.stroke(1);
        pg.strokeWeight(5);
        pg.fill(0, 0, .1f);
        drawArrow(playerTargetAngle);
    }

    private void drawArrow(float targetAngle) {
        float w = 50;
        float h = 15;
        pg.pushMatrix();
        pg.translate(playerPos.x, playerPos.y);
        pg.rotate(targetAngle);
        pg.beginShape();
        pg.vertex(h, 0);
        pg.vertex(0, w);
        pg.vertex(-h, 0);
        pg.vertex(0, -w);
        pg.endShape(CLOSE);
        float size = slider("player head size", 2.3f);
        pg.ellipse(0,0,h*size, h*size);
        pg.popMatrix();
    }

    private void cameraPosition() {
        pg.translate(width/2f- cameraPos.x, height/2f- cameraPos.y);
    }

    void cameraFollow() {
        float lerpAmt = slider("camera lerp", 0.07f);
        cameraPos.x = lerp(cameraPos.x, playerPos.x, lerpAmt);
        cameraPos.y = lerp(cameraPos.y, playerPos.y, lerpAmt);
    }

    private void updateCrowd(){
        deleteOutOfBoundsAndBehind();
        generateOutOfBoundsAndInFront();
        ps.removeAll(psBin);
        psBin.clear();
        for(P p : ps){
            p.update();
        }
    }

    private void generateOutOfBoundsAndInFront() {
        int count = sliderInt("p count", 100);
        while(ps.size() < count){
            ps.add(new P());
        }
    }

    private void deleteOutOfBoundsAndBehind() {

    }

    private void drawCrowd() {
        for(P p : ps){
            p.draw();
        }
    }

    class P {
        PVector pos = new PVector();
        float size = 50;

        P(){

        }

        public void update() {

        }

        public void draw() {

        }
    }

    boolean isInFrontOfPlayer(PVector pos, PVector dir, PVector queryPos) {
        float n = PVector.sub(queryPos, pos).heading() - dir.heading();
        while (n > PI) {
            n -= TAU;
        }
        while (n < -PI) {
            n += TAU;
        }
        return abs(n) < HALF_PI;
    }
}
