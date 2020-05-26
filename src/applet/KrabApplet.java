package applet;

import processing.core.*;
import processing.event.MouseEvent;
import processing.opengl.PShader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.currentTimeMillis;

/**
 * This class offers common functionality to all of my processing sketches, including a GUI, shader reloading at
 * runtime and many other utility functions and features
 */

@SuppressWarnings({"WeakerAccess", "SameParameterValue", "unused", "ConstantConditions"})
public abstract class KrabApplet extends PApplet {
    private static final String STATE_BEGIN = "STATE_BEGIN";
    private static final String STATE_END = "STATE_END";
    private static final String SEPARATOR = "§";
    private static final String UNDO_PREFIX = "UNDO";
    private static final String REDO_PREFIX = "REDO";
    private static final String GROUP_PREFIX = "GROUP";
    private static final String ACTION_PRECISION_ZOOM_IN = "PRECISION_ZOOM_IN";
    private static final String ACTION_PRECISION_ZOOM_OUT = "PRECISION_ZOOM_OUT";
    private static final String ACTION_RESET = "RESET";
    private static final String ACTION_HIDE = "HIDE";
    private static final String ACTION_UNDO = "UNDO";
    private static final String ACTION_REDO = "REDO";
    private static final String ACTION_SAVE = "SAVE";
    private static final String ACTION_COPY = "COPY";
    private static final String ACTION_PASTE = "PASTE";
    private static final int MENU_BUTTON_COUNT = 4;
    private static final String MENU_BUTTON_HIDE = "hide";
    private static final String MENU_BUTTON_UNDO = "undo";
    private static final String MENU_BUTTON_REDO = "redo";
    private static final String MENU_BUTTON_SAVE = "save";
    private static final String SATURATION = "saturation";
    private static final String BRIGHTNESS = "brightness";
    private static final String HUE = "hue";
    private static final float BACKGROUND_ALPHA = .9f;
    private static final float GRAYSCALE_GRID = .3f;
    private static final float GRAYSCALE_TEXT_DARK = .5f;
    private static final float GRAYSCALE_TEXT_SELECTED = 1;
    private static final float INT_PRECISION_MAXIMUM = 100000;
    private static final float INT_PRECISION_MINIMUM = 10f;
    private static final float FLOAT_PRECISION_MAXIMUM = 10000;
    private static final float FLOAT_PRECISION_MINIMUM = .01f;
    private static final float ALPHA_PRECISION_MINIMUM = .005f;
    private static final float ALPHA_PRECISION_MAXIMUM = 10;
    private static final float INTEGER_SLIDER_ROUNDING_LERP_AMT = .05f;
    private static final float UNDERLINE_TRAY_ANIMATION_DURATION = 10;
    private static final float UNDERLINE_TRAY_ANIMATION_EASING = 3;
    private static final float SLIDER_EDGE_DARKEN_EASING = 3;
    private static final float SLIDER_REVEAL_DURATION = 15;
    private static final float SLIDER_REVEAL_START_SKIP = SLIDER_REVEAL_DURATION * .25f;
    private static final float SLIDER_REVEAL_EASING = 1;
    private static final float PICKER_REVEAL_DURATION = 15;
    private static final float PICKER_REVEAL_EASING = 1;
    private static final float PICKER_REVEAL_START_SKIP = PICKER_REVEAL_DURATION * .25f;
    private static final int KEY_REPEAT_DELAY_FIRST = 300;
    private static final int KEY_REPEAT_DELAY = 30;
    private static final float MENU_ROTATION_DURATION = 20;
    private static final float MENU_ROTATION_EASING = 2;
    private static final float DESELECTION_FADEOUT_DURATION = 10;
    private static final float DESELECTION_FADEOUT_EASING = 1;
    private static final float CHECK_ANIMATION_DURATION = 10;
    private static final float CHECK_ANIMATION_EASING = 1;
    private static final float GROUP_TOGGLE_ANIMATION_EASING = 1;
    private static final int GROUP_TOGGLE_ANIMATION_DURATION = 10;
    private static String clipboardSliderFloat = "";
    private static String clipboardSliderXYZ = "";
    private static String clipboardPicker = "";
    private final boolean onWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    private final float textSize = onWindows ? 24 : 48;
    private final float cell = onWindows ? 40 : 80;
    private final float hideButtonWidth = cell * 2;
    private final float menuButtonSize = cell * 1.5f;
    private final float previewTrayBoxWidth = cell * .375f;
    private final float previewTrayBoxMargin = cell * .125f;
    private final float previewTrayBoxOffsetY = -cell * .025f;
    private final float minimumTrayWidth = hideButtonWidth + (MENU_BUTTON_COUNT - 1) * menuButtonSize;
    private final float MAXIMUM_TRAY_WIDTH = cell * 16;
    private final float sliderHeight = cell * 2;
    private final ArrayList<ArrayList<String>> undoStack = new ArrayList<>();
    private final ArrayList<ArrayList<String>> redoStack = new ArrayList<>();
    private final ArrayList<Group> groups = new ArrayList<>();
    private final ArrayList<Key> keyboardKeys = new ArrayList<>();
    private final ArrayList<Key> keyboardKeysToRemove = new ArrayList<>();
    private final ArrayList<String> actions = new ArrayList<>();
    private final ArrayList<String> previousActions = new ArrayList<>();
    private final int menuButtonHoldThreshold = 60;
    private final ArrayList<Float> scrollOffsetHistory = new ArrayList<>();
    private final ArrayList<ShaderSnapshot> snapshots = new ArrayList<>();
    private final int shaderRefreshRateInMillis = 36;
    private final PMatrix3D mouseRotation = new PMatrix3D();
    private final Map<String, PMatrix3D> sliderRotationMatrixMap = new HashMap<>();
    private final Map<String, PVector> previousSliderRotationMap = new HashMap<>();
    private final PVector[] primaryColorMultipliers = new PVector[]{
            new PVector(1, 0, 0),
            new PVector(0, 1, 0),
            new PVector(0, 0, 1)};
    protected String captureDir;
    protected String id = regenIdAndCaptureDir();
    protected float t;
    protected boolean mousePressedOutsideGui = false;
    protected int frameRecordingStarted = 0;
    protected int frameRecordingDuration = 360; // assuming t += radians(1) per frame for a perfect loop
    protected float timeSpeed = 1;
    private float trayWidthWhenExtended = minimumTrayWidth;
    private float trayWidth = minimumTrayWidth;
    private boolean captureScreenshot = false;
    private int screenshotsAlreadyCaptured = 0;
    private Group currentGroup = null;
    private boolean pMousePressed = false;
    private boolean trayVisible = true;
    private boolean overlayVisible;
    private boolean horizontalOverlayVisible;
    private boolean verticalOverlayVisible;
    private boolean pickerOverlayVisible;
    private boolean zOverlayVisible;
    private Element overlayOwner = null; // do not assign directly!
    private float underlineTrayAnimationStarted = -UNDERLINE_TRAY_ANIMATION_DURATION;
    private float undoRotationStarted = -MENU_ROTATION_DURATION;
    private float redoRotationStarted = -MENU_ROTATION_DURATION;
    private float hideRotationStarted = -MENU_ROTATION_DURATION;
    private float saveAnimationStarted = -MENU_ROTATION_DURATION;
    private int undoHoldDuration = 0;
    private int redoHoldDuration = 0;
    private float trayScrollOffset = 0;
    private PGraphics colorSplitResult;
    private PGraphics[] primaryColorCanvases;
    private PGraphics shaderRamp;

    // GUI INTERFACE

    protected int sliderInt() {
        return floor(sliderInt("x"));
    }

    protected int sliderInt(String name) {
        return floor(sliderInt(name, 0));
    }

    protected int sliderInt(String name, int defaultValue) {
        return sliderInt(name, defaultValue, numberOfDigitsInFlooredNumber(defaultValue) * 100);
    }

    protected int sliderInt(String name, int max, boolean defaultMax) {
        return sliderInt(name, defaultMax ? 0 : max, numberOfDigitsInFlooredNumber(max) * 100);
    }

    protected int sliderInt(String name, int defaultValue, int precision) {
        return floor(slider(name, defaultValue, precision, false, -Float.MAX_VALUE, Float.MAX_VALUE, true));
    }

    protected int sliderInt(String name, int min, int max, int defaultValue) {
        return floor(slider(name, defaultValue, numberOfDigitsInFlooredNumber(max) * 100, true, min, max, true));
    }

    protected int sliderInt(String name, int defaultValue, int precision, boolean constrained, int min, int max) {
        return floor(slider(name, defaultValue, precision, constrained, min, max, true));
    }

    protected float slider() {
        return slider("x", 0);
    }

    protected float slider(String name) {
        return slider(name, 0);
    }

    protected float slider(String name, float defaultValue) {
        return slider(name, defaultValue, numberOfDigitsInFlooredNumber(defaultValue) * 10);
    }

    protected float slider(String name, float max, boolean defaultMax) {
        return slider(name, defaultMax ? 0 : max, max * .5f);
    }

    protected float slider(String name, float defaultValue, float precision) {
        return slider(name, defaultValue, precision, false, -Float.MAX_VALUE, Float.MAX_VALUE, false);
    }

    protected float slider(String name, float min, float max, float defaultValue) {
        return slider(name, defaultValue, max - min, true, min, max, false);
    }

    protected float slider(String name, float defaultValue, float precision, boolean constrained, float min,
                           float max) {
        return slider(name, defaultValue, precision, constrained, min, max, false);
    }

    protected float slider(String name, float defaultValue, float precision, boolean constrained, float min,
                           float max, boolean floored) {
        Group currentGroup = getCurrentGroup();
        if (elementDoesntExist(name, currentGroup.name)) {
            SliderFloat newElement = new SliderFloat(currentGroup, name, defaultValue, precision,
                    constrained, min, max, floored);
            currentGroup.elements.add(newElement);
        }
        SliderFloat slider = (SliderFloat) findElement(name, currentGroup.name);
        return slider.value;
    }

    protected PVector sliderXY() {
        return sliderXY("xy");
    }


    protected PVector sliderXY(String name, float defaultX, float defaultY) {
        return sliderXY(name, defaultX, defaultY, numberOfDigitsInFlooredNumber(max(defaultX, defaultY)) * 10);
    }

    protected PVector sliderXY(String name, float defaultXY) {
        return sliderXY(name, defaultXY, defaultXY, numberOfDigitsInFlooredNumber(defaultXY) * 10);
    }

    protected PVector sliderXY(String name) {
        return sliderXY(name, 0, 0, 100);
    }

    protected PVector sliderXY(String name, float defaultX, float defaultY, float precision) {
        Group currentGroup = getCurrentGroup();
        if (elementDoesntExist(name, currentGroup.name)) {
            SliderXY newElement = new SliderXY(currentGroup, name, defaultX, defaultY, precision);
            currentGroup.elements.add(newElement);
        }
        SliderXY slider = (SliderXY) findElement(name, currentGroup.name);
        return slider.value;
    }

    protected PVector sliderXYZ(String name, float value, float precision) {
        return sliderXYZ(name, value, value, value, precision);
    }

    protected PVector sliderXYZ() {
        return sliderXYZ("xyz", 0, 0, 0, 100);
    }

    protected PVector sliderXYZ(String name) {
        return sliderXYZ(name, 0);
    }

    protected PVector sliderXYZ(String name, float defaultXYZ) {
        return sliderXYZ(name, defaultXYZ, defaultXYZ, defaultXYZ, numberOfDigitsInFlooredNumber(defaultXYZ) * 10);
    }

    protected PVector sliderXYZ(String name, float defaultX, float defaultY, float defaultZ) {
        return sliderXYZ(name, defaultX, defaultY, defaultZ, numberOfDigitsInFlooredNumber(max(max(defaultX,
                defaultY), defaultZ) * 10));
    }

    protected PVector sliderXYZ(String name, float x, float y, float z, float precision) {
        Group currentGroup = getCurrentGroup();
        if (elementDoesntExist(name, currentGroup.name)) {
            SliderXYZ newElement = new SliderXYZ(currentGroup, name, x, y, z, precision);
            currentGroup.elements.add(newElement);
        }
        SliderXYZ slider = (SliderXYZ) findElement(name, currentGroup.name);
        return slider.value;
    }

    protected HSBA picker() {
        return picker("color");
    }

    protected HSBA picker(String name) {
        return picker(name, 0, 0, .5f, 1);
    }

    protected HSBA picker(String name, float grayscale) {
        return picker(name, 0, 0, grayscale);
    }

    protected HSBA picker(String name, int grayscale) {
        return picker(name, 0, 0, grayscale);
    }

    protected HSBA picker(String name, float grayscale, float alpha) {
        return picker(name, 0, 0, grayscale, alpha);
    }

    protected HSBA picker(String name, float hue, float sat, float br) {
        return picker(name, hue, sat, br, 1);
    }

    protected HSBA picker(String name, float hue, float sat, float br, float alpha) {
        Group currentGroup = getCurrentGroup();
        if (elementDoesntExist(name, currentGroup.name)) {
            ColorPicker newElement = new ColorPicker(currentGroup, name, hue, sat, br, alpha);
            currentGroup.elements.add(newElement);
        }
        ColorPicker picker = (ColorPicker) findElement(name, currentGroup.name);
        if (picker != null) {
            return picker.getHSBA();
        }
        return new HSBA();
    }

    protected String optionsABC() {
        return options("A", "B", "C");
    }

    protected String options(String defaultValue, String... otherValues) {
        Group currentGroup = getCurrentGroup();
        if (elementDoesntExist(defaultValue, currentGroup.name)) {
            Element newElement = new Radio(currentGroup, defaultValue, otherValues);
            currentGroup.elements.add(newElement);
        }
        Radio radio = (Radio) findElement(defaultValue, currentGroup.name);
        return radio.options.get(radio.valueIndex);
    }

    protected boolean button() {
        return button("button");
    }

    protected boolean button(String name) {
        Group currentGroup = getCurrentGroup();
        if (elementDoesntExist(name, currentGroup.name)) {
            Button newElement = new Button(currentGroup, name);
            currentGroup.elements.add(newElement);
        }
        Button button = (Button) findElement(name, currentGroup.name);
        return button.value;
    }

    protected boolean toggle() {
        return toggle("toggle");
    }

    protected boolean toggle(String name) {
        return toggle(name, false);
    }

    protected boolean toggle(String name, boolean defaultState) {
        Group currentGroup = getCurrentGroup();
        if (elementDoesntExist(name, currentGroup.name)) {
            Toggle newElement = new Toggle(currentGroup, name, defaultState);
            currentGroup.elements.add(newElement);
        }
        Toggle toggle = (Toggle) findElement(name, currentGroup.name);
        return toggle.checked;
    }

    protected void gui() {
        gui(true);
    }

    /**
     * Must be called every frame for the GUI to update and display.
     *
     * @param defaultVisibility should the GUI tray start in the shown state?
     */
    protected void gui(boolean defaultVisibility) {
        t += radians(timeSpeed);
        guiSetup(defaultVisibility);
        updateFps();
        updateKeyboardInput();
        updateMouseState();
        pushStyle();
        pushMatrix();
        strokeCap(SQUARE);
        colorMode(HSB, 1, 1, 1, 1);
        resetMatrixInAnyRenderer();
        hint(DISABLE_DEPTH_TEST);
        updateTrayBackground();
        updateMenuButtons();
        updateScrolling();
        updateGroupsAndTheirElements();
        if (overlayVisible && trayVisible) {
            overlayOwner.updateOverlay();
        }
        hint(ENABLE_DEPTH_TEST);
        popStyle();
        popMatrix();
        pMousePressed = mousePressed;
        if (frameCount == 1) {
            trayVisible = elementCount() != 0;
        }
        resetGroup();
    }

