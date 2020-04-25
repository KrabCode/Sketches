package _2020_04;

import applet.KrabApplet;
import processing.core.*;
import utils.OpenSimplexNoise;

public class TriangleSphere extends KrabApplet {
    private PGraphics pg;
    private OpenSimplexNoise noiseGenerator = new OpenSimplexNoise();
    private PShape planet;
    private PShape sea;
    private float planetR;
    private int planetDetail;
    private float seaR;
    private int seaDetail;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(800, 800, P3D);
    }

    public void setup() {
        pg = createGraphics(width, height, P3D);
        pg.smooth(8);
        surface.setAlwaysOnTop(true);
        group("planet");
        updatePlanet();
        frameRecordingDuration *= 2;
    }

    public void draw() {
        pg.beginDraw();
        group("global");
        fadeToBlack(pg);
        blurPass(pg);
        if(toggle("disable optimized stroke")) {
            pg.hint(PConstants.DISABLE_OPTIMIZED_STROKE);
        }
        pg.translate(width * .5f, height * .5f);
        pg.translate(sliderXYZ("translate").x, sliderXYZ("translate").y, sliderXYZ("translate").z);
        pg.colorMode(HSB,1,1,1,1);
        pg.shininess(slider("shininess", 1));
        pg.specular(picker("specular").clr());
        pg.lightSpecular(picker("specular").hue(), picker("specular").sat(), picker("specular").br());
        pg.ambientLight(picker("ambient").hue(), picker("ambient").sat(), picker("ambient").br());
        pg.directionalLight(1,0,1, sliderXYZ("light").x, sliderXYZ("light").y, sliderXYZ("light").z);
        group("planet");
        updateShader();
        rotate(pg);
        planet.setStrokeWeight(slider("weight", 1));
        planet.setStroke(picker("stroke").clr());
        planet.setFill(picker("fill").clr());
        planet.setFill(!toggle("no fill"));
        updatePlanet();
        pg.shape(planet);
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    private void updateParticles() {

    }

    private void stars() {
        String stars = "shaders/noise/dotNoise.glsl";
        hotFilter(stars, pg);
    }

    private void updateShader() {
        String vert = "shaders/_2020_04/PhongVert.glsl";
        String frag = "shaders/_2020_04/PhongFrag.glsl";
        uniform(frag, vert).set("time", t);
        uniformColorPalette(frag, vert);
        hotShader(frag, vert, pg);
    }

    private void updateSea() {
        boolean shouldUpdate = false;
        float intendedR =slider("r", 400);
        if(intendedR != seaR){
            shouldUpdate = true;
            seaR = intendedR;
        }
        int intendedDetail = sliderInt("detail", 3);
        if(intendedDetail != seaDetail){
            shouldUpdate = true;
            seaDetail = intendedDetail;
        }
        if(button("update") || shouldUpdate){
            sea = createTriangleSphere(seaR, seaDetail, false);
        }
    }

    private void updatePlanet() {
        boolean shouldUpdate = false;
        float intendedR =slider("r", 400);
        if(intendedR != planetR){
            shouldUpdate = true;
            planetR = intendedR;
        }
        int intendedDetail = sliderInt("detail", 3);
        if(intendedDetail != planetDetail){
            shouldUpdate = true;
            planetDetail = intendedDetail;
        }
        if(button("update") || shouldUpdate){
            planet = createTriangleSphere(planetR, planetDetail, false);
        }
    }

    PShape createTriangleSphere(float r, int detail, boolean noise) {
        PShape res = createShape();
        float deg5 = TWO_PI / 5f;
        float deg6 = acos((1f + sqrt(5f)) / (5f + sqrt(5f)));
        PVector top = new PVector(0, r, 0);
        PVector[] sides = new PVector[5];
        for (int i = 0; i < 5; i++) {
            sides[i] = new PVector(
                    r * sin(deg6) * cos(deg5 * i),
                    r * cos(deg6),
                    r * sin(deg6) * sin(deg5 * i));
        }
        res.beginShape(TRIANGLES);
        for (int i = 0; i < 5; i++) {
            trig(res, r, top, sides[(i + 1) % 5], sides[i], detail, noise);
            trig(res, r, sides[i], flip(sides[(i + 3) % 5]), flip(sides[(i + 2) % 5]), detail, noise);
            trig(res, r, flip(sides[i]), sides[(i + 2) % 5], sides[(i + 3) % 5], detail, noise);
            trig(res, r, flip(top), flip(sides[i]), flip(sides[(i + 1) % 5]), detail, noise);
        }
        res.endShape();
        return res;
    }

    PVector flip(PVector v) {
        return new PVector(-v.x, -v.y, -v.z);
    }

    void trig(PShape ps, float r, PVector p1, PVector p2, PVector p3, int detail, boolean noise) {
        if (detail > 1) {
            PVector mid12 = PVector.add(p1, p2);
            mid12.setMag(r);
            PVector mid23 = PVector.add(p2, p3);
            mid23.setMag(r);
            PVector mid13 = PVector.add(p1, p3);
            mid13.setMag(r);
            detail--;
            trig(ps, r, p1, mid12, mid13, detail, noise);
            trig(ps, r, p2, mid23, mid12, detail, noise);
            trig(ps, r, p3, mid13, mid23, detail, noise);
            trig(ps, r, mid12, mid23, mid13, detail, noise);
        } else {
            PVector dir = PVector.add(p1, p2);
            dir.add(p3);
            normal(ps, dir);
            vertex(ps, p1);
            vertex(ps, p2);
            vertex(ps, p3);
        }
    }

    void vertex(PShape ps, PVector v) {
        ps.vertex(v.x, v.y, v.z);
    }

    void normal(PShape ps, PVector v) {
        ps.normal(v.x, v.y, v.z);
    }
}
