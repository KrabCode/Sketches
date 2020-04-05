package _2020_04;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PMatrix3D;
import processing.core.PShape;
import processing.core.PVector;
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
        KrabApplet.main(String.valueOf(new Object() {
        }.getClass().getEnclosingClass()).split(" ")[1]);
    }

    public void settings() {
        size(800, 800, P3D);
    }

    public void setup() {
        pg = createGraphics(width, height, P3D);
        pg.smooth(16);
        surface.setAlwaysOnTop(true);
        group("planet");
        updatePlanet();
        group("sea");
        updateSea();
        frameRecordingDuration *= 4;
    }

    public void draw() {
        pg.beginDraw();
        group("global");
        alphaFade(pg);
        blurPass(pg);
        stars();
        pg.translate(width / 2f, height / 2f);
        pg.translate(sliderXYZ("translate").x, sliderXYZ("translate").y, sliderXYZ("translate").z);

        pg.colorMode(HSB,1,1,1,1);
        pg.lights();
        pg.specular(picker("specular").clr());
        pg.lightSpecular(picker("specular").hue(), picker("specular").sat(), picker("specular").br());
        pg.ambient(picker("ambient").clr());
        pg.ambientLight(picker("ambient").hue(), picker("ambient").sat(), picker("ambient").br());
        pg.directionalLight(1,0,1, sliderXYZ("light").x, sliderXYZ("light").y, sliderXYZ("light").z);
        pg.emissive(picker("emissive").clr());

        group("planet");
        updateShader();
        mouseRotation(pg);
        PVector timeRotation = sliderXYZ("rotate");
        PMatrix3D temp = new PMatrix3D();
        temp.rotateX(timeRotation.x * t);
        temp.rotateY(timeRotation.y * t);
        temp.rotateZ(timeRotation.z * t);
        pg.applyMatrix(temp);
        planet.setStrokeWeight(slider("weight", 1));
        planet.setStroke(picker("stroke").clr());
//        planet.setFill(picker("fill").clr());
//        planet.setFill(!toggle("no fill"));
        updatePlanet();
        pg.shape(planet);
        group("sea");
        updateShader();
        sea.setStrokeWeight(slider("weight", 1));
        sea.setStroke(picker("stroke").clr());
//        sea.setFill(picker("fill").clr());
//        sea.setFill(!toggle("no fill"));
        updateSea();
        pg.shape(sea);

        updateParticles();

        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    private void updateParticles() {

    }

    private void stars() {
        String stars = "shaders/_2020_04/dotNoise.glsl";
        hotFilter(stars, pg);
    }

    private void updateShader() {
        String vert = "shaders/_2020_04/planetVert.glsl";
        String frag = "shaders/_2020_04/planetFrag.glsl";
        uniformColorPalette(frag, vert);
        uniform(frag, vert).set("minRadius", slider("min radius", 300));
        uniform(frag, vert).set("maxRadius", slider("max radius", 500));
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
            planet = createTriangleSphere(planetR, planetDetail, true);
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
            if(noise){
                group("fbm");
                PVector p1temp = p1.copy().mult(1+slider("fbm mag")*fbm(p1));
                PVector p2temp = p2.copy().mult(1+slider("fbm mag")*fbm(p2));
                PVector p3temp = p3.copy().mult(1+slider("fbm mag")*fbm(p3));

                PVector dir = PVector.add(p1temp, p2temp);
                dir.add(p3temp);
                normal(ps, dir);
                vertex(ps, p1temp);
                vertex(ps, p2temp);
                vertex(ps, p3temp);
            }else{
                PVector dir = PVector.add(p1, p2);
                dir.add(p3);
                normal(ps, dir);
                vertex(ps, p1);
                vertex(ps, p2);
                vertex(ps, p3);

            }
        }
    }

    void vertex(PShape ps, PVector v) {
        ps.vertex(v.x, v.y, v.z);
    }

    void normal(PShape ps, PVector v) {
        ps.normal(v.x, v.y, v.z);
    }

    private float fbm(PVector p) {
        group("fbm");
        float noise = 0;
        for (int i = 0; i < sliderInt("octaves", 5); i++) {
            float freq = slider("frequency " + i, 1);
            float amp = slider("amp " + i, 1);
            noise += amp * (1 - 2 * noiseGenerator.eval(p.x * freq, p.y * freq, p.z * freq));
        }
        noise = constrain(noise, slider("noise clamp min", 0), slider("noise clamp max", 100));
        return noise;
    }

    void dynamicQuadBall(float r, int subDiv, boolean noise) {
        pg.stroke(picker("stroke").clr());
        pg.fill(picker("fill").clr());
        pg.strokeWeight(slider("weight"));
        if (toggle("no fill")) {
            pg.noFill();
        }

        if (subDiv < 2) subDiv = 2;
        PVector[] dirs = {
                new PVector(1, 0, 0), new PVector(-1, 0, 0),
                new PVector(0, 1, 0), new PVector(0, -1, 0),
                new PVector(0, 0, 1), new PVector(0, 0, -1)
        };
        PVector[] us = {
                new PVector(0, 1, 0), new PVector(0, 0, 1),
                new PVector(0, 0, 1), new PVector(1, 0, 0),
                new PVector(1, 0, 0), new PVector(0, 1, 0)
        };
        PVector[] vs = {
                new PVector(0, 0, 1), new PVector(0, 1, 0),
                new PVector(1, 0, 0), new PVector(0, 0, 1),
                new PVector(0, 1, 0), new PVector(1, 0, 0)
        };
        pg.beginShape(QUADS);
        PVector up = new PVector();
        PVector vp = new PVector();
        for (int i = 0; i < 6; i++) {
            for (int u = 0; u < subDiv - 1; u++) {
                for (int v = 0; v < subDiv - 1; v++) {
                    for (int j = 0; j < 4; j++) {
                        float uOffs = (j / 2 ^ j % 2);
                        float vOffs = j / 2;
                        float uAngle = map(u + uOffs, 0, subDiv - 1, -QUARTER_PI, QUARTER_PI);
                        float vAngle = map(v + vOffs, 0, subDiv - 1, -QUARTER_PI, QUARTER_PI);
                        up.set(us[i]);
                        up.mult(tan(uAngle));
                        vp.set(vs[i]);
                        vp.mult(tan(vAngle));
                        PVector p = dirs[i].copy();
                        p.add(up);
                        p.add(vp);
                        p.setMag(r + (noise?fbm(p):0));
                        pg.normal(p.x, p.y, p.z);
                        pg.vertex(p.x, p.y, p.z);
                    }
                }
            }
        }
        pg.endShape();
    }

}
