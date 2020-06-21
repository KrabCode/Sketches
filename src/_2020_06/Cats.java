package _2020_06;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

import java.util.ArrayList;

public class Cats extends KrabApplet {
    private PGraphics pg;
    float diagonalHalf;
    float gameBorder;
    boolean newGame = true;
    boolean gameOver = true;
    ArrayList<Cat> deck = new ArrayList<>();
    Cat held = null;
    PImage sticksIdle, sticksHeld, catHeld;
    PImage[] catDown, catRight, catUp;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(1000, 1000, P3D);
        smooth(16);
    }

    public void setup() {
        frameRecordingDuration = 1000;
        if (width < displayWidth) {
            surface.setAlwaysOnTop(true);
            surface.setLocation(2560 - 1020, 20);
        }
        loadImages();
        pg = createGraphics(width, height, P3D);
        diagonalHalf = dist(0, 0, pg.width / 2f, pg.height / 2f);
        gameBorder = diagonalHalf / 2;
        buildDeck();
        pg.beginDraw();
        pg.colorMode(HSB, 1, 1, 1, 1);
        pg.background(0);
        pg.endDraw();
    }

    private void loadImages() {
        sticksIdle = loadImage("images/cats/hulky-idle.png");
        sticksHeld = loadImage("images/cats/hulky-hold.png");
        catHeld = loadImage("images/cats/kitt-held.png");
        catDown = new PImage[]{loadImage("images/cats/kitt-down-1.png"),loadImage("images/cats/kitt-down-2.png")};
        catRight = new PImage[]{loadImage("images/cats/kitt-side-1.png"),loadImage("images/cats/kitt-side-2.png")};
        catUp = new PImage[]{loadImage("images/cats/kitt-up-1.png"),loadImage("images/cats/kitt-up-2.png")};
    }

    public void mouseReleased() {
        held = null;
    }

    public void draw() {
        noCursor();
        pg.beginDraw();
        pg.imageMode(CENTER);
        pg.image(gradient("background"), width/2f, height/2f);
        if (gameOver) {
            drawGameOver();
        } else {
            updateWalkingCats();
        }
        drawCursor();
        updateHeldCat();
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    private void updateHeldCat() {
        if(held == null) {
            return;
        }
        held.update();
        held.draw();
    }

    private void drawCursor() {
        pg.imageMode(CENTER);
        if(mousePressed) {
            pg.image(sticksHeld, mouseX+sticksHeld.width/2f, mouseY-sticksHeld.width/2f);
        }else {
            pg.image(sticksIdle, mouseX+sticksIdle.width/2f, mouseY-sticksIdle.width/2f);
        }
    }

    void buildDeck() {
        deck.clear();
        for (int i = 0; i < 20; i++) {
            deck.add(new Cat());
        }
    }

    void updateWalkingCats() {
        for (Cat c : deck) {
            if(held == null || !held.equals(c)) {
                c.update();
                c.draw();
            }
        }
        pg.noStroke();
        pg.beginShape(TRIANGLE_STRIP);
        float innerRadius = gameBorder;
        float outerRadius = slider("gradient radius", gameBorder+50);
        int detail = 100;
        for (int i = 0; i <= detail; i++) {
            float theta = map(i, 0, detail, 0, TAU);
            pg.fill(1);
            pg.vertex(pg.width/2f+innerRadius*cos(theta), pg.height/2f+innerRadius*sin(theta));
            pg.fill(0);
            pg.vertex(pg.width/2f+outerRadius*cos(theta), pg.height/2f+outerRadius*sin(theta));
        }
        pg.endShape();
    }

    void drawGameOver() {
        if (newGame) {
            pg.textSize(50);
            pg.textAlign(CENTER, CENTER);
            pg.fill(1);
            pg.text("clicc", width / 2f, height / 2f);
        } else {
            pg.textSize(50);
            pg.textAlign(CENTER, CENTER);
            pg.fill(1);
            pg.text("oh no", width / 2f, height / 2f);
        }
    }

    public void mousePressed() {
        if (gameOver) {
            buildDeck();
            newGame = false;
            gameOver = false;
        }
    }

    class Cat {
        PVector pos = new PVector(width / 2f + random(-10, 10), height / 2f + random(-10, 10));
        int direction = 0;
        float size = 50;
        float hue = (.8f + random(.4f)) % 1;
        float sat = random(.5f);
        float br = 1;

        void update() {
            updateDirection();
            move();
            float distanceFromCenter = dist(pos.x, pos.y, width / 2f, height / 2f);

            if (distanceFromCenter > gameBorder) {
                gameOver = true;
            }
            checkCollisions();
        }

        void draw() {
            pg.noStroke();
            pg.fill(hue, sat, br);
            pg.imageMode(CENTER);
            pg.pushMatrix();
            drawImage();
            pg.popMatrix();
        }

        private void move() {
            PVector speed = new PVector();
            if(direction == 0) {
                speed.x = 1;
            }else if(direction == 1) {
                speed.y = 1;
            }else if(direction == 2) {
                speed.x = -1;
            }else if(direction == 3) {
                speed.y = -1;
            }
            speed.mult(slider("speed", 0.5f));
            pos.add(speed);
        }

        private void updateDirection() {
            if(random(1) < slider("turn chance", 0.05f)) {
                if(random(1) > 0.5f) {
                    direction++;
                }else {
                    direction--;
                }
            }
            while(direction < 0) {
                direction += 4;
            }
            direction %= 4;
        }

        private void drawImage() {
            int frame = sin(t*slider("animation speed", 10)) > 0 ? 0 : 1;
            PImage img = null;

            if(direction == 0 || direction == 2) {
                img = catRight[frame];
            }else if(direction == 1) {
                img = catDown[frame];
            }else if(direction == 3) {
                img = catUp[frame];
            }
            if(held != null && held.equals(this)) {
                img = catHeld;
            }
            pg.translate(pos.x+img.width/4f, pos.y-img.height/4f);
            if(direction == 2) {
                pg.scale(-1,1);
            }else {
                pg.scale(1,1);
            }
            pg.image(img,0, 0);

        }


        void checkCollisions() {
            if(held == null && mousePressed && dist(mouseX, mouseY, pos.x, pos.y) < size / 2) {
                held = this;
            }
            if (held != null && held.equals(this)) {
                pos.x = mouseX;
                pos.y = mouseY;
            }
            for (Cat otherCard : deck) {
                if (otherCard.equals(this)) {
                    continue;
                }
                float distanceToOther = dist(pos.x, pos.y, otherCard.pos.x, otherCard.pos.y);
                if (distanceToOther < size / 2 + otherCard.size / 2) {
                    PVector fromOtherToThis = PVector.sub(pos, otherCard.pos);
                    pos.add(fromOtherToThis.normalize());
                }
            }
        }
    }

}
