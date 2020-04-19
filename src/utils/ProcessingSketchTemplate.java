package utils;

import applet.KrabApplet;
import processing.core.PApplet;
import processing.core.PGraphics;

import java.lang.invoke.MethodHandles;

public class ProcessingSketchTemplate extends PApplet {

    public static void main(String[] args) {
        PApplet.main(MethodHandles.lookup().lookupClass());
    }

    public void settings(){
        size(800,800,P2D);
    }

    public void setup(){

    }

    public void draw(){
        background(0);
    }
}
