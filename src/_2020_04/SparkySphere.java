package _2020_04;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PShape;
import processing.core.PVector;

public class SparkySphere extends KrabApplet {
    private PGraphics pg;
    PShape quadBall;
    int diameter = 400;
    int subdivisions = 2;

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
        quadBall = initQuadBall(diameter, subdivisions);
    }

    public void draw() {
        int intendedSubdivisions = sliderInt("subdivisions");
        if(intendedSubdivisions != subdivisions){
            subdivisions = intendedSubdivisions;
            quadBall = initQuadBall(diameter, subdivisions);
        }
        int intendedDiameter = sliderInt("diameter");
        if(intendedDiameter != diameter){
            diameter = intendedDiameter;
            quadBall = initQuadBall(diameter, subdivisions);
        }
        pg.beginDraw();
        alphaFade(pg);
        blurPass(pg);
        pg.translate(width/2, height/2);
        pg.translate(sliderXYZ("translate").x, sliderXYZ("translate").y, sliderXYZ("translate").z);
        mouseRotation(pg);
        pg.background(0);
        quadBall.setStroke(picker("stroke").clr());
        quadBall.setFill(picker("fill").clr());
        quadBall.setStrokeWeight(slider("weight"));
        if(toggle("no fill")){
            quadBall.setFill(false);
        }else{
            quadBall.setFill(true);
        }
        pg.shape(quadBall);
        pg.noStroke();
        pg.fill(picker("inner fill", 0).clr());
        pg.sphere(slider("inner radius", 360));
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }



    // subdivisions: essentially an index of the set of odd numbers. 0->1(cube), 1->3, 2->5, 3->7, ...
    PShape initQuadBall(int diameter, int subdivisions) {
        PShape quadBall = createShape();
        int divs = 1 + 2 * subdivisions;
//        println(divs);
        float divSize = diameter / divs;
        float rad = diameter / 2;
        quadBall.beginShape(QUADS);
        PVector corner = new PVector();
        PVector v = new PVector();
        // XY
        for (int j = -1; j <= 1; j += 2) {
            corner.set(-rad, -rad, rad * j);
            for (int i = 0; i < divs * divs; i++) {
                v.set(corner.x, corner.y, corner.z);
                v.setMag(rad);
                quadBall.vertex(v.x, v.y, v.z);

                v.set(corner.x + divSize, corner.y, corner.z);
                v.setMag(rad);
                quadBall.vertex(v.x, v.y, v.z);

                v.set(corner.x + divSize, corner.y + divSize, corner.z);
                v.setMag(rad);
                quadBall.vertex(v.x, v.y, v.z);

                v.set(corner.x, corner.y + divSize, corner.z);
                v.setMag(rad);
                quadBall.vertex(v.x, v.y, v.z);

                corner.add(divSize, 0, 0);
                if (corner.x > rad - divSize / 2) {
                    corner.x = -rad;
                    corner.y += divSize;
                }
            }

            //XZ
            corner.set(-rad, rad * j, -rad);
            for (int i = 0; i < divs * divs; i++) {
                v.set(corner.x, corner.y, corner.z);
                v.setMag(rad);
                quadBall.vertex(v.x, v.y, v.z);

                v.set(corner.x + divSize, corner.y, corner.z);
                v.setMag(rad);
                quadBall.vertex(v.x, v.y, v.z);

                v.set(corner.x + divSize, corner.y, corner.z + divSize);
                v.setMag(rad);
                quadBall.vertex(v.x, v.y, v.z);

                v.set(corner.x, corner.y, corner.z + divSize);
                v.setMag(rad);
                quadBall.vertex(v.x, v.y, v.z);

                corner.add(divSize, 0, 0);
                if (corner.x > rad - divSize / 2) {
                    corner.x = -rad;
                    corner.z += divSize;
                }
            }

            // YZ
            corner.set(rad * j, -rad, -rad);
            for (int i = 0; i < divs * divs; i++) {
                v.set(corner.x, corner.y, corner.z);
                v.setMag(rad);
                quadBall.vertex(v.x, v.y, v.z);

                v.set(corner.x, corner.y + divSize, corner.z);
                v.setMag(rad);
                quadBall.vertex(v.x, v.y, v.z);

                v.set(corner.x, corner.y + divSize, corner.z + divSize);
                v.setMag(rad);
                quadBall.vertex(v.x, v.y, v.z);

                v.set(corner.x, corner.y, corner.z + divSize);
                v.setMag(rad);
                quadBall.vertex(v.x, v.y, v.z);

                corner.add(0, divSize, 0);
                if (corner.y > rad - divSize / 2) {
                    corner.y = -rad;
                    corner.z += divSize;
                }
            }
        }
        quadBall.endShape();
        return quadBall;
    }

}
