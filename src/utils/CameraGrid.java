package utils;
import applet.KrabApplet;
import processing.core.PImage;
import processing.core.PVector;

public class CameraGrid extends KrabApplet {
    private PVector cameraOffset;
    private PVector playerPos = new PVector();
    private PImage img;

    public static void main(String[] args) {
        KrabApplet.main(String.valueOf(new Object(){}.getClass().getEnclosingClass()).split(" ")[1]);
    }

    public void settings() {
        size(800, 800, P2D);
    }

    public void setup() {
        cameraOffset = new PVector(width * .5f, height * .5f);
        img = loadImage(randomImageUrl(1600));
    }

    public void draw() {
        background(150);
        updateCamera();
        imageMode(CENTER);
        image(img, 0,0);
//        drawGridAroundPlayer();
        updatePlayer();
        drawPlayer();
        gui();
    }

    private void updatePlayer() {
        if (mousePressed) {
            PVector toMouse = new PVector(mouseX - width * .5f, mouseY - height * .5f).normalize().mult(5);
            playerPos.add(toMouse);
        }
    }

    private void updateCamera() {
        float cameraFollowTightness = .05f;
        cameraOffset.x = lerp(cameraOffset.x, (width * .5f) - playerPos.x, cameraFollowTightness);
        cameraOffset.y = lerp(cameraOffset.y, (height * .5f) - playerPos.y, cameraFollowTightness);
        translate(cameraOffset.x, cameraOffset.y);
    }
/*
    private void drawGridAroundPlayer() {
        float cellSize = 40;
        float bufferZone = cellSize * 2;  //set this to -cellSize*2 to see how it works
        float xEdge = width * .5f + bufferZone;
        float yEdge = height * .5f + bufferZone;
        pushMatrix();
        PVector gridOffset = new PVector(playerPos.x % cellSize, playerPos.y % cellSize);
        translate(playerPos.x - gridOffset.x, playerPos.y - gridOffset.y);
        for (float x = -xEdge; x <= xEdge; x += cellSize) {
            line(x, -yEdge, x, yEdge);
        }
        for (float y = -yEdge; y <= yEdge; y += cellSize) {
            line(-xEdge, y, xEdge, y);
        }
        popMatrix();
    }
*/

    private void drawPlayer() {
        push();
        translate(playerPos.x, playerPos.y);
        ellipse(0, 0, 20, 20);
        pop();
    }

}
