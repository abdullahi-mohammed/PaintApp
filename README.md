# Overview

This project is a compact Java Swing paint application that demonstrates interactive GUI programming, basic drawing, and event handling in Java. The program provides a simple canvas where the user can draw freehand by dragging the mouse, choose between preset colors, and clear the drawing. It is implemented in a single source file (`PaintApp.java`) to keep the example focused and easy to study.

The primary goals are:

- provide a minimal, runnable example of custom painting with Swing/AWT
- illustrate handling mouse events and drawing to an off-screen image
- show a straightforward way to structure a small GUI app for learning and experimentation

Demo video: [Software Demo Video](http://youtube.link.goes.here)

## Features

- Freehand drawing with the mouse (click-and-drag)
- Three color choices: Black, Red, Blue
- Clear canvas button to reset the drawing
- Uses an off-screen image to preserve drawing between repaints

## How to Run

Make sure you have a Java JDK installed (Java SE 8 or later recommended). From the project directory run:

```CMD
javac PaintApp.java
java PaintApp
```

The application opens a window with a small toolbar on top and a white drawing canvas.

# Development Environment

- Language: Java (Swing / AWT)
- Minimum: JDK 8; tested with JDK 11+
- Build tools: `javac` and `java` (no external build system required)
- Recommended IDEs: IntelliJ IDEA, Eclipse, or VS Code with the Java extension pack

The app uses only standard Java libraries (no external dependencies). All UI and drawing code is contained within `PaintApp.java`.

# Useful Websites

- Oracle Java Tutorials — Swing: https://docs.oracle.com/javase/tutorial/uiswing/
- Oracle Java SE Documentation: https://docs.oracle.com/en/java/
- ZetCode Swing Tutorial (concise examples): https://zetcode.com/javagui/swing/
- Baeldung: Java GUI articles — https://www.baeldung.com/
- Stack Overflow — for troubleshooting specific API usage questions

# Project Structure

- `PaintApp.java` — main application and GUI implementation

# Future Work

Ideas and improvements to make this program more feature-complete:

- Add more colors and a color picker dialog
- Add brush size selection and custom stroke styles
- Implement undo/redo for drawing actions
- Save/load the canvas to/from an image file (PNG/JPEG)
- Add simple shape tools (lines, rectangles, ovals)
- Improve layout and accessibility of the UI
