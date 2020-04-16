package _2020_03;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import utils.OpenSimplexNoise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("DuplicatedCode")
public class ConcentricClouds_2 extends KrabApplet {
    private OpenSimplexNoise noise = new OpenSimplexNoise();
    private PGraphics pg;
    private ArrayList<PImage> imagesToConsider = new ArrayList<>();
    private Map<String, PImage> cloudImages = new HashMap<>();
    private float cloudCount;

    public static void main(String[] args) {
        KrabApplet.main(new Object() {}.getClass().getEnclosingClass().getName());
    }

    public void settings() {
        size(800, 800, P2D);
    }

    public void setup() {
        surface.setAlwaysOnTop(true);
        cloudImages.put("chubb0", loadImage("data/shaders/_2020_03/clouds/oriznute/chubb0.png"));
        cloudImages.put("chubb1", loadImage("data/shaders/_2020_03/clouds/oriznute/chubb1.png"));
        cloudImages.put("chubb2", loadImage("data/shaders/_2020_03/clouds/oriznute/chubb2.png"));
        cloudImages.put("chubb3", loadImage("data/shaders/_2020_03/clouds/oriznute/chubb3.png"));
        cloudImages.put("chubb23", loadImage("data/shaders/_2020_03/clouds/oriznute/chubb23.png"));
        cloudImages.put("eased1", loadImage("data/shaders/_2020_03/clouds/oriznute/eased1.png"));
        cloudImages.put("eased2", loadImage("data/shaders/_2020_03/clouds/oriznute/eased2.png"));
        cloudImages.put("eased23", loadImage("data/shaders/_2020_03/clouds/oriznute/eased23.png"));

        colorMode(HSB, 1, 1, 1);
        pg = createGraphics(width, height, P2D);
        frameRecordingDuration *= 2;
    }



    public void draw() {
        background();
        pg.beginDraw();
        pg.translate(width * .5f, height * .5f);
        updateClouds();
        filter();
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    private void updateClouds() {
        imagesToConsider.clear();
        group("images");
        for (String key : cloudImages.keySet()) {
            if (toggle(key, true)) {
                imagesToConsider.add(cloudImages.get(key));
            }
        }
        group("draw");
        cloudCount = sliderInt("cloud count", 30);
        float tr = slider("time radius", 1);
        float irot = slider("i rot freq");
        float minScale = slider("min scale");
        float maxScale = slider("max scale");
        for (int i = 0; i < cloudCount; i++) {
            float inorm = clampNorm(i, 0, cloudCount - 1);
            float rotation = 0;
            if(toggle("looping noise")){
                rotation =   (float) (slider("rot amp") * (1 - 2 * noise.eval(
                        inorm * irot + tr * cos(t),
                        inorm * irot + tr * sin(t))));
            }else{
                rotation = (float)(slider("rot amp") * 1-2*noise.eval(
                        inorm*irot+tr*t,
                        inorm*irot+tr*t));
            }
            pg.imageMode(CENTER);
            pg.push();
            float myScale = lerp(minScale, maxScale, (inorm+t*slider("fly speed"))%1);
            pg.scale(myScale);
            pg.rotate(2*TAU*randomDeterministic(i*3)+rotation);
            pg.image(imagesToConsider.get(floor(randomDeterministic(i) * imagesToConsider.size())), 0, 0);
            pg.pop();
        }

    }

    private void background() {
        group("background");
        String background = "shaders/_2020_03/clouds/background.glsl";
        uniform(background).set("time", t);
        uniformColorPalette(background);
        hotFilter(background, pg);
    }

    private void filter() {
        String filter = "shaders/_2020_03/clouds/filter.glsl";
        uniform(filter).set("time", t);
        hotFilter(filter, pg);
    }

}
