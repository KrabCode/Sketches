package _2020_11;

import applet.KrabApplet;
import processing.core.PGraphics;
//import codeanticode.tablet.*;

import java.util.ArrayList;

public class TabletDrawing extends KrabApplet {
    private PGraphics pg;
//    Tablet tablet;
    ArrayList<Movement> movements = new ArrayList<>();
    float deleteRadius = 15;

    public static void main(String[] args) {
        KrabApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(1600,1000,P3D);
    }


    public void setup() {
//        tablet = new Tablet(this);
        colorMode(HSB, 1, 1, 1, 1);
        picker("stroke");
    }

    public void draw() {
        pg = updateGraphics(pg);
        pg.beginDraw();
        pg.image(gradient("background"), 0, 0);
        for (Movement m : movements) {
            pg.beginShape();
            for (Line line : m.lines) {
                pg.noFill();
                pg.stroke(line.strokeColor.clr());
                if(line.skip) {
                    pg.noStroke();
                }
                pg.strokeWeight(line.strokeWeight);
                pg.vertex(line.x0, line.y0);
                pg.vertex(line.x1, line.y1);
            }
            pg.endShape();
        }
        pg.endDraw();
        background(0.1f);
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    public void keyPressed() {
        super.keyPressed();
        if (key == 's') {
            String fileName = year()+nf(month(), 2, 0)+nf(day(), 2, 0)+"_"+nf(hour(), 2, 0)+"."+nf(minute(), 2, 0)+"."+nf(second(), 2, 0);
            save(fileName + ".png");
        }
        if (keyCode == 90) {
            deleteLastMovement();
        }
    }

    public void mousePressed() {
        super.mousePressed();
        if (mouseButton == LEFT) {
            movements.add(new Movement());
        }
        mouseInteract();
    }

    public void mouseReleased() {
        super.mouseReleased();
    }

    public void mouseDragged() {
        super.mouseDragged();
        mouseInteract();
    }

    void mouseInteract() {
        if (mouseButton == RIGHT) {
            drawDeleteCircle(mouseX, mouseY);
            removeLinesCloseTo(mouseX, mouseY);
        } else if (mouseButton == LEFT) {
            makeLine();
        }
    }

    void drawDeleteCircle(float x, float y) {
        stroke(1);
        strokeWeight(2);
        noFill();
        ellipse(x, y, deleteRadius, deleteRadius);
    }

    void makeLine() {
        float pressure = 0 ; // tablet.getPressure();
        if(!mousePressedOutsideGui) {
            return;
        }
        float weight = 1+pow(pressure, slider("pressure curve", 1, .01f, 9.9f))*slider("pressure max", 30);
        getCurrentMovement().lines.add(new Line(pmouseX, pmouseY, mouseX, mouseY, pressure, weight, picker("stroke").copy()));
    }

    Movement getCurrentMovement() {
        if (movements.isEmpty()) {
            movements.add(new Movement());
        }
        return movements.get(movements.size()-1);
    }

    void deleteLastMovement() {
        if (movements.isEmpty()) {
            return;
        }
        movements.remove(movements.size()-1);
    }

    void removeLinesCloseTo(float x, float y) {
        for (Movement m : movements) {
            for (Line line : m.lines) {
                float dist = min(dist(line.x0, line.y0, x, y), dist(line.x1, line.y1, x, y));
                if (dist < 10) {
                    line.skip = true;
                }
            }
        }
    }

    class Movement {
        ArrayList<Line> lines = new ArrayList<Line>();
    }

    class Line {
        public boolean skip;
        HSBA strokeColor;
        float strokeWeight;
        float x0, y0, x1, y1;
        float pressure;
        Line(float x0, float y0, float x1, float y1, float pressure, float strokeWeight, HSBA strokeColor) {
            this.x0 = x0;
            this.y0 = y0;
            this.x1 = x1;
            this.y1 = y1;
            this.pressure = pressure;
            this.strokeWeight = strokeWeight;
            this.strokeColor = strokeColor;
        }
    }
}