    // GENERAL UTILS

    protected void lights(PGraphics pg) {
        lights(pg, 1);
    }

    /**
     * Lights a 3D scene. Any lights beyond the defaultLightCount won't be loaded at startup.
     *
     * @param pg                PGraphics to light
     * @param defaultLightCount default number of directional lights
     */
    protected void lights(PGraphics pg, int defaultLightCount) {
        group("lights");
        HSBA ambientColor = picker("ambient");
        HSBA dirLightColor = picker("dir light color");
        HSBA specLightColor = picker("spec light color");
        pg.ambient(ambientColor.hue(), ambientColor.sat(), ambientColor.br());
        pg.ambientLight(ambientColor.hue(), ambientColor.sat(), ambientColor.br());
        pg.lightSpecular(specLightColor.hue(), specLightColor.sat(), specLightColor.br());
        pg.shininess(slider("shine"));
        for (int i = 0; i < sliderInt("light count", defaultLightCount); i++) {
            PVector dirLightDir = sliderXYZ("light " + (i + 1));
            pg.directionalLight(dirLightColor.hue(), dirLightColor.sat(), dirLightColor.br(), dirLightDir.x,
                    dirLightDir.y, dirLightDir.z);
        }
        resetGroup();
    }

    protected void style(PGraphics pg) {
        style(pg, "");
    }

    /**
     * Applies stroke weight, stroke and fill using the GUI.
     *
     * @param pg     PGraphics to apply to
     * @param prefix optional GUI element prefix
     */
    protected void style(PGraphics pg, String prefix) {
        prefix += " ";
        pg.strokeWeight(slider(prefix + "weight"));
        pg.stroke(picker(prefix + "stroke").clr());
        pg.fill(picker(prefix + "fill").clr());
    }

    protected void fadeToBlack() {
        fadeToBlack(g);
    }

    /**
     * Subtracts all colors from the image, resulting in a slow darkening of any image.
     * Leaves no gray traces as opposed to drawing a transparent black rectangle over the sketch.
     *
     * @param pg PGraphics to darken
     */
    protected void fadeToBlack(PGraphics pg) {
        pg.pushStyle();
        pg.colorMode(HSB, 255, 255, 255, 255);
        pg.hint(DISABLE_DEPTH_TEST);
        pg.blendMode(SUBTRACT);
        pg.noStroke();
        pg.fill(255, slider("fade to black", 0, 255, 50));
        pg.rectMode(CENTER);
        pg.rect(0, 0, width * 2, height * 2);
        pg.hint(ENABLE_DEPTH_TEST);
        pg.popStyle();
    }

    /**
     * Adds white to an image. See the fadeToBlack method.
     *
     * @param pg PGraphics to brighten
     */
    protected void fadeToWhite(PGraphics pg) {
        pg.pushStyle();
        pg.colorMode(HSB, 255, 255, 255, 255);
        pg.hint(DISABLE_DEPTH_TEST);
        pg.blendMode(ADD);
        pg.noStroke();
        pg.fill(255, slider("fade to white", 0, 255, 50));
        pg.rectMode(CENTER);
        pg.rect(0, 0, width * 2, height * 2);
        pg.hint(ENABLE_DEPTH_TEST);
        pg.popStyle();
    }

    protected void mouseRotation() {
        mouseRotation(g);
    }

    /**
     * Allows mouse rotation control and applies the rotation to the PGraphics.
     * Rotations are pre-applied, so the axes are not affected by any previous rotations, which makes it more intuitive.
     *
     * @param pg PGraphics to rotate
     */
    protected void mouseRotation(PGraphics pg) {
        if (mousePressedOutsideGui) {
            if (mouseButton == LEFT) {
                float x = mouseX - pmouseX;
                float y = mouseY - pmouseY;
                float angle = mag(x, y) * 0.01f;
                PMatrix3D temp = new PMatrix3D();
                temp.rotate(angle, -y, x, 0);
                mouseRotation.preApply(temp);
            }
        }
        pg.applyMatrix(mouseRotation);
    }


    /**
     * Translates to the center of the sketch.
     *
     * @param pg PGraphics to translate in
     */
    protected void translateToCenter(PGraphics pg) {
        pg.translate(pg.width * .5f, pg.height * .5f);
    }

    protected void translate(PGraphics pg) {
        translate(pg, "translate");
    }

    /**
     * Translates to an arbitrary vector controlled a 3D slider.
     *
     * @param pg         PGraphics to translate in
     * @param sliderName optional name of the slider
     */
    protected void translate(PGraphics pg, String sliderName) {
        PVector translate = sliderXYZ(sliderName);
        pg.translate(translate.x, translate.y, translate.z);
    }

    protected void preRotate(PGraphics pg) {
        preRotate(pg, "rotate");
    }

    /**
     * Rotates to an arbitrary vector controlled by a 3D slider.
     * Rotations are pre-applied, so the axes are not affected by any previous rotations, which makes it more intuitive.
     * This method can be called any number of times - as long as the slider names are unique it will produce a unique
     * rotation.
     *
     * @param pg         PGraphics to rotate
     * @param sliderName name of the slider and the key of the PMatrix in the sliderRotationMatrixMap
     */
    protected void preRotate(PGraphics pg, String sliderName) {
        PMatrix3D rotationMatrix;
        PVector previousSliderRotation = new PVector();
        if (sliderRotationMatrixMap.containsKey(sliderName)) {
            rotationMatrix = sliderRotationMatrixMap.get(sliderName);
            previousSliderRotation = previousSliderRotationMap.get(sliderName);
        } else {
            rotationMatrix = new PMatrix3D();
            sliderRotationMatrixMap.put(sliderName, rotationMatrix);
        }
        PVector rotation = sliderXYZ(sliderName, 0);
        PVector delta = PVector.sub(previousSliderRotation, rotation);
        if (previousSliderRotation.mag() != 0 && rotation.mag() == 0) {
            delta.mult(0);
            rotationMatrix.reset();
        }
        PMatrix3D temp = new PMatrix3D();
        temp.rotateX(delta.y);
        temp.rotateY(-delta.x);
        temp.rotateZ(delta.z);
        rotationMatrix.preApply(temp);
        pg.applyMatrix(rotationMatrix);
        previousSliderRotation = rotation.copy();
        previousSliderRotationMap.put(sliderName, previousSliderRotation);
    }

    protected PGraphics colorSplit(PGraphics pg) {
        return colorSplit(pg, false);
    }

    /**
     * Takes a PGraphics, splits it up into primary color images and re-assembles them using blendMode(ADD) at
     * different scales growing from the center.
     *
     * @param pg                  input image
     * @param drawResultOverInput draws the result over the input - this method expects the PGraphics to be already
     *                            closed with endDraw() and it calls beginDraw() and endDraw() by itself.
     * @return color split image
     */
    protected PGraphics colorSplit(PGraphics pg, boolean drawResultOverInput) {
        group("color split");
        if (toggle("skip")) {
            return pg;
        }
        if (colorSplitResult == null || colorSplitResult.width != pg.width || colorSplitResult.height != pg.height) {
            colorSplitResult = createGraphics(pg.width, pg.height, P2D);
            primaryColorCanvases = new PGraphics[3];
            for (int i = 0; i < 3; i++) {
                primaryColorCanvases[i] = createGraphics(pg.width, pg.height, P2D);
            }
        }
        PVector scale = sliderXYZ("RGB scales", 1, 0.1f);
        float[] scales = new float[]{scale.x, scale.y, scale.z};
        if (toggle("force scales >= 1")) {
            while (scales[0] < 1 || scales[1] < 1 || scales[2] < 1) {
                //scales smaller than 1 result in ugly edges, we're more interested in the relative color scales anyway
                scales[0] += .001;
                scales[1] += .001;
                scales[2] += .001;
            }
        }
        for (int i = 0; i < 3; i++) {
            PGraphics primaryColorCanvas = primaryColorCanvases[i];
            primaryColorCanvas.beginDraw();
            primaryColorCanvas.clear();
            primaryColorCanvas.image(pg, 0, 0, width, height);
            primaryColorMultipliers[i] = sliderXYZ("multiplier " + (i + 1),
                    primaryColorMultipliers[i].x,
                    primaryColorMultipliers[i].y,
                    primaryColorMultipliers[i].z);
            colorFilter(primaryColorCanvas, primaryColorMultipliers[i]);
            primaryColorCanvas.endDraw();
        }
        colorSplitResult.beginDraw();
        colorSplitResult.clear();
        colorSplitResult.imageMode(CENTER);
        colorSplitResult.blendMode(ADD);
        colorSplitResult.translate(colorSplitResult.width / 2f, colorSplitResult.height / 2f);
        for (int i = 0; i < 3; i++) {
            colorSplitResult.pushMatrix();
            colorSplitResult.scale(scales[i]);
            colorSplitResult.image(primaryColorCanvases[i], 0, 0);
            colorSplitResult.popMatrix();
        }
        colorSplitResult.endDraw();
        if (drawResultOverInput) {
            pg.beginDraw();
            pg.pushStyle();
            pg.clear();
            pg.hint(PConstants.DISABLE_DEPTH_TEST);
            pg.imageMode(CORNER);
            pg.image(colorSplitResult, 0, 0, pg.width, pg.height);
            pg.hint(PConstants.ENABLE_DEPTH_TEST);
            pg.popStyle();
            pg.endDraw();
        }
        return colorSplitResult;
    }

    protected void colorFilter(PGraphics toFilter, PVector multiplier) {
        String filterShader = "shaders/filters/colorFilter.glsl";
        uniform(filterShader).set("multiplier", multiplier);
        hotFilter(filterShader, toFilter);
    }

    /**
     * Takes any float and returns the positive fractional part of it, so the result is always between 0 and 1.
     * For example -0.1 becomes 0.1 and 1.5 becomes 0.5. Used with hue due to its cyclical
     * nature.
     *
     * @param hue float to apply modulo to
     * @return float in the range [0,1)
     */
    protected float hueModulo(float hue) {
        while (hue < 0) {
            hue += 1;
        }
        hue %= 1;
        return hue;
    }

    /**
     * Returns the number of digits in a floored number. Useful for approximating the most useful default precision
     * of a slider.
     *
     * @param inputNumber number to floor and check the size of
     * @return number of digits in floored number
     */
    private int numberOfDigitsInFlooredNumber(float inputNumber) {
        return String.valueOf(floor(inputNumber)).length();
    }

    /**
     * Prepares a new path for capturing images (i.e. every time the sketch is run or a video is recorded).
     *
     * @return new sketch id
     */
    private String regenIdAndCaptureDir() {
        String newId = year() + nf(month(), 2) + nf(day(), 2) + "-" + nf(hour(), 2) + nf(minute(), 2) + nf(second(),
                2) + "_" + this.getClass().getSimpleName();
        captureDir = "out/capture/" + newId + "/";
        return newId;
    }

    /**
     * A random function that always returns the same number for the same seed.
     *
     * @param seed seed to use
     * @return hash value between 0 and 1
     */
    protected float hash(float seed) {
        return abs(sin(seed * 323.121f) * 454.123f) % 1;
    }

    /**
     * Constructs a random image url with the specified size.
     *
     * @param width  image width to request
     * @param height image height to request
     * @return random image url
     */
    public String randomImageUrl(float width, float height) {
        return "https://picsum.photos/" + floor(width) + "/" + floor(height) + ".jpg";
    }

    /**
     * Constructs a random square image url with the specified size.
     *
     * @param size image width to request
     * @return random square image
     */
    public String randomImageUrl(float size) {
        return "https://picsum.photos/" + floor(size) + ".jpg";
    }

    /**
     * Point / rectangle collision check.
     *
     * @param px point x
     * @param py point y
     * @param rx rectangle top left x
     * @param ry rectangle top left y
     * @param rw rectangle width
     * @param rh rectangle height
     * @return is the point inside the rectangle?
     */
    protected boolean isPointInRect(float px, float py, float rx, float ry, float rw, float rh) {
        return px >= rx && px <= rx + rw && py >= ry && py <= ry + rh;
    }

    protected float easeInOutExpo(float currentTime, float startValue, float changeInValue, float duration) {
        currentTime /= duration / 2;
        if (currentTime < 1) return changeInValue / 2 * pow(2, 10 * (currentTime - 1)) + startValue;
        currentTime--;
        return changeInValue / 2 * (-pow(2, -10 * currentTime) + 2) + startValue;
    }

    private float easedAnimation(float startFrame, float duration, float easingFactor) {
        return easedAnimation(startFrame, duration, easingFactor, 0, 1);
    }

    /**
     * This function helps animating anything that has a known start frame and duration. Just multiply the
     * transformation you want to animate by the result of this function. Uses easing to be more pleasant
     * than linear interpolation.
     *
     * @param startFrame   frame the animation has started
     * @param duration     total number of frames the animation takes
     * @param easingFactor easing to apply
     * @return normalized value representing the current state of the animation in the range [0, 1]
     */
    private float easedAnimation(float startFrame, float duration, float easingFactor, float constrainMin,
                                 float constrainMax) {
        float animationNormalized = constrain(norm(frameCount, startFrame,
                startFrame + duration), constrainMin, constrainMax);
        return ease(animationNormalized, easingFactor);
    }

    /**
     * Eases in and out. A normalized value in the range of [0-1] is taken, the values near the limits are suppressed
     * and the transition through the middle sharpened, which makes animations feel more natural.
     *
     * @param p normalized value to ease
     * @param g easing strength
     * @return eased value
     */
    protected float ease(float p, float g) {
        if (p < 0.5)
            return 0.5f * pow(2 * p, g);
        else
            return 1 - 0.5f * pow(2 * (1 - p), g);
    }

    protected float easeInAndOut(float x, float w, float transition, float center, float easing) {
        if (x < center) {
            float fadeIn = 1 - clampNorm(x, center - w, center - w + transition);
            return 1 - ease(fadeIn, easing);
        } else {
            float fadeOut = clampNorm(x, center + w - transition, center + w);
            return 1 - ease(fadeOut, easing);
        }
    }

    protected float easeNorm(float x, float a, float b, float ease) {
        return ease(constrain(norm(x, a, b), 0, 1), ease);
    }

    protected float clampNorm(float x, float min, float max) {
        return constrain(norm(x, min, max), 0, 1);
    }

    /**
     * Returns the angular diameter of a circle with radius 'r' on the edge of a circle with radius 'size'.
     *
     * @param r    the radius of the circle to check the angular diameter of
     * @param size the radius that the circle rests on the edge of
     * @return angular diameter of r at radius size
     */
    public float angularDiameter(float r, float size) {
        return atan(2 * (size / (2 * r)));
    }

    /**
     * Linear interpolation between an arbitrary number of evenly spaced values.
     *
     * @param norm   normalized position of the value you want,
     *               norm <= 0 returns the first value, norm >= 0 returns the last value
     * @param values values to lerp between
     * @return value at position norm between the values
     */
    protected float lerpMany(float norm, float... values) {
        norm = constrain(norm, 0, 1);
        if (norm == 1) {
            return values[values.length - 1];
        }
        if (norm == 0) {
            return values[0];
        }
        float fineIndex = map(norm, 0, 1, 0, values.length - 1);
        int index0 = floor(fineIndex);
        int index1 = index0 + 1;
        float lerpAmt = (fineIndex) % 1;
        return lerp(values[index0], values[index1], lerpAmt);
    }

