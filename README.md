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

Additional implemented features:

- Undo: remove the last stroke
- Export PNG: export the current canvas as a `.png` image
- Load PNG as background: open a PNG and use it as the drawing background (clears strokes)
- Internal design: strokes are stored as nested `ArrayList<List<LineSegment>>` (demonstrates Java Collections)
- Demonstrates `abstract` inheritance (`ShapeStorage`) and a simple storage implementation

## How to Run

Make sure you have a Java JDK installed (Java SE 8 or later recommended). From the project directory run:

```CMD
javac PaintApp.java
java PaintApp
```

The application opens a window with a small toolbar on top and a white drawing canvas.

Save / Export / Load usage:

- Export as PNG image: Click `Save` → choose a filename (the app will append `.png` if you omit an extension). The saved PNG contains the current canvas and strokes.
- Load a PNG: Click `Load` → open a `.png` file to use as the canvas background (this clears any existing strokes).

# Development Environment

- Language: Java (Swing / AWT)
- Minimum: JDK 8; tested with JDK 11+
- Build tools: `javac` and `java` (no external build system required)
- Recommended IDEs: IntelliJ IDEA, Eclipse, or VS Code with the Java extension pack

The app uses only standard Java libraries (no external dependencies). All UI and drawing code is contained within `PaintApp.java`.

# Useful Websites

- Oracle Java Tutorials — Swing: https://docs.oracle.com/javase/tutorial/uiswing/
- Oracle Java SE Documentation: https://docs.oracle.com/en/java/
- ZetCode Swing Tutorial (concise examples): https://zetcode.com/java/
- Stack Overflow — for troubleshooting specific API usage questions

# Project Structure

- `PaintApp.java` — main application and GUI implementation

# Future Work

Ideas and improvements to make this program more feature-complete:

- Add more colors and a color picker dialog
- Add brush size selection and custom stroke styles
- Add simple shape tools (lines, rectangles, ovals)
- Improve layout and accessibility of the UI

Remaining / suggested next steps:

- Add a dedicated "Export PNG" button to make exporting explicit and simpler for users (the Save dialog supports PNG already).
- Add brush-size UI and keyboard shortcuts for better UX.
- Implement a full undo/redo stack (currently Undo removes only the last stroke).
- Create a runnable JAR and native installers using `jpackage` for easy distribution to non-technical users.
- Replace Java object serialization for project files with a human-readable format (JSON) if desired.
