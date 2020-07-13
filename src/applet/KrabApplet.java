package applet;

import processing.core.*;
import processing.event.MouseEvent;
import processing.opengl.PShader;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static java.lang.System.currentTimeMillis;

/**
 * This class offers common functionality to all of my processing sketches, including a GUI, shader reloading at
 * runtime and many other utility functions and features.
 * <p>
 * See the GuiManual in readme for documentation.
 */

// TODO default alpha
// TODO make rec() take intended frame count as param
// TODO migrate saving gui data from custom silly format to json

public abstract class KrabApplet extends PApplet {
    protected static Boolean FFMPEG_ENABLED = true;
    private static final String STATE_BEGIN = "STATE_BEGIN";
    private static final String STATE_END = "STATE_END";
    private static final String SEPARATOR = "§";
    private static final String NEWLINE_PLACEHOLDER = "#NEWLINE#";
    private static final String UNDO_PREFIX = "UNDO";
    private static final String REDO_PREFIX = "REDO";
    private static final String GROUP_PREFIX = "GROUP";
    private static final String ACTION_PRECISION_ZOOM_IN = "PRECISION_ZOOM_IN";
    private static final String ACTION_PRECISION_ZOOM_OUT = "PRECISION_ZOOM_OUT";
    private static final String ACTION_FULLSCREEN_TOGGLE = "FULLSCREEN_TOGGLE";
    private static final String ACTION_RESET = "RESET";
    private static final String ACTION_HIDE = "HIDE";
    private static final String ACTION_UNDO = "UNDO";
    private static final String ACTION_REDO = "REDO";
    private static final String ACTION_SAVE = "SAVE";
    private static final String ACTION_COPY = "COPY";
    private static final String ACTION_PASTE = "PASTE";
    private static final String ACTION_CHANGE_TYPE = "CHANGE_TYPE";
    private static final String ACTION_CHANGE_BLEND = "CHANGE_BLEND";
    private static final int MENU_BUTTON_COUNT = 4;
    private static final String SATURATION = "saturation";
    private static final String BRIGHTNESS = "brightness";
    private static final String HUE = "hue";
    private static final float BACKGROUND_ALPHA = .9f;
    private static final float GRAYSCALE_DARK = .5f;
    private static final float GRAYSCALE_SELECTED = 1;
    private static final float INT_PRECISION_MAXIMUM = 100000;
    private static final float INT_PRECISION_MINIMUM = 10f;
    private static final float FLOAT_PRECISION_MAXIMUM = 10000;
    private static final float FLOAT_PRECISION_MINIMUM = .01f;
    private static final float ALPHA_PRECISION_MINIMUM = .005f;
    private static final float ALPHA_PRECISION_MAXIMUM = 100;
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
    private static String clipboardGradient = "";
    private final int KEY_CTRL_C = 3;
    private final int KEY_CTRL_V = 22;
    @SuppressWarnings("FieldCanBeLocal")
    private final int KEY_CTRL_S = 19;
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
    private final float shadowOffset = cell * 0.05f;
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
    @SuppressWarnings("FieldCanBeLocal")
    private final String videoOutputDir = "/out/video";
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
    private Element overlayOwner = null;
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
    private boolean keyboardLockedByTextEditor = false;
    private final int autoTrayHideDuration = 120;
    private int autoTrayHideStarted = -autoTrayHideDuration;
    boolean mouseWasOutsideTray = false;

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

