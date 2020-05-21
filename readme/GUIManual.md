
#GUI Manual
I built this graphical interface on top of Processing, because it allows for real-time control of the sketches, which results in faster and more comfortable iteration.

## Features
<img src="https://github.com/KrabCode/Sketches/blob/master/readme/00_menu.jpg?raw=true" alt="Menu">

- The arrow on the left hides and shows the tray
    - hotkey H
- The undo and redo arrows allow you to go back and forward in changes to the gui state.
    - hotkeys CTRL+Z and CTRL+Y
    - The buttons are crossed-out when they can go no further
- The save button on the right saves the current state to a file inside the data/gui folder
    - hotkey CTRL+S
    - The sketch attempts to load the most recent state the first time gui() is called.
    - If you don't register a control element by the first time gui() is called, its previous settings will not be loaded.
    - If you saved some values that break your sketch you can restore everything to default by deleting the file from your data/gui folder 

## Control elements

### Button
A button is true for one frame when pressed.
```java
if (button("hello world")) {
    println("Hello, world!");
}
```
<img src="https://github.com/KrabCode/Sketches/blob/master/readme/01_button.jpg?raw=true" width="600" alt="Button">
---

### Toggle
A toggle holds its boolean value and changes it when pressed.
- It is false by default, which can be changed with a second optional parameter.

```java
if (toggle("no fill")) {
    noFill();
}
```
<img src="https://github.com/KrabCode/Sketches/blob/master/readme/02_toggle.jpg?raw=true" width="600" alt="Toggle">
---

### Options

A list of strings that returns the currently selected string and changes the selection when pressed.
- Requires at least two strings.
```java
String projection = options("perspective", "orthographic");
if (projection.equals("perspective")) {
    perspective();
} else if (projection.equals("orthographic")) {
    ortho();
}
```
<img src="https://github.com/KrabCode/Sketches/blob/master/readme/03_options.jpg?raw=true" width="600" alt="Options">
---

### Slider
An infinite slider with variable precision. 
- Dragging your mouse horizontally changes the value.
- Mouse wheel scrolling changes the precision.
- The default value can be specified as an optional parameter.

```java
strokeWeight(slider("stroke weight"));
```
<img src="https://github.com/KrabCode/Sketches/blob/master/readme/04_slider.jpg?raw=true" width="600" alt="Slider">
 ---
 
 ### Slider XYZ
 
 Three infinite sliders with shared variable precision. 
 - Returns the current PVector value.
 - Use PVector.copy() if you want to modify the PVector without affecting the slider.
 - The default value is 0, and it can be set with optional parameters.
 - There is a sliderXY variant without the z slider.
```java
PVector translate = sliderXYZ("translate");
translate(translate.x, translate.y, translate.z);
```
 <img src="https://github.com/KrabCode/Sketches/blob/master/readme/05_sliderXYZ.jpg?raw=true" width="600" alt="SliderXYZ">
 ---
 
 ### Color picker
 
 Four sliders controlling the hue, saturation, brightness and alpha of a color.
 - Use picker("stroke").clr() to get the Processing color (integer) value that is independent of the current colorMode.
 - Use the HSBA class if you want to change the hue, saturation, brightness and alpha after querying the picker. 
 - HSBA returns its values in ranges of 0-1, so you'll probably want to use colorMode(HSB,1,1,1,1).
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
---