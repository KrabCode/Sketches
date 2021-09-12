package _2021_09;

// Based on a GDC talk called "Exploring the Tech and Design of Noita"
// https://www.youtube.com/watch?v=prXuyMCgbTc

import applet.KrabApplet;
import processing.core.PGraphics;

public class NoitaPhysics extends KrabApplet {
    private PGraphics pg;
    float scale = 0.1f;
    float prevScale = scale;
    float rectWidth, rectHeight;
    float fallDirection = 0.5f;
    int w, h;
    Particle[][] grid;
    private float inputSize = 5;
    int sandColor, waterColor, airColor, rockColor, rockStroke;
    String defaultType = "air";
    String[] particleTypes = new String[]{"sand", "water", "rock"};

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
        h = ceil(height * scale);
        w = h;
        grid = new Particle[w][h];
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                grid[x][y] = new Particle();
            }
        }
    }

    public void draw() {
        scale = slider("scale", 0.1f);
        if (prevScale != scale) {
            populateGrid();
        }
        prevScale = scale;
        airColor = picker("air color", 0.1f, 0).clr();
        sandColor = picker("sand color", 1).clr();
        waterColor = picker("water color", 0.5f).clr();
        rockColor = picker("rock color", 0.2f).clr();
        rockStroke = picker("rock stroke", 0.5f).clr();
        fallDirection = slider("fall direction", 0.7f, 0, 1);
        pg = updateGraphics(pg);
        resetGroup();
        if (mousePressedOutsideGui ) {
            inputParticles();
        }
        int slowdown = sliderInt("slowdown", 1, 1, 100000);
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

    float particlesSpawned = 0;

    private void spawnParticles() {
        group("spawn");
        if (toggle("block")) {
            resetGroup();
            return;
        }
        String rainType = options("water", "sand");
        float particlesToSpawn = frameCount;
        while (particlesSpawned < particlesToSpawn) {
            int x = constrain(floor(w / 2f + randomGaussian() * w * slider("spread", 0.3f)), 0, w - 1);
            grid[x][0] = new Particle(rainType);
            particlesSpawned += slider("frequency", 1, 0.01f, 100000);
        }
        resetGroup();
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
        rectHeight = height / (float) (h);
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                float screenX = map(x, 0, w, 0, width);
                float screenY = map(y, 0, h, 0, height);
                Particle p = grid[x][y];
                if(p.getStroke() != 0){
                    pg.stroke(p.getStroke());
                }else{
                    pg.noStroke();
                }
                pg.fill(p.getFill());
                pg.rect(screenX, screenY, rectWidth, rectHeight);
            }
        }
    }

    boolean erasing = false;

    public void mousePressed(){
        super.mousePressed();
        erasing = mouseButton == RIGHT;
    }

    void inputParticles() {
        group("brush");
        float inputSize = slider("size", 10);
        String type = options(defaultType, particleTypes);
        resetGroup();
        int inputX = floor(map(mouseX, 0, width, 0, w));
        int inputY = floor(map(mouseY, 0, height, 0, h));
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (dist(x, y, inputX, inputY) < inputSize) {
                    Particle p = new Particle(erasing ? "air" : type);
                    grid[x][y] = p;
                }
            }
        }
    }

    class Particle {
        String type = "air";
        boolean hasBeenUpdated = false;

        Particle() {

        }

        Particle(String type) {
            this.type = type;
        }

        void update(int x, int y) {
            if (type.equals("air")) {
                return;
            }
            if (type.equals("rock")) {
                return;
            }
            if (type.equals("sand")) {
                updateSand(x, y);
            }
            if (type.equals("water")) {
                updateWater(x, y);
            }
        }

        int getFill() {
            if (type.equals("air")) {
                return airColor;
            }
            if (type.equals("sand")) {
                return sandColor;
            }
            if (type.equals("water")) {
                return waterColor;
            }
            if (type.equals("rock")) {
                return rockColor;
            }
            return 0;
        }

        int getStroke() {
            if (type.equals("rock")) {
                return rockStroke;
            }
            return 0;
        }

        boolean isAir(Particle other) {
            return other.getDensity() == 0;
        }

        boolean isLessDenseThanMe(Particle other) {
            return other.getDensity() < getDensity();
        }

        float getDensity() {
            if (type.equals("air")) {
                return 0;
            }
            if (type.equals("rock")) {
                return 100;
            }
            if (type.equals("sand")) {
                return 10;
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
            } else if (botRight != null && isLessDenseThanMe(botRight) && random(1) > fallDirection) {
                swapParticles(x, y, x + 1, y + 1);
            } else if (botLeft != null && isLessDenseThanMe(botLeft) && random(1) > fallDirection) {
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
            } else if (botRight != null && isLessDenseThanMe(botRight) && random(1) > fallDirection) {
                swapParticles(x, y, x + 1, y + 1);
            } else if (botLeft != null && isLessDenseThanMe(botLeft) && random(1) > fallDirection) {
                swapParticles(x, y, x - 1, y + 1);
            } else if (left != null && isAir(left) && random(1) > fallDirection) {
                swapParticles(x, y, x - 1, y);
            } else if (right != null && isAir(right) && random(1) > fallDirection) {
                swapParticles(x, y, x + 1, y);
            }
        }
    }
}
