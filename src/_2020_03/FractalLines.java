package _2020_03;

import applet.KrabApplet;
import processing.core.PGraphics;
import processing.core.PVector;

import java.util.ArrayList;

public class FractalLines extends KrabApplet {

    private PGraphics pg;
    ArrayList<String> pathIterations = new ArrayList<>();
    ArrayList<Rule> iterationRules = new ArrayList<>();

    public static void main(String[] args) {
        KrabApplet.main(new Object() {}.getClass().getEnclosingClass().getName());
    }

    public void settings() {
        size(800, 800, P2D);
    }

    public void setup() {
        pg = createGraphics(width, height, P2D);
        pathIterations.clear();
        String axiom = "F--F--F";
        pathIterations.add(axiom);
    }

    public void draw() {
        pg.beginDraw();
        group("shaders");
        fadeToBlack(pg);
        blurPass(pg);
        pg.translate(width/2, height/2);
        updateRules();
        drawFractal();
        pg.endDraw();
        image(pg, 0, 0);
        rec(pg);
        gui();
    }

    private void drawFractal() {
        group("draw");
        PVector translate = sliderXY("translate");
        pg.translate(translate.x, translate.y);
        float stepSize = slider("size", 1);
        String path = pathIterations.get(pathIterations.size()-1);
        pg.stroke(picker("stroke").clr());
        pg.strokeWeight(slider("weight"));
        for (char c : path.toCharArray()) {
            if (c == 'F') {
                pg.line(0,0,stepSize,0);
                pg.translate(stepSize, 0);
            }
            if (c == '-') {
                pg.rotate(-PI/3f);
            }
            if (c == '+') {
                pg.rotate(PI/3f);
            }
        }
    }

    private void updateRules() {
        group("rules");
        iterationRules.clear();
        if (toggle("F -> F+F–F+F", true)) {
            iterationRules.add(new Rule('F', "F+F--F+F"));
        }
        if (button("generate next")) {
            String next = applyRule(pathIterations.get(pathIterations.size()-1), iterationRules);
            pathIterations.add(next);
            println(pathIterations.size(), next.length());
        }
    }


    private String applyRule(String input, ArrayList<Rule> rules) {
        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            boolean matched = false;
            for (Rule rule : rules) {
                if (c == rule.match) {
                    result.append(rule.result);
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                result.append(c);
            }
        }
        println(result.toString());
        return result.toString();
    }

    static class Rule {
        char match;
        String result;

        Rule(char match, String result) {
            this.match = match;
            this.result = result;
        }
    }
}
