# "Claire" Particle-based Audio Visualizer

This is a Java Swing based audio visualizer that analyzes input audio files and generates 
batches of frames that can be imported into video editing software for post-processing.

## Demo
_todo_

## Requirements

Java 7 or newer (ideally), and Python 2.7 for audio clip processing.

## Usage

Works best with short audio files, since amplitude data resolution will be higher. Aim for 3-15 second clips.
Audio files should be in wav format. Raw audio files should be placed in `data`, and can be processed by running 
`python filename.wav`. This will process the audio file into `data.json`. Running `src.Renderer.main` will read from this 
file, pull render properties from `src/properties.json`, and render frames into `renders` directory.

Audio files are parsed and rendered at 30 frames per second, but feel free to modify this as per your own needs.

Note: before rendering, if the previously used audio clip had longer duration than current clip, it is advised to 
clear the `renders` directory manually, since new frames will only overwrite existing frames, and leave behind old frames, 
so if the directory is linked in to a video editor like Premiere, you will see residual video content past the duration of 
the current audio clip.

## Configuration

`properties.json` stores config data for the renderer, and tweaking these settings will produce dramatically different 
visuals, depending on the desired effect. For our specific configuration in the film, we used approximately the 
following configuration:  
![Image](https://i.imgur.com/MCmXoaw.png)

Feel free to tweak the config to your own needs; annotations for every setting are provided in `src/Config.java`.

# Creators
Java particle simulation by Andriy Sheptunov  
Python audio parsing by Abbad Vakil
