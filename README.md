# Sketches
This project contains my newest Processing sketches built using my custom GUI built on top of Processing.

The finished animations can be found on my [Instagram](https://www.instagram.com/krabcode/) and [Reddit](https://www.reddit.com/user/Simplyfire).

## How to run it
- Clone the project
- Open it in your favorite Java IDE, I recommend IntelliJ IDEA
- Download and set up Java 1.8
- Download and include the [Processing](https://processing.org/download/) library version 3.5.3
- Mark the src folder as source root
- Open any class from the src folder that contains a main method and run it like a standalone java program (CTRL+SHIFT+F10 in IDEA)

## GUI

All of my sketches use a GUI I built on top of Processing along with a number of other utility functions. It has the following control elements:

### Button
A button is true for one frame when pressed.
```java
if (button("hello world")) {
    println("Hello, world!");
}
```
<img src="https://github.com/KrabCode/Sketches/blob/6de7fe44399360e101e28813ee166d17f89c3d5c/readme/01_button.jpg?raw=true" width="600" alt="Hello, world!">

