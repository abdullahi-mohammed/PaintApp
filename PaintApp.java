import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class PaintApp extends JFrame {

    private int x = -1;
    private int y = -1;

    private Color currentColor = Color.BLACK;
    private float brushSize = 4f;

    private DrawPanel canvas;

    public PaintApp() {
        setTitle("Basic Paint Program");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Top panel for buttons
        JPanel topPanel = new JPanel();

        JButton blackButton = new JButton("Black");
        JButton redButton = new JButton("Red");
        JButton blueButton = new JButton("Blue");
        JButton undoButton = new JButton("Undo");
        JButton saveButton = new JButton("Save");
        JButton loadButton = new JButton("Load");
        JButton clearButton = new JButton("Clear");

        topPanel.add(blackButton);
        topPanel.add(redButton);
        topPanel.add(blueButton);
        topPanel.add(undoButton);
        topPanel.add(saveButton);
        topPanel.add(loadButton);
        topPanel.add(clearButton);

        add(topPanel, BorderLayout.NORTH);

        // Canvas panel
        canvas = new DrawPanel();
        canvas.setBackground(Color.WHITE);
        add(canvas, BorderLayout.CENTER);

        // Button actions
        blackButton.addActionListener(e -> currentColor = Color.BLACK);
        redButton.addActionListener(e -> currentColor = Color.RED);
        blueButton.addActionListener(e -> currentColor = Color.BLUE);
        undoButton.addActionListener(e -> canvas.undoLastStroke());
        saveButton.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Export image (.png)");
            javax.swing.filechooser.FileNameExtensionFilter pngFilter = new javax.swing.filechooser.FileNameExtensionFilter("PNG image (*.png)", "png");
            fc.setFileFilter(pngFilter);
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                // if no extension provided, default to .png
                if (!f.getName().toLowerCase().endsWith(".png")) {
                    f = new File(f.getParentFile(), f.getName() + ".png");
                }
                try {
                    canvas.saveToFile(f);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Save failed: " + ex.getMessage());
                }
            }
        });

        loadButton.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Open image (.png)");
            javax.swing.filechooser.FileNameExtensionFilter pngFilter = new javax.swing.filechooser.FileNameExtensionFilter("PNG image (*.png)", "png");
            fc.setFileFilter(pngFilter);
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                try {
                    canvas.loadFromFile(f);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Load failed: " + ex.getMessage());
                }
            }
        });

        clearButton.addActionListener(e -> canvas.clearCanvas());
    }

    // Abstract class demonstrating inheritance with 'extends' and 'abstract'
    private static abstract class ShapeStorage {
        abstract void addSegment(LineSegment s);
        abstract List<List<LineSegment>> getStrokes();
        abstract void clear();
        abstract void undo();
    }

    // Concrete storage using ArrayList
    private static class ListShapeStorage extends ShapeStorage {
        private final List<List<LineSegment>> strokes = new ArrayList<>();
        private List<LineSegment> currentStroke = null;

        @Override
        void addSegment(LineSegment s) {
            if (currentStroke == null) {
                currentStroke = new ArrayList<>();
                strokes.add(currentStroke);
            }
            currentStroke.add(s);
        }

        void startStroke() {
            currentStroke = new ArrayList<>();
            strokes.add(currentStroke);
        }

        void endStroke() {
            currentStroke = null;
        }

        @Override
        List<List<LineSegment>> getStrokes() {
            return strokes;
        }

        @Override
        void clear() {
            strokes.clear();
        }

        @Override
        void undo() {
            if (!strokes.isEmpty()) strokes.remove(strokes.size() - 1);
        }

        // Removed project file serialization to simplify format: PNG-only export/load
    }

    private class DrawPanel extends JPanel {
        // storage for strokes
        private final ListShapeStorage storage = new ListShapeStorage();
        private BufferedImage backgroundImage = null;

        DrawPanel() {
            addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseDragged(MouseEvent e) {
                    int newX = e.getX();
                    int newY = e.getY();

                    if (x != -1 && y != -1) {
                        storage.addSegment(new LineSegment(x, y, newX, newY, currentColor, brushSize));
                        repaint();
                    }

                    x = newX;
                    y = newY;
                }
            });

            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    storage.startStroke();
                    x = e.getX();
                    y = e.getY();
                }

                public void mouseReleased(MouseEvent e) {
                    x = -1;
                    y = -1;
                    storage.endStroke();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw background image if present, otherwise clear to white
            if (backgroundImage != null) {
                g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
            } else {
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }

            // Draw all strokes (nested loops demonstrate loops and collections)
            for (List<LineSegment> stroke : storage.getStrokes()) {
                for (LineSegment s : stroke) {
                    g2.setColor(s.color);
                    g2.setStroke(new BasicStroke(s.strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(s.x1, s.y1, s.x2, s.y2);
                }
            }
        }

        public void clearCanvas() {
            storage.clear();
            repaint();
        }

        public void undoLastStroke() {
            storage.undo();
            repaint();
        }

        // Save: always export the canvas as a PNG image
        public void saveToFile(File f) throws IOException {
            BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = img.createGraphics();
            this.paint(g2);
            g2.dispose();
            ImageIO.write(img, "png", f);
        }

        // Load: only PNG images supported (used as background)
        public void loadFromFile(File f) throws IOException {
            BufferedImage img = ImageIO.read(f);
            if (img == null) throw new IOException("Unsupported or invalid image file");
            backgroundImage = img;
            // clear strokes when loading an image
            storage.clear();
            repaint();
        }
    }

    private static class LineSegment {
        final int x1, y1, x2, y2;
        final Color color;
        final float strokeWidth;

        LineSegment(int x1, int y1, int x2, int y2, Color color, float strokeWidth) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.color = color;
            this.strokeWidth = strokeWidth;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PaintApp app = new PaintApp();
            app.setVisible(true);
        });
    }
}
