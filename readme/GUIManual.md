# GUI Manual

This manual explains how to use the graphical user interface I built on top of Processing for faster and more comfortable iteration while making stuff with Processing.

Watch the video to see it in action!

[![Watch the video](https://github.com/KrabCode/Sketches/blob/master/readme/showcase_preview.jpg?raw=true)](https://youtu.be/nYxjyk9pOfU)


## Features

- Color pickers
- Sliders (1D, 2D, 3D)
- Text input
- Toggles
- Buttons
- Radio buttons (called 'options' here)
- Groups of control elements that can be collapsed
- Save GUI state to disk and automatically load it on startup
- Undo / redo any gui changes
- Copy / paste values between similar gui elements 
- Easy to use - no need to register the control elements in `setup()` in order to then query them in `draw()`. Just query them in `draw()` (even inside loops) and it will lazily initialize a gui element behind the scenes.

## Menu
<img src="https://github.com/KrabCode/Sketches/blob/master/readme/00_menu.jpg?raw=true" alt="Menu">

- The arrow on the left hides and shows the tray.
    - hotkey H
- The undo and redo arrows allow you to go back and forward in changes to the gui state.
    - hotkeys CTRL+Z and CTRL+Y
    - The buttons are crossed-out when they can go no further.
- The diskette button on the right saves the current state to a file inside the data/gui folder.
    - hotkey CTRL+S
    - The sketch attempts to load the most recent state the first time `gui()` is called.
    - If you saved some values that break your sketch you can restore everything to default by deleting the file from your data/gui folder.
    - The gui state is saved in a custom format that uses the `§` character as a separator, so you cannot use this symbol in any element or group names.
    - If you don't register a control element by the first time `gui()` is called, its previous settings will not be loaded.
        - Beware of loops that are controlled by a slider and whose default number of iterations is 0, setting it to 1 allows any saved values for the elements inside to be loaded.
    

# Control elements
All control elements require a unique name as the first parameter. 
- No other element in the same group can have the same name - the group-name combination must be unique.

## Button
A button is true for one frame when pressed.
```java
if (button("hello world")) {
    println("Hello, world!");
}
```
<img src="https://github.com/KrabCode/Sketches/blob/master/readme/01_button.jpg?raw=true" width="600" alt="Button">

## Toggle
A toggle holds its boolean value and changes it when pressed.
- It is false by default, which can be changed with a second optional parameter.

```java
if (toggle("no fill")) {
    noFill();
}
```
<img src="https://github.com/KrabCode/Sketches/blob/master/readme/02_toggle.jpg?raw=true" width="600" alt="Toggle">

## Options

A list of strings that returns the currently selected string and changes the selection when pressed.
- Requires at least two strings.
- This element uses the first option as its unique name and it shows current selection in the tray instead of the name.
```java
String projection = options("perspective", "orthographic");
if (projection.equals("perspective")) {
    perspective();
} else if (projection.equals("orthographic")) {
    ortho();
}
```
<img src="https://github.com/KrabCode/Sketches/blob/master/readme/03_options.jpg?raw=true" width="600" alt="Options">

## Slider
An infinite slider with variable precision. 
- Dragging your mouse horizontally changes the value.
- Mouse wheel scrolling changes the precision.
- The default value can be specified as an optional parameter.
- There is a sliderInt variant which rounds your floats to the nearest integer and returns int.

```java
float weight = slider("stroke weight");
strokeWeight(weight);
```
<img src="https://github.com/KrabCode/Sketches/blob/master/readme/04_slider.jpg?raw=true" width="600" alt="Slider">
  
 ## Slider XYZ
 
 Three infinite sliders with shared variable precision. 
 - Returns the current PVector value.
 - Your changes to the returned PVector will also change the slider value.
 - Use `result.copy()` if you want to modify the PVector without affecting the slider.
 - The default value is 0, and it can be set with optional parameters.
 - There is a 2D sliderXY variant without the z slider.
```java
PVector translate = sliderXYZ("translate");
translate(translate.x, translate.y, translate.z);
```
 <img src="https://github.com/KrabCode/Sketches/blob/master/readme/05_sliderXYZ.jpg?raw=true" width="600" alt="SliderXYZ">
 
 ## Color picker
 
 Four sliders controlling the hue, saturation, brightness and alpha of a color.
 - Use `picker("stroke").clr()` to get the Processing color (integer) value that is independent of the current colorMode.
 - Use the HSBA class if you want to change the hue, saturation, brightness and alpha after querying the picker. 
 - HSBA returns its values in ranges of 0-1, so you'll probably want to use `colorMode(HSB,1,1,1,1)`.
 - The hue slider is infinite with a constant precision of 1 hue cycle per sketch width.
 - The alpha slider is constrained to the range 0-1 and its precision can be changed with the mouse wheel.
```java
stroke(picker("stroke").clr());
```
```java
// this does the same thing and it allows you to change the values after asking the picker
colorMode(HSB,1,1,1,1);
HSBA myColor = picker("stroke");
float hue = myColor.hue();
float sat = myColor.sat();
float br = myColor.br();
float a = myColor.alpha();
stroke(hue, sat, br, a);
```
<img src="https://github.com/KrabCode/Sketches/blob/master/readme/06_picker.jpg?raw=true" width="600" alt="Color picker">

## Gradient picker

Blend between multiple color pickers to create gradients.
- returns a PGraphics with the gradient drawn to it
- left click pickers to select them and drag them around
- right click to delete pickers or create new ones
- press 'B' to change the blend mode
- press 'T' to change the direction of the gradient

```java
image(gradient("background"), 0, 0);
```
<img src="https://github.com/KrabCode/Sketches/blob/master/readme/10_gradient.jpg?raw=true" width="600" alt="Color picker">

## Text input

A simple text editor.
- Disables most hotkeys when being edited (except for CTRL+C, CTRL+V, CTRL+S).
- DELETE deletes the whole string.
- Can copy the whole value to clipboard and paste from the clipboard to append text to the value.
- The cursor is always at the end of the string.
- Spaces are displayed with dots in the editor for more visual clarity.

```java
String value = textInput("main text"); 
text(value, 0, 0);
```

<img src="https://github.com/KrabCode/Sketches/blob/master/readme/07_textInput.jpg?raw=true" width="600" alt="Text input">

## Groups

Groups are collections of elements that can be collapsed for visual clarity. 
- Any element that is queried is added to the current group.
- Set the current group by calling `group("groupName")`.
    - Group names must be unique within one sketch to allow referencing the group with a string.
- Calling `group()` manually is optional, you can rely on the default SketchName group to hold all of your elements.
- Each time `gui()` ends it resets the current group to the default SketchName group.
- See [GuiExample](https://github.com/KrabCode/Sketches/blob/master/src/readme/GuiExample.java).

 <img src="https://github.com/KrabCode/Sketches/blob/master/readme/99_groups.jpg?raw=true" width="200" alt="Groups">
 