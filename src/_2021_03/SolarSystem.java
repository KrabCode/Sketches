package _2021_03;

import applet.KrabApplet;
import processing.core.PGraphics;

import java.util.ArrayList;

public class SolarSystem extends KrabApplet {
    private PGraphics pg;
    float sunSize = 100;
    float planetRadiusStep = 150;
    float planetSize = 30;
    int count = 4;
    float brightColor = 255;
    float darkColor = 50;
    float bgColor = 0;
    ArrayList<Float> offsets = new ArrayList<Float>();

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(1000,1000, P2D);
        smooth(16);
    }

    public void setup() {
        pg = createGraphics(width, height);
    }

    public void draw() {
        pg.beginDraw();
        pg.image(gradient("background"), 0, 0);
        float t = radians(frameCount*0.1f);
        pg.background(bgColor);
        pg.strokeWeight(3);
        pg.translate(width*.5f, height*.5f);
        pg.noStroke();
        pg.fill(brightColor);
        pg.ellipse(0,0,sunSize, sunSize);
        for(int i = 0; i < count; i++){
            float r = sunSize + planetRadiusStep + i * planetRadiusStep;
            float theta = offset(i) + t*(count-i) + i * TAU;
            float x = r * .5f * cos(theta);
            float y = r * .5f * sin(theta);
            pg.stroke(darkColor);
            pg.noFill();
            pg.ellipse(0,0,r,r); // orbit circle
            pg.pushMatrix();
            pg.translate(x,y);
            pg.noStroke();
            pg.fill(bgColor); // orbit gap
            pg.ellipse(0,0, planetSize * 2, planetSize * 2);
            pg.fill(brightColor);
            // lit side of planet
            pg.arc(0,0,planetSize,planetSize, theta+HALF_PI,theta+HALF_PI+PI);
            pg.fill(darkColor);
            // dark side of planet
            pg.arc(0,0,planetSize,planetSize,theta-HALF_PI,theta+HALF_PI);
            pg.popMatrix();
        }
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg, sliderInt("frames", 360));
        gui();
    }
    float offset(int index){
        if(index >= offsets.size()){
            offsets.add(random(10, 100));
        }
        return offsets.get(index);
    }
}
