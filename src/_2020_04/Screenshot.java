package _2020_04;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PImage;

import java.awt.*;

public class Screenshot extends KrabApplet {
    private PGraphics pg;
    private Robot robot;

    public static void main(String[] args) {
        KrabApplet.main(new Object() {}.getClass().getEnclosingClass().getName());
    }

    public void settings() {
        float scale = .9f;
        size(floor(1920*scale), floor(1080*.9f), P2D);
    }

    public void setup() {
        pg = createGraphics(width, height, P2D);
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void draw() {
        pg.beginDraw();
        pg.translate(width*.5f, height*.5f);
        pg.rotate(slider("rotate"));
        pg.imageMode(CENTER);
        pg.scale(slider("scale", 1));
        pg.image(screenshot(), 0, 0);
        pg.endDraw();
        image(pg, 0, 0);
        gui();
        rec(g);
    }

    PImage screenshot(){
        return new PImage(robot.createScreenCapture(new Rectangle(1920, 1080)));
    }
}
