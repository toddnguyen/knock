# knock
By Todd Nguyen and Alvin Wu  
University of Illinois at Urbana-Champaign  
ECE 498 Mobile Sensing Final Project  
Project presentation here: https://dl.dropboxusercontent.com/u/2321396/knock_presentation.pptx

### How to use the app
Requirements: Stereo microphone
Open app, knock twice on one side of the phone (top or bottom), wait for phone to point direction.

### Demo video (click picture to view)

[![Demo Video](https://img.youtube.com/vi/xmT30UUIg0w/0.jpg)](https://www.youtube.com/watch?v=xmT30UUIg0w)

### Motivation
Imagine you are cooking and your hands are dirty, but you want to scroll the page on the recipe you are looking at. You cannot touch your phone due to dirty hands, but knocking on the countertop next to it is intuitive and could avoids the issue of dirtying your phone's screen.
There are other ways that knocking around your phone could be useful; raising and lowering the volume, or making any surface a smart surface.

### Technical Questions:
* Can knocks be detected with mobile sensors
* Can the location of the knock be extracted
* Is there a power efficient way to achieve this control?

### Knock detection
By analyzing gyroscope and accelerometer, we can detect knocks as clear spikes in the readings.

### Localizing Knocks
#### Important numbers
* Distance between two microphones on my phone: ~6 inches (Samsung Galaxy Note 5)
* Speed of sound at sea level: 340 m/s
* Sampling rate: 44.1 KHz

From these numbers, we see can see that the distance a sound wave travels in one sampling period is about 0.3 inches.
Therefore, we should see a phase difference in between the two microphones of ~16 samples.

### Classifying Knocks
We use a cross correlation to determine knock position. We limit our cross-correlation to -200 to 200 in order o improve speed.

### Problem: Energy
Using the microphone all the time uses a lot of power, instead, we detect two knocks. The first knock is detected by the accelerometer and turns on the microphone. The second knock is detected by both the microphones and accelerometer and determines direction.

### Issues
* Microphone start up time (sometimes up to 500ms). Two knocks that are two close together result in false positive
* Noisy environments mess up localization

### Future work
* Filtering on microphones
