package utils;

import applet.KrabApplet;
import processing.core.PVector;

public class CameraFollow extends KrabApplet {
    float cameraRotateX;
    float cameraRotateY;
    float cameraSpeed;
    float boxSize;
    PVector pos, speed;
    int gridCount = 20;
    float accelMag;
    boolean wPressed, sPressed, aPressed, dPressed;
    PVector pressedDir = new PVector();

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(720, 480, P3D);
    }

    public void setup() {
        boxSize = height/10f;
        cameraSpeed = TWO_PI / width;
        cameraRotateY = -PI/6;
        pos = new PVector();
        speed = new PVector();
        accelMag = 2;
    }

    public void draw() {
        background(120);
        translate(width / 2f, height / 2f);
        rotateX(cameraRotateY);
        rotateY(cameraRotateX);
        drawBox();

        PVector accel = getMovementDir().rotate(cameraRotateX).mult(accelMag);
        speed.add(accel);
        pos.add(speed);
        speed.mult(0.9f);

        float edge = (gridCount - 0.5f) * boxSize / 2;
        if (pos.x > edge) {
            pos.x = edge;
            speed.x = 0;
        }
        if (pos.x < -edge) {
            pos.x = -edge;
            speed.x = 0;
        }
        if (pos.y > edge) {
            pos.y = edge;
            speed.y = 0;
        }
        if (pos.y < -edge) {
            pos.y = -edge;
            speed.y = 0;
        }

        translate(0, boxSize / 2);
        drawGrid(gridCount);
    }

    void drawGrid(int count) {
        translate(-pos.x, 0, -pos.y);
        stroke(255);
        float size = (count - 1) * boxSize;
        for (int i = 0; i < count; i++) {
            float pos = map(i, 0, count-1, -0.5f * size, 0.5f * size);
            line(pos, 0, -size/2, pos, 0, size/2);
            line(-size/2, 0, pos, size/2, 0, pos);
        }
    }

    void drawBox() {
        pushMatrix();
        rotateY(atan2(speed.x, speed.y));
        fill(0);
        box(boxSize, boxSize, boxSize*1.2f);
        popMatrix();
    }

    public void mouseDragged() {
        cameraRotateX += (mouseX - pmouseX) * cameraSpeed;
        cameraRotateY += (pmouseY - mouseY) * cameraSpeed;
        cameraRotateY = constrain(cameraRotateY, -HALF_PI, 0);
    }

    PVector getMovementDir() {
        return pressedDir.copy().normalize();
    }

    public void keyPressed() {
        switch(key) {
            case 'w':
                wPressed = true;
                pressedDir.y = -1;
                break;
            case 's':
                sPressed = true;
                pressedDir.y = 1;
                break;
            case 'a':
                aPressed = true;
                pressedDir.x = -1;
                break;
            case 'd':
                dPressed = true;
                pressedDir.x = 1;
                break;
        }
    }

    public void keyReleased() {
        switch(key) {
            case 'w':
                wPressed = false;
                pressedDir.y = sPressed ? 1 : 0;
                break;
            case 's':
                sPressed = false;
                pressedDir.y = wPressed ? -1 : 0;
                break;
            case 'a':
                aPressed = false;
                pressedDir.x = dPressed ? 1 : 0;
                break;
            case 'd':
                dPressed = false;
                pressedDir.x = aPressed ? -1 : 0;
                break;
        }
    }
}