    protected int sliderInt(String name, int defaultValue, int min, int max) {
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

    protected float slider(String name, float min, float max, float defaultValue, float precision) {
        return slider(name, defaultValue, precision, true, min, max, false);
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

    protected PGraphics gradient(String name) {
        return gradient(name, 2, GradientType.VERTICAL, width, height);
    }

    protected PGraphics gradient(String name, int w, int h) {
        return gradient(name, 2, GradientType.VERTICAL, w, h);
    }

    protected PGraphics gradient(String name, int defaultColorCount, GradientType defaultType, int w, int h) {
        Group currentGroup = getCurrentGroup();
        if (elementDoesntExist(name, currentGroup.name)) {
            GradientEditor newElement = new GradientEditor(currentGroup, name, defaultColorCount, w, h, defaultType);
            currentGroup.elements.add(newElement);
        }
        GradientEditor gradientEditor = (GradientEditor) findElement(name, currentGroup.name);
        if (gradientEditor != null) {
            return gradientEditor.getTexture(w, h);
        }
        throw new IllegalStateException("gradient picker was not found");
    }

    // the defaultValue parameter becomes the name of the element and must be unique within the current group
    protected String options(String defaultValue, String... otherValues) {
        Group currentGroup = getCurrentGroup();
        if (elementDoesntExist(defaultValue, currentGroup.name)) {
            Element newElement = new Radio(currentGroup, defaultValue, otherValues);
            currentGroup.elements.add(newElement);
        }
        Radio radio = (Radio) findElement(defaultValue, currentGroup.name);
        return radio.options.get(radio.valueIndex);
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

    protected String textInput(String name) {
        return textInput(name, "");
    }

    protected String textInput(String name, String defaultValue) {
        Group currentGroup = getCurrentGroup();
        if (elementDoesntExist(name, currentGroup.name)) {
            TextInput newElement = new TextInput(currentGroup, name, defaultValue);
            currentGroup.elements.add(newElement);
        }
        TextInput textInput = (TextInput) findElement(name, currentGroup.name);
        return textInput.value;
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
        updateFullscreenToggle();
        updateKeyboardInput();
        updateMouseState();
        pushStyle();
        pushMatrix();
        blendMode(BLEND);
        strokeCap(SQUARE);
        colorMode(HSB, 1, 1, 1, 1);
        resetMatrixInAnyRenderer();
        hint(DISABLE_DEPTH_TEST);
        updateTray();
        if (overlayVisible && trayVisible) {
            overlayOwner.updateOverlay();
        }
        updateScrolling();
        hint(ENABLE_DEPTH_TEST);
        popStyle();
        popMatrix();
        pMousePressed = mousePressed;
        if (frameCount == 1) {
            trayVisible = elementCount() != 0;
        }
        resetGroup();
    }

    private void updateFullscreenToggle() {
        if(previousActionsContainsLockAware(ACTION_FULLSCREEN_TOGGLE)) {
            toggleFullscreen();
        }
    }

    private void toggleFullscreen() {
        boolean windowed = width == displayWidth;
        if(windowed) {
            surface.setSize(1000,1000);
        }else {
            surface.setSize(displayWidth, displayHeight);
        }
    }

    private void updateTray() {
        pushMatrix();
        updateAutomaticTrayHide();
        updateTrayBackground();
        updateMenuButtons();
        updateGroupsAndTheirElements();
        updateFps();
        popMatrix();
    }

    private void updateAutomaticTrayHide() {
        if (trayVisible && isMouseOutsideVisibleTray() && !mouseWasOutsideTray) {
            autoTrayHideStarted = frameCount;
        }
        int autoTrayHideDelay = 180;
        if (isMouseOutsideVisibleTray()) {
            float hideAnimation = easedAnimation(autoTrayHideStarted + autoTrayHideDelay, autoTrayHideDuration, 3f);
            translate(-trayWidth * hideAnimation, 0);
        } else {
            autoTrayHideStarted = frameCount + autoTrayHideDuration + autoTrayHideDelay;
        }
        mouseWasOutsideTray = isMouseOutsideVisibleTray();
    }

    // GENERAL UTILS

    protected PGraphics updateGraphics(PGraphics pg) {
        return updateGraphics(pg, width, height);
    }

    protected PGraphics updateGraphics(PGraphics pg, int w, int h) {
        if (pg == null || pg.width != w || pg.height != h) {
            pg = createGraphics(width, height, P3D);
        }
        return pg;
    }

    public static void println(String str) {
        PApplet.println(getTime(), str);
    }

    public static void println(String... args) {
        Object[] line = new Object[args.length];
        line[0] = getTime();
        System.arraycopy(args, 0, line, 1, args.length - 1);
        PApplet.println(line);
    }

    private static String getTime() {
        return nf(hour(), 2, 0) + ":" + nf(minute(), 2, 0) + ":" + nf(second(), 2, 0) + "\t";
    }

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
        pg.fill(255, slider("fade to black", 0, 255, 10));
        pg.rectMode(CENTER);
        pg.rect(0, 0, width * 3, height * 3);
        pg.hint(ENABLE_DEPTH_TEST);
        pg.popStyle();
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

    protected void translate2D(PGraphics pg) {
        translate2D(pg, "translate");
    }

    protected void translate2D(PGraphics pg, String sliderName) {
        PVector translate = sliderXY(sliderName);
        pg.translate(translate.x, translate.y);
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
        preRotate(pg, "rotate", 1);
    }

    protected void preRotate(PGraphics pg, String sliderName) {
        preRotate(pg, sliderName, 1);
    }

    /**
     * Rotates to an arbitrary vector controlled by a 3D slider with a precision of PI.
     * Rotations are pre-applied, so the axes are not affected by any previous rotations, which makes it more intuitive.
     * This method can be called any number of times - as long as the slider names are unique it will produce a unique
     * rotation.
     *
     * @param pg         PGraphics to rotate
     * @param sliderName name of the slider and the key of the PMatrix in the sliderRotationMatrixMap
     */
    protected void preRotate(PGraphics pg, String sliderName, float multiplier) {
        PMatrix3D rotationMatrix;
        PVector previousSliderRotation = new PVector();
        if (sliderRotationMatrixMap.containsKey(sliderName)) {
            rotationMatrix = sliderRotationMatrixMap.get(sliderName);
            previousSliderRotation = previousSliderRotationMap.get(sliderName);
        } else {
            rotationMatrix = new PMatrix3D();
            sliderRotationMatrixMap.put(sliderName, rotationMatrix);
        }
        PVector rotation = sliderXYZ(sliderName, 0, PI).copy().mult(multiplier);
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

    /**
     * Takes a PGraphics, splits it up into primary color images and re-assembles them at different scales growing
     * from the center.
     * This method expects the PGraphics to be already closed with endDraw() in order to read from it and write to it.
     * When finished it draws the result over the input PGraphics
     *
     * @param pg input image
     */
    protected void rgbSplitScaleAndOffset(PGraphics pg) {
        if (colorSplitResult == null || colorSplitResult.width != pg.width || colorSplitResult.height != pg.height) {
            colorSplitResult = createGraphics(pg.width, pg.height, P2D);
            primaryColorCanvases = new PGraphics[3];
            for (int i = 0; i < 3; i++) {
                primaryColorCanvases[i] = createGraphics(pg.width, pg.height, P2D);
            }
        }

        colorSplitResult.beginDraw();
        colorSplitResult.clear();
        colorSplitResult.translate(colorSplitResult.width / 2f, colorSplitResult.height / 2f);
        group("scale");
        translate2D(colorSplitResult, "center");
        PVector scale = sliderXYZ("RGB scales", 1, 0.1f);
        float commonScale = slider("common scale", 1, 0.1f);
        if (toggle("set common scale", true)) {
            scale.x = commonScale;
            scale.y = commonScale;
            scale.z = commonScale;
        }
        float[] scales = new float[]{scale.x, scale.y, scale.z};
        if (toggle("force scales >= 1")) { //scales smaller than 1 can result in ugly edges
            while (scales[0] < 1 || scales[1] < 1 || scales[2] < 1) {
                scales[0] += .001;
                scales[1] += .001;
                scales[2] += .001;
            }
        }

        group("colors");
        PVector multiplier = sliderXYZ("multiplier", 1, 0.1f);
        for (int i = 0; i < 3; i++) {
            PGraphics primaryColorCanvas = primaryColorCanvases[i];
            primaryColorCanvas.beginDraw();
            primaryColorCanvas.clear();
            primaryColorCanvas.image(pg, 0, 0, width, height);
            PVector finalMultiplier = primaryColorMultipliers[i].copy();
            if (i == 0) {
                finalMultiplier.x *= multiplier.x;
            } else if (i == 1) {
                finalMultiplier.y *= multiplier.y;
            } else {
                finalMultiplier.z *= multiplier.z;
            }
            colorFilter(primaryColorCanvas, finalMultiplier);
            primaryColorCanvas.endDraw();
        }
        for (int i = 0; i < 3; i++) {
            colorSplitResult.pushMatrix();
            colorSplitResult.imageMode(CENTER);
            colorSplitResult.blendMode(ADD);
            PVector offset = sliderXY("move " + indexToPrimaryColorShorthand(i));
            colorSplitResult.translate(offset.x, offset.y);
            colorSplitResult.scale(scales[i]);
            colorSplitResult.image(primaryColorCanvases[i], 0, 0);
            colorSplitResult.popMatrix();
        }
        colorSplitResult.endDraw();

        pg.beginDraw();
        pg.pushStyle();
        pg.clear();
        pg.hint(PConstants.DISABLE_DEPTH_TEST);
        pg.imageMode(CORNER);
        pg.image(colorSplitResult, 0, 0, pg.width, pg.height);
        pg.hint(PConstants.ENABLE_DEPTH_TEST);
        pg.popStyle();
        pg.endDraw();

        resetGroup();
    }

    String indexToPrimaryColorShorthand(int index) {
        switch (index) {
            case 0: {
                return "R";
            }
            case 1: {
                return "G";
            }
            case 2: {
                return "B";
            }
            default: {
                return "";
            }
        }
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
     * Constructs a random square image url with the specified size.
     *
     * @param size image width to request
     * @return random square image
     */
    public String randomImageUrl(float size) {
        return randomImageUrl(size, size);
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

    protected boolean isPointInCircle(float px, float py, float cx, float cy, float cr) {
        return dist(px, py, cx, cy) < cr;
    }

    protected float easeInOutExpo(float currentTime, float startValue, float changeInValue, float duration) {
        currentTime /= duration / 2;
        if (currentTime < 1) return changeInValue / 2 * pow(2, 10 * (currentTime - 1)) + startValue;
        currentTime--;
        return changeInValue / 2 * (-pow(2, -10 * currentTime) + 2) + startValue;
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
    private float easedAnimation(float startFrame, float duration, float easingFactor) {
        float animationNormalized = constrain(norm(frameCount, startFrame,
                startFrame + duration), 0f, 1f);
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

    protected float clampNorm(float x, float min, float max) {
        return constrain(norm(x, min, max), 0, 1);
    }

    @SuppressWarnings("unused")
    protected float clampMap(float x, float xMin, float xMax, float min, float max) {
        return constrain(map(x, xMin, xMax, min, max), min, max);
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
    @SuppressWarnings("unused")
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

    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    protected void uniformRamp(String fragPath) {
        uniformRamp(fragPath, null, "ramp", 4);
    }

    /**
     * Creates a gradient with adjustable colors and color positions using the GUI
     * and passes it to a shader as a texture. Deprecated in favor of GradientEditor.
     *
     * @param fragPath          path to the fragment shader
     * @param vertPath          path to the vertex shader, can be null
     * @param rampName          name of the ramp's GUI group
     * @param defaultColorCount default number of colors
     *                          any saved settings for things higher than this number won't be loaded on startup
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    protected void uniformRamp(String fragPath, String vertPath, String rampName, int defaultColorCount) {
        if (shaderRamp == null) {
            shaderRamp = createGraphics(5, 1000, P2D);
        }
        shaderRamp.beginDraw();
        shaderRamp.clear();
        ramp(shaderRamp, rampName, defaultColorCount, true);
        shaderRamp.endDraw();
        if (vertPath != null) {
            uniform(fragPath, vertPath).set("ramp", shaderRamp);
        } else {
            uniform(fragPath).set("ramp", shaderRamp);
        }
    }

    protected void ramp(PGraphics pg) {
        ramp(pg, "ramp", 4);
    }

    protected void ramp(PGraphics pg, int defaultColorCount) {
        ramp(pg, "ramp", defaultColorCount);
    }

    protected void ramp(PGraphics pg, String rampName, int defaultColorCount) {
        group(rampName);
        boolean vertical = options("vertical", "circular").equals("vertical");
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
     * Creates an array of PShapes each holding up to 100000 shapes at position 0 and of the given shapeType.
     *
     * @param count     total number of PShapes across all lists
     * @param shapeType type of shapes to create
     * @return array of shapes of the type shapeType
     */
    @SuppressWarnings("SameParameterValue")
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
    @SuppressWarnings("unused")
    protected void spiralSphere(PGraphics pg) {
        group("sphere");
//        pg.beginShape(POINTS);
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
            pg.point(cos(lon) * r * scl, sin(lon) * r * scl, z * scl);
            z = z - dz;
            lon = lon + s / r;
        }
//        pg.endShape();
        pg.noStroke();
        if (!toggle("hollow")) {
            pg.fill(0);
            pg.sphereDetail(floor(slider("core detail", 20)));
            pg.sphere(slider("scale") - slider("core size", 5));
        }
        resetGroup();
    }

    /**
     * Gets vertices of a spiral sphere at a given detail level.
     *
     * @param count number of vertices to use
     * @return array of points that together form a spiral sphere
     */
    @SuppressWarnings("unused")
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
        mousePressedOutsideGui = mousePressed && isMouseOutsideVisibleTray() && (!trayVisible || !overlayVisible);
    }

    private void guiSetup(boolean defaultVisibility) {
        if (frameCount == 1) {
            trayVisible = defaultVisibility;
            textSize(textSize * 2);
        } else if (frameCount == 2) {
            loadLastStateFromFile(true);
        }
    }

    protected void resetGroup() {
        currentGroup = null;
    }

    private void updateScrolling() {
        scrollOffsetHistory.add(trayScrollOffset);
        int scrollOffsetHistorySize = 3;
        while (scrollOffsetHistory.size() > scrollOffsetHistorySize) {
            scrollOffsetHistory.remove(0);
        }
        if (trayVisible && isMousePressedInsideRect(0, 0, trayWidth, height) && abs(pmouseY - mouseY) > 2) {
            trayScrollOffset += mouseY - pmouseY;
        }
    }

    private boolean trayNotMovedInAWhile() {
        for (Float historicalTrayOffset : scrollOffsetHistory) {
            if (historicalTrayOffset != trayScrollOffset) {
                return false;
            }
        }
        return true;
    }

    // RECORDING

    public void rec(int frames) {
        frameRecordingDuration = frames;
        rec(g);
    }

    public void rec(PGraphics pg, int frames) {
        frameRecordingDuration = frames;
        savePGraphics(pg);
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
            println(frameNumber, "/", frameRecordingEnd - frameRecordingStarted, "saved");
            pg.save(captureDir + frameNumber + ".jpg");
            if (frameCount == frameRecordingEnd - 1) {
                runFfmpeg();
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void runFfmpeg() {
        if (!FFMPEG_ENABLED) {
            return;
        }
        String sketchPath = sketchPath().replaceAll("\\\\", "/");
        if (!sketchFile(videoOutputDir).exists()) {
            sketchFile(videoOutputDir).mkdir();
        }
        String imageSequenceFormat = "%01d.jpg";
        String command = String.format("ffmpeg -framerate 60 -an -start_number_range 100 -i %s/%s%s %s/%s.mp4",
                sketchPath, captureDir, imageSequenceFormat, sketchPath + videoOutputDir, id);
        println();
        println("running ffmpeg: " + command);
        try {
            Process proc = Runtime.getRuntime().exec(command);
            new Thread(() -> {
                Scanner sc = new Scanner(proc.getErrorStream());
                while (sc.hasNextLine()) {
                    println(sc.nextLine());
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // TRAY

    private void updateFps() {
        int nonFlickeringFrameRate = floor(frameRate > 55 ? 60 : frameRate);
        String fps = nonFlickeringFrameRate + " fps";
        surface.setTitle(this.getClass().getSimpleName() + " " + fps);
        if (trayVisible) {
            pushStyle();
            colorMode(HSB, 1, 1, 1, 1);
            textSize(textSize);
            textAlign(LEFT, TOP);
            fill(0);
            text(fps, trayWidth + cell * .5f, cell * .5f);
            fill(GRAYSCALE_DARK);
            text(fps, trayWidth + cell * .475f, cell * .475f);
            popStyle();
        }
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
        float grayscale = isMouseOver(x, y, w, h) ? GRAYSCALE_SELECTED : GRAYSCALE_DARK;
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
            if (actionsContainsLockAware(ACTION_UNDO) || isMousePressedInsideRect(x, y, w, h)) {
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
        displayStateButton(x, y, w, h, rotation * TWO_PI, false, undoStack.size());
    }

    private void updateMenuButtonRedo(boolean hide, float x, float y, float w, float h) {
        boolean canRedo = redoStack.size() > 0;
        if (canRedo && trayVisible) {
            if (actionsContainsLockAware(ACTION_REDO) || isMousePressedInsideRect(x, y, w, h)) {
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
        displayStateButton(x, y, w, h, rotation * TWO_PI, true, redoStack.size());
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
        if (activated("", x, y, w, h) || actionsContainsLockAware(ACTION_SAVE)) {
            saveAnimationStarted = frameCount;
            saveStateToFile();
            println("✔ settings saved");
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
        float grayscale = isMouseOver(x, y, w, h) ? GRAYSCALE_SELECTED : GRAYSCALE_DARK;
        stroke(grayscale);
        strokeWeight(2);
        noFill();
        rect(x + w * .5f, y + h * .5f, w * .5f * animation, h * .5f * animation);
        rect(x + w * .5f, y + h * .5f - animation * h * .12f, w * .25f * animation, h * .25f * animation);
    }

    private void displayStateButton(float x, float y, float w, float h, float rotation,
                                    boolean direction, int stackSize) {
        textSize(textSize);
        textAlign(CENTER, CENTER);
        float grayscale = isMouseOver(x, y, w, h) ? GRAYSCALE_SELECTED : GRAYSCALE_DARK;
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
                        displayElement(el, x, y, group.elementAlpha);
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

    private void displayElement(Element el, float x, float y, float alpha) {
        boolean isSelected = isMouseOverScrollAware(0, y - cell, trayWidth, cell);
        float grayScale;
        if (isSelected) {
            el.lastSelected = frameCount;
            grayScale = GRAYSCALE_SELECTED;
        } else {
            float deselectionFadeout = easedAnimation(el.lastSelected, DESELECTION_FADEOUT_DURATION,
                    DESELECTION_FADEOUT_EASING);
            grayScale = lerp(GRAYSCALE_DARK, GRAYSCALE_SELECTED, 1 - deselectionFadeout);
        }
        pushStyle();
        fill(grayScale, alpha);
        stroke(grayScale, alpha);
        el.displayOnTray(x, y);
        popStyle();
    }

    private void updateTrayBackground() {
        if (!trayVisible) {
            return;
        }
        textSize(textSize);
        trayWidthWhenExtended = constrain(findLongestNameWidth() + cell * 2, minimumTrayWidth, MAXIMUM_TRAY_WIDTH);
        trayWidth = trayWidthWhenExtended;
        noStroke();
        fill(0, BACKGROUND_ALPHA);
        rectMode(CORNER);
        rect(0, 0, trayWidth, height);
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
        return (previousActionsContainsLockAware(ACTION_HIDE) || mouseJustReleasedHere(x, y, w, h));
    }

    // INPUT

    private boolean activated(String query, float x, float y, float w, float h) {
        return mouseJustReleasedHereScrollAware(x, y, w, h) || (overlayOwner != null && (overlayOwner.group + overlayOwner.name).equals(query));
    }

    private boolean mouseJustReleasedHereScrollAware(float x, float y, float w, float h) {
        return mouseJustReleasedHere(x, y + trayScrollOffset, w, h) && trayNotMovedInAWhile();
    }

    private boolean mouseJustReleasedHere(float x, float y, float w, float h) {
        return mouseJustReleased() && isPointInRect(mouseX, mouseY, x, y, w, h);
    }

    private boolean mouseJustReleased() {
        return pMousePressed && !mousePressed;
    }

    private boolean isMousePressedInsideRect(float x, float y, float w, float h) {
        return mousePressed && isPointInRect(mouseX, mouseY, x, y, w, h);
    }

    protected boolean mouseJustPressedOutsideTray() {
        return !pMousePressed && mousePressed && isMouseOutsideVisibleTray();
    }

    private boolean isMouseOutsideVisibleTray() {
        return !trayVisible || !isPointInRect(mouseX, mouseY, 0, 0, trayWidth, height);
    }

    private boolean isMouseOutsideTray() {
        return !isPointInRect(mouseX, mouseY, 0, 0, trayWidth, height);
    }

    @SuppressWarnings("SameParameterValue")
    private boolean isMouseOverScrollAware(float x, float y, float w, float h) {
        return isMouseOver(x, y + trayScrollOffset, w, h);
    }

    private boolean isMouseOver(float x, float y, float w, float h) {
        return frameCount > 1 && isPointInRect(mouseX, mouseY, x, y, w, h);
    }

    public void mouseWheel(MouseEvent event) {
        float direction = event.getCount();
        if (isMouseOutsideTray()) {
            if (direction < 0) {
                actions.add(ACTION_PRECISION_ZOOM_IN);
            } else if (direction > 0) {
                actions.add(ACTION_PRECISION_ZOOM_OUT);
            }
        } else {
            if (direction < 0) {
                trayScrollOffset -= 100;
            } else if (direction > 0) {
                trayScrollOffset += 100;
            }
        }
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
        if (overlayOwner != null) {
            overlayOwner.keyPressed();
        }
        if (!keyboardLockedByTextEditor) {
            if (key == 'k') {
                frameRecordingStarted = frameCount + 1;
                id = regenIdAndCaptureDir();
            }
            if (key == 'l') {
                frameRecordingStarted = frameCount - frameRecordingDuration * 2;
                runFfmpeg();
            }
            if (key == 'i') {
                captureScreenshot = true;
            }
        }
    }

    private boolean isActionLockImmune(String action) {
        return action.equals(ACTION_SAVE) || action.equals(ACTION_COPY) || action.equals(ACTION_PASTE);
    }

    private boolean previousActionsContainsLockAware(String action) {
        if (keyboardLockedByTextEditor) {
            return isActionLockImmune(action) && previousActions.contains(action);
        }
        return previousActions.contains(action);
    }

    private boolean actionsContainsLockAware(String action) {
        if (keyboardLockedByTextEditor) {
            return isActionLockImmune(action) && actions.contains(action);
        }
        return actions.contains(action);
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
        if (kk.character == 'r' || kk.character == 'R') {
            actions.add(ACTION_RESET);
        }
        if (kk.character == 'h' || kk.character == 'H') {
            actions.add(ACTION_HIDE);
        }
        if (kk.character == 't' || kk.character == 'T') {
            actions.add(ACTION_CHANGE_TYPE);
        }
        if (kk.character == 'b' || kk.character == 'B') {
            actions.add(ACTION_CHANGE_BLEND);
        }
        if (kk.character == 'f' || kk.character == 'F') {
            actions.add(ACTION_FULLSCREEN_TOGGLE);
        }
        if (kk.character == KEY_CTRL_S) {
            actions.add(ACTION_SAVE);
        }
        if (kk.character == KEY_CTRL_C) {
            actions.add(ACTION_COPY);
        }
        if (kk.character == KEY_CTRL_V) {
            actions.add(ACTION_PASTE);
        }

    }

    private boolean actionJustReleased(String action) {
        return previousActionsContainsLockAware(action) && !actionsContainsLockAware(action);
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

    private boolean elementDoesntExist(String elementName, String groupName) {
        for (Group g : groups) {
            for (Element el : g.elements) {
                if (g.name.equals(groupName) && el.name.equals(elementName)) {
                    return false;
                }
            }
        }
        return true;
    }

    private Element findElement(String elementName, String groupName) {
        for (Group g : groups) {
            for (Element el : g.elements) {
                if (g.name.equals(groupName) && el.name.equals(elementName)) {
                    return el;
                }
            }
        }
        throw new IllegalArgumentException("Element " + elementName + " was not found in group " + groupName);
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
                if (elementDoesntExist(splitState[1], splitState[0])) {
                    continue;
                }
                Element el = findElement(splitState[1], splitState[0]);
                try {
                    el.setState(state);
                } catch (Exception ex) {
                    println("Error loading state for", el.group.name, el.name);
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

    protected void multiplyPass(PGraphics pg) {
        String multiplyFrag = "shaders/filters/multiply.glsl";
        uniform(multiplyFrag).set("amt", slider("multiply", 1));
        hotFilter(multiplyFrag, pg);
    }

    protected void fbmDisplacePass(PGraphics pg) {
        group("displace");
        if (toggle("skip")) {
            return;
        }
        String shaderPath = "shaders/_2020_06/Unrelated/fbmNoiseDisplace.glsl";
        uniform(shaderPath).set("time", t * slider("time", 1));
        uniform(shaderPath).set("timeSpeed", slider("time radius", 0.2f));
        uniform(shaderPath).set("angleOffset", slider("angle offset", 1));
        uniform(shaderPath).set("angleRange", slider("angle range", 2));
        uniform(shaderPath).set("freqs", sliderXYZ("noise details", 0.5f, 3, 20));
        uniform(shaderPath).set("amps", sliderXYZ("noise speeds", 0));
        hotFilter(shaderPath, pg);
        resetGroup();
    }

    protected void blurPass(PGraphics pg) {

        group("blur");
        String blur = "shaders/filters/blur.glsl";
        uniform(blur).set("innerEdge", slider("inner edge", 0));
        uniform(blur).set("outerEdge", slider("outer edge", 1));
        uniform(blur).set("intensity", slider("intensity", 0));
        hotFilter(blur, pg);
        resetGroup();
    }

    protected void gaussBlurPass(PGraphics pg) {
        String split = "shaders/filters/gaussBlur.glsl";
        uniform(split).set("sigma", slider("sigma", 0));
        uniform(split).set("blurSize", slider("blur size", 0));
        hotFilter(split, pg);
    }

    protected void chromaticAberrationPass(PGraphics pg) {
        group("chromatic ab.");
        String shaderPath = "shaders/filters/chromaticAberration.glsl";
        uniform(shaderPath).set("rotation", slider("rotation", 0) + t * sliderInt("rotation speed"));
        uniform(shaderPath).set("innerEdge", slider("inner edge", 0));
        uniform(shaderPath).set("outerEdge", slider("outer edge", 1));
        uniform(shaderPath).set("intensity", slider("intensity", 0));
        hotFilter(shaderPath, pg);
        resetGroup();
    }

    protected void colorFilter(PGraphics toFilter, PVector multiplier) {
        String filterShader = "shaders/filters/colorFilter.glsl";
        uniform(filterShader).set("multiplier", multiplier);
        hotFilter(filterShader, toFilter);
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

    // CLASSES

    private static class Key {
        boolean justPressed;
        boolean coded;
        int character;

        Key(Integer character, boolean coded) {
            this.character = character;
            this.coded = coded;
            this.justPressed = true;
        }
    }

    protected class ShaderSnapshot {
        String fragPath;
        String vertPath;
        File fragFile;
        File vertFile;
        PShader compiledShader;
        long fragLastKnownModified, vertLastKnownModified, lastChecked;
        boolean compiledOk = false;
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
            if (compiledOk && currentTimeMillis < lastChecked + shaderRefreshRateInMillis) {
//                println("working shader did not change, not checking, standard apply");
                applyShader(compiledShader, filter, pg);
                return;
            }
            if (!compiledOk && lastModified > lastKnownUncompilable) {
//                println("file changed, trying to compile");
                tryCompileNewVersion(lastModified);
                return;
            }
            lastChecked = currentTimeMillis;
            if (lastModified > fragLastKnownModified && lastModified > lastKnownUncompilable) {
//                println("file changed, repeat try");
                tryCompileNewVersion(lastModified);
            } else if (compiledOk) {
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

        private void tryCompileNewVersion(long lastModified) {
            try {
                PShader candidate;
                if (vertFile == null) {
                    candidate = loadShader(fragPath);
                } else {
                    candidate = loadShader(fragPath, vertPath);
                }
                candidate.init();
                compiledShader = candidate;
                compiledOk = true;
                fragLastKnownModified = lastModified;
                println("✔ compiled", fragPath != null ? fragFile.getName() : "",
                        vertPath != null ? vertFile.getName() : "");
            } catch (Exception ex) {
                lastKnownUncompilable = lastModified;
                println("❌" + (fragPath != null ? " " + fragFile.getName() : ""),
                        (vertPath != null ? " " + vertFile.getName() : ""));
                println(ex.getMessage());
            }
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
            pushStyle();
            boolean isSelected = (isMouseOverScrollAware(0, y - cell, trayWidth, cell));
            float clr = isSelected ? GRAYSCALE_SELECTED : GRAYSCALE_DARK;
            fill(clr);
            stroke(clr);
            strokeWeight(2);
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
            popStyle();
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

        void keyPressed() {

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

        @SuppressWarnings("SameParameterValue")
        void onActivationWithoutOverlay(int x, float y, float w, float h) {

        }

        void displayOnTray(float x, float y) {
            displayOnTray(x, y, name);
        }

        void displayOnTray(float x, float y, String text) {
            textAlign(LEFT, BOTTOM);
            textSize(textSize);
            if (overlayVisible && this.equals(overlayOwner)) {
                underlineAnimation(underlineTrayAnimationStarted, x, y);
            }
            text(text, x, y);
        }

        float trayTextWidth() {
            return textWidth(name);
        }

        void underlineAnimation(float startFrame, float x, float y) {
            float fullWidth = textWidth(name);
            float animation = easedAnimation(startFrame, KrabApplet.UNDERLINE_TRAY_ANIMATION_DURATION,
                    UNDERLINE_TRAY_ANIMATION_EASING);
            float w = fullWidth * animation;
            float centerX = x + fullWidth * .5f;
            strokeWeight(2);
            line(centerX - w * .5f, y, centerX + w * .5f, y);
        }

        void displayCheckMarkOnTray(float x, float y, float animation, boolean fadeIn, boolean displayBox) {
            float w = previewTrayBoxWidth;
            pushMatrix();
            pushStyle();
            translate(x - previewTrayBoxMargin, previewTrayBoxOffsetY);
            noFill();
            if (displayBox) {
                rectMode(CENTER);
                pushStyle();
                strokeWeight(1);
                stroke(GRAYSCALE_DARK);
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
            popStyle();
            popMatrix();
        }

        void handleActions() {
        }

        float screenDistanceToValueDistance(float screenSpaceDelta, float precision) {
            float valueToScreenRatio = precision / width;
            return screenSpaceDelta * valueToScreenRatio;
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
            pushStyle();
            for (int i = 0; i < options.size(); i++) {
                float size = 4;
                float rectX = x + cell * .15f + i * size * 2.5f;
                if (i == valueIndex) {
                    size *= 1.8f;
                    pushStyle();
                    noFill();
                }
                strokeWeight(2);
                rectMode(CENTER);
                rect(rectX, y + cell * .1f, size, size);
                if (i == valueIndex) {
                    popStyle();
                }
            }
            popStyle();
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
            if (overlayVisible && overlayOwner != null && overlayOwner.equals(this) && actionsContainsLockAware(ACTION_RESET)) {
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

    private class TextInput extends Element {
        private final float overlayTextSize = 24;
        private String value;

        TextInput(Group currentGroup, String name, String defaultValue) {
            super(currentGroup, name);
            this.name = name;
            this.value = defaultValue;
        }

        @Override
        boolean canHaveOverlay() {
            return true;
        }

        void update() {
            keyboardLockedByTextEditor = false;
        }

        void updateOverlay() {
            keyboardLockedByTextEditor = true;
            displayOverlay();
        }

        void handleActions() {
            if (previousActionsContainsLockAware(ACTION_PASTE)) {
                pasteFromClipboardToValue();
            } else if (previousActionsContainsLockAware(ACTION_COPY)) {
                copyFromValueToClipboard();
            }
        }

        private void copyFromValueToClipboard() {
            try {
                Toolkit.getDefaultToolkit()
                        .getSystemClipboard()
                        .setContents(
                                new StringSelection(value),
                                null
                        );
            } catch (Exception ignored) {
            }
        }

        private void pasteFromClipboardToValue() {
            Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable t = c.getContents(this);
            if (t == null)
                return;
            try {
                ArrayList<DataFlavor> availableDataFlavors = new ArrayList<>(Arrays.asList(t.getTransferDataFlavors()));
                if (availableDataFlavors.contains(DataFlavor.stringFlavor)) {
                    value += (String) t.getTransferData(DataFlavor.stringFlavor);
                }
            } catch (Exception ignored) {
            }
        }

        void keyPressed() {
            // println("coded: " + (key == CODED), (int) key, keyCode);
            if (!overlayVisible || !trayVisible || key == KEY_CTRL_C || key == KEY_CTRL_V) {
                return;
            }
            if (key == BACKSPACE) {
                if (value.length() > 0) {
                    value = value.substring(0, value.length() - 1);
                }
            } else if (key == DELETE) {
                value = "";
            } else if (keyCode != SHIFT && keyCode != CONTROL && keyCode != ALT) {
                value = value + key;
            }
        }

        private void displayOverlay() {
            float overlayHeight = textHeightPlusPadding();
            pushStyle();
            noStroke();
            fill(0, BACKGROUND_ALPHA);
            rectMode(CORNER);
            rect(0, height - overlayHeight, width, overlayHeight);
            float x = width / 2f;
            float y = height - overlayHeight / 2;
            fill(GRAYSCALE_SELECTED);
            textAlign(CENTER, CENTER);
            textSize(overlayTextSize);
            String displayValue = value;
            displayValue = displayValue.replaceAll(" ", "·");
            text(displayValue, x, y);
            popStyle();
        }

        private float textHeightPlusPadding() {
            int newLineCount = 0;
            for (char c : value.toCharArray()) {
                if (c == '\n') {
                    newLineCount++;
                }
            }
            return overlayTextSize + newLineCount * overlayTextSize + cell * 2;
        }

        String getState() {
            return super.getState() + value.replace("\n", NEWLINE_PLACEHOLDER);
        }

        void setState(String state) {
            String[] split = state.split(SEPARATOR);
            if (split.length >= 3) {
                String valueToSet = split[2];
                value = valueToSet.replaceAll(NEWLINE_PLACEHOLDER, "\n");
            } else {
                value = "";
            }
        }
    }

    private abstract class Slider extends Element {
        Slider(Group parent, String name) {
            super(parent, name);
        }

        void updateOverlay() {
        }

        @SuppressWarnings("SameParameterValue")
        protected float updateFullHorizontalSlider(float x, float y, float w, float h, float value, float precision,
                                                   float horizontalRevealAnimationStarted) {
            float deltaX = updateInfiniteSlider(precision, true, true);
            float horizontalAnimation = easedAnimation(horizontalRevealAnimationStarted - SLIDER_REVEAL_START_SKIP,
                    SLIDER_REVEAL_DURATION, SLIDER_REVEAL_EASING);
            displayInfiniteSliderCenterMode(x + width * .5f, y, w, h,
                    precision, value, horizontalAnimation, true, true, false, -Float.MAX_VALUE, Float.MAX_VALUE);
            return deltaX;
        }

        @SuppressWarnings("SameParameterValue")
        protected float updateFullHeightVerticalSlider(float x, float y, float w, float h, float value, float precision,
                                                       float verticalRevealAnimationStarted) {
            float deltaY = updateInfiniteSlider(precision, false, true);
            float verticalAnimation = easedAnimation(verticalRevealAnimationStarted - SLIDER_REVEAL_START_SKIP,
                    SLIDER_REVEAL_DURATION, SLIDER_REVEAL_EASING);
            displayInfiniteSliderCenterMode(x + height * .5f, y, w, h,
                    precision, value, verticalAnimation, false, true, false, -Float.MAX_VALUE, Float.MAX_VALUE);
            return deltaY;
        }

        protected float updateInfiniteSlider(float precision, boolean horizontal, boolean reversed) {
            if (mousePressed && isMouseOutsideVisibleTray()) {
                float screenSpaceDelta = horizontal ? (pmouseX - mouseX) : (pmouseY - mouseY);
                if (reversed) {
                    screenSpaceDelta *= -1;
                }
                return screenDistanceToValueDistance(screenSpaceDelta, precision);
            }
            return 0;
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
            displayValue(precision, value, revealAnimation, floored);
            popMatrix();
            popStyle();
        }

        void displaySliderBackground(float w, float h, boolean cutout, boolean horizontal) {
            fill(0, BACKGROUND_ALPHA);
            rectMode(CENTER);
            float xOffset = 0;
            if (cutout) {
                if (!horizontal) {
                    xOffset = h;
                }
            }
            rect(xOffset, 0, w, h);
        }

        void displayHorizontalLine(float w, float revealAnimation) {
            stroke(GRAYSCALE_DARK);
            beginShape();
            w *= revealAnimation;
            for (int i = 0; i < w; i++) {
                float iNorm = norm(i, 0, w);
                float screenX = lerp(-w, w, iNorm);
                stroke(GRAYSCALE_SELECTED, darkenEdges(screenX, w));
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
            fill(GRAYSCALE_SELECTED, grayscale * revealAnimationEased);
            stroke(GRAYSCALE_SELECTED, grayscale * revealAnimationEased);
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

        void displayValue(float precision, float value, float animationEased, boolean floored) {
            fill(GRAYSCALE_DARK);
            textAlign(CENTER, CENTER);
            textSize(textSize * 1.2f);
            float textY = -cell * 2.5f;
            float textX = 0;
            String text;
            if (floored) {
                text = String.valueOf(floor(value));
            } else if (abs(value) < 2) {
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
            if (text.equals("-0")) {
                text = "0";
            }
            noStroke();
            fill(0, BACKGROUND_ALPHA);
            rectMode(CENTER);
            rect(textX, textY + textSize * .2f, textWidth(text) + 20, textSize * 1.2f + 20);
            fill(GRAYSCALE_SELECTED * animationEased);
            text(text, textX, textY);
            stroke(GRAYSCALE_SELECTED);
            line(0, -5, 0, 5);
        }

        float darkenEdges(float screenX, float w) {
            float xNorm = norm(screenX, -w, w);
            float distanceFromCenter = abs(.5f - xNorm) * 4;
            return 1 - ease(distanceFromCenter, SLIDER_EDGE_DARKEN_EASING);
        }

        void recordStateForUndo() {
            if (mouseJustPressedOutsideTray()) {
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
            fill(GRAYSCALE_DARK);
            textAlign(CENTER, CENTER);
            textSize(textSize * .75f);
            text(prettyPrecisionFormat(precision), x, y - 2);
            popStyle();
        }

        private String prettyPrecisionFormat(float precision) {
            if (precision >= 1) {
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
                println(ex.getMessage());
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
            }
        }

        void handleActions() {
            if (previousActionsContainsLockAware(ACTION_COPY)) {
                clipboardSliderFloat = getState();
            }
            if (previousActionsContainsLockAware(ACTION_PASTE)) {
                if (!clipboardSliderFloat.isEmpty()) {
                    pushCurrentStateToUndo();
                    setState(clipboardSliderFloat);
                }
            }
            if (previousActionsContainsLockAware(ACTION_PRECISION_ZOOM_OUT) &&
                    ((!floored && precision < FLOAT_PRECISION_MAXIMUM) || (floored && precision < INT_PRECISION_MAXIMUM))) {
                precision *= 10f;
                pushCurrentStateToUndo();
            }
            if (previousActionsContainsLockAware(ACTION_PRECISION_ZOOM_IN) &&
                    ((!floored && precision > FLOAT_PRECISION_MINIMUM) || (floored && precision > INT_PRECISION_MINIMUM))) {
                precision *= .1f;
                pushCurrentStateToUndo();
            }
            if (overlayVisible && overlayOwner.equals(this) && actionsContainsLockAware(ACTION_RESET)) {
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
            float valueDelta = updateInfiniteSlider(precision, true, false);
            recordStateForUndo();
            value += valueDelta;
            lastValueDelta = valueDelta;
            if (floored && valueDelta == 0) {
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
                    horizontalRevealAnimationStarted);
            deltaY = updateFullHeightVerticalSlider(0, width - cell, height, sliderHeight, value.y, precision,
                    verticalRevealAnimationStarted);
        }

        void handleActions() {
            if (previousActionsContainsLockAware(ACTION_COPY)) {
                clipboardSliderXYZ = getState();
            }
            if (previousActionsContainsLockAware(ACTION_PASTE)) {
                if (!clipboardSliderXYZ.isEmpty()) {
                    pushCurrentStateToUndo();
                    setState(clipboardSliderXYZ);
                }
            }
            if (previousActionsContainsLockAware(ACTION_PRECISION_ZOOM_IN) && precision > FLOAT_PRECISION_MINIMUM) {
                precision *= .1f;
                pushCurrentStateToUndo();
            }
            if (previousActionsContainsLockAware(ACTION_PRECISION_ZOOM_OUT) && precision < FLOAT_PRECISION_MAXIMUM) {
                precision *= 10f;
                pushCurrentStateToUndo();
            }
            if (overlayVisible && overlayOwner.equals(this) && actionsContainsLockAware(ACTION_RESET)) {
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
            deltaZ = updateInfiniteSlider(precision, false, true);
            lockOtherSlidersOnMouseOver();
            value.x += deltaX;
            value.y += deltaY;
            if (isMouseOverZSlider()) {
                value.z += deltaZ;
            }
            float zAnimation = easedAnimation(zRevealAnimationStarted, SLIDER_REVEAL_DURATION, SLIDER_REVEAL_EASING);
            displayInfiniteSliderCenterMode(height - height * .2f, width - sliderHeight * 2, height / 3f,
                    sliderHeight * .8f, precision, value.z, zAnimation, false, false,
                    false, -Float.MAX_VALUE, Float.MAX_VALUE);
            super.updateXYSliders();
            displayPrecision(precision);
        }

        private boolean isMouseOverZSlider() {
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
        private final HSBA hsba;
        private final float defaultHue;
        private final float defaultSat;
        private final float defaultBr;
        private final float defaultAlpha;
        public float gradientPosition;
        public boolean gradientPositionLocked;
        public boolean hueLocked = false;
        private float alphaPrecision = 1;
        private float pickerRevealStarted = -PICKER_REVEAL_DURATION;
        private boolean brightnessLocked, saturationLocked;
        private boolean satChanged, brChanged;

        /**
         * Standard constructor for a standalone color picker.
         */
        ColorPicker(Group currentGroup, String name, float hue, float sat, float br, float alpha) {
            super(currentGroup, name);
            this.hsba = new HSBA(hue, sat, br, alpha);
            this.defaultHue = hue;
            this.defaultSat = sat;
            this.defaultBr = br;
            this.defaultAlpha = alpha;
        }

        /**
         * Used as a part of GradientEditor
         * pickers created with this constructor are not members of a group as a standalone element
         * and will never be searched for using the group and name combination.
         *
         * @param gradientPosition position of this color in the gradient in the range [0,1]
         */
        public ColorPicker(float gradientPosition, boolean locked, float hue, float sat, float br, float alpha) {
            super(getCurrentGroup(), "anonymous gradient picker");
            this.gradientPosition = gradientPosition;
            this.gradientPositionLocked = locked;
            this.hsba = new HSBA(hue, sat, br, alpha);
            this.defaultHue = hue;
            this.defaultSat = sat;
            this.defaultBr = br;
            this.defaultAlpha = alpha;
        }

        void handleActions() {
            if (previousActionsContainsLockAware(ACTION_COPY)) {
                clipboardPicker = getState();
            }
            if (previousActionsContainsLockAware(ACTION_PASTE)) {
                if (!clipboardPicker.isEmpty()) {
                    pushCurrentStateToUndo();
                    setState(clipboardPicker);
                }
            }
            if (overlayVisible && overlayOwner != null && overlayOwner.equals(this) && actionsContainsLockAware(ACTION_RESET)) {
                pushCurrentStateToUndo();
                reset();
            }
            if (alphaPrecision > ALPHA_PRECISION_MINIMUM && previousActionsContainsLockAware(ACTION_PRECISION_ZOOM_IN)) {
                alphaPrecision *= .1f;
                pushCurrentStateToUndo();
            }
            if (alphaPrecision < ALPHA_PRECISION_MAXIMUM && previousActionsContainsLockAware(ACTION_PRECISION_ZOOM_OUT)) {
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
            stroke(GRAYSCALE_DARK);
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

        void updateOverlay() {
            updateOverlay(false);
        }

        void updateOverlay(boolean hideValuePreview) {
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
            float tinySliderHeight = cell * 8;
            float tinySliderTopY =
                    height - sliderHeight * .5f - cell * tinySliderMarginCellFraction - tinySliderHeight * revealAnimation;
            float lastSat = hsba.sat;
            hsba.sat = updateTinySlider(x, tinySliderTopY, tinySliderWidth, tinySliderHeight, brightnessLocked,
                    SATURATION);
            if (hsba.sat != lastSat && !saturationLocked) {
                brightnessLocked = true;
            }
            if (saturationLocked) {
                hsba.sat = lastSat;
            }
            displayTinySlider(x, tinySliderTopY, tinySliderWidth, tinySliderHeight, hsba.sat, SATURATION,
                    brightnessLocked);

            x += tinySliderWidth * 1.2f;
            float lastBr = hsba.br;
            hsba.br = updateTinySlider(x, tinySliderTopY, tinySliderWidth, tinySliderHeight, saturationLocked,
                    BRIGHTNESS);
            if (hsba.br != lastBr && !brightnessLocked) {
                saturationLocked = true;
            }
            if (brightnessLocked) {
                hsba.br = lastBr;
            }
            displayTinySlider(x, tinySliderTopY, tinySliderWidth, tinySliderHeight, hsba.br, BRIGHTNESS,
                    saturationLocked);

            displayInfiniteSliderCenterMode(height - height / 4f, width - sliderHeight * .5f, height / 2f, sliderHeight,
                    alphaPrecision, hsba.alpha, revealAnimation, false, false, false, 0, 1);
            fill(GRAYSCALE_DARK);
            textAlign(CENTER, CENTER);
            textSize(textSize);
            text("alpha", width - sliderHeight * .5f, 15);
            float alphaDelta = updateInfiniteSlider(alphaPrecision, false, false);
            boolean isMouseInTopHalf = isMouseOver(width * .5f, 0, width * .5f, height / 2f);
            if (!satChanged && !brChanged && isMouseInTopHalf) {
                hsba.alpha += alphaDelta;
            }

            displayHueSlider(sliderHeight, revealAnimation);
            float huePrecision = .5f;
            float hueDelta = updateInfiniteSlider(huePrecision, true, false);
            if (!satChanged && !brChanged && !hueLocked) {
                hsba.hue += hueDelta;
            }
            satChanged = false;
            brChanged = false;
            hsba.enforceConstraints();
            if (!hideValuePreview) {
                displayPreview(sliderHeight);
            }
            popStyle();
        }

        private void displayHueSlider(float h, float revealAnimation) {
            displayHueStripCornerMode(height + cell - h * revealAnimation, h * .5f, revealAnimation);
            displayHueStripCornerMode(height + cell - h * .5f * revealAnimation, h * .5f, revealAnimation);
        }

        private void displayHueStripCornerMode(float y, float h, float revealAnimation) {
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

        private void displayPreview(float hueSliderHeight) {
            float x = width * .5f;
            float y = height - hueSliderHeight - cell;
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
                    GRAYSCALE_SELECTED : GRAYSCALE_DARK);
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

        public HSBA copy() {
            return new HSBA(hue, sat, br, alpha);
        }
    }

    // TODO make getColorAt() and make it more efficient than get()
    class GradientEditor extends Element {
        private final GradientType defaultGradientType;
        private final BlendType defaultBlendType;
        private BlendType blendType;
        private PGraphics pg;
        private final PGraphics preview;
        private final ArrayList<ColorPicker> pickers = new ArrayList<>();
        private final ArrayList<ColorPicker> pickersToRemove = new ArrayList<>();
        private final int defaultColorCount;
        float previewCenterX, previewCenterY;
        float previewWidth = cell * 8;
        float previewHeight = cell * 4;
        private GradientType gradientType;
        private ColorPicker selected = null;
        private ColorPicker held = null;
        private boolean blockDeselectionUntilMouseRelease = false;
        private int blendTypeChangedFrame, gradientTypeChangedFrame;

        GradientEditor(Group group, String name, int defaultColorCount, int w, int h, GradientType defaultGradientType) {
            super(group, name);
            this.defaultColorCount = defaultColorCount;
            this.defaultGradientType = defaultGradientType;
            this.defaultBlendType = BlendType.RGB_LERP;
            this.blendType = defaultBlendType;
            this.gradientType = defaultGradientType;
            updatePGraphics(w, h);
            updatePreviewPos();
            initPickers();
            preview = createGraphics(floor(previewWidth), floor(previewHeight), P2D);
            drawGradientToTexture(pg, gradientType, blendType);
        }

        private void updatePGraphics(int w, int h) {
            if(pg == null || pg.width != w || pg.height != h) {
                pg = createGraphics(w, h, P2D);
            }
        }

        private void updatePreviewPos() {
            previewCenterX = width / 2f;
            previewCenterY = height - cell * 4;
        }

        private void initPickers() {
            pickers.clear();
            for (int i = 0; i < defaultColorCount; i++) {
                float iNorm = norm(i, 0, defaultColorCount - 1);
                boolean locked = i == 0 || i == defaultColorCount - 1;
                pickers.add(new ColorPicker(iNorm, locked, 0, 0, iNorm, 1));
            }
        }

        boolean canHaveOverlay() {
            return true;
        }

        void update() {
            /* in the first few frames anything drawn to PGraphics somehow don't really persist,
            / so we need to continually update the texture even when the overlay is hidden
            / the fps costs of this are tiny, the shader that draws it is really fast  */
            drawGradientToTexture(pg, gradientType, blendType);
        }

        void updateOverlay() {
            super.updateOverlay();
            updatePreviewPos();
            updateColorPickers();
            drawPreview();
            float y = previewCenterY + previewHeight / 2f + 15;
            textSize(20);
            textAlign(LEFT, CENTER);
            drawTextIndicator("T: " + gradientType.getSymbol(), previewCenterX - previewWidth * .25f, y, gradientTypeChangedFrame);
            drawTextIndicator("B: " + blendType.toString(), previewCenterX, y, blendTypeChangedFrame);
        }

        private void drawTextIndicator(String text, float x, float y, int lastChanged) {
            pushStyle();
            fill(0, 1);
            text(text, x + shadowOffset, y + shadowOffset);
            float changeAnimation = clampNorm(frameCount, lastChanged, lastChanged + 60);
            fill(lerp(GRAYSCALE_DARK, GRAYSCALE_SELECTED, 1 - changeAnimation), 1);
            text(text, x, y);
            popStyle();
        }

        void reset() {
            gradientType = defaultGradientType;
            blendType = defaultBlendType;
            initPickers();
        }

        void handleActions() {
            if (actionsContainsLockAware(ACTION_RESET)) {
                pushCurrentStateToUndo();
                reset();
            }
            if (previousActionsContainsLockAware(ACTION_COPY)) {
                clipboardGradient = getState();
            }
            if (previousActionsContainsLockAware(ACTION_PASTE) && selected == null && !clipboardGradient.isEmpty()) {
                pushCurrentStateToUndo();
                setState(clipboardGradient);
            }
            if (previousActions.contains(ACTION_CHANGE_TYPE)) {
                int typeIndex = gradientType.getIndex();
                int nextIndex = (typeIndex + 1) % GradientType.values().length;
                gradientType = GradientType.parseIndex(nextIndex);
                gradientTypeChangedFrame = frameCount;
            }
            if (previousActions.contains(ACTION_CHANGE_BLEND)) {
                int typeIndex = blendType.getIndex();
                int nextIndex = (typeIndex + 1) % BlendType.values().length;
                blendType = BlendType.parseIndex(nextIndex);
                blendTypeChangedFrame = frameCount;
            }
        }

        String getState() {
            StringBuilder state = new StringBuilder(super.getState());
            state.append(gradientType.toString()).append(SEPARATOR);
            state.append(blendType.toString()).append(SEPARATOR);
            for (ColorPicker p : pickers) {
                HSBA hsba = p.getHSBA();
                state.append(p.gradientPosition).append(SEPARATOR)
                        .append(p.gradientPositionLocked).append(SEPARATOR)
                        .append(hsba.hue).append(SEPARATOR)
                        .append(hsba.sat).append(SEPARATOR)
                        .append(hsba.br).append(SEPARATOR)
                        .append(hsba.alpha).append(SEPARATOR);
            }
            return state.toString();
        }

        void setState(String newState) {
            String[] split = newState.split(SEPARATOR);
            gradientType = GradientType.parseType(split[2]);
            blendType = BlendType.parseType(split[3]);
            pickers.clear();
            int i = 4;
            while (i < split.length) {
                float gradientPosition = Float.parseFloat(split[i++]);
                boolean locked = Boolean.parseBoolean(split[i++]);
                float hue = Float.parseFloat(split[i++]);
                float sat = Float.parseFloat(split[i++]);
                float br = Float.parseFloat(split[i++]);
                float alpha = Float.parseFloat(split[i++]);
                pickers.add(new ColorPicker(gradientPosition, locked, hue, sat, br, alpha));
            }
            drawGradientToTexture(pg, gradientType, blendType);
        }

        private void drawPreview() {
            preview.beginDraw();
            drawGradientToTexture(preview, GradientType.HORIZONTAL, blendType);
            preview.endDraw();
            pushStyle();
            imageMode(CENTER);
            image(preview, previewCenterX, previewCenterY, previewWidth, previewHeight);
            blendTypePreview();
            popStyle();
        }

        private void blendTypePreview() {

        }

        private void updateColorPickers() {
            pushStyle();
            colorMode(HSB, 1, 1, 1, 1);
            boolean pickerDeleted = false;
            float pickerHandleRadius = 40;
            sortPickersByGradientPosition();
            for (ColorPicker picker : pickers) {
                float x = map(picker.gradientPosition, 0, 1,
                        previewCenterX - previewWidth * .5f + 3,
                        previewCenterX + previewWidth * .5f - 3);
                float y = previewCenterY;
                float h = previewHeight * 1.5f;
                float lineTopY = y - h / 2;
                float pickerHandleY = lineTopY - pickerHandleRadius / 2;
                HSBA pickerColor = picker.getHSBA();
                boolean isMouseInsideHandle = isPointInCircle(mouseX, mouseY, x, lineTopY - pickerHandleRadius / 2, pickerHandleRadius / 2);
                if (isMouseInsideHandle) {
                    if (mousePressed && !picker.gradientPositionLocked) {
                        if (mouseJustPressedOutsideTray() && isPickerSelected(picker)) {
                            pushCurrentStateToUndo();
                        }
                        if (held == null) {
                            held = picker;
                        }
                        blockDeselectionUntilMouseRelease = true;
                    }
                    if (mouseJustPressedOutsideTray() && !isPickerSelected(picker)) {
                        selected = picker;
                        selected.onOverlayShown();
                        blockDeselectionUntilMouseRelease = true;
                    } else if (mouseJustReleased() && isPickerSelected(picker) && !blockDeselectionUntilMouseRelease) {
                        selected = null;
                    }
                    if (mouseButton == RIGHT && mouseJustReleased() && !pickerDeleted && !picker.gradientPositionLocked) {
                        pickersToRemove.add(picker);
                        if (isPickerSelected(picker)) {
                            selected = null;
                        }
                        if (pickerHeld(picker)) {
                            held = null;
                        }
                        pickerDeleted = true;
                    }
                }
                if (picker.equals(held) && isMousePressedInsideRect(width / 2f - previewWidth / 2, 0, previewWidth, height)) {
                    float delta = map(mouseX - pmouseX, 0, previewWidth, 0, 1);
                    picker.gradientPosition += delta;
                    picker.gradientPosition = constrain(picker.gradientPosition, 0, 1);
                }
                if (isPickerSelected(picker)) {
                    stroke(picker.getHSBA().clr());
                    strokeWeight(6);
                    line(x, lineTopY, x, y - previewHeight / 2f);
                    stroke(1);
                } else {
                    stroke(GRAYSCALE_DARK);
                }
                strokeWeight(3);
                fill(pickerColor.clr());
                if (picker.gradientPositionLocked) {
                    rectMode(CENTER);
                    rect(x, pickerHandleY, pickerHandleRadius, pickerHandleRadius);
                } else {
                    ellipse(x, pickerHandleY, pickerHandleRadius, pickerHandleRadius);
                }
            }

            if (!mousePressed) {
                held = null;
            }
            if (mouseJustReleased()) {
                blockDeselectionUntilMouseRelease = false;
            }
            popStyle();
            if (selected != null) {
                selected.hueLocked = held != null;
                selected.update();
                selected.handleActions();
                selected.updateOverlay(true);
            }
            pickers.removeAll(pickersToRemove);
            boolean mouseJustReleasedInsideGradientEditor = mouseJustReleasedHere(previewCenterX - previewWidth / 2,
                    previewCenterY - previewHeight, previewWidth, previewHeight * 2);
            if (mouseJustReleasedInsideGradientEditor && mouseButton == RIGHT && !pickerDeleted) {
                createNewPicker();
            }
        }

        private void createNewPicker() {
            float pickerPosition = clampNorm(mouseX,
                    previewCenterX - previewWidth / 2, previewCenterX + previewWidth / 2);
            int colorAtPos = preview.get(floor(pickerPosition * preview.width), floor(previewHeight / 2));
            ColorPicker newPicker = new ColorPicker(pickerPosition, false,
                    hue(colorAtPos), saturation(colorAtPos), brightness(colorAtPos), alpha(colorAtPos));
            pickers.add(newPicker);
            selected = newPicker;
        }

        private boolean pickerHeld(ColorPicker picker) {
            return held != null && held.equals(picker);
        }

        private boolean isPickerSelected(ColorPicker picker) {
            return selected != null && selected.equals(picker);
        }

        private void drawGradientToTexture(PGraphics pg, GradientType gradType, BlendType blendType) {
            sortPickersByGradientPosition();
            pg.beginDraw();
            pg.clear();
            String gradientFragShader = "shaders/applet/gradient.glsl";
            uniform(gradientFragShader).set("gradientType", gradType.getIndex());
            uniform(gradientFragShader).set("blendType", blendType.getIndex());
            uniform(gradientFragShader).set("colorCount", pickers.size());
            uniform(gradientFragShader).set("colorPositions", getColorPositions(), 1);
            uniform(gradientFragShader).set("colorValues", getColorValues(), 4);
            hotFilter(gradientFragShader, pg);
            pg.endDraw();
        }

        private float[] getColorPositions() {
            float[] colorPositions = new float[pickers.size()];
            int i = 0;
            for (ColorPicker p : pickers) {
                colorPositions[i++] = p.gradientPosition;
            }
            return colorPositions;
        }

        private float[] getColorValues() {
            float[] colorValues = new float[pickers.size() * 4];
            int i = 0;
            for (ColorPicker p : pickers) {
                HSBA clr = p.getHSBA();
                colorValues[i++] = clr.hue();
                colorValues[i++] = clr.sat();
                colorValues[i++] = clr.br();
                colorValues[i++] = clr.alpha();
            }
            return colorValues;
        }

        void sortPickersByGradientPosition() {
            pickers.sort((picker2, picker1) -> {
                if (picker1.gradientPosition == picker2.gradientPosition) {
                    return 0;
                }
                return picker1.gradientPosition > picker2.gradientPosition ? -1 : 1;
            });
        }

        PGraphics getTexture(int w, int h) {
            updatePGraphics(w, h);
            return pg;
        }
    }

    protected enum BlendType {
        RGB_LERP("rgb lerp", 0),
        SAT_LERP("sat lerp", 1),
        HSV_LERP("hsv lerp", 2),
        SMOOTHSTEP("smoothstep", 3);

        String name;
        int index;

        BlendType(String name, int index) {
            this.name = name;
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public String toString() {
            return name;
        }

        public static BlendType parseIndex(int query) {
            for (BlendType type : BlendType.values()) {
                if (type.index == query) {
                    return type;
                }
            }
            return RGB_LERP;
        }

        public static BlendType parseType(String query) {
            for (BlendType type : BlendType.values()) {
                if (type.name.equals(query)) {
                    return type;
                }
            }
            return RGB_LERP;
        }
    }

    protected enum GradientType {
        VERTICAL(0, "vertical", '↑'),
        HORIZONTAL(1, "horizontal", '→'),
        CIRCULAR(2, "circular", '♂');

        public int index;
        public char symbol;
        public String value;

        GradientType(int index, String value, char symbol) {
            this.value = value;
            this.index = index;
            this.symbol = symbol;
        }

        public static GradientType parseIndex(int query) {
            for (GradientType type : GradientType.values()) {
                if (type.index == query) {
                    return type;
                }
            }
            return GradientType.values()[0];
        }

        public static GradientType parseType(String query) {
            for (GradientType type : GradientType.values()) {
                if (type.toString().equals(query)) {
                    return type;
                }
            }
            return GradientType.values()[0];
        }

        public int getIndex() {
            return index;
        }

        public String toString() {
            return value;
        }

        char getSymbol() {
            return symbol;
        }
    }

}
