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

## KrabApplet
All of my sketches extend [KrabApplet](https://github.com/KrabCode/Sketches/blob/master/src/applet/KrabApplet.java) which implements a GUI and many common utility functions.

### GUI
See [GUI Manual](https://github.com/KrabCode/Sketches/blob/master/readme/GUIManual.md).

<img src="https://github.com/KrabCode/Sketches/blob/master/readme/preview.jpg?raw=true" width="400" alt="GUI">

### Keyboard controls
| Hotkey  | Action |
| ------------- | ------------- |
| H | hide GUI  |
| CTRL+Z | undo |
| CTRL+Y | redo |
| CTRL+S | save |
| R | reset value |
| I | screenshot |
| K | record mp4 |

### Recording
In order to use the 'I' and 'K' hotkeys you must include rec() or rec(pGraphics) in your sketch near the end of draw().

#### Screenshots
   Pressing 'I' saves an image of the current sketch or PGraphics to out/capture/\<timestamp\>_SketchName
#### Video
   Pressing 'K' 
   - saves 360 frames to out/capture/Timestamp_SketchName
        - the number of frames can be changed by changing the value of frameRecordingDuration inside setup()
   - it calls ffmpeg when done to save a video to out/video
        - you'll need to download ffmpeg and modify the ffmpegCommand in KrabApplet to match your directories for it to work
        - if you don't want to use ffmpeg and just want the images, set ffmpegEnabled in KrabApplet to false
#### Animations
    
#### Perfect loops

### Shader reloading

### Utility functions

