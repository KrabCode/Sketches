#GUI Manual

## Features

- a
- b
- c

## Control elements

### Button
A button is true for one frame when pressed.
```java
if (button("hello world")) {
    println("Hello, world!");
}
```
<img src="https://github.com/KrabCode/Sketches/blob/6de7fe44399360e101e28813ee166d17f89c3d5c/readme/01_button.jpg?raw=true" width="600" alt="Button">

### Toggle
A toggle holds its boolean value and changes it when pressed.
- It is false by default, which can be changed with a second optional parameter.

```java
if (toggle("no fill")) {
    noFill();
}
```
<img src="https://github.com/KrabCode/Sketches/blob/6de7fe44399360e101e28813ee166d17f89c3d5c/readme/02_toggle.jpg?raw=true" width="600" alt="Toggle">

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
<img src="https://github.com/KrabCode/Sketches/blob/6de7fe44399360e101e28813ee166d17f89c3d5c/readme/03_options.jpg?raw=true" width="600" alt="Options">

### Slider
An infinite slider with variable precision. 
- Dragging your mouse horizontally changes the value.
- Mouse wheel scrolling changes the precision.
- The default value can be specified as an optional parameter.

```java
strokeWeight(slider("stroke weight"));
```
<img src="https://github.com/KrabCode/Sketches/blob/6de7fe44399360e101e28813ee166d17f89c3d5c/readme/04_slider.jpg?raw=true" width="600" alt="Slider">
 
 
 ### Slider XYZ
 
 Three infinite sliders with shared variable precision. 
 - Returns the current PVector value.
 - Use PVector.copy() if you want to modify the PVector without affecting the slider.
 - The default value is 0, and it can be set with optional parameters.
 - Has a XY variant where the z slider is not used.
```java
PVector translate = sliderXYZ("translate");
translate(translate.x, translate.y, translate.z);
```
 <img src="https://github.com/KrabCode/Sketches/blob/6de7fe44399360e101e28813ee166d17f89c3d5c/readme/05_sliderXYZ.jpg?raw=true" width="600" alt="SliderXYZ">
 
 ### Color picker
 
 Four sliders controlling the hue, saturation, brightness and alpha of a color.
 - Use picker("stroke").clr() to get the Processing color (integer) value that is independent of the current colorMode.
 - Use the HSBA class if you want to change the hue, saturation, brightness and alpha after querying the picker. 
 - HSBA gives you values in a range of 0-1, so you'll probably want to use colorMode(HSB,1,1,1,1).
 - The hue slider is infinite with a constant precision of 1 hue cycle per sketch width.
```java
stroke(picker("stroke").clr());
```
```java
// this does the same thing but it allows you to change the values after asking the picker
colorMode(HSB,1,1,1,1);
HSBA myColor = picker("stroke");
float hue = myColor.hue();
float sat = myColor.sat();
float br = myColor.br();
float a = myColor.alpha();
stroke(hue, sat, br, a);
```
<img src="https://github.com/KrabCode/Sketches/blob/6de7fe44399360e101e28813ee166d17f89c3d5c/readme/06_picker.jpg?raw=true" width="600" alt="Color picker">