    /**
     * Linear interpolation between an arbitrary number of evenly spaced PVectors.
     *
     * @param norm   normalized position of the vector you want,
     *               norm <= 0 returns the first vector, norm >= 0 returns the last vector
     * @param values vectors to lerp between
     * @return vector at position norm between the values
     */
    protected PVector lerpMany(float norm, PVector... values) {
        norm = constrain(norm, 0, 1);
        if (norm == 1) {
            return values[values.length - 1];
        }
        if (norm == 0) {
            return values[0];
        }
        float fineIndex = map(norm, 0, 1, 0, values.length - 1);
        int index0 = floor(fineIndex);
        int index1 = index0 + 1;
        float lerpAmt = (fineIndex) % 1;
        return PVector.lerp(values[index0], values[index1], lerpAmt);
    }

    protected void uniformRamp(String shaderPath) {
        uniformRamp(shaderPath, "ramp", 4);
    }

    /**
     * Creates a gradient with adjustable colors and color positions using the GUI
     * and passes it to a shader as a texture.
     * // TODO implement various color blending methods
     * https://www.shadertoy.com/view/lsdGzN
     *
     * @param shaderPath        path to the shader
     * @param rampName          name of the ramp's GUI group
     * @param defaultColorCount default number of colors
     *                          any saved settings for things higher than this number won't be loaded on startup
     */
    protected void uniformRamp(String shaderPath, String rampName, int defaultColorCount) {
        if (shaderRamp == null) {
            shaderRamp = createGraphics(5, 1000, P2D);
        }
        shaderRamp.beginDraw();
        shaderRamp.clear();
        ramp(shaderRamp, rampName, 4, true);
        shaderRamp.endDraw();
        uniform(shaderPath).set("ramp", shaderRamp);
    }

    protected void ramp(PGraphics pg) {
        ramp(pg, "ramp", 4);
    }

    protected void ramp(PGraphics pg, int defaultColorCount) {
        ramp(pg, "ramp", defaultColorCount);
    }

    protected void ramp(PGraphics pg, String rampName, int defaultColorCount) {
        boolean vertical = options("vertical", "circular").equals("vertical");
        group(rampName);
        ramp(pg, rampName, defaultColorCount, vertical);
    }

    /**
     * Creates a gradient with adjustable colors and color positions using the GUI.
     *
     * @param pg                PGraphics to draw the gradient on
     * @param rampName          name of the ramp's GUI group
     * @param defaultColorCount default number of colors
     *                          any saved settings for things higher than this number won't be loaded on startup
     */
    protected void ramp(PGraphics pg, String rampName, int defaultColorCount, boolean vertical) {
        pg.hint(PConstants.DISABLE_DEPTH_TEST);
        group(rampName);
        int count = sliderInt("count", defaultColorCount);
        int detail = 10;
        float prevY = 0;
        float prevR = 0;
        HSBA prevColor = new HSBA();
        for (int i = 0; i < count; i++) {
            float yNorm;
            if (i == 0) {
                yNorm = 0;
            } else if (i == count - 1) {
                yNorm = 1;
            } else {
                yNorm = slider((i + 1) + " ", map(i, 0, count - 1, 0, 1));
            }
            HSBA thisColor = picker(String.valueOf(i + 1), 1 - yNorm);
            pg.noStroke();
            pg.beginShape(TRIANGLE_STRIP);
            if (vertical) {
                int verticalDetail = 10;
                float y = yNorm * pg.height;
                for (int j = 0; j < verticalDetail; j++) {
                    float x = map(j, 0, verticalDetail - 1, 0, width);
                    pg.fill(prevColor.clr());
                    pg.vertex(x, prevY);
                    pg.fill(thisColor.clr());
                    pg.vertex(x, y);
                }
                prevY = y;
            } else {
                int circularDetail = 100;
                float diagonalLength = dist(0, 0, pg.width / 2f, pg.height / 2f);
                float r = yNorm * diagonalLength;
                for (int j = 0; j < circularDetail; j++) {
                    float theta = map(j, 0, circularDetail - 1, 0, TAU);
                    pg.fill(prevColor.clr());
                    pg.vertex(pg.width / 2f + prevR * cos(theta), pg.height / 2f + prevR * sin(theta));
                    pg.fill(thisColor.clr());
                    pg.vertex(pg.width / 2f + r * cos(theta), pg.height / 2f + r * sin(theta));
                }
                prevR = r;
            }
            pg.endShape();
            prevColor = thisColor;
        }
        pg.hint(PConstants.ENABLE_DEPTH_TEST);
        resetGroup();
    }

    /**
     * Creates an array of vectors that when connected draw an n-sided polygon.
     *
     * @param radius radius of the polygon
     * @param detail number of vectors to use
     * @param sides  number of sides of the polygon
     * @return array of vectors that form an n-sided polygon
     */
    protected ArrayList<PVector> ngon(float radius, int detail, int sides) {
        sides = max(1, sides);
        ArrayList<PVector> corners = new ArrayList<>();
        for (int i = 0; i <= sides; i++) {
            float theta = map(i, 0, sides, 0, TAU);
            corners.add(PVector.fromAngle(theta).mult(radius));
        }
        ArrayList<PVector> shape = new ArrayList<>();
        for (int i = 0; i < detail; i++) {
            float inorm = clampNorm(i, 0, detail);
            float side = map(inorm, 0, 1, 0, sides);
            int lastCorner = floor(side);
            int nextCorner = ceil(side);
            shape.add(PVector.lerp(corners.get(lastCorner), corners.get(nextCorner), side % 1));
        }
        return shape;
    }

    /**
     * Creates an array of PShapes each holding up to 100000 shapes at position 0 and of the given shapeType.
     *
     * @param count     total number of PShapes across all lists
     * @param shapeType type of shapes to create
     * @return array of shapes of the type shapeType
     */
    protected ArrayList<PShape> shapes(int count, int shapeType) {
        ArrayList<PShape> pointArrays = new ArrayList<>();
        int maxPshapePop = 100000;
        int pshapesNeeded = ceil(count / (float) maxPshapePop);
        int pointIndex = 0;
        for (int shapeIndex = 0; shapeIndex < pshapesNeeded; shapeIndex++) {
            PShape pointArray = createShape();
            pointArray.beginShape(shapeType);
            for (int j = 0; j < maxPshapePop; j++) {
                pointArray.vertex(pointIndex++, 0, 0);
            }
            pointArray.endShape();
            pointArrays.add(pointArray);
        }
        return pointArrays;
    }

    /**
     * Draws a sphere using a spiral approach that avoids vertex clusters on the poles.
     *
     * @param pg PGraphics to draw the sphere to.
     */
    protected void spiralSphere(PGraphics pg) {
        group("planet");
        pg.beginShape(POINTS);
        pg.stroke(picker("stroke").clr());
        pg.strokeWeight(slider("weight", 5));
        pg.noFill();
        float N = slider("count", 3000);
        float s = 3.6f / sqrt(N);
        float dz = 2.0f / N;
        float lon = 0;
        float z = 1 - dz / 2;
        float scl = slider("scale", 260);
        for (int k = 0; k < N; k++) {
            float r = sqrt(1 - z * z);
            pg.vertex(cos(lon) * r * scl, sin(lon) * r * scl, z * scl);
            z = z - dz;
            lon = lon + s / r;
        }
        pg.endShape();
        pg.noStroke();
        if (!toggle("hollow")) {
            pg.fill(0);
            pg.sphereDetail(floor(slider("detail", 20)));
            pg.sphere(slider("scale") - slider("core", 5));
        }
    }

    /**
     * Gets vertices of a spiral sphere at a given detail level.
     *
     * @param count number of vertices to use
     * @return array of points that together form a spiral sphere
     */
    protected ArrayList<PVector> spiralSpherePoints(int count) {
        ArrayList<PVector> points = new ArrayList<>();
        float s = 3.6f / sqrt(count);
        float dz = 2.0f / count;
        float lon = 0;
        float z = 1 - dz / 2;
        for (int k = 0; k < count; k++) {
            float r = sqrt(1 - z * z);
            points.add(new PVector(cos(lon) * r, sin(lon) * r, z));
            z = z - dz;
            lon = lon + s / r;
        }
        return points;
    }

    // GUI UTILS

    private void updateMouseState() {
        mousePressedOutsideGui = mousePressed && isMouseOutsideGui() && (!trayVisible || !overlayVisible);
    }

    private void guiSetup(boolean defaultVisibility) {
        if (frameCount == 1) {
            trayVisible = defaultVisibility;
            textSize(textSize * 2);
        } else if (frameCount == 3) {
            loadLastStateFromFile(true);
        }
    }

    protected void resetGui() {
        for (Group group : groups) {
            for (Element el : group.elements) {
                el.reset();
            }
        }
    }

    protected void resetGroup() {
        currentGroup = null;
    }

    private void updateScrolling() {
        if (!(trayVisible && isMousePressedHere(0, 0, trayWidth, height))) {
            return;
        }
        scrollOffsetHistory.add(trayScrollOffset);
        int scrollOffsetHistorySize = 3;
        while (scrollOffsetHistory.size() > scrollOffsetHistorySize) {
            scrollOffsetHistory.remove(0);
        }
        if (abs(pmouseY - mouseY) > 2) {
            trayScrollOffset += mouseY - pmouseY;
        }
    }

    private boolean trayHasntMovedInAWhile() {
        for (Float historicalTrayOffset : scrollOffsetHistory) {
            if (historicalTrayOffset != trayScrollOffset) {
                return false;
            }
        }
        return true;
    }

    // RECORDING

    protected void cam() {
        cam(g);
    }

    protected void cam(PGraphics pg) {
        PVector t = sliderXYZ("translate");
        pg.translate(pg.width / 2f + t.x, pg.height / 2f + t.y, t.z);
        PVector r = sliderXYZ("rotate");
        pg.rotateX(r.x);
        pg.rotateY(r.y);
        pg.rotateZ(r.z);
    }

    public void rec() {
        rec(g);
    }

    public void rec(PGraphics pg) {
        savePGraphics(pg);
    }

