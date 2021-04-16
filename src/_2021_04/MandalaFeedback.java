package _2021_04;

import applet.KrabApplet;
import processing.core.PGraphics;
import utils.OpenSimplexNoise;

public class MandalaFeedback extends KrabApplet {
    private PGraphics pg;
    private OpenSimplexNoise noise = new OpenSimplexNoise();

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    int mirrors = 12;

    public void settings() {
        fullScreen(P3D);
    }

    public void setup() {
        toggleFullscreen();
        pg = updateGraphics(pg);
        pg.beginDraw();
        pg.background(0);
        pg.endDraw();
    }

    public void draw() {
        background(0);
        pg = updateGraphics(pg);
        pg.beginDraw();


        float t = radians(frameCount)*.1f;
        mirrors = sliderInt("mirrors", mirrors);

        pg.blendMode(BLEND);
        pg.pushMatrix();
        pg.translate(width*.5f,height*.5f);
        pg.rotate(slider("scaled rotate", 0));
        pg.scale(slider("scale", 1.25f));
        pg.imageMode(CENTER);
        pg.tint(255, slider("tint alpha", 50));
        pg.image(pg,0,0);
        pg.popMatrix();

        pg.blendMode(SUBTRACT);
        pg.noStroke();
        pg.fill(slider("darken", 5));
        pg.rect(0,0,width,height);

        if(toggle("add", true)) {
            pg.blendMode(ADD);
        }else {
            pg.blendMode(BLEND);
        }

        pg.strokeWeight(slider("weight", 6));
        int count = sliderInt("count", 200);
        float r = slider("radius", 500);
        float freq = slider("noise freq", 4);
        float timeRadius = slider("time radius", 1);
        float timeSpeed = sliderInt("time speed", 1);
        for(int i = 0; i < count; i++){
            float norm = norm(i, 0, count);
            float timeX = timeRadius * sin(t*timeSpeed);
            float timeY = timeRadius * cos(t*timeSpeed);
            float noiseX = norm*freq;
            float x = (float) (r*(noise.eval(noiseX,timeX+100, timeY+30)));
            float y = (float) (r*(noise.eval(noiseX+100, timeX-20, timeY+100)));
            pg.stroke(gradientColorAt("gradient", norm));
            drawMirrored(x,y);
        }

        pg.endDraw();
        image(pg, 0, 0);
        rec(pg, sliderInt("frames", 360));
        gui();
    }

    void drawMirrored(float x, float y){
        pg.pushMatrix();
        pg.translate(width * .5f, height * .5f);
        for(int i = 0; i < mirrors; i++){
            float angle = map(i, 0, mirrors, 0, TAU);
            pg.pushMatrix();
            pg.rotate(angle);
            pg.point(x,y);
            pg.popMatrix();
        }
        pg.popMatrix();
    }
}
