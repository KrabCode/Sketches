package _2020_04;

import applet.KrabApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PShape;

import java.util.ArrayList;

public class ParticlePositionTextures extends KrabApplet {
    private PGraphics pg;
    private PGraphics particlePositionTexture;
    private PGraphics particleSpeedTexture;
    int particleCount = 30000;
    private ArrayList<PShape> pointArray;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(800, 800, P3D);
    }

    public void setup() {
        pg = createGraphics(width, height, P3D);
        particlePositionTexture = createGraphics(particleCount, 1, P2D);
        particleSpeedTexture = createGraphics(particleCount, 1, P2D);
        surface.setAlwaysOnTop(true);
        pointArray = shapes(particleCount, POINTS);
    }

    public void draw() {
        updateParticleSpeeds();
        updateParticlePositions();
        pg.beginDraw();
        pg.background(0);
        pg.hint(PConstants.DISABLE_OPTIMIZED_STROKE);
        translateToCenter(pg);
        translate(pg);
        updateDisplayShader();
        for (PShape p : pointArray) {
            p.setStrokeWeight(max(.1f, slider("weight", 1)));
            pg.shape(p);
        }
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }


    private void updateDisplayShader() {
        String frag = "shaders/_2020_04/particlePositionTextures/PointFrag.glsl";
        String vert = "shaders/_2020_04/particlePositionTextures/PointVert.glsl";
        uniform(frag, vert).set("particleCount", particleCount);
        uniform(frag, vert).set("particlePositions", particlePositionTexture);
        hotShader(frag, vert, pg);
    }

    private void updateParticlePositions() {
        String frag = "shaders/_2020_04/particlePositionTextures/positions.glsl";
        uniform(frag).set("time", t);
        uniform(frag).set("particleCount", particleCount);
        uniform(frag).set("particleSpeeds", particleSpeedTexture);
        hotFilter(frag, particlePositionTexture);
    }

    private void updateParticleSpeeds() {
        String frag = "shaders/_2020_04/particlePositionTextures/speeds.glsl";
        uniform(frag).set("time", t);
        uniform(frag).set("particleCount", particleCount);
        uniform(frag).set("sketchResolution", (float) width, (float) height);
        uniform(frag).set("particlePositions", particlePositionTexture);
        uniform(frag).set("mousePos", (float) mouseX, (float) mouseY);
        hotFilter(frag, particleSpeedTexture);
    }
}
