
# Sketches
This project contains some of my Processing sketches.

The finished animations can be found on my [Instagram](https://www.instagram.com/krabcode/) and [Reddit](https://www.reddit.com/user/Simplyfire/posts).

## How to run it
- Clone the project
- Open it in your favorite Java IDE, I recommend IntelliJ IDEA
- Download and set up Java 1.8 as the SDK
- Set the project language level to 8
- Unzip libs.zip and add ALL the libraries inside to the project in Project Structure
- Mark the src folder as source root
- Open any Java class from the src folder that contains a main method and run it as a standalone Java program (CTRL+SHIFT+F10 in IDEA)

# KrabApplet
All of my sketches extend the [KrabApplet](https://github.com/KrabCode/Sketches/blob/master/src/applet/KrabApplet.java) class which builds on top of Processing's PApplet and implements a GUI and some utility functions.

## GUI
See [GUI Manual](https://github.com/KrabCode/Sketches/blob/master/readme/GUIManual.md).

<img src="https://github.com/KrabCode/Sketches/blob/master/readme/preview.jpg?raw=true" width="400" alt="GUI">

## Keyboard controls
| Hotkey  | Action |
| ------------- | ------------- |
| H | hide GUI  |
| CTRL+Z | undo |
| CTRL+Y | redo |
| CTRL+S | save |
| CTRL+C | copy value |
| CTRL+V | paste value |
| R | reset value |
| I | screenshot |
| K | record mp4 |

## Recording
In order to use the 'I' and 'K' hotkeys you must include `rec()` or `rec(pGraphics)` in your sketch near the end of `draw()`.

### Screenshots
   Pressing 'I' saves an image of the current sketch or PGraphics to out/capture/\<timestamp\>_SketchName   
### Video
   Pressing 'K' 
   - Saves 360 frames to out/capture/\<timestamp\>_SketchName
        - The number of frames to save can be changed by adjusting the value of `frameRecordingDuration` before starting the recording.
   - Runs ffmpeg when done to save a video to out/video.
        - You'll need to download ffmpeg and put it in your classpath.
        - If you don't want to use ffmpeg and just want the images, set `FFMPEG_ENABLED` at the top of KrabApplet to false.
   
   Pressing 'L'
   - Stops recording manually and runs ffmpeg if enabled.
### Animations and perfect loops
   KrabApplet contains a 't' variable which increments by `TWO_PI / 360` every frame, making a complete 'rotation' in 360 frames.
   - The `frameRecordingDuration` value does not affect this.
   - A simple perfect loop can be achieved by plugging this t value into a `sin()` function and recording the default number of frames.
   - A more complex 2D perfect loop can be done with the parametric equation of a circle and then plugged into a noise function, see Etienne Jacob's [tutorial](https://necessarydisorder.wordpress.com/2017/11/15/drawing-from-noise-and-then-making-animated-loopy-gifs-from-there/).
   ```java
    float timeRadius = 1.6f;
    float timeX = timeRadius*cos(t);
    float timeY = timeRadius*sin(t);
    float loopedNoise = noise.eval(someX,someY,timeX,timeY);
   ```   
## Shader reloading
   KrabApplet allows you to modify your shaders and see the results in real-time without having to close and re-run the sketch.
   - The [shader\(\)](https://processing.org/reference/shader_.html) and [filter\(\)](https://processing.org/reference/filter_.html) Processing functions have their counterparts in `hotShader()` and `hotFilter()`.
   - KrabApplet manages the PShader variables so there's no need to call `loadShader()` yourself or keep any PShader variables around.
   - Shaders refresh when the last modified timestamp of the file changes, so all you need to do is to save your changes to the shader in any text editor.
   - Pass uniforms to the shader using the `uniform()` function which returns a PShader reference you can call `set()` on.
   - The optional PGraphics parameter specifies a PGraphics to apply the shader or filter to. It is applied to the main canvas otherwise. 
        - I recommend creating a separate PGraphics for drawing everything with KrabApplet, because otherwise the shaders and other things done on the main canvas can negatively impact drawing the GUI which is always drawn on the main canvas.
        - You can display the PGraphics easily on the main canvas using `image(pg, 0, 0, width, height)` before calling `rec(pg)` and `gui()` at the end of `draw()`.
   - You can include a vertex shader in the parameters, it will also be reloaded at runtime, but when calling any of these functions always pass both the fragment and the vertex path as parameters.
```java
String frag = "shaders/templates/frag.glsl";
uniform(frag).set("time", t);
hotFilter(frag, pg);
```