    private void savePGraphics(PGraphics pg) {
        if (captureScreenshot) {
            captureScreenshot = false;
            screenshotsAlreadyCaptured++;
            String filename = captureDir + "screenshot_" + screenshotsAlreadyCaptured + ".jpg";
            println(filename + " saved");
            pg.save(filename);
        }
        int frameRecordingEnd = frameRecordingStarted + frameRecordingDuration + 1;
        if (frameRecordingStarted > 0 && frameCount < frameRecordingEnd) {
            int frameNumber = frameCount - frameRecordingStarted + 1;
            println(frameNumber, "/", frameRecordingEnd - frameRecordingStarted - 1, "saved");
            PImage currentSketch = pg.get();
            pg.save(captureDir + frameNumber + ".jpg");
            boolean ffmpegEnabled = true;
            if (frameCount == frameRecordingEnd - 1 && ffmpegEnabled) {
                println("capture ended, running ffmpeg, please wait...");
                try {
                    String ffmpegCommand = "ffmpeg -framerate 60 -an -start_number_range 1000000 -i " +
                            "E:/Sketches/" + captureDir + "%01d.jpg " +
                            "E:/Sketches/out/video/" + id + ".mp4";
                    Process processDuration = Runtime.getRuntime().exec(ffmpegCommand);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // TRAY

    private void updateFps() {
        int nonFlickeringFrameRate = floor(frameRate > 55 ? 60 : frameRate);
        String fps = nonFlickeringFrameRate + " fps";
        surface.setTitle(this.getClass().getSimpleName() + " " + fps);
    }

    private void updateMenuButtons() {
        float x = 0;
        float y = 0;
        float size = menuButtonSize;
        updateMenuButtonHide(x, y, hideButtonWidth, size);
        boolean hide = false;
        if (!trayVisible) {
            hide = true;
        }
        x += hideButtonWidth;
        updateMenuButtonUndo(hide, x, y, size, size);
        x += size;
        updateMenuButtonRedo(hide, x, y, size, size);
        x += size;
        updateMenuButtonSave(hide, x, y, size, size);
    }

    private void updateMenuButtonHide(float x, float y, float w, float h) {
        if (hideActivated(x, y, w, h)) {
            trayVisible = !trayVisible;
            trayWidth = trayVisible ? trayWidthWhenExtended : 0;
            hideRotationStarted = frameCount;
        }
        float grayscale = (keyboardSelected(MENU_BUTTON_HIDE) || isMouseOver(x, y, w, h)) ? GRAYSCALE_TEXT_SELECTED :
                GRAYSCALE_TEXT_DARK;
        fill(grayscale);
        stroke(grayscale);
        float rotation = easedAnimation(hideRotationStarted, MENU_ROTATION_DURATION, MENU_ROTATION_EASING);
        if (trayVisible) {
            rotation += 1;
        }
        if (isMouseOver(x, y, w, h) || trayVisible) {
            displayMenuButtonHideShow(x, y, w, h, rotation * PI);
        }
    }

    private void updateMenuButtonUndo(boolean hide, float x, float y, float w, float h) {
        boolean canUndo = undoStack.size() > 0;
        if (canUndo && trayVisible) {
            if (actions.contains(ACTION_UNDO) || isMousePressedHere(x, y, w, h)) {
                undoHoldDuration++;
            } else if (!isMouseOver(x, y, w, h)) {
                undoHoldDuration = 0;
            }
            if (mouseJustReleasedHere(x, y, w, h) || actionJustReleased(ACTION_UNDO)) {
                if (undoHoldDuration < menuButtonHoldThreshold) {
                    pushCurrentStateToRedo();
                    popUndoToCurrentState();
                } else {
                    while (!undoStack.isEmpty()) {
                        pushCurrentStateToRedo();
                        popUndoToCurrentState();
                    }
                }
                undoRotationStarted = frameCount;
                undoHoldDuration = 0;
            }
        }
        if (hide) {
            return;
        }
        float rotation = easedAnimation(undoRotationStarted, MENU_ROTATION_DURATION, MENU_ROTATION_EASING);
        rotation -= constrain(norm(undoHoldDuration, 0, menuButtonHoldThreshold), 0, 1);
        displayStateButton(x, y, w, h, rotation * TWO_PI, false, MENU_BUTTON_UNDO, undoStack.size());
    }

    private void updateMenuButtonRedo(boolean hide, float x, float y, float w, float h) {
        boolean canRedo = redoStack.size() > 0;
        if (canRedo && trayVisible) {
            if (actions.contains(ACTION_REDO) || isMousePressedHere(x, y, w, h)) {
                redoHoldDuration++;
            } else if (!isMouseOver(x, y, w, h)) {
                redoHoldDuration = 0;
            }
            if (mouseJustReleasedHere(x, y, w, h) || actionJustReleased(ACTION_REDO)) {
                if (redoHoldDuration < menuButtonHoldThreshold) {
                    pushCurrentStateToUndoWithoutClearingRedo();
                    popRedoToCurrentState();
                } else {
                    while (!redoStack.isEmpty()) {
                        pushCurrentStateToUndoWithoutClearingRedo();
                        popRedoToCurrentState();
                    }
                }
                redoRotationStarted = frameCount;
                redoHoldDuration = 0;
            }
        }
        if (hide) {
            return;
        }
        float rotation = easedAnimation(redoRotationStarted, MENU_ROTATION_DURATION, MENU_ROTATION_EASING);
        rotation -= constrain(norm(redoHoldDuration, 0, menuButtonHoldThreshold), 0, 1);
        displayStateButton(x, y, w, h, rotation * TWO_PI, true, MENU_BUTTON_REDO, redoStack.size());
    }

    private void displayMenuButtonHideShow(float x, float y, float w, float h, float rotation) {
        pushMatrix();
        pushStyle();
        translate(x + w * .5f, y + h * .5f);
        strokeWeight(2);
        rotate(rotation);
        float arrowWidth = w * .22f;
        line(-arrowWidth, 0, w * .2f, 0);
        beginShape();
        vertex(-arrowWidth * .5f, h * .05f);
        vertex(-arrowWidth, 0);
        vertex(-arrowWidth * .5f, -h * .05f);
        endShape(CLOSE);
        popStyle();
        popMatrix();
    }

    private void updateMenuButtonSave(boolean hide, float x, float y, float w, float h) {
        if (activated(MENU_BUTTON_SAVE, x, y, w, h) || actions.contains(ACTION_SAVE)) {
            saveAnimationStarted = frameCount;
            saveStateToFile();
            println("settings saved");
        }
        if (hide) {
            return;
        }
        rectMode(CENTER);
        float animation = 1 - easedAnimation(saveAnimationStarted, MENU_ROTATION_DURATION, 3);
        if (animation == 0) {
            animation = 1;
        }
        displayMenuButtonSave(x, y, w, h, animation);
    }

    private void displayMenuButtonSave(float x, float y, float w, float h, float animation) {
        float grayscale = (keyboardSelected(MENU_BUTTON_SAVE) || isMouseOver(x, y, w, h)) ?
                GRAYSCALE_TEXT_SELECTED : GRAYSCALE_TEXT_DARK;
        stroke(grayscale);
        strokeWeight(2);
        noFill();
        rect(x + w * .5f, y + h * .5f, w * .5f * animation, h * .5f * animation);
        rect(x + w * .5f, y + h * .5f - animation * h * .12f, w * .25f * animation, h * .25f * animation);
    }

    private void displayStateButton(float x, float y, float w, float h, float rotation,
                                    boolean direction, String menuButtonType, int stackSize) {
        textSize(textSize);
        textAlign(CENTER, CENTER);
        float grayscale = (keyboardSelected(menuButtonType) || isMouseOver(x, y, w, h)) ?
                GRAYSCALE_TEXT_SELECTED : GRAYSCALE_TEXT_DARK;
        fill(grayscale);
        pushMatrix();
        translate(x + w * .5f, y + h * .5f);
        rotate(PI + (direction ? rotation : -rotation));
        float margin = 0;
        noFill();
        stroke(grayscale);
        strokeWeight(2);
        if (stackSize == 0) {
            float crossSize = .08f;
            line(-w * crossSize, -h * crossSize, w * crossSize, h * crossSize);
            line(-w * crossSize, h * crossSize, w * crossSize, -h * crossSize);
        }
        float radiusMultiplier = .5f;
        arc(0, 0, w * radiusMultiplier, h * radiusMultiplier, margin, PI - margin);
        fill(grayscale);
        stroke(grayscale);
        beginShape();
        vertex((direction ? -1 : 1) * w * radiusMultiplier * .4f, h * .1f);
        vertex((direction ? -1 : 1) * w * radiusMultiplier * .5f, 0);
        vertex((direction ? -1 : 1) * w * radiusMultiplier * .55f, h * .1f);
        endShape();

        popMatrix();
    }

    protected void group(String name) {
        if (groups.isEmpty()) {
            createDefaultGroup();
        }
        Group group = findGroup(name);
        if (!groupExists(name)) {
            group = new Group(name);
            groups.add(group);
        }
        setCurrentGroup(group);
    }

    private void updateGroupsAndTheirElements() {
        float x = cell * .5f;
        float y = cell * 2.5f;
        pushMatrix();
        translate(0, trayScrollOffset);
        for (Group group : groups) {
            if (group.elements.isEmpty()) {
                continue;
            }
            group.update(y);
            if (trayVisible) {
                group.displayInTray(x, y);
            }
            if (group.expanded) {
                x += cell * .5f;
                for (Element el : group.elements) {
                    y += cell;
                    if (el.equals(overlayOwner)) {
                        el.handleActions();
                    }
                    updateElement(group, el, y);
                    if (trayVisible) {
                        displayElement(group, el, x, y, group.elementAlpha);
                    }
                }
                x -= cell * .5f;
            }
            y += cell;
        }
        popMatrix();
    }

    private void updateElement(Group group, Element el, float y) {
        el.update();
        if (activated(group.name + el.name, 0, y - cell, trayWidth, cell)) {
            if (!el.canHaveOverlay()) {
                el.onActivationWithoutOverlay(0, y - cell, trayWidth, cell);
                return;
            }
            if (!overlayVisible) {
                setOverlayOwner(el);
            } else if (!el.equals(overlayOwner)) {
                setOverlayOwner(el);
            } else if (el.equals(overlayOwner)) {
                overlayVisible = false;
            }
        }
    }

    private void displayElement(Group group, Element el, float x, float y, float alpha) {
        boolean isSelected = keyboardSelected(group.name + el.name) ||
                isMouseOverScrollAware(0, y - cell, trayWidth, cell);
        float grayScale;
        if (isSelected) {
            el.lastSelected = frameCount;
            grayScale = GRAYSCALE_TEXT_SELECTED;
        } else {
            float deselectionFadeout = easedAnimation(el.lastSelected, DESELECTION_FADEOUT_DURATION,
                    DESELECTION_FADEOUT_EASING);
            grayScale = lerp(GRAYSCALE_TEXT_DARK, GRAYSCALE_TEXT_SELECTED, 1 - deselectionFadeout);
        }
        fill(grayScale, alpha);
        stroke(grayScale, alpha);
        el.displayOnTray(x, y);
    }

    private void updateTrayBackground() {
        if (!trayVisible) {
            return;
        }
        textSize(textSize);
        trayWidthWhenExtended = constrain(findLongestNameWidth() + cell * 2, minimumTrayWidth, MAXIMUM_TRAY_WIDTH);
        noStroke();
        fill(0, BACKGROUND_ALPHA);
        rectMode(CORNER);
        rect(0, 0, trayWidth, height);
    }

    private void displayTrayGrid() {
        stroke(GRAYSCALE_GRID);
        for (float x = cell; x < trayWidth; x += cell) {
            line(x, 0, x, height);
        }
        for (float y = cell; y < height; y += cell) {
            line(0, y, trayWidth, y);
        }
    }

    private void resetMatrixInAnyRenderer() {
        if (sketchRenderer().equals(P3D)) {
            camera();
        } else {
            resetMatrix();
        }
    }

    private void setOverlayOwner(Element overlayOwnerToSet) {
        this.overlayOwner = overlayOwnerToSet;
        this.overlayOwner.onOverlayShown();
        overlayVisible = true;
        underlineTrayAnimationStarted = frameCount;
    }

    private boolean hideActivated(float x, float y, float w, float h) {
        return actions.contains(ACTION_HIDE) || mouseJustReleasedHere(x, y, w, h);
    }

    // INPUT

    private boolean activated(String query, float x, float y, float w, float h) {
        return mouseJustReleasedHereScrollAware(x, y, w, h) || (overlayOwner != null && (overlayOwner.group + overlayOwner.name).equals(query));
    }

    private boolean mouseJustReleasedHereScrollAware(float x, float y, float w, float h) {
        return mouseJustReleasedHere(x, y + trayScrollOffset, w, h) && trayHasntMovedInAWhile();
    }

    private boolean mouseJustReleasedHere(float x, float y, float w, float h) {
        return mouseJustReleased() && isPointInRect(mouseX, mouseY, x, y, w, h);
    }

    private boolean mouseJustReleased() {
        return pMousePressed && !mousePressed;
    }

    private boolean isMousePressedHere(float x, float y, float w, float h) {
        return mousePressed && isPointInRect(mouseX, mouseY, x, y, w, h);
    }

    private boolean mouseJustPressed() {
        return !pMousePressed && mousePressed;
    }

    protected boolean mouseJustPressedOutsideGui() {
        return !pMousePressed && mousePressed && isMouseOutsideGui();
    }

    private boolean isMouseOutsideGui() {
        return !trayVisible || !isPointInRect(mouseX, mouseY, 0, 0, trayWidth, height);
    }

    private boolean isMouseOverScrollAware(float x, float y, float w, float h) {
        return isMouseOver(x, y + trayScrollOffset, w, h);
    }

    private boolean isMouseOver(float x, float y, float w, float h) {
        return frameCount > 1 && isPointInRect(mouseX, mouseY, x, y, w, h);
    }

    public void mouseWheel(MouseEvent event) {
        float direction = event.getCount();
        if (direction < 0) {
            actions.add(ACTION_PRECISION_ZOOM_IN);
        } else if (direction > 0) {
            actions.add(ACTION_PRECISION_ZOOM_OUT);
        }
    }

    private boolean isAnyGroupKeyboardSelected() {
        return findKeyboardSelectedGroup() != null;
    }

    private boolean isAnyElementKeyboardSelected() {
        return findKeyboardSelectedElement() != null;
    }

    private boolean keyboardSelected(String query) {
        return false;
    }

    private int keyboardSelectionLength() {
        int elementCount = 0;
        for (Group group : groups) {
            elementCount += group.elements.size();
        }
        return MENU_BUTTON_COUNT + groups.size() + elementCount;
    }

    public void keyPressed() {
//        println((key == CODED ? "code: " + keyCode : "key: " + key));
        if (key == CODED) {
            if (keyboardKeysDoesntContain(keyCode, true)) {
                keyboardKeys.add(new Key(keyCode, true));
            }
        } else {
            if (keyboardKeysDoesntContain(key, false)) {
                keyboardKeys.add(new Key((int) key, false));
            }
        }
        if (key == 'k') {
            frameRecordingStarted = frameCount + 1;
            id = regenIdAndCaptureDir();
        }
        if (key == 'i') {
            captureScreenshot = true;
        }
    }

    private boolean keyboardKeysDoesntContain(int keyCode, boolean coded) {
        for (Key kk : keyboardKeys) {
            if (kk.character == keyCode && kk.coded == coded) {
                return false;
            }
        }
        return true;
    }

    public void keyReleased() {
        if (key == CODED) {
            removeKey(keyCode, true);
        } else {
            removeKey(key, false);
        }
    }

    private void removeKey(int keyCodeToRemove, boolean coded) {
        keyboardKeysToRemove.clear();
        for (Key kk : keyboardKeys) {
            if (kk.coded == coded && kk.character == keyCodeToRemove) {
                keyboardKeysToRemove.add(kk);
            }
        }
        keyboardKeys.removeAll(keyboardKeysToRemove);
    }

    private void updateKeyboardInput() {
        previousActions.clear();
        previousActions.addAll(actions);
        actions.clear();
        for (Key kk : keyboardKeys) {
            if (!kk.coded) {
                parseRepeatableActions(kk);
                if (!kk.justPressed) {
                    continue;
                }
                parseNonRepeatableActions(kk);
            }
            kk.justPressed = false;
        }
    }

    private void parseRepeatableActions(Key kk) {
        if (kk.character == 'z' || kk.character == 26) {
            actions.add(ACTION_UNDO);
        }
        if (kk.character == 'y' || kk.character == 25) {
            actions.add(ACTION_REDO);
        }
    }

    private void parseNonRepeatableActions(Key kk) {
        if (kk.character == '*' || kk.character == '+') {
            actions.add(ACTION_PRECISION_ZOOM_IN);
        }
        if (kk.character == '/' || kk.character == '-') {
            actions.add(ACTION_PRECISION_ZOOM_OUT);
        }
        if (kk.character == 'r') {
            actions.add(ACTION_RESET);
        }
        if (kk.character == 'h') {
            actions.add(ACTION_HIDE);
        }
        if (kk.character == 19) { // CTRL + S
            actions.add(ACTION_SAVE);
        }
        if (kk.character == 'c' || kk.character == 3) { // CTRL + C
            actions.add(ACTION_COPY);
        }
        if (kk.character == 'v' || kk.character == 22) { // CTRL + V
            actions.add(ACTION_PASTE);
        }
    }

    private boolean actionJustReleased(String action) {
        return previousActions.contains(action) && !actions.contains(action);
    }

    private boolean upAndDownArrowsControlOverlay() {
        return overlayVisible && (verticalOverlayVisible || pickerOverlayVisible);
    }

    private float findLongestNameWidth() {
        float longestNameWidth = 0;
        for (Group group : groups) {
            for (Element el : group.elements) {
                if (el.trayTextWidth() > longestNameWidth) {
                    longestNameWidth = el.trayTextWidth();
                }
            }
        }
        return longestNameWidth;
    }

    // GROUP AND ELEMENT HANDLING

    private int elementCount() {
        int sum = 0;
        for (Group group : groups) {
            sum += group.elements.size();
        }
        return sum;
    }

    private Group getCurrentGroup() {
        if (currentGroup == null) {
            if (groups.isEmpty()) {
                createDefaultGroup();
            } else {
                return groups.get(0);
            }
        }
        return currentGroup;
    }

    private void setCurrentGroup(Group currentGroup) {
        this.currentGroup = currentGroup;
    }

    private void createDefaultGroup() {
        Group defaultGroup = new Group(this.getClass().getSimpleName());
        groups.add(defaultGroup);
        currentGroup = defaultGroup;
    }

    private Group getLastGroup() {
        if (groups.isEmpty()) {
            return null;
        }
        return groups.get(groups.size() - 1);
    }

    private Group findGroup(String name) {
        for (Group group : groups) {
            if (group.name.equals(name)) {
                return group;
            }
        }
        return null;
    }

    private boolean groupExists(String name) {
        return findGroup(name) != null;
    }

    private Group findPreviousGroup(String query) {
        for (Group group : groups) {
            if (group.name.equals(query)) {
                int index = groups.indexOf(group);
                if (index > 0) {
                    return groups.get(index - 1);
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    private Group findKeyboardSelectedGroup() {
        for (Group group : groups) {
            if (keyboardSelected(group.name)) {
                return group;
            }
        }
        return null;
    }

    private Element findKeyboardSelectedElement() {
        for (Group group : groups) {
            for (Element el : group.elements)
                if (keyboardSelected(group.name + el.name)) {
                    return el;
                }
        }
        return null;
    }

    private boolean elementDoesntExist(String elementName, String groupName) {
        return findElement(elementName, groupName) == null;
    }

    private Element findElement(String elementName, String groupName) {
        for (Group g : groups) {
            for (Element el : g.elements) {
                if (g.name.equals(groupName) && el.name.equals(elementName)) {
                    return el;
                }
            }
        }
        return null;
    }

    private void pushCurrentStateToRedo() {
        redoStack.add(getGuiState());
    }

    // STATE

    private void pushStateToUndo(ArrayList<String> state) {
        setGuiState(state);
        pushCurrentStateToUndo();
    }

    private void pushCurrentStateToUndo() {
        redoStack.clear();
        undoStack.add(getGuiState());
    }

    private void pushStateToRedo(ArrayList<String> state) {
        redoStack.add(state);
    }

    private void pushCurrentStateToUndoWithoutClearingRedo() {
        undoStack.add(getGuiState());
    }

    private void popUndoToCurrentState() {
        if (undoStack.isEmpty()) {
            return;
        }
        setGuiState(undoStack.remove(undoStack.size() - 1));
    }

    private void popRedoToCurrentState() {
        if (redoStack.isEmpty()) {
            return;
        }
        setGuiState(redoStack.remove(redoStack.size() - 1));
    }

    private ArrayList<String> getGuiState() {
        ArrayList<String> states = new ArrayList<>();
        for (Group group : groups) {
            states.add(group.getState());
            for (Element el : group.elements) {
                states.add(el.getState());
            }
        }
        return states;
    }

    private void setGuiState(ArrayList<String> statesToSet) {
        for (String state : statesToSet) {
            String[] splitState = state.split(SEPARATOR);
            if (state.startsWith(GROUP_PREFIX)) {
                Group group = findGroup(splitState[1]);
                if (group == null) {
                    continue;
                }
                group.setState(state);
            } else {
                Element el = findElement(splitState[1], splitState[0]);
                if (el == null) {
//                    println("element does not exist", splitState[0], splitState[1]);
                    continue;
                }
                try {
                    el.setState(state);
                } catch (Exception ex) {
                    println(ex.getMessage());
                }
            }
        }
    }

    void saveStateToFile() {
        pushCurrentStateToUndo();
        File file = dataFile(settingsPath());
        ArrayList<String> save = new ArrayList<>(Arrays.asList(loadLastStateFromFile(false)));
        save.add(STATE_BEGIN);
        save.add(UNDO_PREFIX);
        save.addAll(undoStack.get(undoStack.size() - 1));
        save.add(STATE_END);
        String[] saveArray = arrayListToStringArray(save);
        saveStrings(file, saveArray);
    }

    private String[] arrayListToStringArray(ArrayList<String> input) {
        String[] array = new String[input.size()];
        for (int i = 0; i < input.size(); i++) {
            array[i] = input.get(i);
        }
        return array;
    }

    protected String[] loadLastStateFromFile(boolean alsoPush) {
        File file = dataFile(settingsPath());
        if (!file.exists()) {
            return new String[0];
        }
        String[] lines = loadStrings(file);
        if (alsoPush) {
            redoStack.clear();
            undoStack.clear();
            boolean pushingUndo = false;
            ArrayList<String> runningState = new ArrayList<>();
            for (String line : lines) {
                if (line.startsWith(UNDO_PREFIX)) {
                    pushingUndo = true;
                } else if (line.startsWith(REDO_PREFIX)) {
                    pushingUndo = false;
                } else if (line.startsWith(STATE_BEGIN)) {
                    runningState.clear();
                } else if (line.startsWith(STATE_END)) {
                    if (pushingUndo) {
//                        println("pushing ", concat(runningState));
                        pushStateToUndo(runningState);
                    } else {
//                        println("pushing ", concat(runningState));
                        pushStateToRedo(runningState);
                    }
                    runningState.clear();
                } else {
                    runningState.add(line);
                }
            }
            popUndoToCurrentState();
        }
        return lines;
    }

    private String settingsPath() {
        return "gui\\" + this.getClass().getSimpleName() + ".txt";
    }

    protected void uniformColorPalette(String fragPath) {
        uniformColorPalette(fragPath, null);
    }

    protected void uniformColorPalette(String fragPath, String vertPath) {
        int colorCount = sliderInt("color count", 5);
        for (int i = 0; i < colorCount; i++) {
            HSBA color = picker(i + "");
            if (vertPath != null) {
                uniform(fragPath, vertPath).set("hsba_" + i, color.hue(), color.sat(), color.br(), color.alpha());
            } else {
                uniform(fragPath).set("hsba_" + i, color.hue(), color.sat(), color.br(), color.alpha());
            }
        }
        if (vertPath != null) {
            uniform(fragPath, vertPath).set("colorCount", colorCount);
        } else {
            uniform(fragPath).set("colorCount", colorCount);
        }
    }

    // SHADERS

    protected void displacePass(PGraphics pg) {
        String displace = "displace.glsl";
        uniform(displace).set("time", t * slider("time speed"));
        PVector globalMove = sliderXY("global move");
        uniform(displace).set("globalMove", globalMove.x, globalMove.y);
        uniform(displace).set("worleyMove", slider("worley amp", .0f));
        uniform(displace).set("worleyFreq", slider("worley freq", 5));
        uniform(displace).set("rotateAmp", slider("rotate amp", 1));
        hotFilter(displace, pg);
    }

    protected void vignettePass(PGraphics pg) {
        String vignette = "vignette.glsl";
        uniform(vignette).set("startRadius", slider("start"));
        uniform(vignette).set("endRadius", slider("end"));
        hotFilter(vignette, pg);
    }

    protected void rayMarchPass(PGraphics pg) {
        String raymarch = "raymarch.glsl";
        uniform(raymarch).set("time", t);
        uniform(raymarch).set("translate", sliderXYZ("translate", 0, 0, -5, 100).add(sliderXYZ("speed")));
        uniform(raymarch).set("lightDirection", sliderXYZ("light dir"));
        uniform(raymarch).set("diffuseMag", slider("diffuse"));
        uniform(raymarch).set("specularMag", slider("specular"));
        uniform(raymarch).set("shininess", slider("shininess"));
        uniform(raymarch).set("rotate", slider("rotate") + radians(frameCount) * slider("rotate speed", 0));
        uniform(raymarch).set("distFOV", slider("distFOV", 1));
        hotFilter(raymarch, pg);
    }

    protected void radialBlurPass(PGraphics pg) {
        String radialBlur = "radialBlur.glsl";
        uniform(radialBlur).set("delta", slider("radial delta", 1));
        uniform(radialBlur).set("power", slider("radial power", 1));
        hotFilter(radialBlur, pg);
    }

    protected void splitPass(PGraphics pg) {
        String split = "shaders/filters/split.glsl";
        uniform(split).set("delta", slider("split"));
        hotFilter(split, pg);
    }

    protected void blurPass(PGraphics pg) {
        String split = "shaders/filters/blur.glsl";
        uniform(split).set("delta", slider("blur"));
        hotFilter(split, pg);
    }

    protected void gaussBlurPass(PGraphics pg) {
        String split = "shaders/filters/gaussBlur.glsl";
        uniform(split).set("sigma", slider("sigma", 0));
        uniform(split).set("blurSize", slider("blur size", 0));
        hotFilter(split, pg);
    }

    protected void chromaticAberrationPass(PGraphics pg) {
        String chromatic = "postFX\\chromaticAberrationFrag.glsl";
        uniform(chromatic).set("maxDistort", slider("chromatic", 5));
        hotFilter(chromatic, pg);
    }

    protected void noiseOffsetPass(PGraphics pg, float t) {
        String noiseOffset = "noiseOffset.glsl";
        uniform(noiseOffset).set("time", t);
        uniform(noiseOffset).set("mixAmt", slider("mix", 0, 1, .1f));
        uniform(noiseOffset).set("mag", slider("mag", 0, .01f, .001f));
        uniform(noiseOffset).set("frq", slider("frq", 0, 50, 8.5f));
        hotFilter(noiseOffset, pg);
    }

    protected void noisePass(float t, PGraphics pg) {
        String noise = "postFX/noiseFrag.glsl";
        uniform(noise).set("time", t);
        uniform(noise).set("amount", slider("noise magnitude", 0, .24f, .05f));
        uniform(noise).set("speed", slider("noise speed", 1));
        hotFilter(noise, pg);
    }

    protected void rgbSplitPassUniform(PGraphics pg) {
        String rgbSplit = "rgbSplitUniform.glsl";
        uniform(rgbSplit).set("delta", slider("RGB split", 2));
        hotFilter(rgbSplit, pg);
    }

    protected void rgbSplitPass(PGraphics pg) {
        String rgbSplit = "postFX/rgbSplitFrag.glsl";
        uniform(rgbSplit).set("delta", slider("RGB split", 10));
        hotFilter(rgbSplit, pg);
    }

    protected void saturationVibrancePass(PGraphics pg) {
        String saturationVibrance = "postFX/saturationVibranceFrag.glsl";
        uniform(saturationVibrance).set("saturation", slider("saturation", 0, 0.5f, 0));
        uniform(saturationVibrance).set("vibrance", slider("vibrance", 0, 0.5f, 0));
        hotFilter(saturationVibrance, pg);
    }

    protected void toonPass(PGraphics pg) {
        String toonPass = "postFX/toonFrag.glsl";
        hotFilter(toonPass, pg);
    }

    protected void brightnessContractFrag(PGraphics pg) {
        String brightnessContractPass = "postFX/brightnessContrastFrag.glsl";
        uniform(brightnessContractPass).set("brightness", slider("brightness", 1, false));
        uniform(brightnessContractPass).set("contrast", slider("contrast", 2));
        hotFilter(brightnessContractPass, pg);
    }

    // SHADER RELOADING

    public PShader uniform(String fragPath) {
        ShaderSnapshot snapshot = findSnapshotByPath(fragPath);
        snapshot = initIfNull(snapshot, fragPath, null);
        return snapshot.compiledShader;
    }

    public PShader uniform(String fragPath, String vertPath) {
        ShaderSnapshot snapshot = findSnapshotByPath(fragPath);
        snapshot = initIfNull(snapshot, fragPath, vertPath);
        return snapshot.compiledShader;
    }

    public void hotFilter(String path, PGraphics canvas) {
        hotShader(path, null, true, canvas);
    }

    public void hotFilter(String path) {
        hotShader(path, null, true, g);
    }

    public void hotShader(String fragPath, String vertPath, PGraphics canvas) {
        hotShader(fragPath, vertPath, false, canvas);
    }

    public void hotShader(String fragPath, String vertPath) {
        hotShader(fragPath, vertPath, false, g);
    }

    public void hotShader(String fragPath, PGraphics canvas) {
        hotShader(fragPath, null, false, canvas);
    }

    public void hotShader(String fragPath) {
        hotShader(fragPath, null, false, g);
    }

    private void hotShader(String fragPath, String vertPath, boolean filter, PGraphics canvas) {
        ShaderSnapshot snapshot = findSnapshotByPath(fragPath);
        snapshot = initIfNull(snapshot, fragPath, vertPath);
        snapshot.update(filter, canvas);
    }

    private ShaderSnapshot initIfNull(ShaderSnapshot snapshot, String fragPath, String vertPath) {
        if (snapshot == null) {
            snapshot = new ShaderSnapshot(fragPath, vertPath);
            snapshots.add(snapshot);
        }
        return snapshot;
    }

    private ShaderSnapshot findSnapshotByPath(String path) {
        for (ShaderSnapshot snapshot : snapshots) {
            if (snapshot.fragPath.equals(path)) {
                return snapshot;
            }
        }
        return null;
    }

//  PARAMETRIC EQUATIONS

    protected void drawParametric(PGraphics pg) {
        pg.pushMatrix();
        PVector translate = sliderXYZ("translate");
        pg.translate(translate.x + width * .5f, translate.y + height * .5f, translate.z);
        PVector rot = sliderXYZ("rotation");
        pg.rotateX(rot.x);
        pg.rotateY(rot.y);
        pg.rotateZ(rot.z + (toggle("z rotation") ? t : 0));
        int uMax = sliderInt("u", 1, 1000, 10);
        int vMax = sliderInt("v", 1, 1000, 10);
        float r = slider("radius", 10);
        float h = r * (1 + slider("height", 0));
        pg.strokeWeight(slider("weight", 1));
        pg.stroke(picker("stroke", 1).clr());
        if (toggle("no stroke")) {
            pg.noStroke();
        }
        pg.fill(picker("fill", 0).clr());
        for (int uIndex = 0; uIndex < uMax; uIndex++) {
            if (toggle("points")) {
                pg.beginShape(POINTS);
            } else {
                pg.beginShape(TRIANGLE_STRIP);
            }
            for (int vIndex = 0; vIndex <= vMax; vIndex++) {
                float u0 = norm(uIndex, 0, uMax);
                float u1 = norm(uIndex + 1, 0, uMax);
                float v = norm(vIndex, 0, vMax);
                PVector a = getVector(u0, v, r, h);
                pg.vertex(a.x, a.y, a.z);
                if (!toggle("points")) {
                    PVector b = getVector(u1, v, r, h);
                    pg.vertex(b.x, b.y, b.z);
                }
            }
            pg.endShape();
        }
        pg.popMatrix();
    }

    private PVector getVector(float u, float v, float r, float h) {
        String option = options("russian", "catenoid", "screw", "hexaedron", "moebius",
                "torus", "multitorus", "helicoidal", "ufo", "sphere");
        switch (option) {
            case "russian":
                return russianRoof(u, v, r, h);
            case "catenoid":
                return catenoid(u, v, r, h);
            case "screw":
                return screw(u, v, r, h);
            case "hexaedron":
                return hexaedron(u, v, r, h);
            case "moebius":
                return moebius(u, v, r, h);
            case "torus":
                return torus(u, v, r, h);
            case "multitorus":
                return multitorus(u, v, r, h);
            case "helicoidal":
                return helicoidal(u, v, r, h);
            case "ufo":
                return ufo(u, v, r, h);
            case "sphere":
                return sphere(u, v, r, h);
        }
        return new PVector();
    }

    private PVector sphere(float u, float v, float r, float h) {
        u = -HALF_PI + u * PI;
        v = v * TWO_PI;
        return new PVector(
                r * cos(u) * cos(v),
                r * cos(u) * sin(v),
                h * sin(u)
        );
    }

    private PVector ufo(float u, float v, float r, float h) {
        u = -PI + u * TWO_PI;
        v = -PI + v * TWO_PI;
        return new PVector(
                r * (cos(u) / (sqrt(2) + sin(v))),
                r * (sin(u) / (sqrt(2) + sin(v))),
                h * (1 / (sqrt(2) + cos(v)))
        );
    }

    private PVector helicoidal(float u, float v, float r, float h) {
        u = -PI + u * TWO_PI;
        v = -PI + v * TWO_PI;
        return new PVector(
                r * (sinh(v) * sin(u)),
                r * (-sinh(v) * cos(u)),
                h * (3 * u)
        );
    }

    private PVector multitorus(float u, float v, float r, float h) {
        u = -PI + u * TWO_PI;
        v = -PI + v * TWO_PI;
        float mtTime = t * slider("time");
        float R3 = sliderInt("R3", 3);
        float R = sliderInt("R", 5);
        float N = sliderInt("N", 10);
        float N2 = sliderInt("N2", 4);
        return new PVector(
                r * (-sin(u) * multitorusF1(u - mtTime, v, N, R3, R, N2)),
                r * (cos(u) * multitorusF1(u - mtTime, v, N, R3, R, N2)),
                h * (multitorusF2(u - mtTime, v, N, R3, R, N2))
        );
    }

    private float multitorusF1(float u, float v, float N, float R3, float R, float N2) {
        return (R3 + (R / (10 * N)) * cos(N2 * u / N + ((R / (10 * N)) - R / 10) / (R / (10 * N)) * v) + (R / 10 - (R / (10 * N))) * cos(N2 * u / N + v));
    }

    private float multitorusF2(float u, float v, float N, float R3, float R, float N2) {
        return ((R / (10 * N)) * sin(N2 * u / N + ((R / (10 * N)) - R / 10) / (R / (10 * N)) * v) + (R / 10 - (R / (10 * N))) * sin(N2 * u / N + v));
    }

    private PVector torus(float u, float v, float r, float h) {
        u = u * TWO_PI;
        v = v * TWO_PI;
        return new PVector(
                r * ((1 + 0.5f * cos(u)) * cos(v)),
                r * ((1 + 0.5f * cos(u)) * sin(v)),
                h * (0.5f * sin(u))
        );
    }

    private PVector moebius(float u, float v, float r, float h) {
        u = -.4f + u * .8f;
        v = v * TWO_PI;
        return new PVector(
                r * (cos(v) + u * cos(v / 2) * cos(v)),
                r * (sin(v) + u * cos(v / 2) * sin(v)),
                h * (u * sin(v / 2))
        );
    }

    private PVector hexaedron(float u, float v, float r, float h) {
        u = -1.3f + u * 2.6f;
        v = v * TWO_PI;
        return new PVector(
                r * pow(cos(v), 3) * pow(cos(u), 3),
                r * pow(sin(v), 3) * pow(cos(u), 3),
                h * pow(sin(u), 3)
        );
    }

    private PVector screw(float u, float v, float r, float h) {
        u = u * 12.4f;
        v = v * 2;
        return new PVector(
                r * cos(u) * sin(v),
                r * sin(u) * sin(v),
                h * ((cos(v) + log(tan(v / 2f))) + 0.2f * u)
        );
    }

    private PVector catenoid(float u, float v, float r, float h) {
        u = PI - u * TWO_PI;
        v = PI - v * TWO_PI;
        return new PVector(
                r * 2 * cosh(v / 2) * cos(u),
                r * 2 * cosh(v / 2) * sin(u),
                h * v
        );
    }

    private PVector russianRoof(float u, float v, float r, float h) {
        u = u * TWO_PI;
        float easing = slider("easing", 1);
        return new PVector(
                (r - r * ease(v, easing)) * cos(u),
                (r - r * ease(v, easing)) * sin(u),
                (-1 + 2 * v) * h
        );
    }

    private float cosh(float n) {
        return (float) Math.cosh(n);
    }

    private float sinh(float n) {
        return (float) Math.sinh(n);
    }

    // CLASSES

    private class ShaderSnapshot {
        String fragPath;
        String vertPath;
        File fragFile;
        File vertFile;
        PShader compiledShader;
        long fragLastKnownModified, vertLastKnownModified, lastChecked;
        boolean compiledAtLeastOnce = false;
        long lastKnownUncompilable = -shaderRefreshRateInMillis;


        ShaderSnapshot(String fragPath, String vertPath) {
            if (vertPath != null) {
                compiledShader = loadShader(fragPath, vertPath);
                vertFile = dataFile(vertPath);
                vertLastKnownModified = vertFile.lastModified();
                if (!vertFile.isFile()) {
                    println("Could not find shader at " + vertFile.getPath());
                }
            } else {
                compiledShader = loadShader(fragPath);
            }
            fragFile = dataFile(fragPath);
            fragLastKnownModified = fragFile.lastModified();
            lastChecked = currentTimeMillis();
            if (!fragFile.isFile()) {
                println("Could not find shader at " + fragFile.getPath());
            }
            this.fragPath = fragPath;
            this.vertPath = vertPath;
        }

        @SuppressWarnings("ManualMinMaxCalculation")
        long max(long a, long b) {
            if (a > b) {
                return a;
            }
            return b;
        }

        void update(boolean filter, PGraphics pg) {
            long currentTimeMillis = currentTimeMillis();
            long lastModified = fragFile.lastModified();
            if (vertFile != null) {
                lastModified = max(lastModified, vertFile.lastModified());
            }
            if (compiledAtLeastOnce && currentTimeMillis < lastChecked + shaderRefreshRateInMillis) {
//                println("compiled at least once, not checking, standard apply");
                applyShader(compiledShader, filter, pg);
                return;
            }
            if (!compiledAtLeastOnce && lastModified > lastKnownUncompilable) {
//                println("first try");
                tryCompileNewVersion(filter, pg, lastModified);
                return;
            }
            lastChecked = currentTimeMillis;
            if (lastModified > fragLastKnownModified && lastModified > lastKnownUncompilable) {
//                println("file changed, repeat try");
                tryCompileNewVersion(filter, pg, lastModified);
            } else if (compiledAtLeastOnce) {
//                println("file didn't change, standard apply");
                applyShader(compiledShader, filter, pg);
            }
        }

        private void applyShader(PShader shader, boolean filter, PGraphics pg) {
            if (filter) {
                pg.filter(shader);
            } else {
                pg.shader(shader);
            }
        }

        private void tryCompileNewVersion(boolean filter, PGraphics pg, long lastModified) {
            try {
                PShader candidate;
                if (vertFile == null) {
                    candidate = loadShader(fragPath);
                } else {
                    candidate = loadShader(fragPath, vertPath);
                }
                // we need to call filter() or shader() here in order to catch any compilation errors and not halt
                // the sketch
                applyShader(candidate, filter, pg);
                compiledShader = candidate;
                compiledAtLeastOnce = true;
                fragLastKnownModified = lastModified;
            } catch (Exception ex) {
                lastKnownUncompilable = lastModified;
                println("\n" + fragFile.getName() + ": " + ex.getMessage());
            }
        }
    }

    private class Key {
        boolean justPressed;
        boolean repeatedAlready = false;
        boolean coded;
        int character;
        int lastRegistered = -1;

        Key(Integer character, boolean coded) {
            this.character = character;
            this.coded = coded;
            this.justPressed = true;
        }

        boolean repeatCheck() {
            boolean shouldApply = justPressed ||
                    (!repeatedAlready && millis() > lastRegistered + KEY_REPEAT_DELAY_FIRST) ||
                    (repeatedAlready && millis() > lastRegistered + KEY_REPEAT_DELAY);
            if (shouldApply) {
                lastRegistered = millis();
                if (!justPressed) {
                    repeatedAlready = true;
                }
            }
            justPressed = false;
            return shouldApply;
        }
    }

    private class Group {
        String name;
        int animationStarted = -GROUP_TOGGLE_ANIMATION_DURATION;
        boolean expanded = true;
        ArrayList<Element> elements = new ArrayList<>();
        float elementAlpha = 1;

        Group(String name) {
            this.name = name;
        }

        public void update(float y) {
            if (activated(name, 0, y - cell, trayWidth, cell)) {
                expanded = !expanded;
                animationStarted = frameCount;
            }
        }

        public void displayInTray(float x, float y) {
            boolean isSelected = (keyboardSelected(name) || isMouseOverScrollAware(0, y - cell, trayWidth, cell));
            float clr = isSelected ? GRAYSCALE_TEXT_SELECTED : GRAYSCALE_TEXT_DARK;
            fill(clr);
            stroke(clr);
            textAlign(LEFT, BOTTOM);
            textSize(textSize);
            float animation = easedAnimation(animationStarted, GROUP_TOGGLE_ANIMATION_DURATION,
                    GROUP_TOGGLE_ANIMATION_EASING);
            if (!expanded) {
                animation = 1 - animation;
            }
            elementAlpha = animation;
            pushMatrix();
            translate(cell * .3f, y - textSize * .55f);
            rotate(animation * HALF_PI);
            float size = cell * 0.08f;
            line(-size, size, size, 0);
            line(-size, -size, size, 0);
            popMatrix();
            text(name, x, y);
        }

        public String getState() {
            return GROUP_PREFIX + SEPARATOR + name + SEPARATOR + expanded;
        }

        public void setState(String state) {
            String[] split = state.split(SEPARATOR);
            expanded = Boolean.parseBoolean(split[2]);
        }
    }

    private abstract class Element {
        public float lastSelected = -DESELECTION_FADEOUT_DURATION;
        Group group;
        String name;

        Element(Group group, String name) {
            this.group = group;
            this.name = name;
        }

        void reset() {

        }


        protected String fullElementName() {
            return group.name + SEPARATOR + name + SEPARATOR;
        }

        abstract boolean canHaveOverlay();

        String getState() {
            return fullElementName();
        }

        void setState(String newState) {

        }

        void update() {

        }

        void updateOverlay() {

        }

        void onOverlayShown() {

        }

        void onActivationWithoutOverlay(int x, float y, float w, float h) {

        }

        void displayOnTray(float x, float y) {
            displayOnTray(x, y, name);
        }

        void displayOnTray(float x, float y, String text) {
            textAlign(LEFT, BOTTOM);
            textSize(textSize);
            if (overlayVisible && this.equals(overlayOwner)) {
                underlineAnimation(underlineTrayAnimationStarted, UNDERLINE_TRAY_ANIMATION_DURATION, x, y, true);
            }
            text(text, x, y);
        }

        float trayTextWidth() {
            return textWidth(name);
        }

        void underlineAnimation(float startFrame, float duration, float x, float y, boolean stayExtended) {
            float fullWidth = textWidth(name);
            float animation = easedAnimation(startFrame, duration, UNDERLINE_TRAY_ANIMATION_EASING);
            if (!stayExtended && animation == 1) {
                animation = 0;
            }
            float w = fullWidth * animation;
            float centerX = x + fullWidth * .5f;
            strokeWeight(2);
            line(centerX - w * .5f, y, centerX + w * .5f, y);
        }

        void displayCheckMarkOnTray(float x, float y, float animation, boolean fadeIn, boolean displayBox) {
            float w = previewTrayBoxWidth;
            pushMatrix();
            translate(x - previewTrayBoxMargin, previewTrayBoxOffsetY);
            noFill();
            if (displayBox) {
                rectMode(CENTER);
                pushStyle();
                strokeWeight(1);
                stroke(GRAYSCALE_TEXT_DARK);
                rect(-w, y - textSize * .5f, w, w);
                popStyle();
            }
            strokeWeight(2);
            beginShape();
            int detail = 30;
            float checkMarkTopLeftX = -w * 1.25f;
            float checkMarkTopLeftY = y - textSize * .6f;
            float lowestCheckMarkPointX = -w;
            float lowestCheckMarkPointY = y - textSize * .4f;
            float checkMarkTopRightX = 0;
            float checkMarkTopRightY = y - textSize * .9f;
            for (int i = 0; i < detail; i++) {
                float iNorm = norm(i, 0, detail - 1);
                if ((fadeIn && iNorm > animation) || (!fadeIn && iNorm < animation)) {
                    continue;
                }
                if (iNorm < .333f) {
                    float downwardStroke = norm(iNorm, 0, .333f);
                    float downwardX = lerp(checkMarkTopLeftX, lowestCheckMarkPointX, downwardStroke);
                    float downwardY = lerp(checkMarkTopLeftY, lowestCheckMarkPointY, downwardStroke);
                    vertex(downwardX, downwardY);
                    continue;
                }
                float upwardStroke = norm(iNorm, .333f, 1);
                float upwardX = lerp(lowestCheckMarkPointX, checkMarkTopRightX, upwardStroke);
                float upwardY = lerp(lowestCheckMarkPointY, checkMarkTopRightY, upwardStroke);
                vertex(upwardX, upwardY);
            }
            endShape();
            popMatrix();
        }

        void handleActions() {
        }
    }

    private class Radio extends Element {
        ArrayList<String> options = new ArrayList<>();
        int valueIndex = 0;

        Radio(Group parent, String name, String[] options) {
            super(parent, name);
            this.options.add(name);
            this.options.addAll(Arrays.asList(options));
        }

        String getState() {
            return super.getState() + valueIndex;
        }

        void setState(String newState) {
            valueIndex = Integer.parseInt(newState.split(SEPARATOR)[2]);
        }

        protected void reset() {
            valueIndex = 0;
        }

        boolean canHaveOverlay() {
            return false;
        }

        String value() {
            return options.get(valueIndex);
        }

        void displayOnTray(float x, float y) {
            super.displayOnTray(x, y, value());
            displayDotsOnTray(x, y);
        }

        private void displayDotsOnTray(float x, float y) {
            for (int i = 0; i < options.size(); i++) {
                float size = 4;
                float rectX = x + cell * .15f + i * size * 2.5f;
                if (i == valueIndex) {
                    size *= 1.8f;
                    pushStyle();
                    noFill();
                }
                rectMode(CENTER);
                rect(rectX, y + cell * .1f, size, size);
                if (i == valueIndex) {
                    popStyle();
                }
            }
        }

        void onActivationWithoutOverlay(int x, float y, float w, float h) {
            pushCurrentStateToUndo();
            valueIndex++;
            if (valueIndex >= options.size()) {
                valueIndex = 0;
            }
        }

        float trayTextWidth() {
            return textWidth(value());
        }
    }

    private class Button extends Element {
        boolean value;
        private float activationStarted = -CHECK_ANIMATION_DURATION * 2;

        Button(Group parent, String name) {
            super(parent, name);
        }

        boolean canHaveOverlay() {
            return false;
        }

        void onActivationWithoutOverlay(int x, float y, float w, float h) {
            value = true;
            activationStarted = frameCount;
        }

        void displayOnTray(float x, float y) {
            float checkMarkAnimation = easedAnimation(activationStarted, CHECK_ANIMATION_DURATION * 2,
                    CHECK_ANIMATION_EASING);
            if (checkMarkAnimation > 0 && checkMarkAnimation < 1) {
                if (checkMarkAnimation < .5) {
                    displayCheckMarkOnTray(x, y, checkMarkAnimation * 2, true, false);
                } else {
                    displayCheckMarkOnTray(x, y, (checkMarkAnimation - .5f) * 2, false, false);
                }
            }
            super.displayOnTray(x, y);
        }

        void update() {
            value = false;
        }
    }

    private class Toggle extends Element {
        boolean checked, checkByDefault;
        private float activationStarted = -UNDERLINE_TRAY_ANIMATION_DURATION;

        Toggle(Group parent, String name, boolean initialState) {
            super(parent, name);
            this.checkByDefault = initialState;
            this.checked = initialState;
        }

        String getState() {
            return super.getState() + checked;
        }

        void setState(String newState) {
            this.checked = Boolean.parseBoolean(newState.split(SEPARATOR)[2]);
        }

        boolean canHaveOverlay() {
            return false;
        }

        void displayOnTray(float x, float y) {
            float checkMark = easedAnimation(activationStarted, CHECK_ANIMATION_DURATION, CHECK_ANIMATION_EASING);
            displayCheckMarkOnTray(x, y, checkMark, checked, true);
            super.displayOnTray(x, y);
        }

        void reset() {
            checked = checkByDefault;
        }

        void update() {
            if (overlayVisible && overlayOwner != null && overlayOwner.equals(this) && actions.contains(ACTION_RESET)) {
                pushCurrentStateToUndo();
                reset();
            }
        }

        void onActivationWithoutOverlay(int x, float y, float w, float h) {
            pushCurrentStateToUndo();
            activationStarted = frameCount;
            checked = !checked;
        }
    }

    private abstract class Slider extends Element {
        Slider(Group parent, String name) {
            super(parent, name);
        }

        void updateOverlay() {
        }

        protected float updateFullHorizontalSlider(float x, float y, float w, float h, float value, float precision,
                                                   float horizontalRevealAnimationStarted, boolean alternative,
                                                   float minValue, float maxValue) {
            float deltaX = updateInfiniteSlider(precision, width, true, true, alternative);
            float horizontalAnimation = easedAnimation(horizontalRevealAnimationStarted - SLIDER_REVEAL_START_SKIP,
                    SLIDER_REVEAL_DURATION, SLIDER_REVEAL_EASING);
            displayInfiniteSliderCenterMode(x + width * .5f, y, w, h,
                    precision, value, horizontalAnimation, true, true, false, minValue, maxValue);
            return deltaX;
        }

        @SuppressWarnings("SuspiciousNameCombination")
        protected float updateFullHeightVerticalSlider(float x, float y, float w, float h, float value, float precision,
                                                       float verticalRevealAnimationStarted, boolean alternative,
                                                       float minValue, float maxValue) {
            float deltaY = updateInfiniteSlider(precision, height, false, true, alternative);
            float verticalAnimation = easedAnimation(verticalRevealAnimationStarted - SLIDER_REVEAL_START_SKIP,
                    SLIDER_REVEAL_DURATION, SLIDER_REVEAL_EASING);
            displayInfiniteSliderCenterMode(x + height * .5f, y, w, h,
                    precision, value, verticalAnimation, false, true, false, minValue, maxValue);
            return deltaY;
        }

        protected float updateInfiniteSlider(float precision, float sliderWidth, boolean horizontal, boolean reversed,
                                             boolean alternative) {
            if (mousePressed && isMouseOutsideGui()) {
                float screenSpaceDelta = horizontal ? (pmouseX - mouseX) : (pmouseY - mouseY);
                if (reversed) {
                    screenSpaceDelta *= -1;
                }
                return screenDistanceToValueDistance(screenSpaceDelta, precision);
            }
            return 0;
        }


        float screenDistanceToValueDistance(float screenSpaceDelta, float precision) {
            float valueToScreenRatio = precision / width;
            return screenSpaceDelta * valueToScreenRatio;
        }

        void displayInfiniteSliderCenterMode(float x, float y, float w, float h, float precision, float value,
                                             float revealAnimation, boolean horizontal, boolean cutout,
                                             boolean floored, float minValue, float maxValue) {
            float markerHeight = h * revealAnimation;
            pushMatrix();
            pushStyle();
            if (!horizontal) {
                translate(width * .5f, height * .5f);
                rotate(-HALF_PI);
                translate(-height * .5f, -width * .5f);
            }
            translate(x, y);
            noStroke();
            displaySliderBackground(w, h, cutout, horizontal);
            float weight = 2;
            strokeWeight(weight);
            displayHorizontalLine(w, revealAnimation);
            if (!horizontal) {
                pushMatrix();
                scale(-1, 1);
            }
            displayMarkerLines(precision * 0.5f, 0, markerHeight * .6f, weight * revealAnimation,
                    true, value, precision, w, h, !horizontal, revealAnimation, minValue, maxValue);
            displayMarkerLines(precision * .05f, 10, markerHeight * .3f, weight * revealAnimation,
                    false, value, precision, w, h, !horizontal, revealAnimation, minValue, maxValue);
            if (!horizontal) {
                popMatrix();
            }
            displayValue(w, h, precision, value, revealAnimation, floored);
            popMatrix();
            popStyle();
        }

        void displaySliderBackground(float w, float h, boolean cutout, boolean horizontal) {
            fill(0, BACKGROUND_ALPHA);
            rectMode(CENTER);
            float xOffset = 0;
            if (cutout) {
                if (horizontal && trayVisible) {
                    xOffset = trayWidth;
                } else if (!horizontal) {
                    xOffset = h;
                }
            }
            rect(xOffset, 0, w, h);
        }

        void displayHorizontalLine(float w, float revealAnimation) {
            stroke(GRAYSCALE_TEXT_DARK);
            beginShape();
            w *= revealAnimation;
            for (int i = 0; i < w; i++) {
                float iNorm = norm(i, 0, w);
                float screenX = lerp(-w, w, iNorm);
                stroke(GRAYSCALE_TEXT_SELECTED, darkenEdges(screenX, w));
                vertex(screenX, 0);
            }
            endShape();
        }

        void displayMarkerLines(float frequency, int skipEveryNth, float markerHeight, float horizontalLineHeight,
                                boolean shouldDisplayValue, float value, float precision, float w, float h,
                                boolean flipTextHorizontally, float revealAnimationEased, float minValue,
                                float maxValue) {
            float markerValue = -precision - value - frequency;
            int i = 0;
            while (markerValue < precision - value) {
                markerValue += frequency;
                if (skipEveryNth != 0 && i++ % skipEveryNth == 0) {
                    continue;
                }
                float markerNorm = norm(markerValue, -precision - value, precision - value);
                displayMarkerLine(markerValue, precision, w, h, markerHeight, horizontalLineHeight, value,
                        shouldDisplayValue, flipTextHorizontally,
                        revealAnimationEased, minValue, maxValue);
            }
        }

        void displayMarkerLine(float markerValue, float precision, float w, float h, float markerHeight,
                               float horizontalLineHeight,
                               float value, boolean shouldDisplayValue, boolean flipTextHorizontally,
                               float revealAnimationEased, float minValue, float maxValue) {
            float moduloValue = markerValue;
            while (moduloValue > precision) {
                moduloValue -= precision * 2;
            }
            while (moduloValue < -precision) {
                moduloValue += precision * 2;
            }
            float screenX = map(moduloValue, -precision, precision, -w, w);
            float displayValue = moduloValue + value;
            boolean isEdgeValue =
                    (displayValue < minValue + precision * .1 && displayValue > minValue - precision * .1) ||
                            (displayValue > maxValue - precision * .1 && displayValue < maxValue + precision * .1);
            if (!isEdgeValue && (displayValue > maxValue || displayValue < minValue)) {
                return;
            }
            float grayscale = darkenEdges(screenX, w);
            fill(GRAYSCALE_TEXT_SELECTED, grayscale * revealAnimationEased);
            stroke(GRAYSCALE_TEXT_SELECTED, grayscale * revealAnimationEased);
            line(screenX, -markerHeight * .5f, screenX, -horizontalLineHeight * .5f);
            if (shouldDisplayValue) {
                if (flipTextHorizontally) {
                    pushMatrix();
                    scale(-1, 1);
                }
                String displayText = nf(displayValue, 0, 0);
                if (displayText.equals("-0")) {
                    displayText = "0";
                }
                pushMatrix();
                textAlign(CENTER, CENTER);
                textSize(textSize);
                float textX = screenX + ((displayText.equals("0") || displayValue > 0) ? 0 : -textWidth("-") * .5f);
                text(displayText, flipTextHorizontally ? -textX : textX, h * .25f);
                if (flipTextHorizontally) {
                    popMatrix();
                }
                popMatrix();
            }
        }

        void displayValue(float w, float sliderHeight, float precision, float value, float animationEased,
                          boolean floored) {
            fill(GRAYSCALE_TEXT_DARK);
            textAlign(CENTER, CENTER);
            textSize(textSize * 1.2f);
            float textY = -cell * 2.5f;
            float textX = 0;
            String text;
            if (floored) {
                text = String.valueOf(floor(value));
            } else if (abs(value) < 1) {
                if (abs(value) < precision * .001f) {
                    text = nf(value, 0, 0);
                } else {
                    text = String.valueOf(value);
                }
            } else {
                text = nf(value, 0, 2);
            }
            if (text.startsWith("-")) {
                textX -= textWidth("-") * .5f;
            }
            noStroke();
            fill(0, BACKGROUND_ALPHA);
            rectMode(CENTER);
            rect(textX, textY + textSize * .2f, textWidth(text) + 20, textSize * 1.2f + 20);
            fill(GRAYSCALE_TEXT_SELECTED * animationEased);
            text(text, textX, textY);
            stroke(GRAYSCALE_TEXT_SELECTED);
            line(0, -5, 0, 5);
        }

        float darkenEdges(float screenX, float w) {
            float xNorm = norm(screenX, -w, w);
            float distanceFromCenter = abs(.5f - xNorm) * 4;
            return 1 - ease(distanceFromCenter, SLIDER_EDGE_DARKEN_EASING);
        }

        void recordStateForUndo() {
            if (mouseJustPressedOutsideGui()) {
                pushCurrentStateToUndo();
            }
        }

        void displayPrecision(float precision) {
            pushStyle();
            fill(0, BACKGROUND_ALPHA);
            noStroke();
            rectMode(CENTER);
            float x = width - cell * 3.1f;
            float y = height - cell * 2.6f;
            rect(x, y, cell * 2, cell, cell * .8f);
            fill(GRAYSCALE_TEXT_DARK);
            textAlign(CENTER, CENTER);
            textSize(textSize * .75f);
            text(prettyPrecisionFormat(precision), x, y - 2);
            popStyle();
        }

        private String prettyPrecisionFormat(float precision) {
            if (precision > 1) {
                return String.valueOf(floor(precision));
            }
            try {
                String p = String.valueOf(precision).split("\\.")[1];
                StringBuilder result = new StringBuilder("0.");
                for (char c : p.toCharArray()) {
                    result.append(c);
                    if (c != '0') {
                        break;
                    }
                }
                return result.toString();
            } catch (Exception ex) {
                return String.valueOf(precision);
            }
        }
    }

    private class SliderFloat extends Slider {
        boolean constrained, floored;
        float value, precision, defaultValue, defaultPrecision, minValue, maxValue, lastValueDelta;
        float sliderRevealAnimationStarted = -SLIDER_REVEAL_DURATION;

        SliderFloat(Group parent, String name, float defaultValue, float precision,
                    boolean constrained, float min, float max, boolean floored) {
            super(parent, name);
            this.value = defaultValue;
            this.defaultValue = defaultValue;
            this.precision = precision;
            this.defaultPrecision = precision;
            this.floored = floored;
            if (constrained) {
                this.constrained = true;
                minValue = min;
                maxValue = max;
            } else {
                autoDetectConstraints(name);
            }
        }

        private void autoDetectConstraints(String name) {
            if (name.contains("ease") || name.contains("easing")) {
                this.defaultValue = 1;
                value = defaultValue;
            }
            if (name.contains("weight")) {
                this.constrained = true;
                minValue = 0;
                maxValue = Float.MAX_VALUE;
                defaultValue = 1;
                value = defaultValue;
            }
            if (name.equals("fill") || name.equals("stroke")) {
                this.constrained = true;
                minValue = 0;
                maxValue = 255;
            } else if (name.contains("count") || name.contains("size") ||
                    (name.contains("step") && !name.contains("smoothstep"))) {
                constrained = true;
                if (name.contains("count") && defaultValue == 0) {
                    defaultValue = 1;
                }
                minValue = 0;
                maxValue = Float.MAX_VALUE;
                if (value == 0) {
                    this.value = 1;
                }
            } else if (name.equals("drag")) {
                this.constrained = true;
                minValue = 0;
                maxValue = 1;
            }
        }

        void handleActions() {
            if (previousActions.contains(ACTION_COPY)) {
                clipboardSliderFloat = getState();
            }
            if (previousActions.contains(ACTION_PASTE)) {
                if (!clipboardSliderFloat.isEmpty()) {
                    pushCurrentStateToUndo();
                    setState(clipboardSliderFloat);
                }
            }
            if (previousActions.contains(ACTION_PRECISION_ZOOM_OUT) &&
                    ((!floored && precision < FLOAT_PRECISION_MAXIMUM) || (floored && precision < INT_PRECISION_MAXIMUM))) {
                precision *= 10f;
                pushCurrentStateToUndo();
            }
            if (previousActions.contains(ACTION_PRECISION_ZOOM_IN) &&
                    ((!floored && precision > FLOAT_PRECISION_MINIMUM) || (floored && precision > INT_PRECISION_MINIMUM))) {
                precision *= .1f;
                pushCurrentStateToUndo();
            }
            if (overlayVisible && overlayOwner.equals(this) && actions.contains(ACTION_RESET)) {
                pushCurrentStateToUndo();
                reset();
            }
        }

        void reset() {
            precision = defaultPrecision;
            value = defaultValue;
        }

        String getState() {
            return super.getState() + value + SEPARATOR + precision;
        }

        void setState(String newState) {
            String[] split = newState.split(SEPARATOR);
//            println(name, "value", value);
            value = Float.parseFloat(split[2]);
            precision = Float.parseFloat(split[3]);
        }

        void onOverlayShown() {
            if (!overlayVisible || !horizontalOverlayVisible) {
                sliderRevealAnimationStarted = frameCount;
            }
            horizontalOverlayVisible = true;
            verticalOverlayVisible = false;
            pickerOverlayVisible = false;
            zOverlayVisible = false;
        }

        boolean canHaveOverlay() {
            return true;
        }

        void updateOverlay() {
            super.updateOverlay();
            float valueDelta = updateInfiniteSlider(precision, width, true, false, false);
            recordStateForUndo();
            value += valueDelta;
            lastValueDelta = valueDelta;
            if (floored && valueDelta == 0 && lastValueDelta == 0) {
                value = lerp(value, round(value), INTEGER_SLIDER_ROUNDING_LERP_AMT);
            }
            if (constrained) {
                value = constrain(value, minValue, maxValue);
            }
            float revealAnimation = easedAnimation(sliderRevealAnimationStarted - SLIDER_REVEAL_START_SKIP,
                    SLIDER_REVEAL_DURATION,
                    SLIDER_REVEAL_EASING);
            displayInfiniteSliderCenterMode(width * .5f, height - cell, width, sliderHeight, precision,
                    value, revealAnimation, true, true, floored,
                    constrained ? minValue : -Float.MAX_VALUE,
                    constrained ? maxValue : Float.MAX_VALUE);
            displayPrecision(precision);
        }
    }

    private class SliderXY extends Slider {
        float deltaX;
        float deltaY;
        PVector value = new PVector();
        PVector defaultValue = new PVector();
        float precision, defaultPrecision;
        float horizontalRevealAnimationStarted = -SLIDER_REVEAL_DURATION;
        float verticalRevealAnimationStarted = -SLIDER_REVEAL_DURATION;
        float interactionBufferMultiplier = 2.5f;

        SliderXY(Group currentGroup, String name, float defaultX, float defaultY, float precision) {
            super(currentGroup, name);
            this.precision = precision;
            this.defaultPrecision = precision;
            value.x = defaultX;
            value.y = defaultY;
            defaultValue.x = defaultX;
            defaultValue.y = defaultY;
        }

        String getState() {
            return super.getState() + precision + SEPARATOR + value.x + SEPARATOR + value.y;
        }

        void setState(String newState) {
            String[] xyz = newState.split(SEPARATOR);
            precision = Float.parseFloat(xyz[2]);
            value.x = Float.parseFloat(xyz[3]);
            value.y = Float.parseFloat(xyz[4]);
        }

        boolean canHaveOverlay() {
            return true;
        }

        void onOverlayShown() {
            if (!overlayVisible || !horizontalOverlayVisible) {
                horizontalRevealAnimationStarted = frameCount;
            }
            if (!overlayVisible || !verticalOverlayVisible) {
                verticalRevealAnimationStarted = frameCount;
            }
            horizontalOverlayVisible = true;
            verticalOverlayVisible = true;
            pickerOverlayVisible = false;
            zOverlayVisible = false;
        }

        void updateOverlay() {
            super.updateOverlay();
            recordStateForUndo();
            updateXYSliders();
            lockOtherSlidersOnMouseOver();
            value.x += deltaX;
            value.y += deltaY;
            displayPrecision(precision);
        }

        protected void lockOtherSlidersOnMouseOver() {
            if (isMouseOverXSlider()) {
                deltaY = 0;
            } else if (isMouseOverYSlider()) {
                deltaX = 0;
            }
        }

        protected boolean isMouseOverXSlider() {
            return isMouseOver(0, height - cell * interactionBufferMultiplier, width, sliderHeight * 2);
        }

        protected boolean isMouseOverYSlider() {
            return isMouseOver(width - cell * interactionBufferMultiplier, 0, sliderHeight * 2, height);
        }


        void updateXYSliders() {
            deltaX = updateFullHorizontalSlider(0, height - cell, width, sliderHeight, value.x, precision,
                    horizontalRevealAnimationStarted, false, -Float.MAX_VALUE, Float.MAX_VALUE);
            deltaY = updateFullHeightVerticalSlider(0, width - cell, height, sliderHeight, value.y, precision,
                    verticalRevealAnimationStarted, false, -Float.MAX_VALUE, Float.MAX_VALUE);
        }

        void handleActions() {
            if (previousActions.contains(ACTION_COPY)) {
                clipboardSliderXYZ = getState();
            }
            if (previousActions.contains(ACTION_PASTE)) {
                if (!clipboardSliderXYZ.isEmpty()) {
                    pushCurrentStateToUndo();
                    setState(clipboardSliderXYZ);
                }
            }
            if (previousActions.contains(ACTION_PRECISION_ZOOM_IN) && precision > FLOAT_PRECISION_MINIMUM) {
                precision *= .1f;
                pushCurrentStateToUndo();
            }
            if (previousActions.contains(ACTION_PRECISION_ZOOM_OUT) && precision < FLOAT_PRECISION_MAXIMUM) {
                precision *= 10f;
                pushCurrentStateToUndo();
            }
            if (overlayVisible && overlayOwner.equals(this) && actions.contains(ACTION_RESET)) {
                pushCurrentStateToUndo();
                reset();
            }
        }

        void reset() {
            precision = defaultPrecision;
            value.x = defaultValue.x;
            value.y = defaultValue.y;
            value.z = defaultValue.z;
        }
    }

    private class SliderXYZ extends SliderXY {
        private float zRevealAnimationStarted = -SLIDER_REVEAL_DURATION;
        private float deltaZ;

        SliderXYZ(Group currentGroup, String name, float defaultX, float defaultY, float defaultZ, float precision) {
            super(currentGroup, name, defaultX, defaultY, precision);
            this.defaultValue.z = defaultZ;
            this.value.z = defaultZ;
        }

        void update() {
            super.update();
        }

        void handleActions() {
            super.handleActions();
        }

        String getState() {
            return super.getState() + SEPARATOR + value.z;
        }

        void setState(String newState) {
            super.setState(newState);
            value.z = Float.parseFloat(newState.split(SEPARATOR)[5]);
        }

        void updateOverlay() {
            super.updateOverlay();
            recordStateForUndo();
            deltaZ = updateInfiniteSlider(precision, height * .5f, false, true, true);
            lockOtherSlidersOnMouseOver();
            value.x += deltaX;
            value.y += deltaY;
            value.z += deltaZ;
            float zAnimation = easedAnimation(zRevealAnimationStarted, SLIDER_REVEAL_DURATION, SLIDER_REVEAL_EASING);
            displayInfiniteSliderCenterMode(height - height * .2f, width - sliderHeight * 2, height / 3f,
                    sliderHeight * .8f, precision, value.z, zAnimation, false, false,
                    false, -Float.MAX_VALUE, Float.MAX_VALUE);
            super.updateXYSliders();
            displayPrecision(precision);
        }

        private boolean isMouseOverZSlider() {
            stroke(1);
            return isMouseOver(width - sliderHeight * 4, 0, sliderHeight * 3, height * .4f);
        }

        protected void lockOtherSlidersOnMouseOver() {
            if (isMouseOverXSlider()) {
                deltaY = 0;
                deltaZ = 0;
            }
            if (isMouseOverYSlider()) {
                deltaX = 0;
                deltaZ = 0;
            }
            if (isMouseOverZSlider()) {
                deltaX = 0;
                deltaY = 0;
            }
        }


        void onOverlayShown() {
            if (!overlayVisible || !horizontalOverlayVisible) {
                horizontalRevealAnimationStarted = frameCount;
            }
            if (!overlayVisible || !verticalOverlayVisible) {
                verticalRevealAnimationStarted = frameCount;
            }
            if (!overlayVisible || !zOverlayVisible) {
                zRevealAnimationStarted = frameCount;
            }
            horizontalOverlayVisible = true;
            verticalOverlayVisible = true;
            pickerOverlayVisible = false;
            zOverlayVisible = true;
        }

    }

    private class ColorPicker extends Slider {
        HSBA hsba;
        float defaultHue, defaultSat, defaultBr, defaultAlpha;
        float pickerRevealStarted = -PICKER_REVEAL_DURATION;
        float huePrecision = .5f;
        float alphaPrecision = 1;
        private boolean brightnessLocked, saturationLocked;
        private boolean satChanged, brChanged;

        ColorPicker(Group currentGroup, String name, float hue, float sat, float br, float alpha) {
            super(currentGroup, name);
            this.hsba = new HSBA(hue, sat, br, alpha);
            this.defaultHue = hue;
            this.defaultSat = sat;
            this.defaultBr = br;
            this.defaultAlpha = alpha;
        }

        void handleActions() {
            if (previousActions.contains(ACTION_COPY)) {
                clipboardPicker = getState();
            }
            if (previousActions.contains(ACTION_PASTE)) {
                if (!clipboardPicker.isEmpty()) {
                    pushCurrentStateToUndo();
                    setState(clipboardPicker);
                }
            }
            if (overlayVisible && overlayOwner != null && overlayOwner.equals(this) && actions.contains(ACTION_RESET)) {
                pushCurrentStateToUndo();
                reset();
            }
            if (alphaPrecision > ALPHA_PRECISION_MINIMUM && previousActions.contains(ACTION_PRECISION_ZOOM_IN)) {
                alphaPrecision *= .1f;
                pushCurrentStateToUndo();
            }
            if (alphaPrecision < ALPHA_PRECISION_MAXIMUM && previousActions.contains(ACTION_PRECISION_ZOOM_OUT)) {
                alphaPrecision *= 10;
                pushCurrentStateToUndo();
            }
            hsba.enforceConstraints();
        }

        void reset() {
            hsba.hue = defaultHue;
            hsba.sat = defaultSat;
            hsba.br = defaultBr;
            hsba.alpha = defaultAlpha;
        }

        String getState() {
            return super.getState() + hsba.hue + SEPARATOR + hsba.sat + SEPARATOR + hsba.br + SEPARATOR + hsba.alpha + SEPARATOR + alphaPrecision;
        }

        void setState(String newState) {
            String[] split = newState.split(SEPARATOR);
            hsba.hue = Float.parseFloat(split[2]);
            hsba.sat = Float.parseFloat(split[3]);
            hsba.br = Float.parseFloat(split[4]);
            hsba.alpha = Float.parseFloat(split[5]);
            alphaPrecision = Float.parseFloat(split[6]);
        }

        void displayOnTray(float x, float y) {
            pushStyle();
            stroke(GRAYSCALE_TEXT_DARK);
            strokeWeight(1);
            fill(hsba.clr());
            rectMode(CENTER);
            rect(x - previewTrayBoxMargin - previewTrayBoxWidth,
                    y - textSize * .5f, previewTrayBoxWidth, previewTrayBoxWidth);
            popStyle();
            super.displayOnTray(x, y);
        }

        boolean canHaveOverlay() {
            return true;
        }

        void onOverlayShown() {
            if (!pickerOverlayVisible) {
                pickerRevealStarted = frameCount;
            }
            pickerOverlayVisible = true;
            horizontalOverlayVisible = false;
            verticalOverlayVisible = false;
            zOverlayVisible = false;
        }

        @SuppressWarnings("SuspiciousNameCombination")
        void updateOverlay() {
            super.updateOverlay();
            if (mouseJustReleased()) {
                brightnessLocked = false;
                saturationLocked = false;
            }
            recordStateForUndo();
            pushStyle();
            colorMode(HSB, 1, 1, 1, 1);
            float revealAnimation = easedAnimation(pickerRevealStarted - PICKER_REVEAL_START_SKIP,
                    PICKER_REVEAL_DURATION, PICKER_REVEAL_EASING);
            int tinySliderCount = 2;
            float tinySliderMarginCellFraction = .2f;
            float tinySliderWidth = cell * 1.5f * (1 + tinySliderMarginCellFraction);
            float x = width - tinySliderWidth * tinySliderCount * (1 + tinySliderMarginCellFraction)
                    + tinySliderMarginCellFraction * tinySliderCount;
            float h = cell * 4;
            float tinySliderTopY =
                    height - sliderHeight * .5f - cell * tinySliderMarginCellFraction - h * revealAnimation;
            float lastSat = hsba.sat;
            hsba.sat = updateTinySlider(x, tinySliderTopY, tinySliderWidth, h, brightnessLocked, SATURATION);
            if (hsba.sat != lastSat && !saturationLocked) {
                brightnessLocked = true;
            }
            if (saturationLocked) {
                hsba.sat = lastSat;
            }
            displayTinySlider(x, tinySliderTopY, tinySliderWidth, cell * 4, hsba.sat, SATURATION,
                    brightnessLocked);

            x += tinySliderWidth * 1.2f;
            float lastBr = hsba.br;
            hsba.br = updateTinySlider(x, tinySliderTopY, tinySliderWidth, h, saturationLocked, BRIGHTNESS);
            if (hsba.br != lastBr && !brightnessLocked) {
                saturationLocked = true;
            }
            if (brightnessLocked) {
                hsba.br = lastBr;
            }
            displayTinySlider(x, tinySliderTopY, tinySliderWidth, cell * 4, hsba.br, BRIGHTNESS, saturationLocked);

            displayInfiniteSliderCenterMode(height - height / 4f, width - sliderHeight * .5f, height / 2f, sliderHeight,
                    alphaPrecision, hsba.alpha, revealAnimation, false, false, false, 0, 1);
            fill(GRAYSCALE_TEXT_DARK);
            textAlign(CENTER, CENTER);
            textSize(textSize);
            text("alpha", width - sliderHeight * .5f, 15);
            float alphaDelta = updateInfiniteSlider(alphaPrecision, height, false, false, false);
            boolean isMouseInTopHalf = isMouseOver(width * .5f, 0, width * .5f, height / 2f);
            if (!satChanged && !brChanged && isMouseInTopHalf) {
                hsba.alpha += alphaDelta;
            }

            displayHueSlider(sliderHeight, revealAnimation);
            float hueDelta = updateInfiniteSlider(huePrecision, width, true, false, false);
            if (!satChanged && !brChanged) {
                hsba.hue += hueDelta;
            }
            satChanged = false;
            brChanged = false;
            hsba.enforceConstraints();
            displayValueRectangle(sliderHeight);
            popStyle();
        }

        private void displayHueSlider(float h, float revealAnimation) {
            displayHueStripCornerMode(height + cell - h * revealAnimation, h * .5f, true, revealAnimation);
            displayHueStripCornerMode(height + cell - h * .5f * revealAnimation, h * .5f, false, revealAnimation);
        }

        private void displayHueStripCornerMode(float y, float h, boolean top, float revealAnimation) {
            beginShape(TRIANGLE_STRIP);
            noStroke();
            int detail = floor(width * .3f);
            for (int i = 0; i < detail; i++) {
                float iNorm = norm(i, 0, detail - 1);
                float x = iNorm * width;
                if (abs(.5f - iNorm) * 2 > revealAnimation) {
                    continue;
                }
                float iHue = hueModulo(hsba.hue - .5f + iNorm);
                int iColor = getColorAt(iHue, HUE);
                fill(iColor);
                vertex(x, y);
                vertex(x, y + h);
            }
            endShape();
        }

        private void displayValueRectangle(float hueSliderHeight) {
            float x = width * .5f;
            float y = height - hueSliderHeight - cell * 3f;
            noStroke();
            fill(hsba.clr());
            rectMode(CENTER);
            rect(x, y, cell * 3, cell * 3);
        }

        private float updateTinySlider(float x, float topY, float w, float h, boolean forceActive, String type) {
            float interactionBuffer = cell;
            if (forceActive || (mousePressed && isMouseOver(x, topY - interactionBuffer, w,
                    h + interactionBuffer * 1.2f))) {
                float newValue = constrain(map(mouseY, topY, topY + h, 0, 1), 0, 1);
                setTinySliderValue(newValue, type);
            }
            return getTinySliderValue(type);
        }

        private void displayTinySlider(float x, float topY, float w, float h, float value, String type,
                                       boolean mouseOver) {
            beginShape(TRIANGLE_STRIP);
            noStroke();
            int detail = floor(h * .1f);
            for (int i = 0; i < detail; i++) {
                float iNorm = norm(i, 0, detail - 1);
                float y = topY + h * iNorm;
                fill(getColorAt(iNorm, type));
                vertex(x, y);
                vertex(x + w, y);
            }
            endShape();
            float valueY = topY + h * value;
            strokeWeight(2);
            stroke((type.equals(SATURATION) && satChanged) ||
                    (type.equals(BRIGHTNESS) && brChanged) || mouseOver ?
                    GRAYSCALE_TEXT_SELECTED : GRAYSCALE_TEXT_DARK);
            line(x - 2, valueY, x + w + 2, valueY);
        }

        private int getColorAt(float value, String type) {
            if (type.equals(HUE)) {
                return color(value, hsba.sat, hsba.br, hsba.alpha);
            }
            if (type.equals(SATURATION)) {
                return color(hsba.hue, value, hsba.br, hsba.alpha);
            }
            if (type.equals(BRIGHTNESS)) {
                return color(hsba.hue, hsba.sat, value, hsba.alpha);
            }
            return 0;
        }


        private float getTinySliderValue(String type) {
            if (type.equals(SATURATION)) {
                return hsba.sat;
            }
            if (type.equals(BRIGHTNESS)) {
                return hsba.br;
            }
            return 0;
        }

        private void setTinySliderValue(float newValue, String type) {
            if (type.equals(SATURATION)) {
                hsba.sat = newValue;
                satChanged = true;
            }
            if (type.equals(BRIGHTNESS)) {
                hsba.br = newValue;
                brChanged = true;
            }
        }

        HSBA getHSBA() {
            return hsba;
        }
    }

    public class HSBA {
        private float hue, sat, br, alpha;

        public HSBA(float hue, float sat, float br, float alpha) {
            this.hue = hue;
            this.sat = sat;
            this.br = br;
            this.alpha = alpha;
        }

        public HSBA() {
            this.alpha = 1;
        }

        public int clr() {
            pushStyle();
            enforceConstraints();
            colorMode(HSB, 1, 1, 1, 1);
            int result = color(hue, sat, br, alpha);
            popStyle();
            return result;
        }

        public float hue() {
            enforceConstraints();
            return hue;
        }

        public void addHue(float val) {
            hue += val;
            enforceConstraints();
        }

        public float sat() {
            enforceConstraints();
            return sat;
        }


        public void setSat(float val) {
            sat = val;
            enforceConstraints();
        }

        public float br() {
            enforceConstraints();
            return br;
        }

        public void setBr(float val) {
            br = val;
            enforceConstraints();
        }

        public float alpha() {
            enforceConstraints();
            return alpha;
        }

        private void enforceConstraints() {
            hue = hueModulo(hue);
            sat = constrain(sat, 0, 1);
            br = constrain(br, 0, 1);
            alpha = constrain(alpha, 0, 1);
        }

        public void setAlpha(float val) {
            alpha = val;
            enforceConstraints();
        }
    }
}
