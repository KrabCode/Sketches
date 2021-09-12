package _2021_09;

// Based on a GDC talk called "Exploring the Tech and Design of Noita"
// https://www.youtube.com/watch?v=prXuyMCgbTc

import applet.KrabApplet;
import processing.core.PGraphics;

public class NoitaPhysics extends KrabApplet {
    private PGraphics pg;
    float scale = 0.1f;
    float rectWidth, rectHeight;
    int w, h;
    String selectedInput = "sand";
    Particle[][] grid;
    private float inputSize = 10;
    int sandColor, waterColor, airColor;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        fullScreen(P3D);
    }

    public void setup() {
        toggleFullscreen();
        surface.setAlwaysOnTop(true);
        populateGrid();
    }

    private void populateGrid() {
        w = ceil(width * scale);
        h = ceil(height * scale);
        grid = new Particle[w][h];
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                grid[x][y] = new Particle();
            }
        }
    }

    public void draw() {
        airColor = picker("air color", 0).clr();
        sandColor = picker("sand color", 1).clr();
        waterColor = picker("water color", 0.5f).clr();
        pg = updateGraphics(pg);
        selectedInput = options("empty", "water", "sand");
        inputSize = slider("size", 10);
        if (mousePressedOutsideGui) {
            inputParticles();
        }
        int slowdown = sliderInt("slowdown");
        slowdown = constrain(slowdown, 1, 100000);
        if (frameCount % slowdown == 0) {
            spawnParticles();
            updateGrid();
        }
        pg.beginDraw();
        pg.colorMode(HSB, 1, 1, 1, 1);
        pg.image(gradient("bg"), 0, 0);
        displayGrid();
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg, sliderInt("frames", 360));
        gui(false);
    }

    private void spawnParticles() {
        int x = constrain(floor(w / 2f + randomGaussian() * w * .3f), 0, w - 1);
        int y = 1;
        String rainType = options("empty", "water", "sand");
        grid[x][y] = new Particle(rainType);
    }

    private void updateGrid() {
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Particle p = grid[x][y];
                p.hasBeenUpdated = false;
            }
        }
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Particle p = grid[x][y];
                p.update(x, y);
            }
        }
    }

    private void displayGrid() {
        rectWidth = width / (float) w;
        rectHeight = height / (float) h;
        pg.noStroke();
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                float screenX = map(x, 0, w, 0, width);
                float screenY = map(y, 0, h, 0, height);
                Particle p = grid[x][y];
                pg.fill(p.getColor());
                pg.rect(screenX, screenY, rectWidth, rectHeight);
            }
        }
    }

    void inputParticles() {
        int inputX = floor(map(mouseX, 0, width, 0, w));
        int inputY = floor(map(mouseY, 0, height, 0, h));
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (dist(x, y, inputX, inputY) < inputSize) {
                    String type = options("empty", "water", "sand");
                    Particle p = new Particle(type);
                    grid[x][y] = p;
                }
            }
        }
    }

    class Particle {
        String type = "empty";
        boolean hasBeenUpdated = false;

        Particle() {

        }

        Particle(String type) {
            this.type = type;
        }

        void update(int x, int y) {
            if (type.equals("empty")) {
                return;
            }
            if (type.equals("sand")) {
                updateSand(x, y);
            }
            if (type.equals("water")) {
                updateWater(x, y);
            }
        }

        int getColor() {
            if (type.equals("empty")) {
                return airColor;
            }
            if (type.equals("sand")) {
                return sandColor;
            }
            if (type.equals("water")) {
                return waterColor;
            }
            return 0;
        }

        boolean isLessDenseThanMe(Particle other) {
            return other.getDensity() < getDensity();
        }

        float getDensity() {
            if (type.equals("empty")) {
                return 0;
            }
            if (type.equals("sand")) {
                return 5;
            }
            if (type.equals("water")) {
                return 2;
            }
            return 0;
        }

        Particle getParticleSafely(int x, int y) {
            // avoid index out of bounds, return null instead
            if (x < 0 || x >= w || y < 0 || y >= h) {
                return null;
            }
            return grid[x][y];
        }

        void swapParticles(int x0, int y0, int x1, int y1) {
            Particle a = grid[x0][y0];
            Particle b = grid[x1][y1];
            a.hasBeenUpdated = true;
            b.hasBeenUpdated = true;
            grid[x0][y0] = b;
            grid[x1][y1] = a;
        }

        @SuppressWarnings("DuplicatedCode")
        private void updateSand(int x, int y) {
            Particle bot = getParticleSafely(x, y + 1);
            Particle botRight = getParticleSafely(x + 1, y + 1);
            Particle botLeft = getParticleSafely(x - 1, y + 1);
            if (hasBeenUpdated) {
                return;
            }
            if (bot != null && isLessDenseThanMe(bot)) {
                swapParticles(x, y, x, y + 1);
            } else if (botRight != null && isLessDenseThanMe(botRight)) {
                swapParticles(x, y, x + 1, y + 1);
            } else if (botLeft != null && isLessDenseThanMe(botLeft)) {
                swapParticles(x, y, x - 1, y + 1);
            }
        }

        @SuppressWarnings("DuplicatedCode")
        private void updateWater(int x, int y) {
            Particle bot = getParticleSafely(x, y + 1);
            Particle botRight = getParticleSafely(x + 1, y + 1);
            Particle botLeft = getParticleSafely(x - 1, y + 1);
            Particle left = getParticleSafely(x - 1, y);
            Particle right = getParticleSafely(x + 1, y);
            if (hasBeenUpdated) {
                return;
            }
            if (bot != null && isLessDenseThanMe(bot)) {
                swapParticles(x, y, x, y + 1);
            } else if (botRight != null && isLessDenseThanMe(botRight)) {
                swapParticles(x, y, x + 1, y + 1);
            } else if (botLeft != null && isLessDenseThanMe(botLeft)) {
                swapParticles(x, y, x - 1, y + 1);
            } else if (left != null && isLessDenseThanMe(left)) {
                swapParticles(x, y, x - 1, y);
            } else if (right != null && isLessDenseThanMe(right)) {
                swapParticles(x, y, x + 1, y);
            }
        }
    }
}
