package _2020_06;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

import java.util.ArrayList;

public class Cats extends KrabApplet {
    private float time;
    private int catCount = 12;
    float imageScale = 1.5f;
    private ArrayList<Cat> cats = new ArrayList<>();
    private Cat held = null;
    private PImage sticksIdle, sticksHeld, catHeld;
    private PImage[] catDown, catRight, catUp;
    private PGraphics pg;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
//        size(1000, 1000, P3D);
        fullScreen(P2D);
        smooth(16);
    }

    public void setup() {
        frameRecordingDuration = 1000;
        if (width < displayWidth) {
            surface.setAlwaysOnTop(true);
            surface.setLocation(displayWidth - 1020, 20);
        }
        loadImages();
        pg = createGraphics(width, height, P2D);
        generateCats();
        pg.beginDraw();
        pg.colorMode(HSB, 1, 1, 1, 1);
        pg.background(0);
        pg.endDraw();
    }

    private void loadImages() {
        sticksIdle = loadImage("images/cats/hulky-idle.png");
        sticksHeld = loadImage("images/cats/hulky-hold.png");
        catHeld = loadImage("images/cats/kitt-held.png");
        catDown = new PImage[]{loadImage("images/cats/kitt-down-1.png"), loadImage("images/cats/kitt-down-2.png")};
        catRight = new PImage[]{loadImage("images/cats/kitt-side-1.png"), loadImage("images/cats/kitt-side-2.png")};
        catUp = new PImage[]{loadImage("images/cats/kitt-up-1.png"), loadImage("images/cats/kitt-up-2.png")};
    }

    public void mouseReleased() {
        held = null;
    }

    public void draw() {
        time = radians(frameCount);
        noCursor();
        pg.beginDraw();
        pg.imageMode(CENTER);
        pg.background(0);
        updateWalkingCats();
        drawCursor();
        updateHeldCat();
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
    }

    private void updateHeldCat() {
        if (held == null) {
            return;
        }
        held.update();
        held.draw();
    }

    private void drawCursor() {
        float w = sticksHeld.width*imageScale;
        float h = sticksHeld.height*imageScale;
        pg.imageMode(CENTER);
        float x = mouseX + w * 0.37f;
        float y = mouseY + h * -0.37f;
        if (mousePressed) {
            pg.image(sticksHeld, x, y, w, h);
        } else {
            pg.image(sticksIdle, x, y, w, h);
        }
        /*
        pg.strokeWeight(5);
        pg.stroke(1);
        pg.point(mouseX, mouseY);
        */
    }

    void generateCats() {
        cats.clear();
        for (int i = 0; i < catCount; i++) {
            cats.add(new Cat());
        }
    }

    void updateWalkingCats() {
        for (Cat c : cats) {
            if (held == null || !held.equals(c)) {
                c.update();
                c.draw();
            }
        }
    }

    class Cat {
        private PVector pos = new PVector(width / 2f + random(-10, 10), height / 2f + random(-10, 10));
        private int direction = floor(random(4));
        private float size = 62*imageScale;
        private float hue = (.7f + random(.4f)) % 1;
        private float sat = random(.15f, .4f);
        private float br = random(.8f, 1);
        private float timeOffset = random(TAU);

        void update() {
            mouseInteract();
            if(!thisHeld()){
                updateDirection();
                move();
                checkCollisions();
            }
        }

        void draw() {
            pg.noStroke();
            pg.fill(hue, sat, br);
            pg.imageMode(CENTER);
            drawImage();
        }

        private void move() {
            PVector speed = new PVector();
            if (direction == 0) {
                speed.x = 1;
            } else if (direction == 1) {
                speed.y = 1;
            } else if (direction == 2) {
                speed.x = -1;
            } else if (direction == 3) {
                speed.y = -1;
            }
            speed.mult(0.5f);
            pos.add(speed);
            if (pos.x < size / 2f) {
                pos.x += width + size;
            }
            if (pos.x > width + size / 2f) {
                pos.x -= width + size;
            }
            if (pos.y < size / 2f) {
                pos.y += height + size;
            }
            if (pos.y > height + size / 2f) {
                pos.y -= height + size;
            }
        }

        private void updateDirection() {
            if (random(1) < 0.01f) {
                changeDirection();
            }
        }

        private void changeDirection() {
            if (random(1) > 0.5f) {
                direction++;
            } else {
                direction--;
            }
            while (direction < 0) {
                direction += 4;
            }
            direction %= 4;
        }

        private void drawImage() {
            int frame = sin(time * 8 + timeOffset) > 0 ? 0 : 1;
            PImage img = null;
            if (direction == 0 || direction == 2) {
                img = catRight[frame];
            } else if (direction == 1) {
                img = catDown[frame];
            } else if (direction == 3) {
                img = catUp[frame];
            }
            if (img == null) {
                pg.fill(1, 1, 1);
                pg.rectMode(CENTER);
                pg.rect(pos.x, pos.y, size, size);
                return;
            }
            boolean flipX = direction == 2 && !thisHeld();
            pg.tint(hue, sat, br);
            pg.pushMatrix();
            pg.translate(pos.x, pos.y);
            if (thisHeld()) {
                img = catHeld;
            }
            flipIfNeeded(flipX);
            pg.image(img, 0, 0, size, size);
            pg.popMatrix();

            pg.pushMatrix();
            // draw wrap around copy when on edge of screen
            pg.translate(pos.x, pos.y);
            if (pos.x < size / 2f) {
                pg.translate(width, 0);
            }
            if (pos.x > width - size / 2f) {
                pg.translate( -width, 0);
            }
            if (pos.y < size / 2f) {
                pg.translate( 0, height);
            }
            if (pos.y > height - size / 2f) {
                pg.translate( 0, -height);
            }
            flipIfNeeded(flipX);
            pg.image(img, 0, 0, size, size);
            pg.popMatrix();
        }

        private void flipIfNeeded(boolean flipX) {
            if (flipX) {
                pg.scale(-1, 1);
            } else {
                pg.scale(1, 1);
            }
        }

        private boolean thisHeld() {
            if (held == null) {
                return false;
            }
            return held.equals(this);
        }

        void mouseInteract(){
            if (held == null && mousePressed && dist(mouseX, mouseY, pos.x, pos.y) < size / 2) {
                held = this;
            }
            if (held != null && held.equals(this)) {
                pos.x = lerp(pos.x, mouseX, .5f);
                pos.y = lerp(pos.y, mouseY, .5f);
            }
        }

        void checkCollisions() {
            for (Cat otherCard : cats) {
                if (otherCard.equals(this)) {
                    continue;
                }
                float distanceToOther = dist(pos.x, pos.y, otherCard.pos.x, otherCard.pos.y);
                if (distanceToOther < size) {
                    PVector fromOtherToThis = PVector.sub(pos, otherCard.pos);
                    float repulsion = (1 / norm(distanceToOther, 0, size))*.5f;
                    pos.add(fromOtherToThis.normalize().mult(repulsion));
                }
            }
        }
    }
}
